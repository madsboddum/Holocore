/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.holocore.resources.support.data.server_info.loader;

import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.holocore.resources.support.data.server_info.SdbLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DestructionMissionLoader extends DataLoader {
	
	private final Map<Terrain, Collection<DestructionMissionInfo>> missionInfoMap;
	
	public DestructionMissionLoader() {
		missionInfoMap = new HashMap<>();
	}
	
	@NotNull
	public Collection<DestructionMissionInfo> getTerrainMissions(Terrain terrain) {
		return missionInfoMap.getOrDefault(terrain, Collections.emptyList());
	}
	
	@Override
	public void load() throws IOException {
		try (SdbLoader.SdbResultSet set = SdbLoader.load(new File("serverdata/shared/missions/destruction_missions.sdb"))) {
			while (set.next()) {
				String planet = set.getText("terrain");
				Terrain terrain = Terrain.getTerrainFromName(planet);
				assert terrain != null : "unable to find terrain by name " + planet;
				
				Collection<DestructionMissionInfo> terrainMissionInfos = missionInfoMap.computeIfAbsent(terrain, k -> new ArrayList<>());
				
				DestructionMissionInfo terrainMissionInfo = new DestructionMissionInfo(set);
				terrainMissionInfos.add(terrainMissionInfo);
			}
		}
	}
	
	public static final class DestructionMissionInfo {
		private final long id;
		private final StringId title;
		private final String creator;
		private final StringId description;
		private final String target;
		private final String template;
		
		private DestructionMissionInfo(SdbLoader.SdbResultSet set) {
			id = set.getInt("id");
			title = new StringId(set.getText("title"));
			creator = set.getText("creator");
			description = new StringId(set.getText("description"));
			target = set.getText("target");
			template = set.getText("template");
		}
		
		public long getId() {
			return id;
		}
		
		@NotNull
		public StringId getTitle() {
			return title;
		}
		
		@NotNull
		public String getCreator() {
			return creator;
		}
		
		@NotNull
		public StringId getDescription() {
			return description;
		}
		
		@NotNull
		public String getTarget() {
			return target;
		}
		
		@NotNull
		public String getTemplate() {
			return template;
		}
	}
}
