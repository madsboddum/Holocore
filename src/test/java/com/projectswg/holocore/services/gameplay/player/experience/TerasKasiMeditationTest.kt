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
package com.projectswg.holocore.services.gameplay.player.experience

import com.projectswg.holocore.headless.*
import com.projectswg.holocore.resources.support.global.player.AccessLevel
import com.projectswg.holocore.test.runners.AcceptanceTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TerasKasiMeditationTest : AcceptanceTest() {

	@BeforeEach
	fun setUp() {
		addUser("username", "password", accessLevel = AccessLevel.DEV)
	}

	@Test
	fun noviceTerasKasiSitting() {
		val character = HeadlessSWGClient.createZonedInCharacter("username", "password", "adminchar")
		character.adminGrantSkill("combat_unarmed_novice")
		
		character.sit()
		character.meditate()
		
		assertEquals("meditating", character.player.creatureObject.moodAnimation)
	}

	@Test
	fun noviceTerasKasiStanding() {
		val character = HeadlessSWGClient.createZonedInCharacter("username", "password", "adminchar")
		character.adminGrantSkill("combat_unarmed_novice")
		
		assertThrows<CommandFailedException> { character.meditate() }
	}

	@Test
	fun notNoviceTerasKasi() {
		val character = HeadlessSWGClient.createZonedInCharacter("username", "password", "adminchar")

		character.sit()
		assertThrows<CommandFailedException> { character.meditate() }
	}

}
