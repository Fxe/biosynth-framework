package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;

@Deprecated
public class TestKeggDrugTransform {

	private KeggDrugTransform transform;
	
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		transform = new KeggDrugTransform();
	}

	@After
	public void tearDown() throws Exception {
	}

	
	
	@Test
	public void test_D00001() {
		KeggDrugMetaboliteEntity entity = new KeggDrugMetaboliteEntity();
		transform.etlTransform(entity);
		
		assertEquals(true, true);
	}

}
