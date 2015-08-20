package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;

public class TestDotGraphFactory {

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
		GenericMetabolite metabolite1 = new GenericMetabolite();
		metabolite1.setEntry("cpd-0000A");
		metabolite1.setName("some substrate");
		GenericMetabolite metabolite2 = new GenericMetabolite();
		metabolite2.setEntry("cpd-0000B");
		GenericMetabolite metabolite3 = new GenericMetabolite();
		metabolite3.setEntry("cpd-0000C");
		metabolite3.setName("some product");
//		GenericMetabolite metabolite4 = new GenericMetabolite();
//		metabolite4.setEntry("cpd-0000D");
//		metabolite4.setName("some product");
//		GenericMetabolite metabolite5 = new GenericMetabolite();
//		metabolite5.setEntry("cpd-0000E");
//		metabolite5.setName("some product");
		
		GenericReaction reaction1 = new GenericReaction();
		reaction1.setEntry("rxn-0000A");
		reaction1.getReactantStoichiometry().put("cpd-0000A", 1.0d);
		reaction1.getProductStoichiometry().put("cpd-0000B", 1.0d);
		reaction1.setOrientation(Orientation.LeftToRight);
//		reaction1.set
		GenericReaction reaction2 = new GenericReaction();
		reaction2.setEntry("rxn-0000B");
		reaction2.getReactantStoichiometry().put("cpd-0000B", 1.0d);
		reaction2.getProductStoichiometry().put("cpd-0000C", 1.0d);
		reaction2.setOrientation(Orientation.LeftToRight);
		
		BinaryGraph<DotNode, DotEdge> graph =
				new DotGraphFactory()
				.withName("some test pathway")
				.withMetabolite(metabolite1)
				.withMetabolite(metabolite2)
				.withMetabolite(metabolite3)
//				.withMetabolite(metabolite4)
//				.withMetabolite(metabolite5)
				.withReaction(reaction1)
				.withReaction(reaction2)
				.build();
				
		System.out.println(graph);
		
		assertEquals(5, graph.order());
		assertEquals(3, graph.size());
	}

	
	@Test
	public void test_duplicateEdge() {
		GenericMetabolite metabolite1 = new GenericMetabolite();
		metabolite1.setEntry("cpd-0000A");
		metabolite1.setName("some substrate");
		GenericMetabolite metabolite2 = new GenericMetabolite();
		metabolite2.setEntry("cpd-0000B");
		metabolite2.setName("some product");
		GenericReaction reaction1 = new GenericReaction();
		reaction1.setEntry("rxn-0000A");
		reaction1.getReactantStoichiometry().put("cpd-0000A", 1.0d);
		reaction1.getProductStoichiometry().put("cpd-0000B", 1.0d);
		reaction1.setOrientation(Orientation.LeftToRight);
		GenericReaction reaction2 = new GenericReaction();
		reaction2.setEntry("rxn-0000B");
		reaction2.getReactantStoichiometry().put("cpd-0000A", 1.0d);
		reaction2.getProductStoichiometry().put("cpd-0000B", 1.0d);
		reaction2.setOrientation(Orientation.LeftToRight);
		
		BinaryGraph<DotNode, DotEdge> graph =
				new DotGraphFactory()
				.withName("some test pathway")
				.withMetabolite(metabolite1)
				.withMetabolite(metabolite2)
				.withReactionInPathway(reaction1, "pwy2")
				.withReactionInPathway(reaction1, "pwy1")
				.withPathwayColor("pwy2", "blue")
				.build();
		
		System.out.println(graph);
		
		String dot = GraphVizGenerator.dotGraphToDot(graph);
		
		System.out.println(dot);
	}
	
	@Test
	public void omg() {
		System.out.println(StringUtils.countMatches("1.1.2", "."));
		
	}
}
