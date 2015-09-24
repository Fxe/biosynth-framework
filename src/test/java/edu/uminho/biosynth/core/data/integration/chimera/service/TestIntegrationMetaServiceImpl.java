package edu.uminho.biosynth.core.data.integration.chimera.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestIntegrationMetaServiceImpl {

	private static final String GRAPH_DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static final String HBM_CFG = "D:/home/data/java_config/hbm_mysql_chimera_meta.cfg.xml";
	private static IntegrationMetaService integrationMetaService;
	private static IntegrationDataDao data;
	private static IntegrationMetadataDao meta;
	private static SessionFactory sessionFactory;
	private static GraphDatabaseService graphDatabaseService;
	
	private static org.neo4j.graphdb.Transaction data_tx;
	private static org.hibernate.Transaction meta_tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(
				new File(HBM_CFG));
		graphDatabaseService = HelperNeo4jConfigInitializer
				.initializeNeo4jDataDatabaseConstraints(GRAPH_DB_PATH);
		HbmIntegrationMetadataDaoImpl chimeraMetadataDaoImpl = 
				new HbmIntegrationMetadataDaoImpl();
		chimeraMetadataDaoImpl.setSessionFactory(sessionFactory);
		
		Neo4jChimeraDataDaoImpl chimeraDataDaoImpl =
				new Neo4jChimeraDataDaoImpl();
		chimeraDataDaoImpl.setGraphDatabaseService(graphDatabaseService);
		
		data = chimeraDataDaoImpl;
		meta = chimeraMetadataDaoImpl;
		
		IntegrationMetaServiceImpl integrationMetaServiceImpl = 
				new IntegrationMetaServiceImpl();
		integrationMetaServiceImpl.setData(data);
		integrationMetaServiceImpl.setMeta(meta);
		
		integrationMetaService = integrationMetaServiceImpl;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
		graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		meta_tx = sessionFactory.getCurrentSession().beginTransaction();
		data_tx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		meta_tx.rollback();
		data_tx.success();
	}

	@Test
	public void test() {
		IntegrationSet integrationSet = meta.getIntegrationSet(1L);
		Map<String, Integer> warningsCount = 
				integrationMetaService.countWarnings(integrationSet);

		System.out.println(warningsCount);
	
	}

}
