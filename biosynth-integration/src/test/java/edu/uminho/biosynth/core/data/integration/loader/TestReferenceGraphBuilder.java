package edu.uminho.biosynth.core.data.integration.loader;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.service.BiggService;
import pt.uminho.sysbio.biosynthframework.core.data.service.BiocycService;
import pt.uminho.sysbio.biosynthframework.core.data.service.IMetaboliteService;
import pt.uminho.sysbio.biosynthframework.core.data.service.KeggService;
import pt.uminho.sysbio.biosynthframework.core.data.service.MnxService;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;
import edu.uminho.biosynth.core.data.integration.ReferenceGraphBuilder;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.loader.ReferenceLoader;
import edu.uminho.biosynth.core.data.integration.references.TransformBiggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.references.TransformBiocycMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.references.TransformKeggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.references.TransformMnxMetaboliteCrossReference;
import edu.uminho.biosynth.util.GraphDotUtil;
import edu.uminho.biosynth.util.ReferenceNodeVertexTransformer;

public class TestReferenceGraphBuilder {

//	private static SessionFactory sessionFactory;
//	private static IGenericDao dao;
//	private static Transaction tx;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("");
////		dao = TestConfig.dao;
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		sessionFactory.close();
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		tx = sessionFactory.getCurrentSession().beginTransaction();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		tx.commit();
//	}
//
//	@Test
//	public void testBuildEntireGraph() {
//		Logger.getLogger("").setLevel(Level.SEVERE);
//		
//		TransformKeggMetaboliteCrossReference keggXrefTrans = new TransformKeggMetaboliteCrossReference();
//		keggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		TransformBiocycMetaboliteCrossReference biocycXrefTrans =  new TransformBiocycMetaboliteCrossReference();
//		biocycXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		TransformBiggMetaboliteCrossReference biggXrefTrans = new TransformBiggMetaboliteCrossReference();
//		biggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		
//		IMetaboliteService<BioCycMetaboliteEntity> biocycService = new BiocycService(dao);
//		IMetaboliteService<BiggMetaboliteEntity> biggService = new BiggService(dao);
//		IMetaboliteService<KeggCompoundMetaboliteEntity> keggService = new KeggService(dao);
//		
//		ReferenceLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossreferenceEntity> biocycLoader = 
//				new ReferenceLoader<>(BioCycMetaboliteEntity.class, BioCycMetaboliteCrossreferenceEntity.class, 
//						biocycXrefTrans);
//		
//		biocycLoader.setService(biocycService);
//		biocycLoader.setReferenceTransformer(biocycXrefTrans);
//		
//		ReferenceLoader<BiggMetaboliteEntity, BiggMetaboliteCrossreferenceEntity> biggLoader = 
//				new ReferenceLoader<>(BiggMetaboliteEntity.class, BiggMetaboliteCrossreferenceEntity.class, biggXrefTrans);
//		
//		biggLoader.setService(biggService);
//		biggLoader.setReferenceTransformer(biggXrefTrans);
//		
//		ReferenceLoader<KeggCompoundMetaboliteEntity, KeggCompoundMetaboliteCrossreferenceEntity> keggLoader =
//				new ReferenceLoader<>(KeggCompoundMetaboliteEntity.class, KeggCompoundMetaboliteCrossreferenceEntity.class, keggXrefTrans);
//		keggLoader.setReferenceTransformer(keggXrefTrans);
//		keggLoader.setService(keggService);
//		
//		//SETUP BIGG DICTIONARY
//		List<BiggMetaboliteEntity> listBiggCpds = biggService.getAllMetabolites();
//		Map<String, Map<String, String>> biggIdToEntryDictionary = new HashMap<> ();
//		biggIdToEntryDictionary.put("BIGG", 
//				new HashMap<String, String> ());
//		for (BiggMetaboliteEntity biggCpd : listBiggCpds) {
//			biggIdToEntryDictionary.get("BIGG")
//				.put(biggCpd.getId().toString().toUpperCase(), biggCpd.getEntry().toUpperCase());
//		}
//		biocycXrefTrans.setValueTransformMap(biggIdToEntryDictionary);
//		
//		ReferenceGraphBuilder xrefBuilder = new ReferenceGraphBuilder();
//		xrefBuilder.getLoadersList().add(biocycLoader);
//		xrefBuilder.getLoadersList().add(biggLoader);
//		xrefBuilder.getLoadersList().add(keggLoader);
//		BinaryGraph<ReferenceNode, ReferenceLink> graph = xrefBuilder.omg();
//		
//		System.out.println(graph);
//		
//		try {
////			BioSynthUtilsIO.writeToFile(GraphDotUtil.graphToDot(graph, new ReferenceNodeVertexTransformer()), "D:/omg_big.dot");
//			
//		} catch (Exception e) {
//			
//		}
//		
////		assertEquals(3, graph.order());
////		assertEquals(6, graph.size());
//	}
//	
//	@Test
//	public void testBuildPartialGraph() {
//		Logger.getLogger("").setLevel(Level.SEVERE);
//		
//		TransformKeggMetaboliteCrossReference keggXrefTrans = new TransformKeggMetaboliteCrossReference();
//		keggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		TransformBiocycMetaboliteCrossReference biocycXrefTrans =  new TransformBiocycMetaboliteCrossReference();
//		biocycXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		TransformBiggMetaboliteCrossReference biggXrefTrans = new TransformBiggMetaboliteCrossReference();
//		biggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		TransformMnxMetaboliteCrossReference mnxXrefTrans = new TransformMnxMetaboliteCrossReference();
//		mnxXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		
//		IMetaboliteService<BioCycMetaboliteEntity> biocycService = new BiocycService(dao);
//		IMetaboliteService<BiggMetaboliteEntity> biggService = new BiggService(dao);
//		IMetaboliteService<KeggCompoundMetaboliteEntity> keggService = new KeggService(dao);
//		IMetaboliteService<MnxMetaboliteEntity> mxnService = new MnxService(dao);
//		
//		ReferenceLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossreferenceEntity> biocycLoader = 
//				new ReferenceLoader<>(BioCycMetaboliteEntity.class, BioCycMetaboliteCrossreferenceEntity.class, 
//						biocycXrefTrans);
//		biocycLoader.setService(biocycService);
//		biocycLoader.setReferenceTransformer(biocycXrefTrans);
//		
//		ReferenceLoader<BiggMetaboliteEntity, BiggMetaboliteCrossreferenceEntity> biggLoader = 
//				new ReferenceLoader<>(BiggMetaboliteEntity.class, BiggMetaboliteCrossreferenceEntity.class, biggXrefTrans);
//		biggLoader.setService(biggService);
//		biggLoader.setReferenceTransformer(biggXrefTrans);
//		
//		ReferenceLoader<KeggCompoundMetaboliteEntity, KeggCompoundMetaboliteCrossreferenceEntity> keggLoader =
//				new ReferenceLoader<>(KeggCompoundMetaboliteEntity.class, KeggCompoundMetaboliteCrossreferenceEntity.class, keggXrefTrans);
//		keggLoader.setReferenceTransformer(keggXrefTrans);
//		keggLoader.setService(keggService);
//		
//		ReferenceLoader<MnxMetaboliteEntity, MnxMetaboliteCrossreferenceEntity> mnxLoader =
//				new ReferenceLoader<>(MnxMetaboliteEntity.class, MnxMetaboliteCrossreferenceEntity.class, mnxXrefTrans);
//		mnxLoader.setReferenceTransformer(mnxXrefTrans);
//		mnxLoader.setService(mxnService);
//		
//		//SETUP BIGG DICTIONARY
//		List<BiggMetaboliteEntity> listBiggCpds = biggService.getAllMetabolites();
//		Map<String, Map<String, String>> biggIdToEntryDictionary = new HashMap<> ();
//		biggIdToEntryDictionary.put("BIGG", 
//				new HashMap<String, String> ());
//		for (BiggMetaboliteEntity biggCpd : listBiggCpds) {
//			biggIdToEntryDictionary.get("BIGG")
//				.put(biggCpd.getId().toString().toUpperCase(), biggCpd.getEntry().toUpperCase());
//		}
//		biocycXrefTrans.setValueTransformMap(biggIdToEntryDictionary);
//		
//		ReferenceGraphBuilder xrefBuilder = new ReferenceGraphBuilder();
//		xrefBuilder.getLoadersList().add(biocycLoader);
//		xrefBuilder.getLoadersList().add(biggLoader);
//		xrefBuilder.getLoadersList().add(keggLoader);
//		xrefBuilder.getLoadersList().add(mnxLoader);
//		BinaryGraph<ReferenceNode, ReferenceLink> graph 
//			= xrefBuilder.extractReferenceGraph(new String[] {
//					"h2o", "oh1",
//					"WATER", "OH", "CPD-12377",
//					"MNXM2",
//					"C00001", "C16844", "C01328"
//				});
//		//"acald", "btal", "BUTANAL", "VANILLIN"
//		
//		
//		
//		System.out.println(graph);
//		
//		try {
//			BioSynthUtilsIO.writeToFile(GraphDotUtil.graphToDot(graph, new ReferenceNodeVertexTransformer()), "D:/omg_small.dot");
//			
//		} catch (Exception e) {
//			
//		}
//		
////		assertEquals(3, graph.order());
////		assertEquals(6, graph.size());
//	}

}

