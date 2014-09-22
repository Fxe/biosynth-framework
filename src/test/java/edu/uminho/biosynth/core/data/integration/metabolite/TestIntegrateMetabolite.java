package edu.uminho.biosynth.core.data.integration.metabolite;

import static org.junit.Assert.*;

import java.util.ArrayList;
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

import pt.uminho.sysbio.metropolis.network.graph.algorithm.GraphCluster;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggCompoundMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.representation.basic.graph.BinaryGraph;
import edu.uminho.biosynth.core.data.integration.DefaultMetaboliteIntegrationStrategy;
import edu.uminho.biosynth.core.data.integration.IIntegrationStrategy;
import edu.uminho.biosynth.core.data.integration.ReferenceGraphBuilder;
import edu.uminho.biosynth.core.data.integration.components.IntegratedMetabolite;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
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

public class TestIntegrateMetabolite {

	private static SessionFactory sessionFactory;
	private static IGenericDao dao;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
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
		IMetaboliteService<BiggMetaboliteEntity> biggService2 = new BiggService(dao);
		biggService2.setServiceId("bigg personal");
		IMetaboliteService<KeggCompoundMetaboliteEntity> keggService = new KeggService(dao);
		IMetaboliteService<MnxMetaboliteEntity> mxnService = new MnxService(dao);
		
		ReferenceLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossreferenceEntity> biocycLoader = 
				new ReferenceLoader<>(BioCycMetaboliteEntity.class, BioCycMetaboliteCrossreferenceEntity.class, 
						biocycXrefTrans);
		biocycLoader.setService(biocycService);
		biocycLoader.setReferenceTransformer(biocycXrefTrans);
		
		ReferenceLoader<BiggMetaboliteEntity, BiggMetaboliteCrossreferenceEntity> biggLoader = 
				new ReferenceLoader<>(BiggMetaboliteEntity.class, BiggMetaboliteCrossreferenceEntity.class, biggXrefTrans);
		biggLoader.setService(biggService);
		biggLoader.setReferenceTransformer(biggXrefTrans);
		
		ReferenceLoader<BiggMetaboliteEntity, BiggMetaboliteCrossreferenceEntity> biggLoader2 = 
				new ReferenceLoader<>(BiggMetaboliteEntity.class, BiggMetaboliteCrossreferenceEntity.class, biggXrefTrans);
		biggLoader2.setService(biggService2);
		biggLoader2.setReferenceTransformer(biggXrefTrans);
		
		ReferenceLoader<KeggCompoundMetaboliteEntity, KeggCompoundMetaboliteCrossreferenceEntity> keggLoader =
				new ReferenceLoader<>(KeggCompoundMetaboliteEntity.class, KeggCompoundMetaboliteCrossreferenceEntity.class, keggXrefTrans);
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
//		xrefBuilder.getLoadersList().add(biggLoader2);
		BinaryGraph<ReferenceNode, ReferenceLink> graph 
			= xrefBuilder.extractReferenceGraph(new String[] {
					"h2o", "oh1",
					"WATER", "OH", "CPD-12377", "VANILLIN",
					"MNXM2",
					"C00001", "C16844", "C01328", "C00755"
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
		serviceMap.put(biggService2.getServiceId(), biggService2);
		for (String clusterId : clusterAlgorithm.getClustersToListOfVertex().keySet()) {
			System.out.println(clusterId + ":");
			System.out.println(clusterAlgorithm.getClustersToListOfVertex().get(clusterId));
			List<GenericMetabolite> cluster = new ArrayList<> ();
			for (ReferenceNode node : clusterAlgorithm.getClustersToListOfVertex().get(clusterId)) {
				String entry = node.getEntryTypePair().getFirst();
				for (String serviceId : node.getRelatedServiceIds()) {
					if (serviceMap.containsKey(serviceId)) {
						GenericMetabolite cpd = serviceMap.get(serviceId).getMetaboliteByEntry(entry);
						cluster.add(cpd);
						System.out.println(cpd.getEntry() + ":" + serviceId);
					} else {
						System.out.println("No Service For: " + entry);
					}
				}
			}
			
			IKeyGenerator<String> generator = new IKeyGenerator<String>() {
				@Override
				public String generateKey() {
					return "CPD-0";
				}

				@Override
				public void reset() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void generateFromLastElement(String key) {
					System.out.println("bzbzbbzbzb");
				}

				@Override
				public String getCurrentKey() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			
			IIntegrationStrategy<GenericMetabolite, IntegratedMetabolite> integrationStrategy =
					new DefaultMetaboliteIntegrationStrategy();
			((DefaultMetaboliteIntegrationStrategy)integrationStrategy).setEntryGenerator(generator);
			
			System.out.println(integrationStrategy.integrate(cluster));
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
