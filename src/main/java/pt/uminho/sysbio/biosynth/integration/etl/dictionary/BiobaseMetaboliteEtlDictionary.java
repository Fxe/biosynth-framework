package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.components.Metabolite;

public class BiobaseMetaboliteEtlDictionary<M extends Metabolite> implements EtlDictionary<String, String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BiobaseMetaboliteEtlDictionary.class);
	
	private final Class<M> clazz;
	
	public BiobaseMetaboliteEtlDictionary(Class<M> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public String translate(String lookup) {
		String result = BioDbDictionary.translateDatabase(lookup);

		LOGGER.debug(String.format("Translated %s -> %s using modifier %s", lookup, result, clazz));
		return result;
	}
}
