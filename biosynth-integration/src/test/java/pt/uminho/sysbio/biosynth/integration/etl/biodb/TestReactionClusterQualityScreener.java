package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.etl.ReactionClusterQualityScreener;
import pt.uminho.sysbio.biosynth.integration.etl.ReactionQualityLabel;
import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.factory.DefaultReactionFactory;

public class TestReactionClusterQualityScreener {

	private ReactionClusterQualityScreener rcqs;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		YetAnotherReactionDao anotherReactionDao = new YetAnotherReactionDao(
//				integrationSet, graphDataService, integrationMetadataDao);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		rcqs = new ReactionClusterQualityScreener(null);
		rcqs.setProtonEntry("[H]");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmptyCluster() {
		List<GenericReaction> rxnList = new ArrayList<> ();
		
		Set<ReactionQualityLabel> rql = rcqs.something(rxnList);
		
		assertEquals(0, rql.size());
	}
	
//	@Test
	public void testSingleton() {
		List<GenericReaction> rxnList = new ArrayList<> ();
		DefaultReaction defaultReaction1 = new DefaultReaction();
		defaultReaction1.setEntry("ABC");
		
		rxnList.add(defaultReaction1);
		Set<ReactionQualityLabel> rql = rcqs.something(rxnList);
		System.out.println(rql);
		assertEquals(0, rql.size());
	}
	
//	@Test
	public void test_Proton_Label_With_Align() {
		List<GenericReaction> rxnList = new ArrayList<> ();
		DefaultReaction defaultReaction1 = new DefaultReactionFactory()
					.withEntry("RX-1")
					.withUnitLeftStoichiometry(new String[]{"A", "B"})
					.withUnitRightStoichiometry(new String[]{"C", "D", "[H]"})
					.build();
		DefaultReaction defaultReaction2 = new DefaultReactionFactory()
					.withEntry("RX-2")
					.withUnitLeftStoichiometry(new String[]{"A", "B"})
					.withUnitRightStoichiometry(new String[]{"C", "D"})
					.build();
		
		rxnList.add(defaultReaction1);
		rxnList.add(defaultReaction2);
		Set<ReactionQualityLabel> rql = rcqs.something(rxnList);
		
		System.out.println(rql);
		assertEquals(0, rql.size());
	}
	
//	@Test
	public void test_Proton_Label_Without_Aligned() {
		List<GenericReaction> rxnList = new ArrayList<> ();
		DefaultReaction defaultReaction1 = new DefaultReactionFactory()
				.withEntry("RX-1")
				.withUnitLeftStoichiometry(new String[]{"A", "B"})
				.withUnitRightStoichiometry(new String[]{"C", "D", "[H]"})
				.build();
		DefaultReaction defaultReaction2 = new DefaultReactionFactory()
				.withEntry("RX-2")
				.withUnitLeftStoichiometry(new String[]{"C", "D"})
				.withUnitRightStoichiometry(new String[]{"A", "B"})
				.build();
		
		rxnList.add(defaultReaction1);
		rxnList.add(defaultReaction2);
		Set<ReactionQualityLabel> rql = rcqs.something(rxnList);
		
		System.out.println(rql);
		assertEquals(0, rql.size());
	}
	
//	@Test
	public void test_Mismatch_Label_Without_Align() {
		List<GenericReaction> rxnList = new ArrayList<> ();
		DefaultReaction defaultReaction1 = new DefaultReactionFactory()
					.withEntry("RX-1")
					.withUnitLeftStoichiometry(new String[]{"A", "K"})
					.withUnitRightStoichiometry(new String[]{"C", "J", "[H]"})
					.build();
		DefaultReaction defaultReaction2 = new DefaultReactionFactory()
					.withEntry("RX-2")
					.withUnitLeftStoichiometry(new String[]{"C", "D", "[H]"})
					.withUnitRightStoichiometry(new String[]{"A", "B"})
					.build();
		
		rxnList.add(defaultReaction1);
		rxnList.add(defaultReaction2);
		Set<ReactionQualityLabel> rql = rcqs.something(rxnList);
		
		System.out.println(rql);
		assertEquals(0, rql.size());
	}
}
