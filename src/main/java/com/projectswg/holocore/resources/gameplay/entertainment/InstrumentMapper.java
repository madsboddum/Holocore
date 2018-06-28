package com.projectswg.holocore.resources.gameplay.entertainment;

public class InstrumentMapper {
	public String getInstrumentByTemplate(String template) {
		String fileName = template.replace("object/tangible/instrument/", "");
		String instrument = fileName.replace("shared_", "").replace(".iff", "");
		
		return instrument;
	}
}
