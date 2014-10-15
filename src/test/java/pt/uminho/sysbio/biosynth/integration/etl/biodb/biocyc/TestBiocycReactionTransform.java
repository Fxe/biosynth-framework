package pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.factory.BiocycReactionCrossreferenceFactory;
import pt.uminho.sysbio.biosynthframework.biodb.factory.BiocycReactionFactory;
import pt.uminho.sysbio.biosynthframework.biodb.factory.BiocycReactionStoichiometryFactory;

public class TestBiocycReactionTransform {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmpty() {
		BioCycReactionEntity entity = new BioCycReactionEntity();
		BiocycReactionTransform biocycReactionTransform = new BiocycReactionTransform("FooCyc");
		GraphReactionEntity centralReactionEntity = biocycReactionTransform.etlTransform(entity);
		
		assertEquals(0, centralReactionEntity.getLeft().size());
		assertEquals(0, centralReactionEntity.getRight().size());
	}

	@Test
	public void testFinal() {
		BioCycReactionEntity entity = new BiocycReactionFactory("FOO", "BAR")
			.withId(10L)
			.withDescription("BAR Reaction")
			.withGibbs(123.456d)
			.withOrientation(Orientation.Reversible)
			.withName("FooBar")
			.withLeftEntity(
					new BiocycReactionStoichiometryFactory("Baz").buildLeft())
			.withRightEntity(
					new BiocycReactionStoichiometryFactory("Foz").buildRight())
			.withCrossreference(
					new BiocycReactionCrossreferenceFactory("BarDb", "BarRef")
							.withUrl("http://bar.foo")
							.withRelationship("identity")
							.build())
			.build();
		
		BiocycReactionTransform biocycReactionTransform = new BiocycReactionTransform("FooCyc");
		GraphReactionEntity centralReactionEntity = biocycReactionTransform.etlTransform(entity);

		System.out.println(centralReactionEntity);
		
		assertEquals("BAR Reaction", centralReactionEntity.getProperties().get("description"));
		assertEquals("FOO:BAR", centralReactionEntity.getProperties().get("entry"));
		assertEquals("FOO", centralReactionEntity.getProperties().get("source"));
//		assertEquals("123.456", centralReactionEntity.getProperties().get("gibbs"));
		assertEquals(1, centralReactionEntity.getLeft().size());
		assertEquals(1, centralReactionEntity.getRight().size());
		assertEquals(1, centralReactionEntity.getCrossreferences().size());
	}
}
