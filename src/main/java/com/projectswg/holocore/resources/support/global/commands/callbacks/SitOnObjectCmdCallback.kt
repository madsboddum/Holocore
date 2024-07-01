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
package com.projectswg.holocore.resources.support.global.commands.callbacks

import com.projectswg.common.data.encodables.tangible.Posture
import com.projectswg.common.network.packets.swg.zone.object_controller.SitOnObject
import com.projectswg.holocore.resources.support.global.commands.ICmdCallback
import com.projectswg.holocore.resources.support.global.commands.Locomotion
import com.projectswg.holocore.resources.support.global.player.Player
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureState

class SitOnObjectCmdCallback : ICmdCallback {
	override fun execute(player: Player, target: SWGObject?, args: String) {
		val creature = player.creatureObject

		if (Locomotion.KNOCKED_DOWN.isActive(creature) || creature.posture == Posture.DEAD || creature.posture == Posture.INCAPACITATED || creature.isStatesBitmask(CreatureState.RIDING_MOUNT)) return
		val objectID = creature.objectId
		val sot: SitOnObject

		if (args.isNotEmpty()) {
			val cmd = args.split(",".toRegex(), limit = 4).toTypedArray()

			val x = cmd[0].toFloat()
			val z = cmd[1].toFloat()
			val y = cmd[2].toFloat()
			val cellid = cmd[3].toLong()

			sot = SitOnObject(objectID, cellid, x, y, z)
			creature.setStatesBitmask(CreatureState.SITTING_ON_CHAIR)
		} else {
			val loc = creature.location
			sot = SitOnObject(objectID, 0, loc.x.toFloat(), loc.y.toFloat(), loc.z.toFloat())
		}
		creature.posture = Posture.SITTING
		creature.setMovementPercent(0.0)
		creature.setTurnScale(0.0)
		creature.sendObservers(SitOnObject(creature.objectId, sot))
	}
}