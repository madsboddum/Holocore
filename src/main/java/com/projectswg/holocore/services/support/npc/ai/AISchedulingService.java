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
package com.projectswg.holocore.services.support.npc.ai;

import com.projectswg.holocore.intents.support.npc.ai.ScheduleNpcModeIntent;
import com.projectswg.holocore.intents.support.npc.ai.StartNpcCombatIntent;
import com.projectswg.holocore.resources.support.npc.ai.NpcCombatMode;
import com.projectswg.holocore.resources.support.objects.swg.custom.AIObject;
import com.projectswg.holocore.resources.support.objects.swg.custom.NpcMode;
import me.joshlarson.jlcommon.control.IntentHandler;
import me.joshlarson.jlcommon.control.Service;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AISchedulingService extends Service {
	
	private final Map<AIObject, NpcMode> modes;
	
	public AISchedulingService() {
		this.modes = new ConcurrentHashMap<>();
	}
	
	@IntentHandler
	private void handleScheduleNpcModeIntent(ScheduleNpcModeIntent snmi) {
		AIObject obj = snmi.getObj();
		NpcMode next = snmi.getMode();
		if (next == null)
			next = obj.getDefaultMode();
		
		NpcMode prev = next == null ? modes.remove(obj) : modes.put(obj, next);
		if (prev == next)
			return;
		
		stop(obj, prev);
		start(obj, next);
	}
	
	@IntentHandler
	private void handleStartNpcCombatIntent(StartNpcCombatIntent snci) {
		modes.compute(snci.getObj(), (o, prev) -> {
			if (prev instanceof NpcCombatMode) {
				((NpcCombatMode) prev).addTargets(snci.getTargets());
				return prev;
			}
			NpcCombatMode mode = new NpcCombatMode(o);
			mode.addTargets(snci.getTargets());
			start(o, mode);
			return mode;
		});
	}
	
	private void start(@NotNull AIObject obj, @Nullable NpcMode mode) {
		if (mode == null)
			return;
		
		obj.setActiveMode(mode);
	}
	
	private void stop(@NotNull AIObject obj, @Nullable NpcMode mode) {
		if (mode == null)
			return;
		
		obj.setActiveMode(null);
	}
	
}
