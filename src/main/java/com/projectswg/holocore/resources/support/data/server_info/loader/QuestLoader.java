/***********************************************************************************
 * Copyright (c) 2021 /// Project SWG /// www.projectswg.com                       *
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

import com.projectswg.common.data.swgfile.ClientFactory;
import com.projectswg.common.data.swgfile.visitors.DatatableData;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class QuestLoader extends DataLoader {
	
	private final Map<String, QuestListInfo> questListInfoMap;
	private final Map<String, List<QuestLoader.QuestTaskInfo>> questTaskInfosMap;
	
	public QuestLoader() {
		questListInfoMap = Collections.synchronizedMap(new HashMap<>());
		questTaskInfosMap = Collections.synchronizedMap(new HashMap<>());
	}
	
	public QuestListInfo getQuestListInfo(String questName) {
		if (questListInfoMap.containsKey(questName)) {
			return questListInfoMap.get(questName);
		}
		
		String fileName = questName + ".iff";
		QuestListInfo listInfo = new QuestListInfo();
		Path filePath = Paths.get("clientdata", "datatables", "questlist", fileName);
		DatatableData datatableData = (DatatableData) ClientFactory.getInfoFromFile(filePath.toFile());
		
		datatableData.handleRows(row -> {
			Integer level = datatableData.getInt(row, "LEVEL");
			Integer tier = datatableData.getInt(row, "TIER");
			String journalEntryTitle = datatableData.getString(row, "JOURNAL_ENTRY_TITLE");
			String journalEntryDescription = datatableData.getString(row, "JOURNAL_ENTRY_DESCRIPTION");
			String experienceType = datatableData.getString(row, "QUEST_REWARD_EXPERIENCE_TYPE");
			Integer credits = datatableData.getInt(row, "QUEST_REWARD_BANK_CREDITS");
			Boolean completeWhenTasksComplete = datatableData.getBoolean(row, "COMPLETE_WHEN_TASKS_COMPLETE");
			Boolean repeatable = datatableData.getBoolean(row, "ALLOW_REPEATS");
			
			listInfo.setLevel(level);
			listInfo.setTier(tier);
			listInfo.setJournalEntryTitle(journalEntryTitle);
			listInfo.setJournalEntryDescription(journalEntryDescription);
			listInfo.setExperienceType(experienceType);
			listInfo.setCredits(credits);
			listInfo.setCompleteWhenTasksComplete(completeWhenTasksComplete);
			listInfo.setRepeatable(repeatable);
		});
		
		questListInfoMap.put(questName, listInfo);
		
		return listInfo;
	}
	
	public List<QuestTaskInfo> getTaskListInfos(String questName) {
		if (questTaskInfosMap.containsKey(questName)) {
			return questTaskInfosMap.get(questName);
		}
		
		String fileName = questName + ".iff";
		Path filePath = Paths.get("clientdata", "datatables", "questtask", fileName);
		DatatableData datatableData = (DatatableData) ClientFactory.getInfoFromFile(filePath.toFile());
		
		List<QuestTaskInfo> questTaskInfos = new ArrayList<>();
		
		datatableData.handleRows(row -> {
			QuestTaskInfo questTaskInfo = new QuestTaskInfo();
			
			String type = datatableData.getString(row, "ATTACH_SCRIPT");
			String tasksOnComplete = datatableData.getString(row, "TASKS_ON_COMPLETE");
			String name = datatableData.getString(row, "TASK_NAME");
			String commMessageText = datatableData.getString(row, "COMM_MESSAGE_TEXT");
			String npcAppearanceServerTemplate = datatableData.getString(row, "NPC_APPEARANCE_SERVER_TEMPLATE");
			String targetServerTemplate = datatableData.getString(row, "TARGET_SERVER_TEMPLATE");
			String grantQuestOnComplete = datatableData.getString(row, "GRANT_QUEST_ON_COMPLETE");
			Integer count = datatableData.getInt(row, "COUNT");
			Integer minTime = datatableData.getInt(row, "MIN_TIME");
			Integer maxTime = datatableData.getInt(row, "MAX_TIME");
			
			questTaskInfo.setType(type);
			
			String[] split = tasksOnComplete.split(",");
			for (String s : split) {
				if (!s.isBlank()) {
					int taskIdx = Integer.parseInt(s);
					questTaskInfo.addTaskOnComplete(taskIdx);
				}
			}
			
			questTaskInfo.setIndex(row);
			questTaskInfo.setName(name);
			questTaskInfo.setCommMessageText(commMessageText);
			questTaskInfo.setNpcAppearanceServerTemplate(npcAppearanceServerTemplate);
			questTaskInfo.setTargetServerTemplate(targetServerTemplate);
			questTaskInfo.setGrantQuestOnComplete(grantQuestOnComplete);
			questTaskInfo.setCount(count);
			questTaskInfo.setMinTime(minTime);
			questTaskInfo.setMaxTime(maxTime);
			
			questTaskInfos.add(questTaskInfo);
		});
		
		questTaskInfosMap.put(questName, questTaskInfos);
		
		return questTaskInfos;
	}
	
	@Override
	public void load() throws IOException {
	
	}
	
	public static class QuestListInfo {
		private Integer level;
		private Integer tier;
		private String journalEntryTitle;
		private String journalEntryDescription;
		private String experienceType;
		private Integer credits;
		private Boolean completeWhenTasksComplete;
		private Boolean repeatable;
		
		private QuestListInfo() {
		
		}
		
		public Boolean getRepeatable() {
			return repeatable;
		}
		
		private void setRepeatable(Boolean repeatable) {
			this.repeatable = repeatable;
		}
		
		public Boolean getCompleteWhenTasksComplete() {
			return completeWhenTasksComplete;
		}
		
		public void setCompleteWhenTasksComplete(Boolean completeWhenTasksComplete) {
			this.completeWhenTasksComplete = completeWhenTasksComplete;
		}
		
		public Integer getLevel() {
			return level;
		}
		
		private void setLevel(Integer level) {
			this.level = level;
		}
		
		public Integer getTier() {
			return tier;
		}
		
		private void setTier(Integer tier) {
			this.tier = tier;
		}
		
		public String getJournalEntryTitle() {
			return journalEntryTitle;
		}
		
		private void setJournalEntryTitle(String journalEntryTitle) {
			this.journalEntryTitle = journalEntryTitle;
		}
		
		public String getJournalEntryDescription() {
			return journalEntryDescription;
		}
		
		private void setJournalEntryDescription(String journalEntryDescription) {
			this.journalEntryDescription = journalEntryDescription;
		}
		
		public String getExperienceType() {
			return experienceType;
		}
		
		private void setExperienceType(String experienceType) {
			this.experienceType = experienceType;
		}
		
		public Integer getCredits() {
			return credits;
		}
		
		private void setCredits(Integer credits) {
			this.credits = credits;
		}
	}
	
	public static class QuestTaskInfo {
		private final Collection<Integer> nextTasksOnComplete;
		private int index;
		private String type;
		private String name;
		private String commMessageText;
		private String npcAppearanceServerTemplate;
		private String targetServerTemplate;
		private Integer count;
		private String grantQuestOnComplete;
		private Integer minTime;
		private Integer maxTime;
		
		private QuestTaskInfo() {
			nextTasksOnComplete = new ArrayList<>();
		}
		
		public Integer getMinTime() {
			return minTime;
		}
		
		private void setMinTime(Integer minTime) {
			this.minTime = minTime;
		}
		
		public Integer getMaxTime() {
			return maxTime;
		}
		
		private void setMaxTime(Integer maxTime) {
			this.maxTime = maxTime;
		}
		
		public int getIndex() {
			return index;
		}
		
		private void setIndex(int index) {
			this.index = index;
		}
		
		private void addTaskOnComplete(Integer taskIdx) {
			nextTasksOnComplete.add(taskIdx);
		}
		
		public Collection<Integer> getNextTasksOnComplete() {
			return new ArrayList<>(nextTasksOnComplete);
		}
		
		public String getType() {
			return type;
		}
		
		private void setType(String type) {
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		
		private void setName(String name) {
			this.name = name;
		}
		
		public String getCommMessageText() {
			return commMessageText;
		}
		
		private void setCommMessageText(String commMessageText) {
			this.commMessageText = commMessageText;
		}
		
		public String getNpcAppearanceServerTemplate() {
			return npcAppearanceServerTemplate;
		}
		
		private void setNpcAppearanceServerTemplate(String npcAppearanceServerTemplate) {
			this.npcAppearanceServerTemplate = npcAppearanceServerTemplate;
		}
		
		public String getTargetServerTemplate() {
			return targetServerTemplate;
		}
		
		private void setTargetServerTemplate(String targetServerTemplate) {
			this.targetServerTemplate = targetServerTemplate;
		}
		
		public Integer getCount() {
			return count;
		}
		
		private void setCount(Integer count) {
			this.count = count;
		}
		
		public String getGrantQuestOnComplete() {
			return grantQuestOnComplete;
		}
		
		public void setGrantQuestOnComplete(String grantQuestOnComplete) {
			this.grantQuestOnComplete = grantQuestOnComplete;
		}
	}
}
