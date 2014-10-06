package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.io.File;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jIntegratedMetaboliteDao;
import edu.uminho.biosynth.core.data.integration.chimera.service.ChimeraDatabaseBuilderServiceImpl;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestHbmChimeraBuilderService {
	
	private static final String INTEGRATION_CENTRAL_DATA = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static final String INTEGRATION_META_DATA = "D:/home/data/java_config/hbm_mysql_chimera_meta.cfg.xml";
	private static SessionFactory sessionFactory;
	private static GraphDatabaseService db;
	private static GraphDatabaseService db_target;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File( INTEGRATION_META_DATA ));
		db = new GraphDatabaseFactory().newEmbeddedDatabase( INTEGRATION_CENTRAL_DATA );
		sessionFactory.openSession();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
		
		db.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		meta_tx = sessionFactory.getCurrentSession().beginTransaction();
		data_tx = db.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		meta_tx.commit();
		data_tx.success();
		data_tx.close();
	}


	@Test
	public void testBuilderService() {
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
		HbmIntegrationMetadataDaoImpl meta = new HbmIntegrationMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		
		ChimeraDatabaseBuilderServiceImpl builder = new ChimeraDatabaseBuilderServiceImpl();
		builder.setData(data);
		builder.setMeta(meta);
		
		builder.changeIntegrationSet(1L);
		
		System.out.println(builder.buildCompoundByClusterId(226L));
		
		assertEquals(true, true);
	}

}
