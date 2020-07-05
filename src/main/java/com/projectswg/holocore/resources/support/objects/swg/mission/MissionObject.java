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
package com.projectswg.holocore.resources.support.objects.swg.mission;

import com.projectswg.common.data.CRC;
import com.projectswg.common.data.encodables.oob.StringId;
import com.projectswg.common.data.encodables.oob.waypoint.WaypointPackage;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.location.Terrain;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.encoding.StringType;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.network.packets.swg.zone.baselines.Baseline.BaselineType;
import com.projectswg.common.persistable.Persistable;
import com.projectswg.holocore.resources.support.global.network.BaselineBuilder;
import com.projectswg.holocore.resources.support.global.player.Player;
import com.projectswg.holocore.resources.support.objects.swg.intangible.IntangibleObject;
import com.projectswg.holocore.resources.support.objects.swg.waypoint.WaypointObject;

public class MissionObject extends IntangibleObject {
	
	private int difficulty					= 0;
	private MissionLocation location		= new MissionLocation();
	private String missionCreator			= "";
	private int reward						= 0;
	private MissionLocation startLocation	= new MissionLocation();
	private CRC targetAppearance			= new CRC();
	private StringId description			= new StringId();
	private StringId title					= new StringId();
	private int status						= 0;
	private CRC missionType					= new CRC();
	private String targetName				= "";
	private WaypointObject waypoint			= new WaypointObject(0);
	
	public MissionObject(long objectId) {
		super(objectId, BaselineType.MISO);
	}
	
	@Override
	public void createBaseline3(Player target, BaselineBuilder bb) {
		super.createBaseline3(target, bb);
		bb.addInt(difficulty);	// 5
		bb.addObject(location);	// 6
		bb.addUnicode(missionCreator);	// 7
		bb.addInt(reward);	// 8
		bb.addObject(startLocation);	// 9
		bb.addObject(targetAppearance);	// 10
		bb.addObject(description);	// 11
		bb.addObject(title);	// 12
		bb.addInt(status);	// 13
		bb.addObject(missionType);	// 14
		bb.addAscii(targetName);	// 15
		bb.addObject(waypoint.getOOB());	// 16
		bb.incrementOperandCount(12);
	}
	
	@Override
	public void parseBaseline3(NetBuffer buffer) {
		super.parseBaseline3(buffer);
		difficulty = buffer.getInt();
		location = buffer.getEncodable(MissionLocation.class);
		missionCreator = buffer.getUnicode();
		reward = buffer.getInt();
		startLocation = buffer.getEncodable(MissionLocation.class);
		targetAppearance = buffer.getEncodable(CRC.class);
		description = buffer.getEncodable(StringId.class);
		title = buffer.getEncodable(StringId.class);
		status = buffer.getInt();
		missionType = buffer.getEncodable(CRC.class);
		targetName = buffer.getAscii();
		int pos = buffer.position();
		buffer.seek(24);
		buffer.getUnicode();
		waypoint = new WaypointObject(buffer.getLong());
		buffer.position(pos);
		waypoint.setOOB(new WaypointPackage(buffer));
	}

	@Override
	protected void createBaseline6(Player target, BaselineBuilder bb) {
		super.createBaseline6(target, bb);
		bb.addInt(0);	// TODO unsure what this should be, but the NGE client crashes without it.
		bb.incrementOperandCount(1);
	}

	@Override
	public void save(NetBufferStream stream) {
		super.save(stream);
		stream.addByte(0);
		stream.addInt(difficulty);
		stream.addInt(reward);
		stream.addInt(status);
		stream.addUnicode(missionCreator);
		stream.addUnicode(targetName);
		title.save(stream);
		description.save(stream);
		targetAppearance.save(stream);
		missionType.save(stream);
		waypoint.save(stream);
		startLocation.save(stream);
		location.save(stream);
	}
	
	@Override
	public void read(NetBufferStream stream) {
		super.read(stream);
		stream.getByte();
		difficulty = stream.getInt();
		reward = stream.getInt();
		status = stream.getInt();
		missionCreator = stream.getUnicode();
		targetName = stream.getUnicode();
		title.read(stream);
		description.read(stream);
		targetAppearance.read(stream);
		missionType.read(stream);
		waypoint.read(stream);
		startLocation.read(stream);
		location.read(stream);
	}
	
	public static class MissionLocation implements Encodable, Persistable {
		
		private Point3D location;
		private long objectId;
		private Terrain terrain;
		
		public MissionLocation() {
			location = new Point3D();
			objectId = 0;
			terrain = Terrain.TATOOINE;
		}
		
		@Override
		public byte[] encode() {
			NetBuffer data = NetBuffer.allocate(getLength());
			data.addEncodable(location);
			data.addLong(objectId);
			data.addInt(terrain.getCrc());
			return data.array();
		}
		
		@Override
		public void decode(NetBuffer data) {
			location = data.getEncodable(Point3D.class);
			objectId = data.getLong();
			terrain = Terrain.getTerrainFromCrc(data.getInt());
		}
		
		@Override
		public int getLength() {
			return location.getLength() + 12;
		}
		
		@Override
		public void save(NetBufferStream stream) {
			stream.addInt(0);
			stream.addLong(objectId);
			location.save(stream);
			stream.addAscii(terrain.name());
		}
		
		@Override
		public void read(NetBufferStream stream) {
			stream.getInt();
			objectId = stream.getLong();
			location.read(stream);
			terrain = Terrain.valueOf(stream.getAscii());
		}

		public Point3D getLocation() {
			return location;
		}

		public void setLocation(Point3D location) {
			this.location = location;
		}

		public long getObjectId() {
			return objectId;
		}

		public Terrain getTerrain() {
			return terrain;
		}

		public void setTerrain(Terrain terrain) {
			this.terrain = terrain;
		}
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
		sendDelta(3, 5, difficulty);
	}

	public MissionLocation getMissionLocation() {
		return location;
	}

	public void setMissionLocation(MissionLocation location) {
		this.location = location;
		sendDelta(3, 6, location);
	}

	public String getMissionCreator() {
		return missionCreator;
	}

	public void setMissionCreator(String missionCreator) {
		this.missionCreator = missionCreator;
		sendDelta(3, 7, missionCreator, StringType.UNICODE);
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
		sendDelta(3, 8, reward);
	}

	public MissionLocation getStartMissionLocation() {
		return startLocation;
	}

	public void setStartMissionLocation(MissionLocation startLocation) {
		this.startLocation = startLocation;
		sendDelta(3, 9, startLocation);
	}

	public CRC getTargetAppearance() {
		return targetAppearance;
	}

	public void setTargetAppearance(CRC targetAppearance) {
		this.targetAppearance = targetAppearance;
		sendDelta(3, 10, targetAppearance);
	}

	public StringId getDescription() {
		return description;
	}

	public void setDescription(StringId description) {
		this.description = description;
		sendDelta(3, 11, description);
	}

	public StringId getTitle() {
		return title;
	}

	public void setTitle(StringId title) {
		this.title = title;
		sendDelta(3, 12, title);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		sendDelta(3, 13, status);
	}

	public CRC getMissionType() {
		return missionType;
	}

	public void setMissionType(CRC missionType) {
		this.missionType = missionType;
		sendDelta(3, 14, missionType);
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
		sendDelta(3, 15, targetName, StringType.ASCII);
	}

	public WaypointObject getWaypoint() {
		return waypoint;
	}

	public void setWaypoint(WaypointObject waypoint) {
		this.waypoint = waypoint;
		sendDelta(3, 16, waypoint.getOOB());
	}
	
}
