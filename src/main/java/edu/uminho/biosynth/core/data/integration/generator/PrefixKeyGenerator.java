package edu.uminho.biosynth.core.data.integration.generator;

import org.apache.log4j.Logger;

public class PrefixKeyGenerator implements IKeyGenerator<String> {

	private static final Logger LOGGER = Logger.getLogger(PrefixKeyGenerator.class);
	
	private final String prefix;
	private int counter = 0;
	
	public PrefixKeyGenerator(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public String generateKey() {
		return prefix + "_" + counter++;
	}

	@Override
	public void reset() {
		counter = 0;
	}

	@Override
	public void generateFromLastElement(String key) {
		if (!key.startsWith(prefix.concat("_"))) {
			LOGGER.warn(String.format("Invalid last key - [%s]. Expected [%s_{number}]", key, prefix));
			return;
		}
		String value = key.substring(prefix.length() + 1);

		try {
			this.counter = Integer.parseInt(value) + 1;
		} catch (NumberFormatException e) {
			LOGGER.warn(String.format("Invalid last key - [%s]. Expected [%s_{number}]", key, prefix));
		}
	}

	@Override
	public String getCurrentKey() {
		return prefix + "_" + counter;
	}

}
