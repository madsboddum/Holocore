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

import com.projectswg.common.network.packets.swg.login.creation.ClientCreateCharacter
import com.projectswg.holocore.intents.gameplay.player.experience.skills.GrantSkillIntent
import com.projectswg.holocore.resources.support.data.server_info.loader.DataLoader
import com.projectswg.holocore.resources.support.global.player.AccessLevel
import com.projectswg.holocore.resources.support.global.zone.creation.CharacterCreation
import com.projectswg.holocore.resources.support.objects.StaticItemCreator
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import com.projectswg.holocore.resources.support.objects.swg.tangible.TangibleObject
import com.projectswg.holocore.services.gameplay.player.experience.skills.SkillService
import com.projectswg.holocore.services.gameplay.player.experience.skills.skillmod.SkillModService
import com.projectswg.holocore.services.support.global.commands.CommandExecutionService
import com.projectswg.holocore.services.support.global.commands.CommandQueueService
import com.projectswg.holocore.test.resources.GenericPlayer
import com.projectswg.holocore.test.runners.TestRunnerSynchronousIntents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArmorSelectorTest : TestRunnerSynchronousIntents() {

	@BeforeEach
	fun setup() {
		registerService(CommandQueueService(5))
		registerService(CommandExecutionService())
		registerService(SkillService())
		registerService(SkillModService())
	}

	@Test
	fun noArmor() {
		val creatureObject = createCharacter()

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, NoArmor::class)
	}

	@Test
	fun physicalArmor() {
		val creatureObject = createCharacter()
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_polearm_master", creatureObject, true))
		val tangibleObject = StaticItemCreator.createItem("armor_tow_battle_stats_helmet_03_01") as TangibleObject?
		tangibleObject?.moveToContainer(creatureObject)

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, PhysicalArmor::class)
	}

	@Test
	fun terasKasiArmor() {
		val creatureObject = createCharacter()
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_unarmed_master", creatureObject, true))

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, InnateTerasKasiArmor::class)
	}

	@Test
	fun jediArmor() {
		val creatureObject = createCharacter()
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_title_jedi_rank_02", creatureObject, true))

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, InnateJediArmor::class)
	}

	@Test
	fun physicalArmorOverridesJediArmor() {
		val creatureObject = createCharacter()
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_polearm_novice", creatureObject, true))
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_title_jedi_rank_02", creatureObject, true))
		val tangibleObject = StaticItemCreator.createItem("armor_tow_battle_stats_helmet_03_01") as TangibleObject?
		tangibleObject?.moveToContainer(creatureObject)

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, PhysicalArmor::class)
	}

	@Test
	fun physicalArmorOverridesTerasKasiArmor() {
		val creatureObject = createCharacter()
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_polearm_novice", creatureObject, true))
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_unarmed_master", creatureObject, true))
		val tangibleObject = StaticItemCreator.createItem("armor_tow_battle_stats_helmet_03_01") as TangibleObject?
		tangibleObject?.moveToContainer(creatureObject)

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, PhysicalArmor::class)
	}

	@Test
	fun jediArmorOverridesTerasKasiArmor() {
		val creatureObject = createCharacter()
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_unarmed_master", creatureObject, true))
		broadcastAndWait(GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_title_jedi_rank_02", creatureObject, true))

		val armor = ArmorSelector.select(creatureObject)

		assertEquals(armor::class, InnateJediArmor::class)
	}

	private fun createCharacter(): CreatureObject {
		val player = GenericPlayer()
		val clientCreateCharacter = ClientCreateCharacter()
		clientCreateCharacter.biography = ""
		clientCreateCharacter.clothes = "combat_brawler"
		clientCreateCharacter.race = "object/creature/player/shared_human_male.iff"
		clientCreateCharacter.name = "Testing Character"
		val characterCreation = CharacterCreation(player, clientCreateCharacter)

		val mosEisley = DataLoader.zoneInsertions().getInsertion("tat_moseisley")
		val creatureObject = characterCreation.createCharacter(AccessLevel.PLAYER, mosEisley)
		creatureObject.owner = player

		return creatureObject
	}
}