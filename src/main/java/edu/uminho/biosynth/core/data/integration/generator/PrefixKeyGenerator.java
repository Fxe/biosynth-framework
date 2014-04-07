package edu.uminho.biosynth.core.data.integration.generator;

public class PrefixKeyGenerator implements IKeyGenerator<String> {

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

}
