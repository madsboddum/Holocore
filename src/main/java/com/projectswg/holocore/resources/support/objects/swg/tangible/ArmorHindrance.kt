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

data class ArmorLevel(val id: String, val minProtection: Int, val maxProtection: Int) {

	companion object {
		val basic = ArmorLevel("basic", 200, 2000)
		val standard = ArmorLevel("standard", 300, 4000)
		val advanced = ArmorLevel("advanced", 400, 6000)

		fun getById(id: String?): ArmorLevel? {
			return when (id) {
				basic.id -> basic
				standard.id -> standard
				advanced.id -> advanced
				else -> null
			}
		}
	}
}

/** Percentages rounded to one decimal, e.g. 37.6 means a 37.6% penalty. */
data class Hindrance(val movement: Double, val accuracy: Double, val rateOfFire: Double)

object ArmorHindrance {

	private class Mitigation(val commandPrefix: String, val movement: IntArray, val accuracy: IntArray, val rateOfFire: IntArray)

	private val basePenalties = mapOf(
		ArmorCategory.reconnaissance to mapOf(
			ArmorLevel.basic to Hindrance(10.0, 20.0, 30.0),
			ArmorLevel.standard to Hindrance(10.0, 30.0, 40.0),
			ArmorLevel.advanced to Hindrance(20.0, 40.0, 60.0),
		),
		ArmorCategory.battle to mapOf(
			ArmorLevel.basic to Hindrance(20.0, 20.0, 20.0),
			ArmorLevel.standard to Hindrance(30.0, 30.0, 30.0),
			ArmorLevel.advanced to Hindrance(40.0, 40.0, 40.0),
		),
		ArmorCategory.assault to mapOf(
			ArmorLevel.basic to Hindrance(30.0, 20.0, 10.0),
			ArmorLevel.standard to Hindrance(40.0, 30.0, 10.0),
			ArmorLevel.advanced to Hindrance(60.0, 40.0, 20.0),
		),
	)

	// Points subtracted by mitigation rank 1-3. 100 stands for "no penalty left"
	private val mitigations = mapOf(
		ArmorCategory.reconnaissance to Mitigation("recon", intArrayOf(10, 20, 100), intArrayOf(20, 30, 40), intArrayOf(30, 40, 60)),
		ArmorCategory.battle to Mitigation("battle", intArrayOf(20, 30, 40), intArrayOf(20, 30, 40), intArrayOf(20, 30, 40)),
		ArmorCategory.assault to Mitigation("assault", intArrayOf(30, 40, 60), intArrayOf(20, 30, 40), intArrayOf(10, 20, 100)),
	)

	/** @return the piece's own penalties, which grow with its general protection within the range of its [level] */
	fun getBase(category: ArmorCategory, level: ArmorLevel, generalProtection: Int): Hindrance {
		val penalties = basePenalties.getValue(category).getValue(level)
		val range = level.maxProtection - level.minProtection
		val share = ((generalProtection - level.minProtection).toDouble() / range).coerceIn(0.0, 1.0)
		return Hindrance(roundToOneDecimal(penalties.movement * share), roundToOneDecimal(penalties.accuracy * share), roundToOneDecimal(penalties.rateOfFire * share))
	}

	/** @return the penalties left after the certifications that [commands] grant for [category] */
	fun getMitigated(category: ArmorCategory, base: Hindrance, commands: Set<String>): Hindrance {
		val mitigation = mitigations[category] ?: return base
		return Hindrance(
			reduce(commands, mitigation, "move", mitigation.movement, base.movement),
			reduce(commands, mitigation, "accuracy", mitigation.accuracy, base.accuracy),
			reduce(commands, mitigation, "firerate", mitigation.rateOfFire, base.rateOfFire)
		)
	}

	private fun reduce(commands: Set<String>, mitigation: Mitigation, penaltyName: String, reductions: IntArray, penalty: Double): Double {
		for (rank in 3 downTo 1) {
			if ("${mitigation.commandPrefix}_${penaltyName}_mitigate_$rank" in commands)
				return roundToOneDecimal(penalty - reductions[rank - 1]).coerceAtLeast(0.0)
		}
		return roundToOneDecimal(penalty)
	}

	/**
	 * Rounds to the one decimal the examine window shows, halves up, so combat uses the value the player sees.
	 *
	 * - 19.414 -> 19.4
	 * - 19.45 -> 19.5
	 * - 11.899999999999999 (51.9 - 40) -> 11.9
	 */
	private fun roundToOneDecimal(penalty: Double): Double {
		return Math.round(penalty * 10) / 10.0
	}
}
