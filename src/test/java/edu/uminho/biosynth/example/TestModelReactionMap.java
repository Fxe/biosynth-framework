package edu.uminho.biosynth.example;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import edu.uminho.biosynth.core.components.DefaultGenericReaction;
import edu.uminho.biosynth.core.components.StoichiometryPair;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxReactionEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.service.MnxService;
import edu.uminho.biosynth.optflux.ContainerLoader;
import edu.uminho.biosynth.optflux.parser.DefaultSbmlTransformerImpl;
import edu.uminho.biosynth.optflux.parser.SbmlTransformer;

public class TestModelReactionMap {
	
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
	public void testMetabolitePairIdentification() throws IOException {
		File sbml = new File("./src/main/resources/SBML/iJR904.xml");
		SbmlTransformer transformer = new DefaultSbmlTransformerImpl();
		ContainerLoader loader = new ContainerLoader(sbml, transformer);
		IGenericEntityDao dao = new GenericEntityDaoImpl(sessionFactory);
		MnxService service = new MnxService(dao);
//		System.out.println(loader.getReactions().keySet());
//		for (String rxnSpecieId : loader.getReactions().keySet()) {
//			System.out.println(loader.getReactions().get(rxnSpecieId));
//		}
		
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		System.out.println(loader.getBiomassReaction());
		System.out.println(loader.getDrainReactions());
		for (String rxnSiD : loader.getNormalReactions()) {
			DefaultGenericReaction rxn = loader.getReactions().get(rxnSiD);
			
//			System.out.println(rxn);
			List<String> reactants = new ArrayList<> ();
			for (StoichiometryPair stoich : rxn.getReactant()) {
				String biggId = transformer.normalizeMetaboliteId(stoich.getCpdEntry());
				String mxnEntry = service.getMnxMetaboliteFromCrossReference(biggId).getEntry();
				reactants.add(mxnEntry);
			}
			List<String> products = new ArrayList<> ();
			for (StoichiometryPair stoich : rxn.getProduct()) {
				String biggId = transformer.normalizeMetaboliteId(stoich.getCpdEntry());
				String mxnEntry = service.getMnxMetaboliteFromCrossReference(biggId).getEntry();
				products.add(mxnEntry);
			}
//			System.out.println(reactants + " => " + products);
			List<String> res = service.findReactionByReactantsAndProducts(reactants, products);
//			System.out.println(products + " => " + reactants);
			res.addAll(service.findReactionByReactantsAndProducts(products, reactants));
//			System.out.println(res);
//			break;
			
			System.out.println(rxnSiD + "\t" + res);
		}
		
		tx.commit();
	}
	
	@Test
	public void testReactionCrossReferenceIdentification() throws IOException {
		File sbml = new File("./src/main/resources/SBML/iJR904.xml");
		SbmlTransformer transformer = new DefaultSbmlTransformerImpl();
		ContainerLoader loader = new ContainerLoader(sbml, transformer);
		IGenericEntityDao dao = new GenericEntityDaoImpl(sessionFactory);
		MnxService service = new MnxService(dao);
		
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		for (String rxnMid : loader.getNormalReactions()) {
			String biggId = transformer.normalizeReactionId(rxnMid);
			List<MnxReactionEntity> result = service.getReactionByCrossreference(biggId);
			if (result.size() > 1)  {			System.err.println("omg more than 1");
			} else if ( result.size() == 0) {	System.out.println(biggId + "\t" + "RXXXXX");
			} else {
				StringBuilder sb = new StringBuilder(biggId + "\t");
				MnxReactionEntity rxn = result.get(0);
				for (MnxReactionCrossReferenceEntity xref : rxn.getCrossReferences()) {
					if (xref.getRef().toLowerCase().equals("kegg")) {
						sb.append(xref.getValue()).append('\t');
					}
				}
				System.out.println(sb.toString());
			}
		}
		tx.commit();
	}

}
