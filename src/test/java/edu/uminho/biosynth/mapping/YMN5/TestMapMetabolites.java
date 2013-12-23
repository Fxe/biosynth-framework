package edu.uminho.biosynth.mapping.YMN5;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.util.BioSynthUtilsIO;

public class TestMapMetabolites {
private static SessionFactory sessionFactory;
	
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
	
	@Test
	public void testMapMetabolites() throws Exception {
		IGenericEntityDao dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		String rawFileString = BioSynthUtilsIO.readFromFile("./src/main/resources/MISC/YMN_5_species.tsv");
		Map<String, GenericMetabolite> metabolites = new HashMap<> (); 
		String[] lines = rawFileString.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String[] values = lines[i].split("\t");
			if (values[1].equals("SBO:0000247")) {
				GenericMetabolite cpd = new GenericMetabolite();
				
				cpd.setEntry(values[0]);
				cpd.setName(values[2]);
	//			cpd.setFormula(values[5]);
				if (values.length > 3) cpd.setDescription(values[3]);
				if ( !metabolites.keySet().contains(values[0])) {
					metabolites.put(values[0], cpd);
				}
//				System.out.println(cpd.getEntry() + "\t" + cpd.getDescription());
			}
		}
		
		for (String key : metabolites.keySet()) {
			GenericMetabolite cpd = metabolites.get(key);
			String[] miriam = cpd.getDescription().split(":");
			StringBuilder sb = new StringBuilder();
			if (miriam.length > 1) {
				String value = miriam[miriam.length - 1];
				String ref = miriam[miriam.length - 2];
				List<MnxMetaboliteCrossReferenceEntity> res = dao.criteria(MnxMetaboliteCrossReferenceEntity.class, 
						Restrictions.and( Restrictions.eq("value", value), Restrictions.eq("ref", ref)));
				if (res.size() > 1) {
					System.err.println("ERROR > 1: " + ref + ":" + value);
				} else if ( res.size() < 1 ) {
					System.err.println("NOT FOUND: " + ref + ":" + value);
				} else {
					MnxMetaboliteCrossReferenceEntity xref = res.get(0);
					for (MnxMetaboliteCrossReferenceEntity xr : xref.getMnxMetaboliteEntity().getCrossReferences()) {
						if (xr.getRef().equals("kegg")) {
							sb.append(xr).append('\t');
						}
					}
				}
			}
			
			System.out.println(key + "\t" + metabolites.get(key).getDescription() + "\t" + sb.toString());
			
		}
		
		tx.commit();
		fail("Not yet implemented");
	}
}
