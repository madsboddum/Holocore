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

package com.projectswg.holocore.resources.support.objects.radial.terminal;

import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.common.data.radial.RadialItem;
import com.projectswg.common.data.radial.RadialOption;
import com.projectswg.common.data.sui.SuiEvent;
import com.projectswg.holocore.intents.gameplay.player.experience.GrantSkillIntent;
import com.projectswg.holocore.intents.support.objects.CreateStaticItemIntent;
import com.projectswg.holocore.intents.support.objects.ObjectCreatedIntent;
import com.projectswg.holocore.resources.support.global.player.Player;
import com.projectswg.holocore.resources.support.global.zone.sui.SuiButtons;
import com.projectswg.holocore.resources.support.global.zone.sui.SuiListBox;
import com.projectswg.holocore.resources.support.objects.ObjectCreator;
import com.projectswg.holocore.resources.support.objects.radial.RadialHandlerInterface;
import com.projectswg.holocore.resources.support.objects.swg.SWGObject;
import com.projectswg.holocore.resources.support.objects.swg.building.BuildingObject;
import com.projectswg.holocore.resources.support.objects.swg.cell.CellObject;
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject;
import com.projectswg.holocore.services.support.objects.ObjectStorageService;
import com.projectswg.holocore.services.support.objects.items.StaticItemService;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class TerminalCharacterBuilderRadial implements RadialHandlerInterface {

	public TerminalCharacterBuilderRadial() {

	}

	@Override
	public void getOptions(@NotNull Collection<RadialOption> options, @NotNull Player player, @NotNull SWGObject target) {
		options.add(RadialOption.create(RadialItem.ITEM_USE));
		options.add(RadialOption.createSilent(RadialItem.EXAMINE));
	}

	@Override
	public void handleSelection(@NotNull Player player, @NotNull SWGObject target, @NotNull RadialItem selection) {
		if (selection == RadialItem.ITEM_USE) {
			SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a category");

			listBox.addListItem("TRAVEL - Fast Travel Locations");
			listBox.addListItem("SKILLS - Grant skillboxes");
			listBox.addListItem("SKILLS - Unlock Force Sensitive");
			listBox.addListItem("ITEMS - Armor");
			listBox.addListItem("ITEMS - Weapons");
			listBox.addListItem("ITEMS - Wearables");
			listBox.addListItem("ITEMS - Vehicles");
			listBox.addListItem("ITEMS - Tools");
			listBox.addListItem("Credits");

			listBox.addCallback(SuiEvent.OK_PRESSED, "handleCategorySelection", (event, parameters) -> handleCategorySelection(player, parameters));
			listBox.display(player);
		}
	}

	private static void handleCategorySelection(Player player, Map<String, String> parameters) {
		int selection = SuiListBox.getSelectedRow(parameters);

		switch (selection) {
			case 0: handleTravel(player); break;
			case 1: handleSkillsGrantBoxes(player); break;
			case 2: handleSkillsUnlockForceSensitive(player); break;
			case 3: handleArmor(player); break;
			case 4: handleWeapons(player); break;
			case 5: handleWearables(player); break;
			case 6: handleVehicles(player); break;
			case 7: handleTools(player); break;
			case 8: handleCredits(player); break;
		}
	}

	private static void handleSkillsGrantBoxes(Player player) {
		SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a skill you want to learn");

		listBox.addListItem("Social - Entertainer (Master)");
		listBox.addListItem("Social - Dancer (Novice)");
		listBox.addListItem("Melee - Brawler (Master)");
		listBox.addListItem("Melee - Fencer (Novice)");
		listBox.addListItem("Melee - Pikeman (Novice)");
		listBox.addListItem("Melee - Swordsman (Novice)");
		listBox.addListItem("Melee - Teras Kasi (Novice)");
		listBox.addListItem("Ranged - Marksman (Master)");
		listBox.addListItem("Ranged - Pistoleer (Novice)");
		listBox.addListItem("Ranged - Carbineer (Novice)");
		listBox.addListItem("Ranged - Rifleman (Novice)");
		listBox.addListItem("Ranged - Commando (Novice)");
		listBox.addListItem("Science - Medic (Master)");
		listBox.addListItem("Science - Combat Medic (Master)");
		listBox.addListItem("Science - Doctor (Master)");
		listBox.addListItem("Force Sensitive - Dark Jedi ranks (Ranks)");
		listBox.addListItem("Force Sensitive - Light Jedi ranks (Ranks)");
		listBox.addListItem("Force Sensitive - Master Force Defender (Master)");
		listBox.addListItem("Force Sensitive - Master Force Enhancer (Master)");
		listBox.addListItem("Force Sensitive - Master Force Healing (Master)");
		listBox.addListItem("Force Sensitive - Lightsaber Master (Master)");
		listBox.addListItem("Force Sensitive - Master Force Wielder (Master)");
		listBox.addListItem("Force Sensitive - Combat Prowess Master (Master)");
		listBox.addListItem("Force Sensitive - Crafting Mastery (Master)");
		listBox.addListItem("Force Sensitive - Enhanced Reflexes Master (Master)");
		listBox.addListItem("Force Sensitive - Heightened Senses Master (Master)");

		listBox.addCallback(SuiEvent.OK_PRESSED, "handleSkillsSelection", (event, parameters) -> handleSkillsSelection(player, parameters));
		listBox.display(player);
	}

	private static void handleSkillsSelection(Player player, Map<String, String> parameters) {
		int selection = SuiListBox.getSelectedRow(parameters);

		switch (selection) {
			case 0: handleMasterEntertainer(player); break;
			case 1: handleNoviceDancer(player); break;
			case 2: handleMasterBrawler(player); break;
			case 3: handleNoviceFencer(player); break;
			case 4: handleNovicePikeman(player); break;
			case 5: handleNoviceSwordsman(player); break;
			case 6: handleNoviceTerasKasi(player); break;
			case 7: handleMasterMarksman(player); break;
			case 8: handleNovicePistoleer(player); break;
			case 9: handleNoviceCarbineer(player); break;
			case 10: handleNoviceRifleman(player); break;
			case 11: handleNoviceCommando(player); break;
			case 12: handleMasterMedic(player); break;
			case 13: handleMasterCombatMedic(player); break;
			case 14: handleMasterDoctor(player); break;
			case 15: handleJedi_1(player); break;
			case 16: handleJedi_2(player); break;
			case 17: handleJedi_3(player); break;
			case 18: handleJedi_4(player); break;
			case 19: handleJedi_5(player); break;
			case 20: handleJedi_6(player); break;
			case 21: handleJedi_7(player); break;
			case 22: handleJedi_8(player); break;
			case 23: handleJedi_9(player); break;
			case 24: handleJedi_10(player); break;
			case 25: handleJedi_11(player); break;
		}
	}

	private static void handleMasterEntertainer(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "social_entertainer_master", creatureObject, true).broadcast();
	}

	private static void handleNoviceDancer(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "social_dancer_novice", creatureObject, true).broadcast();
	}

	private static void handleMasterBrawler(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_brawler_master", creatureObject, true).broadcast();
	}

	private static void handleNoviceFencer(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_1hsword_novice", creatureObject, true).broadcast();
	}

	private static void handleNovicePikeman(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_polearm_novice", creatureObject, true).broadcast();
	}

	private static void handleNoviceSwordsman(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_2hsword_novice", creatureObject, true).broadcast();
	}

	private static void handleNoviceTerasKasi(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_unarmed_novice", creatureObject, true).broadcast();
	}

	private static void handleMasterMarksman(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_marksman_master", creatureObject, true).broadcast();
	}

	private static void handleNovicePistoleer(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_pistol_novice", creatureObject, true).broadcast();
	}

	private static void handleNoviceCarbineer(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_carbine_novice", creatureObject, true).broadcast();
	}

	private static void handleNoviceRifleman(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_rifleman_novice", creatureObject, true).broadcast();
	}

	private static void handleNoviceCommando(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "combat_commando_novice", creatureObject, true).broadcast();
	}

	private static void handleMasterMedic(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "science_medic_master", creatureObject, true).broadcast();
	}

	private static void handleMasterCombatMedic(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "science_combatmedic_master", creatureObject, true).broadcast();
	}

	private static void handleMasterDoctor(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "science_doctor_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_1(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_rank_dark_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_2(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_rank_light_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_3(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_defender_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_defender_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_4(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_enhancements_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_enhancements_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_5(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_healing_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_healing_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_6(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_light_saber_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_light_saber_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_7(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_powers_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_discipline_powers_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_8(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_combat_prowess_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_combat_prowess_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_9(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_crafting_mastery_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_crafting_mastery_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_10(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_enhanced_reflexes_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_enhanced_reflexes_master", creatureObject, true).broadcast();
	}

	private static void handleJedi_11(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_heightened_senses_novice", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_sensitive_heightened_senses_master", creatureObject, true).broadcast();
	}

	private static void handleSkillsUnlockForceSensitive(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_title_jedi_rank", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_title_jedi_rank_01", creatureObject, true).broadcast();
		new GrantSkillIntent(GrantSkillIntent.IntentType.GRANT, "force_title_jedi_rank_02", creatureObject, true).broadcast();
		spawnItems(player,
				"item_color_crystal_02_28",
				"item_power_crystal_04_19",
				"item_power_crystal_04_19",
				"item_power_crystal_04_19",
				"item_power_crystal_04_19",
				"item_color_crystal_02_00",
				"item_color_crystal_02_01",
				"item_color_crystal_02_02",
				"item_color_crystal_02_03",
				"item_color_crystal_02_04",
				"item_color_crystal_02_05",
				"item_color_crystal_02_06",
				"item_color_crystal_02_07",
				"item_color_crystal_02_08",
				"item_color_crystal_02_09",
				"item_color_crystal_02_10",
				"item_color_crystal_02_11",
				"weapon_cl30_1h_ls",
				"weapon_cl40_1h_ls",
				"weapon_cl50_1h_ls",
				"weapon_cl60_1h_ls",
				"weapon_cl70_1h_ls",
				"weapon_cl80_1h_ls",
				"weapon_cl30_2h_ls",
				"weapon_cl40_2h_ls",
				"weapon_cl50_2h_ls",
				"weapon_cl60_2h_ls",
				"weapon_cl70_2h_ls",
				"weapon_cl80_2h_ls",
				"weapon_cl30_polearm_ls",
				"weapon_cl40_polearm_ls",
				"weapon_cl50_polearm_ls",
				"weapon_cl60_polearm_ls",
				"weapon_cl70_polearm_ls",
				"weapon_cl80_polearm_ls"
		);
	}

	private static void spawnItems(Player player, String ... items) {
		CreatureObject creature = player.getCreatureObject();
		SWGObject inventory = creature.getSlottedObject("inventory");

		new CreateStaticItemIntent(creature, inventory, new StaticItemService.SystemMessageHandler(creature), items).broadcast();
	}

	private static void handleCredits(Player player) {
		CreatureObject creatureObject = player.getCreatureObject();
		int oneMillion = 1_000_000;
		creatureObject.setCashBalance(oneMillion);
		creatureObject.setBankBalance(oneMillion);
	}

	private static void handleArmor(Player player) {
		SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a set of armor to receive.");

		listBox.addListItem("Assault Armor - Basic");
		listBox.addListItem("Assault Armor - Standard");
		listBox.addListItem("Assault Armor - Advanced");
		listBox.addListItem("Battle Armor - Basic");
		listBox.addListItem("Battle Armor - Standard");
		listBox.addListItem("Battle Armor - Advanced");
		listBox.addListItem("Recon Armor - Basic");
		listBox.addListItem("Recon Armor - Standard");
		listBox.addListItem("Recon Armor - Advanced");
		listBox.addListItem("Special Armor - RIS");
		listBox.addListItem("Special Armor - Mandalorian");
		listBox.addListItem("Special Armor - Ithorian");
		listBox.addListItem("Special Armor - Wookiee");
		listBox.addCallback(SuiEvent.OK_PRESSED, "handleArmorSelection", (event, parameters) -> handleArmorSelection(player, parameters));
		listBox.display(player);
	}

	private static void handleArmorSelection(Player player, Map<String, String> parameters) {
		switch (SuiListBox.getSelectedRow(parameters)) {
			case 0: handleAssaultArmorBasic(player); break;
			case 1: handleAssaultArmorStandard(player); break;
			case 2: handleAssaultArmorAdvanced(player); break;
			case 3: handleBattleArmorBasic(player); break;
			case 4: handleBattleArmorStandard(player); break;
			case 5: handleBattleArmorAdvanced(player); break;
			case 6: handleReconArmorBasic(player); break;
			case 7: handleReconArmorStandard(player); break;
			case 8: handleReconArmorAdvanced(player); break;
			case 9: handleRISArmor(player); break;
			case 10: handleMandalorianArmor(player); break;
			case 11: handleIthorianArmor(player); break;
			case 12: handleWookieeArmor(player); break;
		}
	}

	private static void handleAssaultArmorBasic(Player player) {
		spawnItems(player,
				"armor_assault_agi_lvl20_bicep_l_02_01",
				"armor_assault_agi_lvl20_bicep_r_02_01",
				"armor_assault_agi_lvl20_boots_02_01",
				"armor_assault_agi_lvl20_bracer_l_02_01",
				"armor_assault_agi_lvl20_bracer_r_02_01",
				"armor_assault_agi_lvl20_chest_02_01",
				"armor_assault_agi_lvl20_gloves_02_01",
				"armor_assault_agi_lvl20_helmet_02_01",
				"armor_assault_agi_lvl20_leggings_02_01"
		);
	}

	private static void handleAssaultArmorStandard(Player player) {
		spawnItems(player,
				"armor_assault_agi_lvl50_bicep_l_02_01",
				"armor_assault_agi_lvl50_bicep_r_02_01",
				"armor_assault_agi_lvl50_boots_02_01",
				"armor_assault_agi_lvl50_bracer_l_02_01",
				"armor_assault_agi_lvl50_bracer_r_02_01",
				"armor_assault_agi_lvl50_chest_02_01",
				"armor_assault_agi_lvl50_gloves_02_01",
				"armor_assault_agi_lvl50_helmet_02_01",
				"armor_assault_agi_lvl50_leggings_02_01"
		);
	}

	private static void handleAssaultArmorAdvanced(Player player) {
		spawnItems(player,
				"armor_assault_agi_lvl80_bicep_l_02_01",
				"armor_assault_agi_lvl80_bicep_r_02_01",
				"armor_assault_agi_lvl80_boots_02_01",
				"armor_assault_agi_lvl80_bracer_l_02_01",
				"armor_assault_agi_lvl80_bracer_r_02_01",
				"armor_assault_agi_lvl80_chest_02_01",
				"armor_assault_agi_lvl80_gloves_02_01",
				"armor_assault_agi_lvl80_helmet_02_01",
				"armor_assault_agi_lvl80_leggings_02_01"
		);
	}

	private static void handleBattleArmorBasic(Player player) {
		spawnItems(player,
				"armor_battle_agi_lvl20_bicep_l_02_01",
				"armor_battle_agi_lvl20_bicep_r_02_01",
				"armor_battle_agi_lvl20_boots_02_01",
				"armor_battle_agi_lvl20_bracer_l_02_01",
				"armor_battle_agi_lvl20_bracer_r_02_01",
				"armor_battle_agi_lvl20_chest_02_01",
				"armor_battle_agi_lvl20_gloves_02_01",
				"armor_battle_agi_lvl20_helmet_02_01",
				"armor_battle_agi_lvl20_leggings_02_01"
		);
	}

	private static void handleBattleArmorStandard(Player player) {
		spawnItems(player,
				"armor_battle_agi_lvl50_bicep_l_02_01",
				"armor_battle_agi_lvl50_bicep_r_02_01",
				"armor_battle_agi_lvl50_boots_02_01",
				"armor_battle_agi_lvl50_bracer_l_02_01",
				"armor_battle_agi_lvl50_bracer_r_02_01",
				"armor_battle_agi_lvl50_chest_02_01",
				"armor_battle_agi_lvl50_gloves_02_01",
				"armor_battle_agi_lvl50_helmet_02_01",
				"armor_battle_agi_lvl50_leggings_02_01"
		);
	}

	private static void handleBattleArmorAdvanced(Player player) {
		spawnItems(player,
				"armor_battle_agi_lvl80_bicep_l_02_01",
				"armor_battle_agi_lvl80_bicep_r_02_01",
				"armor_battle_agi_lvl80_boots_02_01",
				"armor_battle_agi_lvl80_bracer_l_02_01",
				"armor_battle_agi_lvl80_bracer_r_02_01",
				"armor_battle_agi_lvl80_chest_02_01",
				"armor_battle_agi_lvl80_gloves_02_01",
				"armor_battle_agi_lvl80_helmet_02_01",
				"armor_battle_agi_lvl80_leggings_02_01"
		);
	}

	private static void handleReconArmorBasic(Player player) {
		spawnItems(player,
				"armor_recon_agi_lvl20_bicep_l_02_01",
				"armor_recon_agi_lvl20_bicep_r_02_01",
				"armor_recon_agi_lvl20_boots_02_01",
				"armor_recon_agi_lvl20_bracer_l_02_01",
				"armor_recon_agi_lvl20_bracer_r_02_01",
				"armor_recon_agi_lvl20_chest_02_01",
				"armor_recon_agi_lvl20_gloves_02_01",
				"armor_recon_agi_lvl20_helmet_02_01",
				"armor_recon_agi_lvl20_leggings_02_01"
		);
	}

	private static void handleReconArmorStandard(Player player) {
		spawnItems(player,
				"armor_recon_agi_lvl50_bicep_l_02_01",
				"armor_recon_agi_lvl50_bicep_r_02_01",
				"armor_recon_agi_lvl50_boots_02_01",
				"armor_recon_agi_lvl50_bracer_l_02_01",
				"armor_recon_agi_lvl50_bracer_r_02_01",
				"armor_recon_agi_lvl50_chest_02_01",
				"armor_recon_agi_lvl50_gloves_02_01",
				"armor_recon_agi_lvl50_helmet_02_01",
				"armor_recon_agi_lvl50_leggings_02_01"
		);
	}

	private static void handleReconArmorAdvanced(Player player) {
		spawnItems(player,
				"armor_recon_agi_lvl80_bicep_l_02_01",
				"armor_recon_agi_lvl80_bicep_r_02_01",
				"armor_recon_agi_lvl80_boots_02_01",
				"armor_recon_agi_lvl80_bracer_l_02_01",
				"armor_recon_agi_lvl80_bracer_r_02_01",
				"armor_recon_agi_lvl80_chest_02_01",
				"armor_recon_agi_lvl80_gloves_02_01",
				"armor_recon_agi_lvl80_helmet_02_01",
				"armor_recon_agi_lvl80_leggings_02_01"
		);
	}


	private static void handleMandalorianArmor(Player player) {
		spawnItems(player,
				"armor_mandalorian_bicep_l",
				"armor_mandalorian_bicep_r",
				"armor_mandalorian_bracer_l",
				"armor_mandalorian_bracer_r",
				"armor_mandalorian_chest_plate",
				"armor_mandalorian_gloves",
				"armor_mandalorian_helmet",
				"armor_mandalorian_leggings",
				"armor_mandalorian_shoes"
		);
	}


	private static void handleRISArmor(Player player) {
		spawnItems(player,
				"armor_ris_bicep_l",
				"armor_ris_bicep_r",
				"armor_ris_boots",
				"armor_ris_bracer_l",
				"armor_ris_bracer_r",
				"armor_ris_chest_plate",
				"armor_ris_gloves",
				"armor_ris_helmet",
				"armor_ris_leggings"
		);
	}

	private static void handleIthorianArmor(Player player) {
		spawnItems(player,
				"armor_ithorian_recon_bicep_l",
				"armor_ithorian_recon_bicep_r",
				"armor_ithorian_recon_boots",
				"armor_ithorian_recon_bracer_l",
				"armor_ithorian_recon_bracer_r",
				"armor_ithorian_recon_chest",
				"armor_ithorian_recon_gloves",
				"armor_ithorian_recon_helmet",
				"armor_ithorian_recon_leggings",
				"armor_ithorian_battle_bicep_l",
				"armor_ithorian_battle_bicep_r",
				"armor_ithorian_battle_boots",
				"armor_ithorian_battle_bracer_l",
				"armor_ithorian_battle_bracer_r",
				"armor_ithorian_battle_chest",
				"armor_ithorian_battle_leggings",
				"armor_ithorian_battle_gloves",
				"armor_ithorian_battle_helmet",
				"armor_ithorian_assault_bicep_l",
				"armor_ithorian_assault_bicep_r",
				"armor_ithorian_assault_boots",
				"armor_ithorian_assault_bracer_l",
				"armor_ithorian_assault_bracer_r",
				"armor_ithorian_assault_chest",
				"armor_ithorian_assault_gloves",
				"armor_ithorian_assault_helmet",
				"armor_ithorian_assault_leggings"
		);
	}

	private static void handleWookieeArmor(Player player) {
		spawnItems(player,
				"armor_wookiee_recon_bicep_l",
				"armor_wookiee_recon_bicep_r",
				"armor_wookiee_recon_bracer_l",
				"armor_wookiee_recon_bracer_r",
				"armor_wookiee_recon_chest",
				"armor_wookiee_recon_leggings",
				"armor_wookiee_battle_bicep_l",
				"armor_wookiee_battle_bicep_r",
				"armor_wookiee_battle_bracer_l",
				"armor_wookiee_battle_bracer_r",
				"armor_wookiee_battle_chest",
				"armor_wookiee_battle_leggings",
				"armor_wookiee_assault_bicep_l",
				"armor_wookiee_assault_bicep_r",
				"armor_wookiee_assault_bracer_l",
				"armor_wookiee_assault_bracer_r",
				"armor_wookiee_assault_chest",
				"armor_wookiee_assault_leggings"
		);
	}

	private static void handleWeapons(Player player) {
		SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a weapon category to receive a weapon of that type.");

		listBox.addListItem("CL  1 - Melee/Ranged");
		listBox.addListItem("CL 10 - Melee/Ranged");
		listBox.addListItem("CL 20 - Melee/Ranged");
		listBox.addListItem("CL 30 - Melee/Ranged");
		listBox.addListItem("CL 40 - Melee/Ranged");
		listBox.addListItem("CL 50 - Melee/Ranged");

		listBox.addCallback(SuiEvent.OK_PRESSED, "handleWeaponSelection", (event, parameters) -> handleWeaponSelection(player, parameters));
		listBox.display(player);
	}

	private static void handleWeaponSelection(Player player, Map<String, String> parameters) {
		int selection = SuiListBox.getSelectedRow(parameters);

		switch (selection) {
			case 0: handlecl1(player); break;
			case 1: handlecl10(player); break;
			case 2: handlecl20(player); break;
			case 3: handlecl30(player); break;
			case 4: handlecl40(player); break;
			case 5: handlecl50(player); break;
		}
	}


	private static void handlecl1(Player player) {
		spawnItems(player,
				"weapon_cl1_1h",
				"weapon_cl1_2h",
				"weapon_cl1_carbine",
				"weapon_cl1_heavy",
				"weapon_cl1_pistol",
				"weapon_cl1_polearm",
				"weapon_cl1_rifle",
				"weapon_cl1_unarmed"
		);
	}

	private static void handlecl10(Player player) {
		spawnItems(player,
				"weapon_cl10_1h",
				"weapon_cl10_2h",
				"weapon_cl10_carbine",
				"weapon_cl10_heavy",
				"weapon_cl10_pistol",
				"weapon_cl10_polearm",
				"weapon_cl10_rifle",
				"weapon_cl10_unarmed"
		);
	}

	private static void handlecl20(Player player) {
		spawnItems(player,
				"weapon_cl20_1h",
				"weapon_cl20_2h",
				"weapon_cl20_carbine",
				"weapon_cl20_heavy",
				"weapon_cl20_pistol",
				"weapon_cl20_polearm",
				"weapon_cl20_rifle",
				"weapon_cl20_unarmed"
		);
	}

	private static void handlecl30(Player player) {
		spawnItems(player,
				"weapon_cl30_1h",
				"weapon_cl30_2h",
				"weapon_cl30_carbine",
				"weapon_cl30_heavy",
				"weapon_cl30_pistol",
				"weapon_cl30_polearm",
				"weapon_cl30_rifle",
				"weapon_cl30_unarmed"
		);
	}

	private static void handlecl40(Player player) {
		spawnItems(player,
				"weapon_cl40_1h",
				"weapon_cl40_2h",
				"weapon_cl40_carbine",
				"weapon_cl40_heavy",
				"weapon_cl40_pistol",
				"weapon_cl40_polearm",
				"weapon_cl40_rifle",
				"weapon_cl40_unarmed"
		);
	}

	private static void handlecl50(Player player) {
		spawnItems(player,
				"weapon_cl50_1h",
				"weapon_cl50_2h",
				"weapon_cl50_carbine",
				"weapon_cl50_heavy",
				"weapon_cl50_pistol",
				"weapon_cl50_polearm",
				"weapon_cl50_rifle",
				"weapon_cl50_unarmed"
		);
	}

	private static void handleWearables(Player player) {
		SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a wearable category to receive a weapon of that type.");

		listBox.addListItem("Backpacks");
		listBox.addListItem("Bikinis");
		listBox.addListItem("Bodysuits");
		listBox.addListItem("Boots");
		listBox.addListItem("Bustiers");
		listBox.addListItem("Dress");
		listBox.addListItem("Gloves");
		listBox.addListItem("Goggles");
		listBox.addListItem("Hats");
		listBox.addListItem("Helmets");
		listBox.addListItem("Jackets");
		listBox.addListItem("Pants");
		listBox.addListItem("Robes");
		listBox.addListItem("Shirt");
		listBox.addListItem("Shoes");
		listBox.addListItem("Skirts");
		listBox.addListItem("Vest");
		listBox.addListItem("Ithorian equipment");
		listBox.addListItem("Nightsister equipment");
		listBox.addListItem("Tusken Raider equipment");
		listBox.addListItem("Wookie equipment");

		listBox.addCallback(SuiEvent.OK_PRESSED, "handleWearablesSelection", (event, parameters) -> handleWearablesSelection(player, parameters));
		listBox.display(player);
	}

	private static void handleWearablesSelection(Player player, Map<String, String> parameters) {
		int selection = SuiListBox.getSelectedRow(parameters);

		switch (selection) {
			case 0: handleBackpack(player); break;
			case 1: handleBikini(player); break;
			case 2: handleBodysuit(player); break;
			case 3: handleBoot(player); break;
			case 4: handleBustier(player); break;
			case 5: handleDress(player); break;
			case 6: handleGlove(player); break;
			case 7: handleGoggle(player); break;
			case 8: handleHat(player); break;
			case 9: handleHelmet(player); break;
			case 10: handleJacket(player); break;
			case 11: handlePant(player); break;
			case 12: handleRobe(player); break;
			case 13: handleShirt(player); break;
			case 14: handleShoe(player); break;
			case 15: handleSkirt(player); break;
			case 16: handleVest(player); break;
			case 17: handleIthorianEquipment(player); break;
			case 18: handleNightsisterEquipment(player); break;
			case 19: handleTuskenEquipment(player); break;
			case 20: handleWookieeEquipment(player); break;
		}
	}

	private static void handleBackpack(Player player) {
		spawnItems(player,
				"item_clothing_backpack_agi_lvl1_02_01",
				"item_clothing_backpack_con_lvl1_02_01",
				"item_clothing_backpack_lck_lvl1_02_01",
				"item_clothing_backpack_pre_lvl1_02_01"
		);
	}

	private static void handleBikini(Player player) {
		spawnItems(player,
				"item_clothing_bikini_01_01",
				"item_clothing_bikini_01_02",
				"item_clothing_bikini_01_03",
				"item_clothing_bikini_01_04",
				"item_clothing_bikini_leggings_01_01"
		);
	}

	private static void handleBodysuit(Player player) {
		spawnItems(player,
				"item_clothing_bodysuit_at_at_01_01",
				"item_clothing_bodysuit_bwing_01_01",
				"item_clothing_bodysuit_tie_fighter_01_01",
				"item_clothing_bodysuit_trando_slaver_01_01"
		);
	}

	private static void handleBoot(Player player) {
		spawnItems(player,
				"item_clothing_boots_01_03",
				"item_clothing_boots_01_04",
				"item_clothing_boots_01_05",
				"item_clothing_boots_01_12",
				"item_clothing_boots_01_14",
				"item_clothing_boots_01_15",
				"item_clothing_boots_01_19",
				"item_clothing_boots_01_21",
				"item_clothing_boots_01_22"
		);
	}

	private static void handleBustier(Player player) {
		spawnItems(player,
				"item_clothing_bustier_01_01",
				"item_clothing_bustier_01_02",
				"item_clothing_bustier_01_03"
		);
	}

	private static void handleDress(Player player) {
		spawnItems(player,
				"item_clothing_dress_01_05",
				"item_clothing_dress_01_06",
				"item_clothing_dress_01_07",
				"item_clothing_dress_01_08",
				"item_clothing_dress_01_09",
				"item_clothing_dress_01_10",
				"item_clothing_dress_01_11",
				"item_clothing_dress_01_12",
				"item_clothing_dress_01_13",
				"item_clothing_dress_01_14",
				"item_clothing_dress_01_15",
				"item_clothing_dress_01_16",
				"item_clothing_dress_01_18",
				"item_clothing_dress_01_19",
				"item_clothing_dress_01_23",
				"item_clothing_dress_01_26",
				"item_clothing_dress_01_27",
				"item_clothing_dress_01_29",
				"item_clothing_dress_01_30",
				"item_clothing_dress_01_31",
				"item_clothing_dress_01_32",
				"item_clothing_dress_01_33",
				"item_clothing_dress_01_34",
				"item_clothing_dress_01_35"
		);
	}

	private static void handleGlove(Player player) {
		spawnItems(player,
				"item_clothing_gloves_01_02",
				"item_clothing_gloves_01_03",
				"item_clothing_gloves_01_06",
				"item_clothing_gloves_01_07",
				"item_clothing_gloves_01_10",
				"item_clothing_gloves_01_11",
				"item_clothing_gloves_01_12",
				"item_clothing_gloves_01_13",
				"item_clothing_gloves_01_14"
		);
	}

	private static void handleGoggle(Player player) {
		spawnItems(player,
				"item_clothing_goggles_goggles_01_01",
				"item_clothing_goggles_goggles_01_02",
				"item_clothing_goggles_goggles_01_03",
				"item_clothing_goggles_goggles_01_04",
				"item_clothing_goggles_goggles_01_05",
				"item_clothing_goggles_goggles_01_06"
		);
	}

	private static void handleHat(Player player) {
		spawnItems(player,
				"item_clothing_hat_chef_01_01",
				"item_clothing_hat_chef_01_02",
				"item_clothing_hat_imp_01_01",
				"item_clothing_hat_imp_01_02",
				"item_clothing_hat_rebel_trooper_01_01",
				"item_clothing_hat_01_02",
				"item_clothing_hat_01_04",
				"item_clothing_hat_01_10",
				"item_clothing_hat_01_12",
				"item_clothing_hat_01_13",
				"item_clothing_hat_01_14",
				"item_clothing_hat_twilek_01_01",
				"item_clothing_hat_twilek_01_02",
				"item_clothing_hat_twilek_01_03",
				"item_clothing_hat_twilek_01_04",
				"item_clothing_hat_twilek_01_05"
		);
	}

	private static void handleHelmet(Player player) {
		spawnItems(player,
				"item_clothing_helmet_at_at_01_01",
				"item_clothing_helmet_fighter_blacksun_01_01",
				"item_clothing_helmet_fighter_imperial_01_01",
				"item_clothing_helmet_fighter_privateer_01_01",
				"item_clothing_helmet_fighter_rebel_01_01",
				"item_clothing_helmet_tie_fighter_01_01"
		);
	}

	private static void handleJacket(Player player) {
		spawnItems(player,
				"item_clothing_jacket_01_02",
				"item_clothing_jacket_01_03",
				"item_clothing_jacket_01_04",
				"item_clothing_jacket_01_05",
				"item_clothing_jacket_01_06",
				"item_clothing_jacket_01_07",
				"item_clothing_jacket_01_08",
				"item_clothing_jacket_01_09",
				"item_clothing_jacket_01_10",
				"item_clothing_jacket_01_11",
				"item_clothing_jacket_01_12",
				"item_clothing_jacket_01_13",
				"item_clothing_jacket_01_14",
				"item_clothing_jacket_01_15",
				"item_clothing_jacket_01_16",
				"item_clothing_jacket_01_17",
				"item_clothing_jacket_01_18",
				"item_clothing_jacket_01_19",
				"item_clothing_jacket_01_20",
				"item_clothing_jacket_01_21",
				"item_clothing_jacket_01_22",
				"item_clothing_jacket_01_23",
				"item_clothing_jacket_01_24",
				"item_clothing_jacket_01_25",
				"item_clothing_jacket_01_26"
		);
	}

	private static void handlePant(Player player) {
		spawnItems(player,
				"item_clothing_pants_01_01",
				"item_clothing_pants_01_02",
				"item_clothing_pants_01_03",
				"item_clothing_pants_01_04",
				"item_clothing_pants_01_05",
				"item_clothing_pants_01_06",
				"item_clothing_pants_01_07",
				"item_clothing_pants_01_08",
				"item_clothing_pants_01_09",
				"item_clothing_pants_01_10",
				"item_clothing_pants_01_11",
				"item_clothing_pants_01_12",
				"item_clothing_pants_01_13",
				"item_clothing_pants_01_14",
				"item_clothing_pants_01_15",
				"item_clothing_pants_01_16",
				"item_clothing_pants_01_17",
				"item_clothing_pants_01_18",
				"item_clothing_pants_01_21",
				"item_clothing_pants_01_22",
				"item_clothing_pants_01_24",
				"item_clothing_pants_01_25",
				"item_clothing_pants_01_26",
				"item_clothing_pants_01_27",
				"item_clothing_pants_01_28",
				"item_clothing_pants_01_29",
				"item_clothing_pants_01_30",
				"item_clothing_pants_01_31",
				"item_clothing_pants_01_32",
				"item_clothing_pants_01_33"
		);
	}

	private static void handleRobe(Player player) {
		spawnItems(player,
				"item_clothing_robe_01_01",
				"item_clothing_robe_01_04",
				"item_clothing_robe_01_05",
				"item_clothing_robe_01_12",
				"item_clothing_robe_01_18",
				"item_clothing_robe_01_27",
				"item_clothing_robe_01_32",
				"item_clothing_robe_01_33"
		);
	}

	private static void handleShirt(Player player) {
		spawnItems(player,
				"item_clothing_shirt_01_03",
				"item_clothing_shirt_01_04",
				"item_clothing_shirt_01_05",
				"item_clothing_shirt_01_07",
				"item_clothing_shirt_01_08",
				"item_clothing_shirt_01_09",
				"item_clothing_shirt_01_10",
				"item_clothing_shirt_01_11",
				"item_clothing_shirt_01_12",
				"item_clothing_shirt_01_13",
				"item_clothing_shirt_01_14",
				"item_clothing_shirt_01_15",
				"item_clothing_shirt_01_16",
				"item_clothing_shirt_01_24",
				"item_clothing_shirt_01_26",
				"item_clothing_shirt_01_27",
				"item_clothing_shirt_01_28",
				"item_clothing_shirt_01_30",
				"item_clothing_shirt_01_32",
				"item_clothing_shirt_01_34",
				"item_clothing_shirt_01_38",
				"item_clothing_shirt_01_42"
		);
	}

	private static void handleShoe(Player player) {
		spawnItems(player,
				"item_clothing_shoes_01_01",
				"item_clothing_shoes_01_02",
				"item_clothing_shoes_01_03",
				"item_clothing_shoes_01_07",
				"item_clothing_shoes_01_08",
				"item_clothing_shoes_01_09"
		);
	}

	private static void handleSkirt(Player player) {
		spawnItems(player,
				"item_clothing_skirt_01_03",
				"item_clothing_skirt_01_04",
				"item_clothing_skirt_01_05",
				"item_clothing_skirt_01_06",
				"item_clothing_skirt_01_07",
				"item_clothing_skirt_01_08",
				"item_clothing_skirt_01_09",
				"item_clothing_skirt_01_10",
				"item_clothing_skirt_01_11",
				"item_clothing_skirt_01_12",
				"item_clothing_skirt_01_13",
				"item_clothing_skirt_01_14"
		);
	}

	private static void handleVest(Player player) {
		spawnItems(player,
				"item_clothing_vest_01_01",
				"item_clothing_vest_01_02",
				"item_clothing_vest_01_03",
				"item_clothing_vest_01_04",
				"item_clothing_vest_01_05",
				"item_clothing_vest_01_06",
				"item_clothing_vest_01_09",
				"item_clothing_vest_01_10",
				"item_clothing_vest_01_11",
				"item_clothing_vest_01_15"
		);
	}

	private static void handleIthorianEquipment(Player player) {
		spawnItems(player,
				"item_clothing_ithorian_hat_chef_01_01",
				"item_clothing_ithorian_hat_chef_01_02",
				"item_clothing_ithorian_bodysuit_01_01",
				"item_clothing_ithorian_bodysuit_01_02",
				"item_clothing_ithorian_bodysuit_01_03",
				"item_clothing_ithorian_bodysuit_01_04",
				"item_clothing_ithorian_bodysuit_01_05",
				"item_clothing_ithorian_bodysuit_01_06",
				"item_clothing_ithorian_dress_01_02",
				"item_clothing_ithorian_dress_01_03",
				"item_clothing_ithorian_gloves_01_01",
				"item_clothing_ithorian_gloves_01_02",
				"item_clothing_ithorian_hat_01_01",
				"item_clothing_ithorian_hat_01_02",
				"item_clothing_ithorian_hat_01_03",
				"item_clothing_ithorian_hat_01_04",
				"item_clothing_ithorian_pants_01_01",
				"item_clothing_ithorian_pants_01_02",
				"item_clothing_ithorian_pants_01_03",
				"item_clothing_ithorian_pants_01_04",
				"item_clothing_ithorian_pants_01_05",
				"item_clothing_ithorian_pants_01_06",
				"item_clothing_ithorian_pants_01_07",
				"item_clothing_ithorian_pants_01_08",
				"item_clothing_ithorian_pants_01_09",
				"item_clothing_ithorian_pants_01_10",
				"item_clothing_ithorian_pants_01_11",
				"item_clothing_ithorian_pants_01_12",
				"item_clothing_ithorian_pants_01_13",
				"item_clothing_ithorian_pants_01_14",
				"item_clothing_ithorian_pants_01_15",
				"item_clothing_ithorian_pants_01_16",
				"item_clothing_ithorian_pants_01_17",
				"item_clothing_ithorian_pants_01_18",
				"item_clothing_ithorian_pants_01_19",
				"item_clothing_ithorian_pants_01_20",
				"item_clothing_ithorian_pants_01_21",
				"item_clothing_ithorian_robe_01_02",
				"item_clothing_ithorian_robe_01_03",
				"item_clothing_ithorian_shirt_01_01",
				"item_clothing_ithorian_shirt_01_02",
				"item_clothing_ithorian_shirt_01_03",
				"item_clothing_ithorian_shirt_01_04",
				"item_clothing_ithorian_shirt_01_05",
				"item_clothing_ithorian_shirt_01_06",
				"item_clothing_ithorian_shirt_01_07",
				"item_clothing_ithorian_shirt_01_08",
				"item_clothing_ithorian_shirt_01_09",
				"item_clothing_ithorian_shirt_01_10",
				"item_clothing_ithorian_shirt_01_11",
				"item_clothing_ithorian_shirt_01_12",
				"item_clothing_ithorian_shirt_01_13",
				"item_clothing_ithorian_shirt_01_14",
				"item_clothing_ithorian_skirt_01_01",
				"item_clothing_ithorian_skirt_01_02",
				"item_clothing_ithorian_skirt_01_03",
				"item_clothing_ithorian_vest_01_01",
				"item_clothing_ithorian_vest_01_02"
		);
	}

	private static void handleNightsisterEquipment(Player player) {
		spawnItems(player,
				"item_clothing_boots_nightsister_01_01",
				"item_clothing_dress_nightsister_01_01",
				"item_clothing_hat_nightsister_01_01",
				"item_clothing_hat_nightsister_01_02",
				"item_clothing_hat_nightsister_01_03",
				"item_clothing_pants_nightsister_01_01",
				"item_clothing_pants_nightsister_01_02",
				"item_clothing_shirt_nightsister_01_01",
				"item_clothing_shirt_nightsister_01_02",
				"item_clothing_shirt_nightsister_01_03"
		);
	}

	private static void handleTuskenEquipment(Player player) {
		spawnItems(player,
				"item_clothing_bandolier_tusken_01_01",
				"item_clothing_bandolier_tusken_01_02",
				"item_clothing_bandolier_tusken_01_03",
				"item_clothing_boots_tusken_raider_01_01",
				"item_clothing_gloves_tusken_raider_01_01",
				"item_clothing_helmet_tusken_raider_01_01",
				"item_clothing_helmet_tusken_raider_01_02",
				"item_clothing_robe_tusken_raider_01_01",
				"item_clothing_robe_tusken_raider_01_02"
		);
	}

	private static void handleWookieeEquipment(Player player) {
		spawnItems(player,
				"item_clothing_wookiee_gloves_01_01",
				"item_clothing_wookiee_gloves_01_02",
				"item_clothing_wookiee_gloves_01_03",
				"item_clothing_wookiee_gloves_01_04",
				"item_clothing_wookiee_hat_01_01",
				"item_clothing_wookiee_hood_01_01",
				"item_clothing_wookiee_hood_01_02",
				"item_clothing_wookiee_hood_01_03",
				"item_clothing_wookiee_lifeday_robe_01_01",
				"item_clothing_wookiee_lifeday_robe_01_02",
				"item_clothing_wookiee_lifeday_robe_01_03",
				"item_clothing_wookiee_shirt_01_01",
				"item_clothing_wookiee_shirt_01_02",
				"item_clothing_wookiee_shirt_01_03",
				"item_clothing_wookiee_shirt_01_04",
				"item_clothing_wookiee_shoulder_pad_01_01",
				"item_clothing_wookiee_shoulder_pad_01_02",
				"item_clothing_wookiee_skirt_01_01",
				"item_clothing_wookiee_skirt_01_02",
				"item_clothing_wookiee_skirt_01_03",
				"item_clothing_wookiee_skirt_01_04"
		);
	}

	private static void handleTravel(Player player) {
		SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a location you want to get teleported to.");

		listBox.addListItem("Corellia - Stronghold");
		listBox.addListItem("Coreliia - Corsec Base");
		listBox.addListItem("Dantooine - Force Crystal Hunter's Cave");
		listBox.addListItem("Dantooine - Jedi Temple Ruins");
		listBox.addListItem("Dantooine - The Warren");
		listBox.addListItem("Dathomir - Imperial Prison");
		listBox.addListItem("Dathomir - Nightsister Stronghold");
		listBox.addListItem("Dathomir - Nightsister vs. Singing Moutain Clan");
		listBox.addListItem("Endor - DWB");
		listBox.addListItem("Endor - Jinda Cave");
		listBox.addListItem("Kashyyyk - Etyyy, The Hunting Grounds");
		listBox.addListItem("Kashyyyk - Kachirho, Slaver Camp");
		listBox.addListItem("Kashyyyk - Kkowir, The Dead Forest");
		listBox.addListItem("Kashyyyk - Rryatt Trail, 1");
		listBox.addListItem("Kashyyyk - Rryatt Trail, 2");
		listBox.addListItem("Kashyyyk - Rryatt Trail, 3");
		listBox.addListItem("Kashyyyk - Rryatt Trail, 4");
		listBox.addListItem("Kashyyyk - Rryatt Trail, 5");
		listBox.addListItem("Kashyyyk - Slaver");
		listBox.addListItem("Lok - Droid Cave");
		listBox.addListItem("Lok - Great Maze of Lok");
		listBox.addListItem("Lok - Imperial Outpost");
		listBox.addListItem("Lok - Kimogila Town");
		listBox.addListItem("Mustafar - Mensix Mining Facility");
		listBox.addListItem("Naboo - Emperor's Retreat");
		listBox.addListItem("Rori - Hyperdrive Research Facility");
		listBox.addListItem("Talus - Detainment Center");
		listBox.addListItem("Tatooine - Fort Tusken");
		listBox.addListItem("Tatooine - Imperial Oasis");
		listBox.addListItem("Tatooine - Krayt Graveyard");
		listBox.addListItem("Tatooine - Mos Eisley");
		listBox.addListItem("Tatooine - Mos Taike");
		listBox.addListItem("Tatooine - Squill Cave");
		listBox.addListItem("Yavin 4 - Blueleaf Temple");
		listBox.addListItem("Yavin 4 - Dark Enclave");
		listBox.addListItem("Yavin 4 - Geonosian Cave");
		listBox.addListItem("Yavin 4 - Light Enclave");
		listBox.addListItem("[INSTANCE] - Myyyydril Cave");
		listBox.addListItem("[INSTANCE] - Avatar Platform (EASY)");
		listBox.addListItem("[INSTANCE] - Avatar Platform (MEDIUM)");
		listBox.addListItem("[INSTANCE] - Avatar Platform (HARD)");
		listBox.addListItem("[INSTANCE] - Mustafar Jedi Challenge (EASY)");
		listBox.addListItem("[INSTANCE] - Mustafar Jedi Challenge (MEDIUM)");
		listBox.addListItem("[INSTANCE] - Mustafar Jedi Challenge (HARD)");
		listBox.addListItem("[INVASION] - Droid Army");

		listBox.addCallback(SuiEvent.OK_PRESSED, "handleTravelSelection", (event, parameters) -> handleTravelSelection(player, parameters));
		listBox.display(player);
	}

	private static void handleTravelSelection(Player player, Map<String, String> parameters) {
		int selection = SuiListBox.getSelectedRow(parameters);

		switch (selection) {

			// Planet: Corellia
			case 0: handleCorStronghold(player); break;
			case 1: handleCorCorsecBase(player); break;
			// Planet: Dantooine
			case 2: handleDanCrystalCave(player); break;
			case 3: handleDanJediTemple(player); break;
			case 4: handleDanWarren(player); break;
			// Planet: Dathomir
			case 5: handleDatImperialPrison(player); break;
			case 6: handleDatNS(player); break;
			case 7: handleDatNSvsSMC(player); break;
			// Planet: Endor
			case 8: handleEndDwb(player); break;
			case 9: handleEndJindaCave(player); break;
			// Planet: Kashyyyk
			case 10: handleKasEtyyy(player); break;
			case 11: handleKasKachirho(player); break;
			case 12: handleKasKkowir(player); break;
			case 13: handleKasRryatt1(player); break;
			case 14: handleKasRryatt2(player); break;
			case 15: handleKasRryatt3(player); break;
			case 16: handleKasRryatt4(player); break;
			case 17: handleKasRryatt5(player); break;
			case 18: handleKasSlaver(player); break;
			// Planet: Lok
			case 19: handleLokDroidCave(player); break;
			case 20: handleLokGreatMaze(player); break;
			case 21: handleLokImperialOutpost(player); break;
			case 22: handleLokKimogilaTown(player); break;
			// Planet: Mustafar
			case 23: handleMusMensix(player); break;
			// Planet: Naboo
			case 24: handleNabEmperorsRetreat(player); break;
			// Planet: Rori
			case 25: handleRorHyperdriveFacility(player); break;
			// Planet: Talus
			case 26: handleTalDetainmentCenter(player); break;
			// Planet: Tatooine
			case 27: handleTatFortTusken(player); break;
			case 28: handleTatImperialOasis(player); break;
			case 29: handleTatKraytGrave(player); break;
			case 30: handleTatMosEisley(player); break;
			case 31: handleTatMosTaike(player); break;
			case 32: handleTatSquillCave(player); break;
			// Planet: Yavin 4
			case 33: handleYavBlueleafTemple(player); break;
			case 34: handleYavDarkEnclave(player); break;
			case 35: handleYavGeoCave(player); break;
			case 36: handleYavLightEnclave(player); break;
			// Dungeons:
			case 37: handleInstanceMyyydrilCave(player); break;
			case 38: handleInstanceAvatarPlatformEasy(player); break;
			case 39: handleInstanceAvatarPlatformMedium(player); break;
			case 40: handleInstanceAvatarPlatformHard(player); break;
			// Planet: Mustafar Jedi Challenge
			case 41: handleInstanceMusJediEasy(player); break;
			case 42: handleInstanceMusJediMedium(player); break;
			case 43: handleInstanceMusJediHard(player); break;
			// Invasion:
			case 44: handleInvasionMusDroidArmy(player); break;

		}
	}

// Planet: Corellia

	private static void handleCorStronghold(Player player) {
		teleportTo(player, 4735d, 26d, -5676d, Terrain.CORELLIA);
	}
	private static void handleCorCorsecBase(Player player) {
		teleportTo(player, 5137d, 16d, 1518d, Terrain.CORELLIA);
	}

// Planet: Dantooine

	private static void handleDanJediTemple(Player player) {
		teleportTo(player, 4078d, 10d, 5370d, Terrain.DANTOOINE);
	}
	private static void handleDanCrystalCave(Player player) {
		teleportTo(player, -6225d, 48d, 7381d, Terrain.DANTOOINE);
	}
	private static void handleDanWarren(Player player) {
		teleportTo(player, -564d, 1d, -3789d, Terrain.DANTOOINE);
	}

// Planet: Dathomir

	private static void handleDatImperialPrison(Player player) {teleportTo(player, -6079d, 132d, 971d, Terrain.DATHOMIR);}
	private static void handleDatNS(Player player) {
		teleportTo(player, -3989d, 124d, -10d, Terrain.DATHOMIR);
	}
	private static void handleDatNSvsSMC(Player player) {
		teleportTo(player, -2457d, 117d, 1530d, Terrain.DATHOMIR);
	}

// Planet: Endor

	private static void handleEndJindaCave(Player player) {
		teleportTo(player, -1714d, 31d, -8d, Terrain.ENDOR);
	}
	private static void handleEndDwb(Player player) {
		teleportTo(player, -4683d, 13d, 4326d, Terrain.ENDOR);
	}

// Planet: Kashyyyk

	private static void handleKasEtyyy(Player player) {
		teleportTo(player, 275d, 48d, 503d, Terrain.KASHYYYK_HUNTING);
	}
	private static void handleKasKachirho(Player player) {
		teleportTo(player, 146d, 19d, 162d, Terrain.KASHYYYK_MAIN);
	}
	private static void handleKasKkowir(Player player) {teleportTo(player, -164d, 16d, -262d, Terrain.KASHYYYK_DEAD_FOREST);}
	private static void handleKasRryatt1(Player player) {teleportTo(player, 534d, 173d, 82d, Terrain.KASHYYYK_RRYATT_TRAIL);}
	private static void handleKasRryatt2(Player player) {teleportTo(player, 1422d, 70d, 722d, Terrain.KASHYYYK_RRYATT_TRAIL);}
	private static void handleKasRryatt3(Player player) {teleportTo(player, 2526d, 182d, -278d, Terrain.KASHYYYK_RRYATT_TRAIL);}
	private static void handleKasRryatt4(Player player) {teleportTo(player, 768d, 141d, -439d, Terrain.KASHYYYK_RRYATT_TRAIL);}
	private static void handleKasRryatt5(Player player) {teleportTo(player, 2495d, -24d, -924d, Terrain.KASHYYYK_RRYATT_TRAIL);}
	private static void handleKasSlaver(Player player) {teleportTo(player, 561.8d, 22.8d, 1552.8d, Terrain.KASHYYYK_NORTH_DUNGEONS);}

// Planet: Lok

	private static void handleLokDroidCave(Player player) {
		teleportTo(player, 3331d, 105d, -4912d, Terrain.LOK);
	}
	private static void handleLokGreatMaze(Player player) {
		teleportTo(player, 3848d, 62d, -464d, Terrain.LOK);
	}
	private static void handleLokImperialOutpost(Player player) {
		teleportTo(player, -1914d, 11d, -3299d, Terrain.LOK);
	}
	private static void handleLokKimogilaTown(Player player) {
		teleportTo(player, -70d, 42d, 2769d, Terrain.LOK);
	}

// Planet: Mustafar

	private static void handleMusMensix(Player player) {
		teleportTo(player, -2489d, 230d, 1621d, Terrain.MUSTAFAR);
	}

	// Planet: Naboo

	private static void handleNabEmperorsRetreat(Player player) {
		teleportTo(player, 2535d, 295d, -3887d, Terrain.NABOO);
	}

// Planet: Rori

	private static void handleRorHyperdriveFacility(Player player) {teleportTo(player, -1211d, 98d, 4552d, Terrain.RORI);}

// Planet: Talus

	private static void handleTalDetainmentCenter(Player player) {teleportTo(player, 4958d, 449d, -5983d, Terrain.TALUS);}

// Planet: Tatooine

	private static void handleTatFortTusken(Player player) {
		teleportTo(player, -3941d, 59d, 6318d, Terrain.TATOOINE);
	}
	private static void handleTatKraytGrave(Player player) {
		teleportTo(player, 7380d, 122d, 4298d, Terrain.TATOOINE);
	}
	private static void handleTatMosEisley(Player player) {
		teleportTo(player, 3525d, 4d, -4807d, Terrain.TATOOINE);
	}
	private static void handleTatMosTaike(Player player) {
		teleportTo(player, 3684d, 7d, 2357d, Terrain.TATOOINE);
	}
	private static void handleTatSquillCave(Player player) {
		teleportTo(player, 57d, 152d, -79d, Terrain.TATOOINE);
	}
	private static void handleTatImperialOasis(Player player) {
		teleportTo(player, -5458d, 10d, 2601d, Terrain.TATOOINE);
	}

// Planet: Yavin 4

	private static void handleYavBlueleafTemple(Player player) {
		teleportTo(player, -947d, 86d, -2131d, Terrain.YAVIN4);
	}
	private static void handleYavDarkEnclave(Player player) {
		teleportTo(player, 5107d, 81d, 301d, Terrain.YAVIN4);
	}
	private static void handleYavLightEnclave(Player player) {
		teleportTo(player, -5575d, 87d, 4902d, Terrain.YAVIN4);
	}
	private static void handleYavGeoCave(Player player) {teleportTo(player, -6485d, 83d, -446d, Terrain.YAVIN4); }

// Dungeons:

	private static void handleInstanceMyyydrilCave(Player player) {teleportTo(player, "kas_pob_myyydril_1", 1, -5.2, -1.3, -5.3);}
	private static void handleInstanceAvatarPlatformEasy(Player player) {teleportTo(player, "kas_pob_avatar_1",  1, 103.2, 0.1, 21.7);}
	private static void handleInstanceAvatarPlatformMedium(Player player) {teleportTo(player, "kas_pob_avatar_2",  1, 103.2, 0.1, 21.7);}
	private static void handleInstanceAvatarPlatformHard(Player player) {teleportTo(player, "kas_pob_avatar_3",  1, 103.2, 0.1, 21.7);}
	private static void handleInstanceMusJediEasy(Player player) {teleportTo(player, 2209.8d, 74.8d, 6410.2d, Terrain.MUSTAFAR);	}
	private static void handleInstanceMusJediMedium(Player player) {teleportTo(player, 2195.1d, 74.8d, 4990.40d, Terrain.MUSTAFAR);	}
	private static void handleInstanceMusJediHard(Player player) {teleportTo(player, 2190.5d, 74.8d, 3564.8d, Terrain.MUSTAFAR);	}
	private static void handleInvasionMusDroidArmy(Player player) {teleportTo(player, 4908d, 24d, 6046d, Terrain.MUSTAFAR);}


	private static void teleportTo(Player player, double x, double y, double z, Terrain terrain) {
		player.getCreatureObject().moveToContainer(null, new Location(x, y, z, terrain));
	}

	private static void teleportTo(Player player, String buildoutTag, int cellNumber, double x, double y, double z) {
		BuildingObject building = ObjectStorageService.BuildingLookup.getBuildingByTag(buildoutTag);
		assert building != null : "building does not exist";
		CellObject cell = building.getCellByNumber(cellNumber);
		assert cell != null : "cell does not exist";
		player.getCreatureObject().moveToContainer(cell, new Location(x, y, z, building.getTerrain()));
	}

	private static void teleportTo(Player player, String buildoutTag, String cellName, double x, double y, double z) {
		BuildingObject building = ObjectStorageService.BuildingLookup.getBuildingByTag(buildoutTag);
		assert building != null : "building does not exist";
		CellObject cell = building.getCellByName(cellName);
		assert cell != null : "cell does not exist";
		player.getCreatureObject().moveToContainer(cell, new Location(x, y, z, building.getTerrain()));
	}


	private static void handleVehicles(Player player) {
		String [] items = new String[]{
				"object/tangible/deed/vehicle_deed/shared_barc_speeder_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_ab1_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_av21_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_desert_skiff_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_lava_skiff_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_usv5_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_v35_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_speederbike_swoop_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_xp38_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_landspeeder_tantive4_deed.iff",
				"object/tangible/deed/vehicle_deed/shared_jetpack_deed.iff",
		};
		for (String item : items) {
			SWGObject deed = ObjectCreator.createObjectFromTemplate(item);
			deed.moveToContainer(player.getCreatureObject().getInventory());
			new ObjectCreatedIntent(deed).broadcast();
		}
	}

	private static void handleTools(Player player) {
		SuiListBox listBox = new SuiListBox(SuiButtons.OK_CANCEL, "Character Builder Terminal", "Select a tool to acquire.");

		listBox.addListItem("Gas Pocket Survey Device");
		listBox.addListItem("Chemical Survey Device");
		listBox.addListItem("Flora Survey Tool");
		listBox.addListItem("Mineral Survey Device");
		listBox.addListItem("Water Survey Device");
		listBox.addListItem("Wind Current Surveying Tool");

		listBox.addCallback(SuiEvent.OK_PRESSED, "handleToolsSelection", (event, parameters) -> handleToolsSelection(player, parameters));
		listBox.display(player);
	}

	private static void handleToolsSelection(Player player, Map<String, String> parameters) {
		int selection = SuiListBox.getSelectedRow(parameters);

		switch (selection) {
			case 0: handleGas(player); break;
			case 1: handleChemical(player); break;
			case 2: handleFlora(player); break;
			case 3: handleMineral(player); break;
			case 4: handleWater(player); break;
			case 5: handleWind(player); break;
		}
	}

	private static void handleGas(Player player) {
		spawnItems(player,
				"survey_tool_gas"
		);
	}

	private static void handleChemical(Player player) {
		spawnItems(player,
				"survey_tool_liquid"
		);
	}

	private static void handleFlora(Player player) {
		spawnItems(player,
				"survey_tool_lumber"
		);
	}

	private static void handleMineral(Player player) {
		spawnItems(player,
				"survey_tool_mineral"
		);
	}

	private static void handleWater(Player player) {
		spawnItems(player,
				"survey_tool_moisture"
		);
	}

	private static void handleWind(Player player) {
		spawnItems(player,
				"survey_tool_wind"
		);
	}

}