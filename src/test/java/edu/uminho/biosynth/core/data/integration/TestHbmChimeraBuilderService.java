package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import edu.uminho.biosynth.core.data.integration.chimera.dao.HbmChimeraMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jIntegratedMetaboliteDao;
import edu.uminho.biosynth.core.data.integration.chimera.service.ChimeraDatabaseBuilderServiceImpl;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestHbmChimeraBuilderService {
	
	private static SessionFactory sessionFactory;
	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db";
	private static String DB_TARGET = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.target";
	private static GraphDatabaseService db;
	private static GraphDatabaseService db_target;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hbm_mysql_chimera_meta.cfg.xml");
		db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		db_target = new GraphDatabaseFactory().newEmbeddedDatabase( DB_TARGET );
		sessionFactory.openSession();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
		
		db.shutdown();
		db_target.shutdown();
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
		Neo4jIntegratedMetaboliteDao target = new Neo4jIntegratedMetaboliteDao();
		target.setGraphdb(db_target);
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		
		ChimeraDatabaseBuilderServiceImpl builder = new ChimeraDatabaseBuilderServiceImpl();
		builder.setData(data);
		builder.setMeta(meta);
		builder.setTarget(target);
		builder.setEntryGenerator(new IKeyGenerator<String>() {
			
			private String base = "IM";
			private int counter = 1;
			
			@Override
			public void reset() { this.counter = 1;}
			
			@Override
			public String generateKey() { return base + counter++;}
		});
		
		builder.changeIntegrationSet(1L);
		builder.generateIntegratedDatabase();
		
		assertEquals(true, true);
	}

}
