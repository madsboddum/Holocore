/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/
package com.projectswg.holocore.services.gameplay.entertainment

import com.projectswg.common.data.CRC
import com.projectswg.common.data.encodables.oob.ProsePackage
import com.projectswg.common.data.encodables.oob.StringId
import com.projectswg.common.data.encodables.tangible.Posture
import com.projectswg.common.data.objects.GameObjectType
import com.projectswg.common.network.packets.swg.zone.PlayMusicMessage
import com.projectswg.common.network.packets.swg.zone.object_controller.Animation
import com.projectswg.holocore.intents.gameplay.entertainment.dance.*
import com.projectswg.holocore.intents.gameplay.player.experience.ExperienceIntent
import com.projectswg.holocore.intents.support.global.chat.SystemMessageIntent
import com.projectswg.holocore.intents.support.global.zone.PlayerEventIntent
import com.projectswg.holocore.intents.support.global.zone.PlayerTransformedIntent
import com.projectswg.holocore.resources.support.data.server_info.StandardLog
import com.projectswg.holocore.resources.support.data.server_info.loader.DataLoader.Companion.performances
import com.projectswg.holocore.resources.support.data.server_info.loader.PerformanceLoader
import com.projectswg.holocore.resources.support.global.player.Player
import com.projectswg.holocore.resources.support.global.player.PlayerEvent
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import com.projectswg.holocore.services.support.objects.ObjectStorageService
import me.joshlarson.jlcommon.control.IntentHandler
import me.joshlarson.jlcommon.control.Service
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class EntertainmentService : Service() {
	private val performerMap = mutableMapOf<Long, Performance>()
	private val executorService = Executors.newSingleThreadScheduledExecutor()

	override fun terminate(): Boolean {
		executorService.shutdownNow()
		return super.terminate()
	}

	@IntentHandler
	private fun handleDanceIntent(di: DanceIntent) {
		val player = di.player
		val dancer = player.creatureObject
		val danceName = di.danceName
		val performanceByName = performances().getDancePerformanceByName(danceName)
		if (di.isStartDance) {
			// This intent wants the creature to start dancing
			// If we're changing dance, allow them to do so
			val changeDance = di.isChangeDance
			if (!changeDance && dancer.isPerforming) {
				SystemMessageIntent(player, "@performance:already_performing_self").broadcast()
			} else if (performanceByName != null) {
				// The dance name is valid.
				if (dancer.hasCommand(performanceByName.requiredDance)) {
					if (changeDance) {    // If they're changing dance, we just need to change their animation.
						changeDance(dancer, danceName)
					} else {    // Otherwise, they should begin performing now
						startDancing(player, danceName)
					}
				} else {
					// This creature doesn't have the ability to perform this dance.
					SystemMessageIntent(player, "@performance:dance_lack_skill_self").broadcast()
				}
			} else {
				// This dance name is invalid
				SystemMessageIntent(player, "@performance:dance_unknown_self").broadcast()
			}
		} else {
			// This intent wants the creature to stop dancing
			stopDancing(player)
		}
	}

	@IntentHandler
	private fun handleStartMusicIntent(intent: StartMusicIntent) {
		val player = intent.player
		val performer = player.creatureObject
		val songName = intent.songName

		if (performer.isPerforming) {
			SystemMessageIntent(player, "@performance:already_performing_self").broadcast()
			return
		}
		
		val instrument = getInstrument(performer)
		
		if (instrument == null) {
			SystemMessageIntent(player, "@performance:music_no_instrument").broadcast()
			return
		}
		
		val instrumentId = instrument.template.replace("object/tangible/instrument/shared_", "").replace(".iff", "").replace("_", " ")
		val performanceByName = performances().getMusicPerformanceBySongAndInstrument(songName, instrumentId)

		if (performanceByName == null) {
			SystemMessageIntent(player, "@performance:music_invalid_song").broadcast()
			return
		}
		
		if (!performer.hasCommand(performanceByName.requiredInstrument)) {
			SystemMessageIntent(player, "@performance:music_lack_skill_instrument").broadcast()
			return
		}
		
		if (!performer.hasCommand(performanceByName.requiredSong)) {
			SystemMessageIntent(player, "@performance:music_lack_skill_song_self").broadcast()
			return
		}
		
		startPlaying(player, performanceByName)
	}
	
	@IntentHandler
	private fun handleStopMusicIntent(intent: StopMusicIntent) {
		val player = intent.player
		stopPlaying(player)
	}

	private fun getInstrument(performer: CreatureObject): SWGObject? {
		val lookAtTargetId = performer.lookAtTargetId
		ObjectStorageService.ObjectLookup.getObjectById(lookAtTargetId)?.let {
			if (isInstrument(it)) {
				return it
			}
		}
		
		val rightHandObject = performer.getSlottedObject("hold_r")
		if (isInstrument(rightHandObject)) {
			return rightHandObject
		}
		
		return null
	}

	private fun isInstrument(item: SWGObject?) = item?.gameObjectType == GameObjectType.GOT_MISC_INSTRUMENT

	@IntentHandler
	private fun handlePlayerEventIntent(pei: PlayerEventIntent) {
		val player = pei.player
		val creature = player.creatureObject ?: return
		when (pei.event) {
			PlayerEvent.PE_LOGGED_OUT -> handlePlayerLoggedOut(creature)
			PlayerEvent.PE_ZONE_IN_SERVER -> handlePlayerZoneIn(creature)
			PlayerEvent.PE_DISAPPEAR -> handlePlayerDisappear(player)
			else -> {}
		}
	}

	private fun handlePlayerDisappear(player: Player) {
		val creature = player.creatureObject
		// If a spectator disappears, they need to stop watching and be removed from the audience
		val performerId = creature.performanceListenTarget
		val performance = performerMap[performerId]
		val spectating = performance?.removeSpectator(creature) == true
		if (performerId != 0L && spectating) {
			stopWatching(player, false)
		}

		// If a performer disappears, the audience needs to be cleared
		// They're also removed from the map of active performers.
		if (isEntertainer(creature) && creature.isPerforming) {
			performerMap[creature.objectId]?.clearSpectators()
			performerMap.remove(creature.objectId)
		}
	}

	private fun handlePlayerZoneIn(creature: CreatureObject) {
		if (isEntertainer(creature) && creature.posture == Posture.SKILL_ANIMATING) {
			val danceId = creature.animation.replace("dance_", "").toInt()	// TODO this doesn't cover if we're playing music
			val performanceByDanceId = performances().getPerformanceByDanceId(danceId)
			if (performanceByDanceId != null) {
				scheduleExperienceTask(
					creature, performanceByDanceId
				)
			}
		}
	}

	private fun handlePlayerLoggedOut(creature: CreatureObject) {
		if (isEntertainer(creature) && creature.posture == Posture.SKILL_ANIMATING) {
			cancelExperienceTask(creature)
		}
	}

	@IntentHandler
	private fun handleFlourishIntent(fi: FlourishIntent) {
		val performer = fi.performer
		val performerObject = performer.creatureObject
		if (performerObject.performanceCounter != 0) return
		performerObject.performanceCounter = 1

		// Send the flourish animation to the owner of the creature and owners of creatures observing
		val flourishNumber = fi.flourishNumber
		performerObject.sendObservers(Animation(performerObject.objectId, "skill_action_$flourishNumber"))
		if (performerObject.performanceId > 0) {
			val performance = performerMap[performerObject.objectId] ?: return
			val performanceInfo = performance.performanceInfo
			val flourishSound = performanceInfo.flourishes[flourishNumber - 1]
			val flourishSoundMessage = PlayMusicMessage(performerObject.objectId, flourishSound, 1, false)
			performer.sendPacket(flourishSoundMessage)
			performance.sendFlourishMusicMessage(flourishSoundMessage)
		}
		SystemMessageIntent(performer, "@performance:flourish_perform").broadcast()
	}

	@IntentHandler
	private fun handleWatchIntent(wi: WatchIntent) {
		val target = wi.target
		if (target is CreatureObject) {
			val actor = wi.actor
			if (!isEntertainer(target)) {
				// We can't watch non-entertainers - do nothing
				return
			}
			if (target.isPlayer) {
				if (target.isPerforming) {
					val performance = performerMap[target.objectId] ?: return
					if (wi.isStartWatch) {
						if (performance.addSpectator(actor.creatureObject)) {
							startWatching(actor, target)
						}
					} else {
						if (performance.removeSpectator(actor.creatureObject)) {
							stopWatching(actor, true)
						}
					}
				} else {
					// While this is a valid target for watching, the target is currently not performing.
					SystemMessageIntent(
						actor, ProsePackage(StringId("performance", "dance_watch_not_dancing"), "TT", target.objectName)
					).broadcast()
				}
			} else {
				// You can't watch NPCs, regardless of whether they're dancing or not
				SystemMessageIntent(actor, "@performance:dance_watch_npc").broadcast()
			}
		}
	}

	@IntentHandler
	private fun handlePlayerTransformedIntent(pti: PlayerTransformedIntent) {
		val movedPlayer = pti.player
		val performanceListenTarget = movedPlayer.performanceListenTarget
		if (performanceListenTarget != 0L) {
			// They're watching a performer!
			val performance = performerMap[performanceListenTarget]
			if (performance == null) {
				StandardLog.onPlayerError(this, movedPlayer, "was watching a performer (%d) that didn't exist", performanceListenTarget)
				return
			}
			val performer = performance.performer
			val performerLocation = performer.worldLocation
			val movedPlayerLocation = pti.player.worldLocation // Ziggy: The newLocation in PlayerTransformedIntent isn't the world location, which is what we need here
			if (!movedPlayerLocation.isWithinDistance(performerLocation, WATCH_RADIUS)) {
				// They moved out of the defined range! Make them stop watching
				if (performance.removeSpectator(movedPlayer)) {
					val player = movedPlayer.owner
					if (player != null) {
						stopWatching(player, true)
					}
				} else {
					StandardLog.onPlayerError(this, movedPlayer, "ran out of range of %s, but couldn't stop watching because they weren't watching in the first place", performer)
				}
			}
		}
	}

	/**
	 * Checks if the `CreatureObject` is a Novice Entertainer.
	 *
	 * @param performer
	 * @return true if `performer` is a Novice Entertainer and false if not
	 */
	private fun isEntertainer(performer: CreatureObject): Boolean {
		return performer.hasSkill("social_entertainer_novice") // First entertainer skillbox
	}

	private fun scheduleExperienceTask(performer: CreatureObject, performanceInfo: PerformanceLoader.PerformanceInfo) {
		val loopDuration = performanceInfo.loopDuration
		StandardLog.onPlayerEvent(this, performer, "was scheduled to receive XP every %d seconds", loopDuration.toLong())
		synchronized(performerMap) {
			val performerId = performer.objectId
			val future = executorService.scheduleAtFixedRate(
				EntertainerExperience(performer),
				loopDuration.toLong(),
				loopDuration.toLong(),
				TimeUnit.SECONDS
			)

			// If they went LD but came back before disappearing
			val performance = performerMap[performerId]
			if (performance != null) {
				performance.future = future
			} else {
				performerMap.put(performer.objectId, Performance(performer, future, performanceInfo))
			}
		}
	}

	private fun cancelExperienceTask(performer: CreatureObject) {
		synchronized(performerMap) {
			val performance = performerMap[performer.objectId]
			if (performance == null) {
				StandardLog.onPlayerError(this, performer, "wasn't found in performerMap")
				return
			}
			performance.future.cancel(false)
			StandardLog.onPlayerEvent(this, performer, "no longer receives XP every %d seconds", performance.performanceInfo.loopDuration.toInt())
		}
	}

	private fun startDancing(player: Player, danceName: String) {
		val dancer = player.creatureObject
		val performanceByName = performances().getDancePerformanceByName(danceName)
		if (performanceByName == null) {
			StandardLog.onPlayerEvent(this, dancer, "tried to start unknown dance %s", danceName)
			return
		}
		val danceVisualId = performanceByName.danceVisualId
		dancer.animation = "dance_$danceVisualId"
		dancer.performanceId = 0 // 0 - anything else will make it look like we're playing music
		dancer.performanceCounter = 0
		dancer.isPerforming = true
		dancer.posture = Posture.SKILL_ANIMATING
		scheduleExperienceTask(dancer, performanceByName)
		SystemMessageIntent(player, "@performance:dance_start_self").broadcast()
	}

	private fun startPlaying(player: Player, performanceByName: PerformanceLoader.PerformanceInfo) {
		val musician = player.creatureObject
		musician.animation = "music_3"	// TODO there's a lookup-table for this: serverdata/entertainment/instrument.sdb
		musician.performanceId = performanceByName.index
		musician.performanceCounter = 0
		musician.isPerforming = true
		musician.posture = Posture.SKILL_ANIMATING
		musician.performanceListenTarget = musician.objectId
		scheduleExperienceTask(musician, performanceByName)
		SystemMessageIntent(player, "@performance:music_start_self").broadcast()
	}

	private fun stopPlaying(player: Player) {
		val musician = player.creatureObject
		if (musician.isPerforming) {
			musician.isPerforming = false
			musician.posture = Posture.UPRIGHT
			musician.performanceCounter = 0
			musician.animation = ""
			musician.performanceId = 0

			// Non-entertainers don't receive XP and have no audience - ignore them
			if (isEntertainer(musician)) {
				cancelExperienceTask(musician)
				val performance = performerMap.remove(musician.objectId)
				performance?.clearSpectators()
			}
			SystemMessageIntent(player, "@performance:music_stop_self").broadcast()
		} else {
			SystemMessageIntent(player, "@performance:music_not_performing").broadcast()
		}
	}

	private fun stopDancing(player: Player) {
		val dancer = player.creatureObject
		if (dancer.isPerforming) {
			dancer.isPerforming = false
			dancer.posture = Posture.UPRIGHT
			dancer.performanceCounter = 0
			dancer.animation = ""

			// Non-entertainers don't receive XP and have no audience - ignore them
			if (isEntertainer(dancer)) {
				cancelExperienceTask(dancer)
				val performance = performerMap.remove(dancer.objectId)
				performance?.clearSpectators()
			}
			SystemMessageIntent(player, "@performance:dance_stop_self").broadcast()
		} else {
			SystemMessageIntent(player, "@performance:dance_not_performing").broadcast()
		}
	}

	private fun changeDance(dancer: CreatureObject, newPerformanceName: String) {
		val performance = performerMap[dancer.objectId]
		if (performance != null) {
			val performanceByName = performances().getDancePerformanceByName(newPerformanceName)
			if (performanceByName != null) {
				dancer.animation = "dance_" + performanceByName.performanceName
				performance.performanceInfo = performanceByName
			}
		}
	}

	private fun startWatching(player: Player, creature: CreatureObject) {
		val actor = player.creatureObject
		actor.moodAnimation = "entertained"
		SystemMessageIntent(player, ProsePackage(StringId("performance", "dance_watch_self"), "TT", creature.objectName)).broadcast()
		actor.performanceListenTarget = creature.objectId
	}

	private fun stopWatching(player: Player, displaySystemMessage: Boolean) {
		val actor = player.creatureObject
		actor.moodAnimation = "neutral"
		if (displaySystemMessage) SystemMessageIntent(player, "@performance:dance_watch_stop_self").broadcast()
		actor.performanceListenTarget = 0
	}

	private inner class Performance(val performer: CreatureObject, var future: Future<*>, var performanceInfo: PerformanceLoader.PerformanceInfo) {
		private val audience = mutableSetOf<CreatureObject>()

		fun addSpectator(spectator: CreatureObject): Boolean {
			return audience.add(spectator)
		}

		fun removeSpectator(spectator: CreatureObject): Boolean {
			return audience.remove(spectator)
		}
		
		fun sendFlourishMusicMessage(musicMessage: PlayMusicMessage) {
			audience.forEach { it.sendSelf(musicMessage) }
		}

		fun clearSpectators() {
			audience.mapNotNull { it.owner }.forEach(Consumer { player -> stopWatching(player, true) })
			audience.clear()
		}
	}

	private inner class EntertainerExperience(private val performer: CreatureObject) : Runnable {
		override fun run() {
			val performance = performerMap[performer.objectId]
			if (performance == null) {
				StandardLog.onPlayerError(this, performer, "is not in performerMap")
				return
			}
			val performanceInfo = performance.performanceInfo
			val flourishXpMod = performanceInfo.flourishXpMod
			val performanceCounter = performer.performanceCounter
			val xpGained = performanceCounter * flourishXpMod
			if (xpGained > 0) {
				if (isEntertainer(performer)) {
					val xpType = xpType(performanceInfo.type)
					ExperienceIntent(performer, performer, xpType, xpGained, true).broadcast()
				}
				performer.performanceCounter = performanceCounter - 1
			}
		}
		
		private fun xpType(type: CRC) = if (type == CRC("music")) "music" else "dance"
	}

	companion object {
		private const val WATCH_RADIUS = 20.0
	}
}
