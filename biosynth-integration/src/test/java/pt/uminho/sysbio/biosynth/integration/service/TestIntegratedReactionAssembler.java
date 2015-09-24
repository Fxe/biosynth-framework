package pt.uminho.sysbio.biosynth.integration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedReactionEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphReactionDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.lostandfound.IntegratedReactionAssembler;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestIntegratedReactionAssembler {

	private static IntegratedReactionAssembler assembler;
	private static ReactionHeterogeneousDao<GraphReactionEntity> reactionDao;
	private static MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao;
	private static GraphDatabaseService NEO_DATA;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		NEO_DATA = HelperNeo4jConfigInitializer.initializeNeo4jDatabase("D:/tmp/data.db");
		reactionDao		= new Neo4jGraphReactionDaoImpl(NEO_DATA);
		metaboliteDao	= new Neo4jGraphMetaboliteDaoImpl(NEO_DATA);
		assembler = new IntegratedReactionAssembler();
		assembler.metaboliteUnificationMap = new HashMap<> ();
		
		tx = NEO_DATA.beginTx();
		
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.LigandCompound.toString(), "C00080").getId(), 0L);
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.MetaCyc.toString(), "META:PROTON").getId(), 0L);
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.BiGG.toString(), "h").getId(), 0L);
		
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.LigandCompound.toString(), "C00022").getId(), 1L);
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.MetaCyc.toString(), "META:PYRUVATE").getId(), 1L);
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.BiGG.toString(), "pyr").getId(), 1L);
		
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.LigandCompound.toString(), "C00074").getId(), 2L);
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.MetaCyc.toString(), "META:PHOSPHO-ENOL-PYRUVATE").getId(), 2L);
		assembler.metaboliteUnificationMap.put(metaboliteDao.getMetaboliteByEntry(MetaboliteMajorLabel.BiGG.toString(), "pep").getId(), 2L);

		tx.failure(); tx.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		NEO_DATA.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		tx = NEO_DATA.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		tx.failure();
		tx.close();
	}
	
	@Test
	public void test_aa_translocation() {
		
	}

	@Test
	public void test_pyk() {
		GraphReactionEntity r1 = reactionDao.getReactionByEntry(ReactionMajorLabel.LigandReaction.toString(), "R00200");
		GraphReactionEntity r2 = reactionDao.getReactionByEntry(ReactionMajorLabel.BiGG.toString(), "PYK");
		GraphReactionEntity r3 = reactionDao.getReactionByEntry(ReactionMajorLabel.MetaCyc.toString(), "META:PEPDEPHOS-RXN");
		Set<GraphReactionEntity> r = new HashSet<> ();
		r.add(r1);
		r.add(r2);
		r.add(r3);
		IntegratedReactionEntity reaction = assembler.assemble("J", r);
		IntegratedReactionEntity rxn = assembler.assemble(reaction);
		System.out.println(toString(rxn));
	}

	@Test
	public void test_1() {
//		GraphReactionEntity r1 = reactionDao.getReactionByEntry(ReactionMajorLabel.LigandReaction.toString(), "R00200");
//		GraphReactionEntity r2 = reactionDao.getReactionByEntry(ReactionMajorLabel.BiGG.toString(), "PYK");
		GraphReactionEntity r3 = reactionDao.getReactionByEntry(ReactionMajorLabel.MetaCyc.toString(), "META:RXN-9931");
		Set<GraphReactionEntity> r = new HashSet<> ();
//		r.add(r1);
//		r.add(r2);
		r.add(r3);
		IntegratedReactionEntity reaction = assembler.assemble("J", r);
		IntegratedReactionEntity rxn = assembler.assemble(reaction);
		System.out.println(toString(rxn));
	}
	
	public String equationBuilder(GenericReaction rxn) {
		List<String> left = new ArrayList<> (rxn.getLeftStoichiometry().keySet());
		List<String> right = new ArrayList<> (rxn.getRightStoichiometry().keySet());
		String eq = left.get(0);
		for (int i = 1 ; i < left.size(); i++) {
			String entry = left.get(i);
			double value = rxn.getLeftStoichiometry().get(entry);
			eq = eq + " + " + (value != 1.0 ? value + " " : "") + entry;
		}
		eq = eq + " <=> ";
		eq = eq + (rxn.getRightStoichiometry().get(right.get(0)) != 1.0 ? rxn.getRightStoichiometry().get(right.get(0)) + " " : "") + right.get(0);
		for (int i = 1 ; i < right.size(); i++) {
			String entry = right.get(i);
			double value = rxn.getRightStoichiometry().get(entry);
			eq = eq + " + " + (value != 1.0 ? value + " " : "") + entry;
		}
		return eq;
	}
	
	public String toString(IntegratedReactionEntity rxn) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Reaction     : " + rxn.getEntry()).append('\n');
		sb.append("Translocation: " + rxn.isTranslocation()).append('\n');
		sb.append("Equation     : " + equationBuilder(rxn));
//		sb.append(b)
		
		
		return sb.toString();
	}
}
