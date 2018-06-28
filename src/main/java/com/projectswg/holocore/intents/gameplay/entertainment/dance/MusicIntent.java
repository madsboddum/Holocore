package com.projectswg.holocore.intents.gameplay.entertainment.dance;

import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject;
import me.joshlarson.jlcommon.control.Intent;

public class MusicIntent extends Intent {
	private final String performanceName;
	private final String instrumentTemplate;
	private final CreatureObject musician;
	
	public MusicIntent(String performanceName, String instrumentTemplate, CreatureObject musician) {
		this.performanceName = performanceName;
		this.instrumentTemplate = instrumentTemplate;
		this.musician = musician;
	}
	
	public String getPerformanceName() {
		return performanceName;
	}
	
	public String getInstrumentTemplate() {
		return instrumentTemplate;
	}
	
	public CreatureObject getMusician() {
		return musician;
	}
}
