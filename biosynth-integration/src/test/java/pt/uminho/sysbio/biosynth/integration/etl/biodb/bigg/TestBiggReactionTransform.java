package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionRightEntity;

public class TestBiggReactionTransform {

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
	public void test_DHCR242r() {
		BiggReactionEntity rxn = new BiggReactionEntity();
		rxn.setEntry("DHCR242r");
		rxn.setName("24-dehydrocholesterol reductase [Precursor]");
		List<BiggReactionLeftEntity> left = new ArrayList<> ();
		BiggReactionLeftEntity l1 = new BiggReactionLeftEntity();
		l1.setCpdEntry("chlstol");
		l1.setCompartment("r");
		l1.setStoichiometry(1.0);
		left.add(l1);
		rxn.setLeft(left);
		List<BiggReactionRightEntity> right = new ArrayList<> ();
		rxn.setRight(right);
		
		BiggReactionTransform biggReactionTransform = new BiggReactionTransform();
		
		
		GraphReactionEntity graphReactionEntity = biggReactionTransform.etlTransform(rxn);
		
		System.out.println(graphReactionEntity);
		
		assertEquals("DHCR242r", graphReactionEntity.getEntry());
		assertEquals(MetaboliteMajorLabel.BiGG.toString(), graphReactionEntity.getMajorLabel());
		assertEquals(Orientation.LeftToRight, graphReactionEntity.getOrientation());
	}

}
