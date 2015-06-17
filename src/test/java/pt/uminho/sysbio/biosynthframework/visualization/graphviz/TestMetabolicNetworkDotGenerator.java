package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.Orientation;

public class TestMetabolicNetworkDotGenerator {

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
	public void testSingleNodeGraph() {
		DefaultReaction rxn1 = new DefaultReaction(1001L, "rxn1", "rxnase I", 
																							 new String[] {"A", "X1"}, new double[]{}, 
																							 new String[] {"B", "X2"}, new double[]{}, 
																							 Orientation.LeftToRight);
		DefaultReaction rxn2 = new DefaultReaction(1002L, "rxn1", "rxnase II", 
														 									 new String[] {"B", "X2"}, new double[]{}, 
														 									 new String[] {"C", "X1"}, new double[]{}, 
														 									 Orientation.LeftToRight);
		MetabolicNetworkDotGenerator generator = new MetabolicNetworkDotGenerator();
		generator.addReaction(rxn1);
		generator.addReaction(rxn2);
		String dotStr = generator.build();
		System.out.println(dotStr);
		fail("Not yet implemented");
	}

}
