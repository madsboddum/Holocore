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
package com.projectswg.holocore.services.gameplay.combat.loot

import com.projectswg.holocore.headless.HeadlessSWGClient.Companion.createZonedInCharacter
import com.projectswg.holocore.headless.attack
import com.projectswg.holocore.headless.loot
import com.projectswg.holocore.headless.waitUntilAwareOf
import com.projectswg.holocore.headless.waitUntilPostureUpdate
import com.projectswg.holocore.test.runners.AcceptanceTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LootTest : AcceptanceTest() {
	
	@Test
	fun itemsAreGenerated() {
		val user = generateUser()
		val zonedInCharacter = createZonedInCharacter(user.username, user.password, "tester")
		val npc = spawnNPC("creature_kreetle_swarmling", zonedInCharacter.player.creatureObject.location)
		npc.health = 1
		
		zonedInCharacter.waitUntilAwareOf(npc)
		zonedInCharacter.attack(npc)
		zonedInCharacter.waitUntilPostureUpdate(npc)
		
		val availableItems = zonedInCharacter.loot(npc)
		
		assertTrue(availableItems.isNotEmpty())
	}
}