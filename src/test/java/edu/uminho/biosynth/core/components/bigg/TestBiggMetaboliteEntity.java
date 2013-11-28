package edu.uminho.biosynth.core.components.bigg;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.bigg.components.BiggMetaboliteCrossReferenceEntity;

public class TestBiggMetaboliteEntity {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Test
	public void test36436() {
		BiggMetaboliteEntity cpd = new BiggMetaboliteEntity();
		cpd.setId(36436);
		cpd.setCharge(-1);
		cpd.setName("3-Dehydroquinate");
		cpd.setEntry("3dhq");
		cpd.setFormula("C7H9O6");
		cpd.getCompartments().add("Cytosol");
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.DATABASE, "KEGG", "C00944"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iJR904", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iIT341", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iJN746", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iAF1260", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iND750", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iSB619", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iAF692", "3dhq"));
		cpd.getCrossReferences().add( new BiggMetaboliteCrossReferenceEntity(
				GenericCrossReference.Type.MODEL, "iNJ661", "3dhq"));
		
		assertEquals(36436, cpd.getId());
		assertEquals(9, cpd.getCrossReferences().size());
		
	}

}
