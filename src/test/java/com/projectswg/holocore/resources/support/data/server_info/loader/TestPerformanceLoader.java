package com.projectswg.holocore.resources.support.data.server_info.loader;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPerformanceLoader {
	
	private PerformanceLoader loader;
	
	@Before
	public void setup() throws IOException {
		loader = new PerformanceLoader();
		
		loader.load();
	}
	
	@Test
	public void testSongsSupportMultipleInstruments() {
		boolean starwars1SupportsSlitherhorn = loader.isInstrumentSupported("starwars1", "slitherhorn");
		assertTrue("Song starwars1 should support the Slitherhorn instrument", starwars1SupportsSlitherhorn);
		
		boolean starwars1SupportsFizz = loader.isInstrumentSupported("starwars1", "fizz");
		assertTrue("Song starwars1 should support the Fizz instrument", starwars1SupportsFizz);
	}
	
	@Test
	public void testSongDoesNotSupportInstrument() {
		boolean starwars1SupportsSlitherhorn = loader.isInstrumentSupported("starwars1", "doesnotexist");
		assertFalse("Song starwars1 should not support an instrument by a random name", starwars1SupportsSlitherhorn);
	}
	
	@Test
	public void testNullInstrumentName() {
		boolean starwars1SupportsSlitherhorn = loader.isInstrumentSupported("starwars1", null);
		assertFalse("Song starwars1 should not support a null instrument name", starwars1SupportsSlitherhorn);
	}
}
