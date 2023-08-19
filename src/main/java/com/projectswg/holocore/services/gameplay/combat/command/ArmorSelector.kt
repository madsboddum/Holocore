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

import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import com.projectswg.holocore.resources.support.objects.swg.tangible.TangibleObject


object ArmorSelector {
	fun select(creatureObject: CreatureObject): Armor {
		return if (isWearingPhysicalArmor(creatureObject)) PhysicalArmor
		else if (hasInnateJediArmor(creatureObject)) InnateJediArmor
		else if (hasInnateTerasKasiArmor(creatureObject)) InnateTerasKasiArmor
		else NoArmor
	}

	private fun isWearingPhysicalArmor(target: CreatureObject): Boolean {
		val slottedObjects = target.slottedObjects
		for (slottedObject in slottedObjects) {
			if (slottedObject is TangibleObject) {
				if (slottedObject.protection != null) {
					return true
				}
			}
		}
		return false
	}

	private fun hasInnateJediArmor(target: CreatureObject): Boolean {
		return target.getSkillModValue(jediArmorSkillMod) > 0
	}

	private fun hasInnateTerasKasiArmor(target: CreatureObject): Boolean {
		return target.getSkillModValue(tkaArmorSkillMod) > 0
	}
}
