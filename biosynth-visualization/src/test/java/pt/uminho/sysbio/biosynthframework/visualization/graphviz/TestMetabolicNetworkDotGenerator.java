package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.Metabolite;
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
		DefaultReaction rxn2 = new DefaultReaction(1002L, "rxn2", "rxnase II", 
														 									 new String[] {"B", "X2"}, new double[]{}, 
														 									 new String[] {"C", "X1"}, new double[]{}, 
														 									 Orientation.LeftToRight);
    DefaultReaction rxn3 = new DefaultReaction(1003L, "rxn3", "rxnase III", 
                                               new String[] {"C", }, new double[]{}, 
                                               new String[] {"D", "Z"}, new double[]{}, 
                                               Orientation.LeftToRight);
    AliasMetaboliteToDotNodeTransformer<Metabolite> transformer = 
        new AliasMetaboliteToDotNodeTransformer<>();
    transformer.setAlias("X1", "ATP");
    transformer.setAlias("X2", "ADP");
    
		MetabolicNetworkDotGenerator generator = new MetabolicNetworkDotGenerator();
    generator.setMetaboliteTransformer(transformer);
		generator.addReaction(rxn1);
		generator.addReaction(rxn2);
		generator.addReaction(rxn3);
		String dotStr = generator.build();
		
//		try {
//		  String[] out = GraphVizGenerator.executeGraphviz(dotStr, "D:/opt/graphviz-2.38/bin/dot", "-Tpng", "-o", "D:/aaaaa.png");
//		  System.out.println(out[0]);
//		  System.out.println(out[1]);
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
		System.out.println(dotStr);
//		fail("Not yet implemented");
	}

}
