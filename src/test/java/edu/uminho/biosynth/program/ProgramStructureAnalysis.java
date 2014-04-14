package edu.uminho.biosynth.program;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import edu.uminho.biosynth.chemanalysis.domain.ChemicalSmiles;
import edu.uminho.biosynth.chemanalysis.domain.ChemicalStructureCore;
import edu.uminho.biosynth.chemanalysis.openbabel.OpenBabelProcessWrapper;
import edu.uminho.biosynth.chemanalysis.openbabel.OpenBabelWrapper;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundPropertyLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.PropertyRelationshipType;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class ProgramStructureAnalysis {

	private static final String HBM_STRUCTURE_DB_CFG = "D:/home/data/java_config/chem_structure.cfg.xml";
	private static SessionFactory sessionFactory;
	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	
//	private static Map<Integer, ChemicalStructureCore> idToChemicalStructureCore = new HashMap<> ();
//	private static Map<String, Integer> inchiToChemicalStructureCoreId = new HashMap<> ();
	
	
	public static void loadCentralDataContent() {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx();
		List<Node> inchiNodes = IteratorUtil.asList(GlobalGraphOperations.at(graphDatabaseService)
				.getAllNodesWithLabel(CompoundPropertyLabel.InChI));
		
		Transaction hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
		
		int i = 0;
		for (Node inchiNode: inchiNodes) {
			ChemicalStructureCore core = new ChemicalStructureCore();
			core.setInchiNodeId(inchiNode.getId());
			core.setInchi((String) inchiNode.getProperty("inchi"));
			for (Relationship r: IteratorUtil.asList(inchiNode.getRelationships(PropertyRelationshipType.Isomorphic))) {
				String can = null;
				Long canId = null;
				if (r.getEndNode().hasLabel(CompoundPropertyLabel.CanSMILES)) {
					can = (String) r.getEndNode().getProperty("can");
					canId = r.getEndNode().getId();
					System.out.println("getEndNode");
				} else if (r.getStartNode().hasLabel(CompoundPropertyLabel.CanSMILES)) {
					can = (String) r.getStartNode().getProperty("can");
					canId = r.getStartNode().getId();
					System.out.println("getStartNode");
				}
				core.setCanNodeId(canId);
				core.setCan(can);
			}
			sessionFactory.getCurrentSession().save(core);
			
			if (i % 200 == 0) {
				hbm_tx.commit();
				hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
			}
			i++;
		}
		hbm_tx.commit();
		
		tx.success();
		tx.close();
		graphDatabaseService.shutdown();
	}
	
	public static void loadCentralDataSmilesContent() {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		org.neo4j.graphdb.Transaction tx = graphDatabaseService.beginTx();
		List<Node> smilesNodes = IteratorUtil.asList(GlobalGraphOperations.at(graphDatabaseService)
				.getAllNodesWithLabel(CompoundPropertyLabel.SMILES));
		
		Transaction hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
		
		int i = 0;
		for (Node smilesNode: smilesNodes) {
			ChemicalSmiles smiles = new ChemicalSmiles();
			smiles.setId(smilesNode.getId());
			smiles.setSmiles((String) smilesNode.getProperty("smiles"));

			sessionFactory.getCurrentSession().save(smiles);
			
			if (i % 500 == 0) {
				System.out.println(i);
				hbm_tx.commit();
				hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
			}
			i++;
		}
		hbm_tx.commit();
		
		tx.success();
		tx.close();
		graphDatabaseService.shutdown();
	}
	
	public static void generateCanonicalSmilesFromSmiles() throws IOException {
		OpenBabelWrapper.initializeLibrary();
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		int i = 0;
		@SuppressWarnings("unchecked")
		List<ChemicalSmiles> smilesList = sessionFactory.getCurrentSession().createCriteria(ChemicalSmiles.class).list();
		for (ChemicalSmiles smiles: smilesList) {
			if (smiles.getCan() == null && smiles.getValid() == null && smiles.getSmiles().indexOf('{') < 0) {
				System.out.println(smiles.getId() + " " + smiles.getSmiles());
				String can = OpenBabelWrapper.convertSmilesToCannonicalSmiles(smiles.getSmiles());
//				String can = OpenBabelProcessWrapper.convertSmilesToCannonicalSmiles(smiles.getSmiles());
				System.out.println(can);
				if (can != null && can.trim().isEmpty()) can = null;
				smiles.setCan(can);
				if (can == null) {
					smiles.setValid(false);
				} else {
					smiles.setValid(true);
				}

				sessionFactory.getCurrentSession().update(smiles);
				if (i % 100 == 0) {
					System.out.println(i);
					tx.commit();
					tx = sessionFactory.getCurrentSession().beginTransaction();
				}
				i++;
			}
		}
		
		tx.commit();
	}
	
	public static void generateCanonicalSmilesFromInchis() throws IOException {
		OpenBabelWrapper.initializeLibrary();
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		int i = 0;
		@SuppressWarnings("unchecked")
		List<ChemicalStructureCore> inchisList = sessionFactory.getCurrentSession().createCriteria(ChemicalStructureCore.class).list();
		for (ChemicalStructureCore core: inchisList) {
			if (core.getCan() == null && core.getValid() == null) {
				System.out.println(core.getInchiNodeId() + " " + core.getInchi());
				String can = OpenBabelProcessWrapper.convertInchiToCannonicalSmiles(core.getInchi());
//				String can = OpenBabelWrapper.convert(core.getInchi(), "inchi", "can");
				System.out.println(can);
				if (can != null && can.trim().isEmpty()) can = null;
				core.setCan(can);
				if (can == null) {
					core.setValid(false);
				} else {
					core.setValid(true);
				}
				
				sessionFactory.getCurrentSession().update(core);
				if (i % 10 == 0) {
					System.out.println(i);
					tx.commit();
					tx = sessionFactory.getCurrentSession().beginTransaction();
				}
				i++;
			}
		}
		
		tx.commit();
	}
	
	
	public static void main(String args[]) throws IOException {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File(HBM_STRUCTURE_DB_CFG));
		sessionFactory.openSession();
		
//		loadCentralDataSmilesContent();
		
		generateCanonicalSmilesFromInchis();
		
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
	}
}
