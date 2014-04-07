package edu.uminho.biosynth.program;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import edu.uminho.biosynth.chemanalysis.domain.ChemicalSmiles;
import edu.uminho.biosynth.chemanalysis.openbabel.OpenBabelWrapper;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundPropertyLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.PropertyRelationshipType;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class ProgramCentralDataAnalysis {
	
	private static final Long[] failToConvert = { 171377L, 172059L, 175446L, 179124L, 179827L, 186699L, 198733L, 198837L, 234836L, 248303L, 259719L, 262488L,
		264295L, 264301L, 270286L, 270292L, 273342L, 287571L, 287627L, 287708L, 293246L, 312469L, 414518L, 434089L, 484755L, 
		488812L, 488817L, 526756L, 530823L, 534613L, 538705L, 538710L, 550938L};
	
	private static final String HBM_STRUCTURE_DB_CFG = "D:/home/data/java_config/chem_structure.cfg.xml";
	private static SessionFactory sessionFactory;
	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static GraphDatabaseService graphDatabaseService;
	
	public static void transferSmilesWithCan() {
		ExecutionEngine executionEngine = new ExecutionEngine(graphDatabaseService);
		org.hibernate.Transaction hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
		Transaction tx = graphDatabaseService.beginTx();
		
		@SuppressWarnings("unchecked")
		List<ChemicalSmiles> smilesList = sessionFactory.getCurrentSession().createCriteria(ChemicalSmiles.class).list();
		
		int i = 0;
		for (ChemicalSmiles smiles: smilesList) {
			Node smileNode = graphDatabaseService.getNodeById(smiles.getId());
			
			if (smiles.getCan() != null && smiles.getValid() && 
					(!smileNode.hasProperty("verified") || smileNode.getProperty("verified").equals(false))) {
				//Node Must be a smiles node and the smiles property must must
				if (!smileNode.hasLabel(CompoundPropertyLabel.SMILES) && !smileNode.getProperty("smiles").equals(smiles.getSmiles())) {
					graphDatabaseService.shutdown();
					throw new RuntimeException("smiles does not match with node syncronization failure");
				}
				System.out.println(smileNode);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("smiles", smiles.getSmiles());
				params.put("can", smiles.getCan());
				executionEngine.execute("MERGE (c:CanSMILES {can:{can}})", params);
				executionEngine.execute("MERGE (s:SMILES {smiles:{smiles}}) ON MATCH SET s.verified=true", params);
				executionEngine.execute("MATCH (s:SMILES {smiles:{smiles}}), (c:CanSMILES {can:{can}}) MERGE (s)<-[r:Isomorphic]->(c)", params);
				
				if (i % 50 == 0) {
					System.out.println(i);
					tx.success();
					tx.close();
					tx = graphDatabaseService.beginTx();
				}
				i++;
			}
		}
		
		hbm_tx.commit();
	}
	
	public static void main(String args[]) {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File(HBM_STRUCTURE_DB_CFG));
		graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		transferSmilesWithCan();
		graphDatabaseService.shutdown();
//		OpenBabelWrapper.initializeLibrary();
//		graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
//		ExecutionEngine executionEngine = new ExecutionEngine(graphDatabaseService);
//		
//		Transaction tx = graphDatabaseService.beginTx();
//
//		List<Node> inchiNodes = IteratorUtil.asList(GlobalGraphOperations.at(graphDatabaseService)
//									.getAllNodesWithLabel(CompoundPropertyLabel.InChI));
//		
//		Set<Long> blackList = new HashSet<> (Arrays.asList(failToConvert));
//		int i = 0;
//		for (Node inchiNode : inchiNodes) {
//			boolean skip = false;
//			if (blackList.contains(inchiNode.getId())) {
//				System.out.println("SKIP " + inchiNode.getId());
//				skip = true;
//			}
//			
//			String inchi = (String) inchiNode.getProperty("inchi");
//			Map<String, Object> params = new HashMap<String, Object>();
//			List<Relationship> relationShips = IteratorUtil.asList(inchiNode.getRelationships(PropertyRelationshipType.Isomorphic));
//			
//			if (!relationShips.isEmpty()) {
//				skip = true;
//			}
//			if (!skip) {
//				
//	//			if (params.get("inchi") != null) {
//	//				LOGGER.debug(String.format("Relationship (%s)-[HasInChI]->(%s)", cpd.getEntry(), cpd.getInchi()));
//	//				engine.execute("MERGE (i:InChI {inchi:{inchi}}) ON CREATE SET i.inchikey={inchikey} ON MATCH SET i.inchikey={inchikey}", params);
//	//				engine.execute("MATCH (cpd:KEGG:Compound {entry:{entry}}), (i:InChI {inchi:{inchi}, inchikey:{inchikey}}) MERGE (cpd)-[r:HasInChI]->(i)", params);
//	//			}
//				
//				System.out.println(inchiNode);
//				
//				String can = OpenBabelWrapper.convert(inchi, "inchi", "can");
//				
//				params.put("inchi", inchi);
//				params.put("can", can);
//				executionEngine.execute("MERGE (i:InChI {inchi:{inchi}}) ON MATCH SET i.verified=true", params);
//				executionEngine.execute("MERGE (c:CanSMILES {can:{can}})", params);
//				executionEngine.execute("MATCH (i:InChI {inchi:{inchi}}), (c:CanSMILES {can:{can}}) MERGE (i)<-[r:Isomorphic]->(c)", params);
//				
//				if (i % 50 == 0) {
//					System.out.println(i);
//					tx.success();
//					tx.close();
//					tx = graphDatabaseService.beginTx();
//				}
//				i++;
//			}
//		}
//		
//		tx.success();
//		tx.close();
//		
//		graphDatabaseService.shutdown();
	}
}
