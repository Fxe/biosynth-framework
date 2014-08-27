package pt.uminho.sysbio.biosynth.integration.io.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.tooling.GlobalGraphOperations;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.factory.CentralMetaboliteFactory;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jCentralMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
public class TestNeo4jCentralMetaboliteDaoImpl {

	private static String DB_PATH = "D:/dev/null/test.db";
	private static GraphDatabaseService db;
	private static org.neo4j.graphdb.Transaction tx;
	private MetaboliteHeterogeneousDao<CentralMetaboliteEntity> metaboliteDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = HelperNeo4jConfigInitializer.initializeNeo4jDatabaseConstraints(DB_PATH);
		System.out.println("N:" + GlobalGraphOperations.at(db).getAllNodes());
		System.out.println("L:" + GlobalGraphOperations.at(db).getAllLabels());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		Neo4jCentralMetaboliteDaoImpl daoImpl = new Neo4jCentralMetaboliteDaoImpl();
		daoImpl.setGraphDatabaseService(db);
		metaboliteDao = daoImpl;
		tx = db.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		tx.success();
		tx.close();
	}

	@Test
	public void testSaveMetaboliteSuccess() {
		CentralMetaboliteEntity entity = new CentralMetaboliteFactory()
			.withEntry("neo4j_metabolite_1")
			.build();
		
		metaboliteDao.saveMetabolite("d1", entity);
		fail("Not yet implemented");
	}

}
