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

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.HbmChimeraMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestIntegrationStatisticsServiceImpl {
	
	private static final String GRAPH_DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static final String HBM_CFG = "D:/home/data/java_config/hbm_mysql_chimera_meta.cfg.xml";
	private static final Long IID = 1L;
	
	private static IntegrationDataDao centralDataDao;
	private static ChimeraMetadataDao centralMetadataDao;
	private static IntegrationStatisticsService integrationStatisticsService;
	private static GraphDatabaseService graphDatabaseService;
	private static SessionFactory sessionFactory;
	private static IntegrationSet integrationSet;
	private static org.hibernate.Transaction hbmTx;
	private static org.neo4j.graphdb.Transaction neo4jTx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File(HBM_CFG));
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDatabaseConstraints(GRAPH_DB_PATH);
		HbmChimeraMetadataDaoImpl hbmChimeraMetadataDaoImpl = new HbmChimeraMetadataDaoImpl();
		hbmChimeraMetadataDaoImpl.setSessionFactory(sessionFactory);
		centralMetadataDao = hbmChimeraMetadataDaoImpl;
		Neo4jChimeraDataDaoImpl neo4jChimeraDataDaoImpl = new Neo4jChimeraDataDaoImpl();
		neo4jChimeraDataDaoImpl.setGraphDatabaseService(graphDatabaseService);
		centralDataDao = neo4jChimeraDataDaoImpl;
		
		IntegrationStatisticsServiceImpl impl = new IntegrationStatisticsServiceImpl();
		impl.setData(centralDataDao);
		impl.setMeta(centralMetadataDao);
		integrationStatisticsService = impl;
		
		hbmTx = sessionFactory.getCurrentSession().beginTransaction();
		integrationSet = centralMetadataDao.getIntegrationSet(IID);
		hbmTx.commit();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
		graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		hbmTx = sessionFactory.getCurrentSession().beginTransaction();
		neo4jTx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		hbmTx.commit();
		neo4jTx.success();
		neo4jTx.close();
	}

	@Test
	public void testCountIntegratedMetaboliteMembers() {
		int res = integrationStatisticsService.countIntegratedMetaboliteMembers(integrationSet);
		System.out.println(res);
	}
	
	@Test
	public void testCountIntegratedMetaboliteMembersByMajor() {
		Map<String, Integer> res = integrationStatisticsService.countIntegratedMetaboliteMembersByMajor(integrationSet);
		System.out.println(res);
	}
	
	@Test
	public void testCountTotalMetaboliteMembers() {
		int res = integrationStatisticsService.countTotalMetaboliteMembers();
		System.out.println(res);
	}
	
	@Test
	public void testCountTotalMetaboliteMembersByMajor() {
		Map<String, Integer> res = integrationStatisticsService.countTotalMetaboliteMembersByMajor();
		System.out.println(res);
	}
	
	@Test
	public void testCountIntegratedClusterWithDuplicateProperty() {
//		integrationStatisticsService.getIntegratedClusterPropertyFrequency(integrationSet, CompoundPropertyLabel.Name.toString());
	}
	
	@Test
	public void testCountIntegratedClusterDatabaseFreq() {
		for (Long cid : centralMetadataDao.getAllIntegratedClusterIds(integrationSet.getId())) {
			IntegratedCluster integratedCluster = centralMetadataDao.getIntegratedClusterById(cid);
			Map<String, Integer> mashup = integrationStatisticsService.getIntegratedClusterDatabaseFreq(integratedCluster);
			System.out.println(mashup);
		}
		
	}

}
