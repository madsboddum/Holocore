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
package com.projectswg.holocore.services.gameplay.combat.command;

import com.projectswg.common.data.combat.AttackInfo;
import com.projectswg.common.data.combat.DamageType;
import com.projectswg.holocore.resources.support.global.commands.CombatCommand;
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject;
import com.projectswg.holocore.resources.support.objects.swg.tangible.Protection;
import com.projectswg.holocore.resources.support.objects.swg.tangible.TangibleObject;

import java.util.Map;

class PhysicalArmor implements Armor {

	public void mitigateDamage(AttackInfo info, DamageType damageType, CreatureObject target, CombatCommand command) {
		// Armor mitigation
		int armor = getArmor(damageType, target);
		float armorReduction = getArmorReduction(armor, command);
		int currentDamage = info.getFinalDamage();
		int armorAbsorbed = (int) (currentDamage * armorReduction);
		currentDamage -= armorAbsorbed;

		info.setArmor(armor);    // Assumed to be the amount of armor points the defender has against the primary damage type
		info.setBlockedDamage(armorAbsorbed);    // Describes how many points of damage the armor absorbed

		info.setFinalDamage(currentDamage);
	}

	private static int getArmor(DamageType damageType, CreatureObject creature) {
		int armProtection = 7;
		Map<String, Integer> protectionMap = Map.of("chest2", 35, "pants1", 20, "hat", 14, "bracer_upper_l", armProtection, "bracer_upper_r", armProtection, "bicep_l", armProtection, "bicep_r", armProtection, "utility_belt", 3);

		double armor = 0;

		for (Map.Entry<String, Integer> entry : protectionMap.entrySet()) {
			String slot = entry.getKey();
			TangibleObject slottedObject = (TangibleObject) creature.getSlottedObject(slot);

			if (slottedObject != null) {
				Protection protection = slottedObject.getProtection();

				if (protection != null) {
					int protectionFromArmorPiece = switch (damageType) {
						case KINETIC -> protection.getKinetic();
						case ENERGY -> protection.getEnergy();
						case ELEMENTAL_HEAT -> protection.getHeat();
						case ELEMENTAL_COLD -> protection.getCold();
						case ELEMENTAL_ACID -> protection.getAcid();
						case ELEMENTAL_ELECTRICAL -> protection.getElectricity();
						default -> 0;
					};

					Integer value = entry.getValue();

					armor += protectionFromArmorPiece * (value / 100d);
				}
			}
		}

		double armorBreakPercent = ArmorBreak.getArmorBreakPercent(creature);

		if (armorBreakPercent > 0) {
			armor *= (1 - armorBreakPercent / 100d);
		}

		return (int) armor;
	}

	private static float getArmorReduction(int baseArmor, CombatCommand command) {
		double commandBypassArmor = command.getBypassArmor();

		if (commandBypassArmor > 0) {
			// This command bypasses armor
			baseArmor *= 1.0 - commandBypassArmor;
		}

		float mitigation = (float) (90 * (1 - Math.exp(-0.000125 * baseArmor))) + baseArmor / 9000f;

		return mitigation / 100;

	}
}
