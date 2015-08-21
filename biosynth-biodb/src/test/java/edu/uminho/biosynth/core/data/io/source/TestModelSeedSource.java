package edu.uminho.biosynth.core.data.io.source;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestModelSeedSource {
  
  private static final Logger logger = LoggerFactory.getLogger(TestModelSeedSource.class);
  
	@Test
	public void testModelSeedValidFile() {
//		Logger.getLogger("").setLevel(Level.SEVERE);
//		
//		ModelSeedSource source = new ModelSeedSource("D:/home/data/biosynth/metabolic_data/model_seed/seed_reactions.tsv", "D:/home/data/biosynth/metabolic_data/model_seed/seed_compounds.tsv");
//		source.initialize();
//		
//		System.out.println(source.getMetaboliteInformation2("cpd00001"));
	}
	

	public void testModelSeedInValidFile() {
	  logger.debug("aww");
	  assertEquals(true, true);
	}

}
