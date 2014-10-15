package pt.uminho.sysbio.biosynth.integration.transform;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.factory.DefaultMetaboliteFactory;

public class TestTransformBiggCentral {

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
	public void test() {
		
//		GenericMetabolite cpd = new DefaultMetaboliteFactory()
//			.withEntry("a")
//			.withDescription("abc")
		
		EtlTransform<BiggMetaboliteEntity, GraphMetaboliteEntity> transform;
		
		fail("Not yet implemented");
	}

}
