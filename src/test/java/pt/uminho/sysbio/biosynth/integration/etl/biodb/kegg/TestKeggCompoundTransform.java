package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.factory.KeggCompoundMetaboliteFactory;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;

public class TestKeggCompoundTransform {

	@Test
	public void testEmpty() {
		KeggCompoundMetaboliteEntity entitySrc = 
				new KeggCompoundMetaboliteFactory("C0000A")
					.withId(3L)
					.withDescription("Factory Metabolite")
					.build();
		
		KeggCompoundTransform transform = new KeggCompoundTransform();
		GraphMetaboliteEntity entityDst = transform.etlTransform(entitySrc);
		
		assertEquals(0, entityDst.getPropertyEntities().size());
		assertEquals(0, entityDst.getCrossreferences().size());
	}
	
	@Test
	public void testWithProp() {
		KeggCompoundMetaboliteEntity entitySrc = 
				new KeggCompoundMetaboliteFactory("C0000A")
					.withId(3L)
					.withDescription("Factory Metabolite")
					.withFormula("C10H10O10")
					.withName("A factory metabolite")
					.build();
		
		KeggCompoundTransform transform = new KeggCompoundTransform();
		GraphMetaboliteEntity entityDst = transform.etlTransform(entitySrc);

		assertNotEquals(0, entityDst.getPropertyEntities().size());
		assertEquals(0, entityDst.getCrossreferences().size());
	}
	
	@Test
	public void testWithCrossreference() {
		KeggCompoundMetaboliteEntity entitySrc = 
				new KeggCompoundMetaboliteFactory("C0000A")
					.withId(3L)
					.withDescription("Factory Metabolite")
					.withCrossreference("CAS Registry Number", "1-2-3")
					.build();
		
		KeggCompoundTransform transform = new KeggCompoundTransform();
		GraphMetaboliteEntity entityDst = transform.etlTransform(entitySrc);

		assertEquals(0, entityDst.getPropertyEntities().size());
		assertNotEquals(0, entityDst.getCrossreferences().size());
	}
	
	@Test
	public void testWithAll() {
		KeggCompoundMetaboliteEntity entitySrc = 
				new KeggCompoundMetaboliteFactory("C0000A")
					.withId(3L)
					.withDescription("Factory Metabolite")
					.withFormula("C10H10O10")
					.withName("A factory metabolite")
					.withCrossreference("CAS Registry Number", "1-2-3")
					.build();
		
		KeggCompoundTransform transform = new KeggCompoundTransform();
		GraphMetaboliteEntity entityDst = transform.etlTransform(entitySrc);

		assertNotEquals(0, entityDst.getPropertyEntities().size());
		assertNotEquals(0, entityDst.getCrossreferences().size());
	}

}
