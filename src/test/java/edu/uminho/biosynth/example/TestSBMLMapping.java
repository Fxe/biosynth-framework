package edu.uminho.biosynth.example;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.GenericEntityDAO;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.optflux.ContainerLoader;
import edu.uminho.biosynth.optflux.parser.DefaultSbmlTransformerImpl;

public class TestSBMLMapping {

	private static SessionFactory sessionFactory;
	private static Map<String, MnxMetaboliteEntity> biggToMnxMap;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		sessionFactory.openSession();
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.getCurrentSession().close();
	}
	
	private void map(String file) {
		File sbml = new File(file);
		DefaultSbmlTransformerImpl transformer = new DefaultSbmlTransformerImpl();
//		System.out.println(transformer.normalizeMetaboliteId("M_lald_L_c"));
		
		ContainerLoader loader = new ContainerLoader(sbml, transformer);
		

		for (String cpdId : loader.getMetaboliteIdSet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(cpdId).append("\t");
			if (biggToMnxMap.containsKey(cpdId)) {
				sb.append(biggToMnxMap.get(cpdId).getEntry()).append("\t");
				for (MnxMetaboliteCrossReferenceEntity xref : biggToMnxMap.get(cpdId).getCrossReferences()) {
					if (xref.getRef().toLowerCase().equals("kegg"))
						sb.append(xref).append("\t");
				}
			} else {
				sb.append("NOTFOUND");
			}
			System.out.println(sb.toString());
		}
	}

	@Test
	public void test() {
		biggToMnxMap = new HashMap<> ();
		
		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		List<MnxMetaboliteEntity> metabolites = dao.findAll(MnxMetaboliteEntity.class);
		
		for (MnxMetaboliteEntity mnxCpd : metabolites) {
			for (MnxMetaboliteCrossReferenceEntity xref : mnxCpd.getCrossReferences()) {
				if (xref.getRef().toLowerCase().equals("bigg")) {
					if (biggToMnxMap.put(xref.getValue(), mnxCpd) != null) {
						System.out.println("COLLISION !!! " + mnxCpd);
					}
				}
			}
		}
		tx.commit();

		map("./src/main/resources/sbml/iND750.xml");
		System.out.println("####################################################################");
		System.out.println("####################################################################");
		System.out.println("####################################################################");
		map("./src/main/resources/sbml/recon1.xml");
//		System.out.println("####################################################################");
//		System.out.println("####################################################################");
//		System.out.println("####################################################################");
//		map("./src/main/resources/sbml/iSB619.xml");
		
//		ContainerLoader loader = new ContainerLoader();
		fail("Not yet implemented");
	}

}
