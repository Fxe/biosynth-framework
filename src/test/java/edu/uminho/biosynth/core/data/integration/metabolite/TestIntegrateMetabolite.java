package edu.uminho.biosynth.core.data.integration.metabolite;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.algorithm.graph.GraphCluster;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.loader.ReferenceGraphBuilder;
import edu.uminho.biosynth.core.data.integration.loader.ReferenceLoader;
import edu.uminho.biosynth.core.data.integration.references.TransformBiggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.references.TransformBiocycMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.references.TransformKeggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.references.TransformMnxMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.service.BiggService;
import edu.uminho.biosynth.core.data.service.BiocycService;
import edu.uminho.biosynth.core.data.service.IMetaboliteService;
import edu.uminho.biosynth.core.data.service.KeggService;
import edu.uminho.biosynth.core.data.service.MnxService;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestIntegrateMetabolite {

	private static SessionFactory sessionFactory;
	private static IGenericDao dao;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestConfig.initializeHibernateSession();
		sessionFactory = TestConfig.sessionFactory;
		dao = TestConfig.dao;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TestConfig.closeHibernateSession();
	}

	@Before
	public void setUp() throws Exception {
		tx = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx.commit();
	}

	@Test
	public void test() {
		Logger.getLogger("").setLevel(Level.SEVERE);
		
		TransformKeggMetaboliteCrossReference keggXrefTrans = new TransformKeggMetaboliteCrossReference();
		keggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		TransformBiocycMetaboliteCrossReference biocycXrefTrans =  new TransformBiocycMetaboliteCrossReference();
		biocycXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		TransformBiggMetaboliteCrossReference biggXrefTrans = new TransformBiggMetaboliteCrossReference();
		biggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		TransformMnxMetaboliteCrossReference mnxXrefTrans = new TransformMnxMetaboliteCrossReference();
		mnxXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		
		IMetaboliteService<BioCycMetaboliteEntity> biocycService = new BiocycService(dao);
		IMetaboliteService<BiggMetaboliteEntity> biggService = new BiggService(dao);
		IMetaboliteService<KeggMetaboliteEntity> keggService = new KeggService(dao);
		IMetaboliteService<MnxMetaboliteEntity> mxnService = new MnxService(dao);
		
		ReferenceLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossReferenceEntity> biocycLoader = 
				new ReferenceLoader<>(BioCycMetaboliteEntity.class, BioCycMetaboliteCrossReferenceEntity.class, 
						biocycXrefTrans);
		biocycLoader.setService(biocycService);
		biocycLoader.setReferenceTransformer(biocycXrefTrans);
		
		ReferenceLoader<BiggMetaboliteEntity, BiggMetaboliteCrossReferenceEntity> biggLoader = 
				new ReferenceLoader<>(BiggMetaboliteEntity.class, BiggMetaboliteCrossReferenceEntity.class, biggXrefTrans);
		biggLoader.setService(biggService);
		biggLoader.setReferenceTransformer(biggXrefTrans);
		
		ReferenceLoader<KeggMetaboliteEntity, KeggMetaboliteCrossReferenceEntity> keggLoader =
				new ReferenceLoader<>(KeggMetaboliteEntity.class, KeggMetaboliteCrossReferenceEntity.class, keggXrefTrans);
		keggLoader.setReferenceTransformer(keggXrefTrans);
		keggLoader.setService(keggService);
		
		ReferenceLoader<MnxMetaboliteEntity, MnxMetaboliteCrossReferenceEntity> mnxLoader =
				new ReferenceLoader<>(MnxMetaboliteEntity.class, MnxMetaboliteCrossReferenceEntity.class, mnxXrefTrans);
		mnxLoader.setReferenceTransformer(mnxXrefTrans);
		mnxLoader.setService(mxnService);
		
		//SETUP BIGG DICTIONARY
		List<BiggMetaboliteEntity> listBiggCpds = biggService.getAllMetabolites();
		Map<String, Map<String, String>> biggIdToEntryDictionary = new HashMap<> ();
		biggIdToEntryDictionary.put("BIGG", 
				new HashMap<String, String> ());
		for (BiggMetaboliteEntity biggCpd : listBiggCpds) {
			biggIdToEntryDictionary.get("BIGG")
				.put(biggCpd.getId().toString().toUpperCase(), biggCpd.getEntry().toUpperCase());
		}
		biocycXrefTrans.setValueTransformMap(biggIdToEntryDictionary);
		
		ReferenceGraphBuilder xrefBuilder = new ReferenceGraphBuilder();
		xrefBuilder.getLoadersList().add(biocycLoader);
		xrefBuilder.getLoadersList().add(biggLoader);
		xrefBuilder.getLoadersList().add(keggLoader);
		xrefBuilder.getLoadersList().add(mnxLoader);
		IBinaryGraph<ReferenceNode, ReferenceLink> graph 
			= xrefBuilder.extractReferenceGraph(new String[] {
					"h2o", "oh1",
					"WATER", "OH", "CPD-12377",
					"MNXM2",
					"C00001", "C16844", "C01328"
				});
		
		
		System.out.println(graph);
		
		GraphCluster<ReferenceNode, ReferenceLink> clusterAlgorithm = new GraphCluster<>();
		clusterAlgorithm.generateClusters(graph);
		
		
		// should have a service manager and the reference loader would ask the service manager instead
		Map<String, IMetaboliteService<?>> serviceMap = new HashMap<> ();
		serviceMap.put(biocycService.getServiceId(), biocycService);
		serviceMap.put(keggService.getServiceId(), keggService);
		serviceMap.put(mxnService.getServiceId(), mxnService);
		serviceMap.put(biggService.getServiceId(), biggService);
		
		for (String clusterId : clusterAlgorithm.getClustersToListOfVertex().keySet()) {
			System.out.println(clusterId + ":");
			for (ReferenceNode node : clusterAlgorithm.getClustersToListOfVertex().get(clusterId)) {
				String entry = node.getEntry();
				for (String serviceId : node.getRelatedServiceIds()) {
					if (serviceMap.containsKey(serviceId)) {
						Object obj = serviceMap.get(serviceId).getMetaboliteByEntry(entry);
						System.out.println(obj);
					} else {
						System.out.println("No Service For: " + entry);
					}
				}
			}
		}
		
//		System.out.println(clusterAlgorithm.getClustersToListOfVertex().keySet());
//		for (String clusterId : clusterAlgorithm.getClustersToListOfVertex().keySet()) {
//			System.out.println(clusterId + ":");
//			for (ReferenceNode node : clusterAlgorithm.getClustersToListOfVertex().get(clusterId)) {
//				System.out.println(node.getEntryTypePair().getFirst() + "\t" + node.getEntryTypePair().getSecond().getName());
//			}
//		}
		
		assertEquals(60, graph.size());
	}

}
