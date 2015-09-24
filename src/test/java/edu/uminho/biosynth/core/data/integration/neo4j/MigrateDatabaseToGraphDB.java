package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameBridge;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public class MigrateDatabaseToGraphDB {
	
	static final String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db/";
	public static SessionFactory sessionFactory_stga;
	private static IGenericDao dao_stga;
	private static Transaction tx_stga;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_staging_example_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory_stga.close();
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
	}

	@Test
	public void test() {
		
		GraphDatabaseService service = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		
		try (org.neo4j.graphdb.Transaction tx = service.beginTx(); ) {
			
			Label inchiLabel = DynamicLabel.label("InChI");
			for (MetaboliteInchiDim inchi : dao_stga.findAll(MetaboliteInchiDim.class)) {
				Node inchiNode = service.createNode(inchiLabel);
				System.out.println(inchi.getInchi());
				inchiNode.setProperty("inchi", inchi.getInchi());
				inchiNode.setProperty("key", inchi.getInchiKey());
				inchiNode.setProperty("standard", inchi.getStandard()==null?"null":inchi.getStandard());
				inchiNode.setProperty("version", inchi.getVersion()==null?"null":inchi.getVersion());
				inchiNode.setProperty("charge", inchi.getInchiCharge()==null?"null":inchi.getInchiCharge());
			}
			
			Label formulaLabel = DynamicLabel.label("Formula");
			for (MetaboliteFormulaDim formula : dao_stga.findAll(MetaboliteFormulaDim.class)) {
				Node node = service.createNode(formulaLabel);
				System.out.println(formula.getFormula());
				node.setProperty("formula", formula.getFormula());
			}
			
			Label nameLabel = DynamicLabel.label("Name");
			for (MetaboliteNameDim name : dao_stga.findAll(MetaboliteNameDim.class)) {
				Node node = service.createNode(nameLabel);
				System.out.println(name.getName());
				node.setProperty("name", name.getName());
			}
			
			Label compoundLabel = DynamicLabel.label("Compound");
			for (MetaboliteStga cpd : dao_stga.findAll(MetaboliteStga.class)) {
				
				Label dbLabel; 
				switch (cpd.getMetaboliteServiceDim().getDbType().toLowerCase()) {
				case "bigg":
						dbLabel = DynamicLabel.label("BiGG");
					break;
				case "biocyc":
						dbLabel = DynamicLabel.label("BioCyc");
					break;
				case "mnx":
					dbLabel = DynamicLabel.label("MetaNetX");
					break;
				default:
						dbLabel = DynamicLabel.label("NA");
					break;
				}
				Node node = service.createNode(dbLabel, compoundLabel);
				if (node.hasLabel(DynamicLabel.label("BioCyc"))) {
					node.addLabel(DynamicLabel.label("MetaCyc"));
				}
				node.setProperty("numericKey", cpd.getNumeryKey());
				node.setProperty("textKey", cpd.getTextKey());
				node.setProperty("formula", cpd.getFormula());
				ResourceIterable<Node> nodes;
				nodes = service.findNodesByLabelAndProperty(
						DynamicLabel.label("InChI"), "inchi", cpd.getMetaboliteInchiDim().getInchi());
				for (Node n : nodes) {
					node.createRelationshipTo(n, DynamicRelationshipType.withName("Has_InChI"));
				}
				nodes = service.findNodesByLabelAndProperty(
						DynamicLabel.label("Formula"), "formula", cpd.getMetaboliteFormulaDim().getFormula());
				for (Node n : nodes) {
					node.createRelationshipTo(n, DynamicRelationshipType.withName("Has_Formula"));
				}
				
				for (MetaboliteNameBridge bridge : cpd.getMetaboliteNameGroupDim().getMetaboliteNameBridges()) {
					nodes = service.findNodesByLabelAndProperty(
							DynamicLabel.label("Name"), "name", bridge.getMetaboliteNameDim().getName());
					for (Node n : nodes) {
						node.createRelationshipTo(n, DynamicRelationshipType.withName("Has_Name"));
					}
				}

			}
			
			
			
//			Node i1 = service.createNode();
//			Node i2 = service.createNode();
//
//			i1.setProperty("InChI", "InChI=");
//			i2.setProperty("InChI", "InChI=");
			tx.success();
		}
		
		service.shutdown();
	}

}
