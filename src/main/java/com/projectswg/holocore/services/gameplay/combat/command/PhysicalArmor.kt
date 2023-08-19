/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.holocore.services.gameplay.combat.command

import com.projectswg.common.data.combat.AttackInfo
import com.projectswg.common.data.combat.DamageType
import com.projectswg.holocore.resources.support.global.commands.CombatCommand
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import com.projectswg.holocore.resources.support.objects.swg.tangible.TangibleObject
import com.projectswg.holocore.services.gameplay.combat.command.ArmorBreak.getArmorBreakPercent
import kotlin.math.exp

internal object PhysicalArmor : Armor {
	override fun mitigateDamage(info: AttackInfo, damageType: DamageType, target: CreatureObject, command: CombatCommand) {
		// Armor mitigation
		val armor = getArmor(damageType, target)
		val armorReduction = getArmorReduction(armor, command)
		var currentDamage = info.finalDamage
		val armorAbsorbed = (currentDamage * armorReduction).toInt()
		currentDamage -= armorAbsorbed
		info.armor = armor.toLong() // Assumed to be the amount of armor points the defender has against the primary damage type
		info.blockedDamage = armorAbsorbed // Describes how many points of damage the armor absorbed
		info.finalDamage = currentDamage
	}

	private fun getArmor(damageType: DamageType, creature: CreatureObject): Int {
		val armProtection = 7
		val protectionMap = mapOf(
			"chest2" to 35,
			"pants1" to 20,
			"hat" to 14,
			"bracer_upper_l" to armProtection,
			"bracer_upper_r" to armProtection,
			"bicep_l" to armProtection,
			"bicep_r" to armProtection,
			"utility_belt" to 3,
		)
		var armor = 0.0
		for ((slot, value) in protectionMap) {
			val slottedObject = creature.getSlottedObject(slot) as TangibleObject?
			if (slottedObject != null) {
				val protection = slottedObject.protection
				if (protection != null) {
					val protectionFromArmorPiece = when (damageType) {
						DamageType.KINETIC              -> protection.kinetic
						DamageType.ENERGY               -> protection.energy
						DamageType.ELEMENTAL_HEAT       -> protection.heat
						DamageType.ELEMENTAL_COLD       -> protection.cold
						DamageType.ELEMENTAL_ACID       -> protection.acid
						DamageType.ELEMENTAL_ELECTRICAL -> protection.electricity
						else                            -> 0
					}
					armor += protectionFromArmorPiece * (value / 100.0)
				}
			}
		}
		val armorBreakPercent = getArmorBreakPercent(creature)
		if (armorBreakPercent > 0) {
			armor *= 1 - armorBreakPercent / 100.0
		}
		return armor.toInt()
	}

	private fun getArmorReduction(baseArmor: Int, command: CombatCommand): Float {
		var effectiveArmor = baseArmor
		val commandBypassArmor = command.bypassArmor
		if (commandBypassArmor > 0) {
			// This command bypasses armor
			effectiveArmor = (effectiveArmor * (1.0 - commandBypassArmor)).toInt()
		}
		val mitigation = (90 * (1 - exp(-0.000125 * effectiveArmor))).toFloat() + effectiveArmor / 9000f
		return mitigation / 100
	}
}
