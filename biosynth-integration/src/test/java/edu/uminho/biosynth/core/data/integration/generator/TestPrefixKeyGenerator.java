package edu.uminho.biosynth.core.data.integration.generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPrefixKeyGenerator {

	@Test
	public void testGenerateKeys() {
		PrefixKeyGenerator generator = new PrefixKeyGenerator("KEY");
		
		assertEquals("KEY_0", generator.generateKey());
		assertEquals("KEY_1", generator.generateKey());
		assertEquals("KEY_2", generator.generateKey());
		assertEquals("KEY_3", generator.generateKey());
		assertEquals("KEY_4", generator.generateKey());
	}
	
	@Test
	public void testGenerateKeysWithReset() {
		PrefixKeyGenerator generator = new PrefixKeyGenerator("KEY");
		
		assertEquals("KEY_0", generator.generateKey());
		assertEquals("KEY_1", generator.generateKey());
		assertEquals("KEY_2", generator.generateKey());
		generator.reset();
		assertEquals("KEY_0", generator.generateKey());
		assertEquals("KEY_1", generator.generateKey());
	}
	
	@Test
	public void testGenerateKeysWithValidRestartPoint() {
		PrefixKeyGenerator generator = new PrefixKeyGenerator("KEY");
		generator.generateFromLastElement("KEY_60");
		assertEquals("KEY_61", generator.generateKey());
		assertEquals("KEY_62", generator.generateKey());
		assertEquals("KEY_63", generator.generateKey());
		generator.reset();
		assertEquals("KEY_0", generator.generateKey());
		assertEquals("KEY_1", generator.generateKey());
	}

	@Test
	public void testGenerateKeysWithInvalidRestartPoint() {
		PrefixKeyGenerator generator = new PrefixKeyGenerator("KEY");
		generator.generateFromLastElement("YEK_60");
		assertEquals("KEY_0", generator.generateKey());
	}
}
