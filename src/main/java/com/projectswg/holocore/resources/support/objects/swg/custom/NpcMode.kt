/***********************************************************************************
 * Copyright (c) 2025 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is an emulation project for Star Wars Galaxies founded on            *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create one or more emulators which will provide servers for      *
 * players to continue playing a game similar to the one they used to play.        *
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
package com.projectswg.holocore.resources.support.objects.swg.custom

import com.projectswg.common.data.encodables.tangible.Posture
import com.projectswg.common.data.location.Location
import com.projectswg.holocore.intents.support.objects.MoveObjectIntent
import com.projectswg.holocore.resources.support.npc.spawn.Spawner
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.joshlarson.jlcommon.log.Log

abstract class NpcMode(val ai: AIObject) {
	
	fun act(coroutineScope: CoroutineScope): Job {
		return coroutineScope.launch {
			onModeStart()
			try {
				while (isActive) {
					val start = System.nanoTime()
					onModeLoop()
					val time = (System.nanoTime() - start) / 1e6
					if (time < 50)
						Log.w("Possible NpcMode fault with AI $ai  and mode ${this@NpcMode}")
				}
			} finally {
				onModeEnd()
			}
		}
	}

	fun onPlayerEnterAware(player: CreatureObject, distance: Double) {
	}

	open fun onPlayerMoveInAware(player: CreatureObject, distance: Double) {
	}

	open fun onPlayerExitAware(player: CreatureObject) {
	}

	open suspend fun onModeStart() {
	}
	
	open suspend fun onModeLoop() {
	}

	open suspend fun onModeEnd() {
	}

	val nearbyPlayers: Collection<CreatureObject>
		get() = ai.nearbyPlayers

	val isRooted: Boolean
		get() = when (ai.posture) {
			Posture.DEAD, Posture.INCAPACITATED, Posture.INVALID, Posture.KNOCKED_DOWN, Posture.LYING_DOWN, Posture.SITTING                                                                                   -> true
			Posture.BLOCKING, Posture.CLIMBING, Posture.CROUCHED, Posture.DRIVING_VEHICLE, Posture.FLYING, Posture.PRONE, Posture.RIDING_CREATURE, Posture.SKILL_ANIMATING, Posture.SNEAKING, Posture.UPRIGHT ->                // Rooted if there are no nearby players
				nearbyPlayers.isEmpty()

			else                                                                                                                                                                                              -> nearbyPlayers.isEmpty()
		}

	val spawner: Spawner?
		get() = ai.spawner

	val walkSpeed: Double
		get() = (ai.movementPercent * ai.movementScale * ai.walkSpeed).toDouble()

	val runSpeed: Double
		get() = (ai.movementPercent * ai.movementScale * ai.runSpeed).toDouble()

	fun moveTo(parent: SWGObject?, location: Location?) {
		MoveObjectIntent(ai, parent, location!!, walkSpeed).broadcast()
	}

	fun moveTo(location: Location?) {
		MoveObjectIntent(ai, location!!, walkSpeed).broadcast()
	}

	fun walkTo(parent: SWGObject?, location: Location) {
		ai.moveTo(parent, location, walkSpeed)
	}

	fun walkTo(location: Location) {
		ai.moveTo(null, location, walkSpeed)
	}

	fun runTo(parent: SWGObject?, location: Location) {
		ai.moveTo(parent, location, runSpeed)
	}

	fun runTo(location: Location) {
		ai.moveTo(null, location, runSpeed)
	}
}
