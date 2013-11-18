package edu.uminho.biosynth.core.data.io.source;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import edu.uminho.biosynth.core.data.io.source.ModelSeedSource;

public class TestModelSeedSource {

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
		fail("Not yet implemented");
	}

}
