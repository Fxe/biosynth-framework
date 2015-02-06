package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;

public class TestNeo4jGraphMetaboliteDaoImpl {
	
	private final static String NEO_DATA_DB_PATH = "D:/tmp/testtt.db";
	
	private static GraphDatabaseService graphDatabaseService;
	private static Neo4jGraphMetaboliteDaoImpl neo4jGraphMetaboliteDaoImpl;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDataDatabaseConstraints(NEO_DATA_DB_PATH);
		neo4jGraphMetaboliteDaoImpl = new Neo4jGraphMetaboliteDaoImpl(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		tx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		tx.failure();
		tx.close();
	}

	@Test
	public void test_metabolite_entity_with_no_connected_link() {
		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
			.withEntry("CX9999")
			.withLabel(MetaboliteMajorLabel.LigandCompound)
			.withLabel(GlobalLabel.KEGG)
			.withProperty("formula", "CHO")
			.withProperty("name", "name1; name2;")
			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandCompound);
		
		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
		
		assertNotNull(metabolite.getId());
	}
	
	@Test
	public void test_metabolite_entity_with_crossreference() {
		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
			.withEntry("DX9998")
			.withLabel(MetaboliteMajorLabel.LigandDrug)
			.withLabel(GlobalLabel.KEGG)
			.withProperty("formula", "COSP")
			.withProperty("name", "name1; name2; name3 (uuu);")
			.withProperty("pro", "abc")
			.withLinkTo(new SomeNodeFactory()
				.withEntry("1-1-1")
				.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.CAS), 
						new SomeNodeFactory()
				.buildMetaboliteEdge(MetaboliteRelationshipType.has_crossreference_to))
			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
		
		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
		
		assertNotNull(metabolite.getId());
	}

	@Test
	public void test_metabolite_entity_with_properties() {
		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
			.withEntry("DX9997")
			.withLabel(MetaboliteMajorLabel.LigandDrug)
			.withLabel(GlobalLabel.KEGG)
			.withProperty("formula", "COSP")
			.withProperty("name", "name1; name2; name3 (uuu);")
			.withProperty("pro", "abc")
			.withLinkTo(new SomeNodeFactory()
				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Charge, 0), 
						new SomeNodeFactory()
				.buildMetaboliteEdge(MetaboliteRelationshipType.has_charge))
			.withLinkTo(new SomeNodeFactory()
				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name1"), 
						new SomeNodeFactory()
				.buildMetaboliteEdge(MetaboliteRelationshipType.has_name))
			.withLinkTo(new SomeNodeFactory()
				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name2"), 
						new SomeNodeFactory()
				.buildMetaboliteEdge(MetaboliteRelationshipType.has_name))
			.withLinkTo(new SomeNodeFactory()
				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.MolecularFormula, "COSP"), 
						new SomeNodeFactory()
				.buildMetaboliteEdge(MetaboliteRelationshipType.has_molecular_formula))
			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
		
		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
		
		assertNotNull(metabolite.getId());
	}
}
