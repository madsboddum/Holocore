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
package com.projectswg.holocore.services.gameplay.mission.destruction;

import com.projectswg.common.data.CRC;
import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.common.network.packets.SWGPacket;
import com.projectswg.common.network.packets.swg.zone.object_controller.MissionAcceptRequest;
import com.projectswg.common.network.packets.swg.zone.object_controller.MissionListRequest;
import com.projectswg.holocore.intents.support.global.chat.SystemMessageIntent;
import com.projectswg.holocore.intents.support.global.network.InboundPacketIntent;
import com.projectswg.holocore.intents.support.global.zone.PlayerEventIntent;
import com.projectswg.holocore.intents.support.objects.swg.ObjectCreatedIntent;
import com.projectswg.holocore.resources.support.data.server_info.loader.DestructionMissionLoader;
import com.projectswg.holocore.resources.support.data.server_info.loader.ServerData;
import com.projectswg.holocore.resources.support.global.player.Player;
import com.projectswg.holocore.resources.support.global.player.PlayerEvent;
import com.projectswg.holocore.resources.support.objects.ObjectCreator;
import com.projectswg.holocore.resources.support.objects.swg.SWGObject;
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject;
import com.projectswg.holocore.resources.support.objects.swg.mission.MissionObject;
import com.projectswg.holocore.services.support.objects.ObjectStorageService;
import me.joshlarson.jlcommon.control.IntentHandler;
import me.joshlarson.jlcommon.control.Service;
import me.joshlarson.jlcommon.log.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DestructionMissionService extends Service {
	
	private static final int MISSIONS_IN_BROWSER = 5;	// Amount of missions displayed in the mission list
	
	private DestructionMissionLoader destructionMissionLoader;
	
	@Override
	public boolean initialize() {
		destructionMissionLoader = ServerData.INSTANCE.getDestructionMissionLoader();
		
		return super.initialize();
	}
	
	@IntentHandler
	private void handlePlayerEvent(PlayerEventIntent intent) {
		if (intent.getEvent() == PlayerEvent.PE_FIRST_ZONE) {
			Player player = intent.getPlayer();
			CreatureObject creatureObject = player.getCreatureObject();
			
			SWGObject missionBag = creatureObject.getSlottedObject("mission_bag");
			Collection<SWGObject> containedObjects = missionBag.getContainedObjects();
			
			int toCreate = Math.max(0, MISSIONS_IN_BROWSER - containedObjects.size());
			
			for (int i = 0; i < toCreate; i++) {
				MissionObject missionObject = (MissionObject) ObjectCreator.createObjectFromTemplate("object/mission/shared_mission_object.iff");
				missionObject.moveToContainer(missionBag);
				
				ObjectCreatedIntent.broadcast(missionObject);
			}
		}
	}
	
	@IntentHandler
	private void handleInboundPacket(InboundPacketIntent intent) {
		SWGPacket packet = intent.getPacket();
		
		if (packet instanceof MissionAcceptRequest) {
			SystemMessageIntent.broadcastPersonal(intent.getPlayer(), "Missions cannot be accepted yet.");
		} else if (packet instanceof MissionListRequest) {
			handleMissionListRequest((MissionListRequest) packet, intent.getPlayer());
		}
	}
	
	private void handleMissionListRequest(MissionListRequest request, Player player) {
		long terminalId = request.getTerminalId();
		SWGObject terminal = ObjectStorageService.ObjectLookup.getObjectById(terminalId);
		
		if (terminal == null) {
			// Requested terminal could not be found
			Log.w("Unable to find terminal by object ID %d", terminalId);
			return;
		}
		
		if (!"object/tangible/terminal/shared_terminal_mission.iff".equals(terminal.getTemplate())) {
			// This is not a destruction mission terminal
			Log.t("Destruction missions not populated because terminal is a different type");
			return;
		}
		
		CreatureObject creatureObject = player.getCreatureObject();
		SWGObject missionBag = creatureObject.getSlottedObject("mission_bag");
		
		List<MissionObject> missionObjects = missionBag.getContainedObjects().stream()
				.map(object -> (MissionObject) object)
				.collect(Collectors.toList());
		
		Terrain terrain = creatureObject.getTerrain();
		
		List<DestructionMissionLoader.DestructionMissionInfo> randomMissions = getRandomMissions(terrain, missionObjects.size());
		
		for (int i = 0; i < randomMissions.size(); i++) {
			DestructionMissionLoader.DestructionMissionInfo missionInfo = randomMissions.get(i);
			MissionObject missionObject = missionObjects.get(i);
			short difficulty = creatureObject.getLevel();
			
			missionObject.setMissionType(new CRC("destroy"));
			missionObject.setTargetAppearance(new CRC(missionInfo.getTemplate()));
			missionObject.setDifficulty(difficulty);
			missionObject.setMissionCreator(missionInfo.getCreator());
			missionObject.setTargetName(missionInfo.getTarget());
			missionObject.setTitle(missionInfo.getTitle());
			missionObject.setDescription(missionInfo.getDescription());
			missionObject.setReward(getRandomCreditAmount(difficulty));
			
			Location creatureWorldLocation = creatureObject.getWorldLocation();
			Location randomLocation = getRandomLocation(creatureWorldLocation);
			
			MissionObject.MissionLocation location = new MissionObject.MissionLocation();
			Point3D locationPoint = randomLocation.getPosition();
			location.setLocation(locationPoint);
			location.setTerrain(terrain);
			missionObject.setMissionLocation(location);
			
			MissionObject.MissionLocation startLocation = new MissionObject.MissionLocation();
			Point3D startLocationPoint = randomLocation.getPosition();
			startLocation.setLocation(startLocationPoint);
			startLocation.setTerrain(terrain);
			missionObject.setStartMissionLocation(startLocation);
			
			missionObject.setStatus(request.getTickCount()); // This is the magic that makes the client redisplay the mission.
		}
	}
	
	private List<DestructionMissionLoader.DestructionMissionInfo> getRandomMissions(Terrain terrain, int count) {
		List<DestructionMissionLoader.DestructionMissionInfo> terrainMissions = new ArrayList<>(destructionMissionLoader.getTerrainMissions(terrain));
		
		if (terrainMissions.size() <= count) {
			// This terrain has too few missions. Return whatever's available.
			return terrainMissions;
		}
		
		List<DestructionMissionLoader.DestructionMissionInfo> result = new ArrayList<>();
		
		while (result.size() < count) {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			
			int next = random.nextInt(terrainMissions.size());
			
			// Removing the mission from the picking pool is important to prevent the same mission being displayed multiple times
			DestructionMissionLoader.DestructionMissionInfo randomMission = terrainMissions.remove(next);
			
			result.add(randomMission);
		}
		
		return result;
	}
	
	private int getRandomCreditAmount(int difficulty) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int offset = random.nextInt(-100, 101);	// -100 to 100
		
		return 1000 + offset + (100 * difficulty);
	}
	
	@NotNull
	private Location getRandomLocation(Location base) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		double offsetX = random.nextDouble(1500, 2500);
		double offsetZ = random.nextDouble(1500, 2500);
		
		offsetX = random.nextBoolean() ? offsetX : -offsetX;
		offsetZ = random.nextBoolean() ? offsetZ : -offsetZ;
		
		double x = base.getX() + offsetX;
		double z = base.getZ() + offsetZ;
		// TODO calculate y when we can read terrain height based on x and z to prevent location likely being mid-air
		
		// TODO prevent location from being in a no-build zone
		return Location.builder(base)
				.setX(x)
				.setZ(z)
				.build();
	}
}
