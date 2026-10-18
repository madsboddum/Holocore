/***********************************************************************************
 * Copyright (c) 2026 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.holocore.resources.support.objects.swg.tangible

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ArmorHindranceTest {

	@Test
	fun `base penalty scales with general protection within the level range`() {
		val expected = Hindrance(37.6, 37.6, 37.6)
		val actual = ArmorHindrance.getBase(ArmorCategory.battle, ArmorLevel.advanced, 5664)

		assertEquals(expected, actual)
	}

	@Test
	fun `base penalty differs per penalty type for reconnaissance`() {
		val expected = Hindrance(19.4, 38.8, 58.2)
		val actual = ArmorHindrance.getBase(ArmorCategory.reconnaissance, ArmorLevel.advanced, 5836)

		assertEquals(expected, actual)
	}

	@Test
	fun `no certifications leaves the base penalty`() {
		val base = ArmorHindrance.getBase(ArmorCategory.assault, ArmorLevel.advanced, 5243)
		val expected = base
		val actual = ArmorHindrance.getMitigated(ArmorCategory.assault, base, emptySet())

		assertEquals(expected, actual)
	}

	@Test
	fun `highest certification rank is the one that counts`() {
		val base = ArmorHindrance.getBase(ArmorCategory.assault, ArmorLevel.advanced, 5243)
		val commands = setOf("assault_move_mitigate_1", "assault_move_mitigate_2")
		val expected = Hindrance(11.9, 34.6, 17.3)
		val actual = ArmorHindrance.getMitigated(ArmorCategory.assault, base, commands)

		assertEquals(expected, actual)
	}

	@Test
	fun `penalty never drops below zero`() {
		val base = ArmorHindrance.getBase(ArmorCategory.battle, ArmorLevel.basic, 300)
		val commands = setOf("battle_move_mitigate_3", "battle_accuracy_mitigate_3", "battle_firerate_mitigate_3")
		val expected = Hindrance(0.0, 0.0, 0.0)
		val actual = ArmorHindrance.getMitigated(ArmorCategory.battle, base, commands)

		assertEquals(expected, actual)
	}

	@Test
	fun `certifications for another category do nothing`() {
		val base = ArmorHindrance.getBase(ArmorCategory.assault, ArmorLevel.advanced, 5243)
		val commands = setOf("battle_move_mitigate_3", "recon_accuracy_mitigate_3")
		val expected = base
		val actual = ArmorHindrance.getMitigated(ArmorCategory.assault, base, commands)

		assertEquals(expected, actual)
	}

	@Test
	fun `full certification removes advanced penalties`() {
		val base = ArmorHindrance.getBase(ArmorCategory.reconnaissance, ArmorLevel.advanced, 6000)
		val commands = setOf("recon_move_mitigate_3", "recon_accuracy_mitigate_3", "recon_firerate_mitigate_3")
		val expected = Hindrance(0.0, 0.0, 0.0)
		val actual = ArmorHindrance.getMitigated(ArmorCategory.reconnaissance, base, commands)

		assertEquals(expected, actual)
	}
}
