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
package com.projectswg.holocore.resources.support.global.commands.callbacks.entertainer

import com.projectswg.common.data.sui.SuiEvent
import com.projectswg.holocore.resources.support.global.commands.ICmdCallback
import com.projectswg.holocore.resources.support.global.player.Player
import com.projectswg.holocore.resources.support.global.zone.sui.SuiButtons
import com.projectswg.holocore.resources.support.global.zone.sui.SuiListBox
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject

abstract class StartPerformanceCallback(private val abilityNamePrefix: String, private val listBoxText: ListBoxText) : ICmdCallback {
	override fun execute(player: Player, target: SWGObject?, args: String) {
		val creatureObject = player.creatureObject
		if (args.isEmpty()) {// TODO check if this is some kind of insanity flow we have implemented ourselves or if it's a client thing
			displayPerformanceSelectionWindow(creatureObject, player)
		} else {
			onSelectPerformance(player, args)
		}
	}

	private fun displayPerformanceSelectionWindow(creatureObject: CreatureObject, player: Player) {
		val listBox = SuiListBox(SuiButtons.OK_CANCEL, listBoxText.title, listBoxText.prompt)
		val performanceAbilityNames = creatureObject.commands.filter { it.startsWith(abilityNamePrefix) }

		for (performanceAbilityName in performanceAbilityNames) {
			val displayName = performanceAbilityName.replace(abilityNamePrefix, "")
			val firstCharacter = displayName.substring(0, 1)
			val otherCharacters = displayName.substring(1, displayName.length)

			listBox.addListItem(firstCharacter.uppercase() + otherCharacters)
		}

		listBox.addOkButtonCallback("handleSelectedItem") { event: SuiEvent?, parameters: Map<String?, String?>? ->
			val selection = SuiListBox.getSelectedRow(parameters)
			val selectedSongName = listBox.getListItem(selection).name.lowercase()

			onSelectPerformance(player, selectedSongName)
		}

		listBox.display(player)
	}

	abstract fun onSelectPerformance(player: Player, selection: String)
	abstract fun onStopPerformance(player: Player)
	abstract fun onChangePerformance(player: Player, selection: String)
}

data class ListBoxText(val title: String, val prompt: String)
