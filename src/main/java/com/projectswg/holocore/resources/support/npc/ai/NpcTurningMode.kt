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
package com.projectswg.holocore.resources.support.npc.ai

import com.projectswg.common.data.location.Location
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.custom.AIObject
import com.projectswg.holocore.resources.support.objects.swg.custom.NpcMode
import kotlinx.coroutines.delay
import java.util.concurrent.ThreadLocalRandom

/**
 * AI object that loiters the area
 */
class NpcTurningMode(obj: AIObject) : NpcMode(obj) {
	
	private var mainParent: SWGObject? = null
	private var mainLocation: Location? = null
	
	override suspend fun onModeStart() {
		val currentLocation = ai.location
		var startingLocation = mainLocation
		if (startingLocation == null) startingLocation = currentLocation
		if (mainParent == null) mainParent = ai.parent
		if (startingLocation.distanceTo(currentLocation) >= 1) runTo(startingLocation)
		mainLocation = startingLocation
	}
	
	override suspend fun onModeLoop() {
		val random = ThreadLocalRandom.current()
		if (isRooted) {
			delay((10000 + random.nextInt(5000) - 2500L))
			return
		}
		
		if (random.nextDouble() > 0.25) {  // Only a 25% movement chance
			delay(30_000L + random.nextInt(6000) - 3000L)
			return
		}
		val theta = random.nextDouble() * 360
		
		moveTo(mainParent, Location.builder(mainLocation).setHeading(theta).build())
		delay(30_000L + random.nextInt(6000) - 3000L)
	}
	
}
