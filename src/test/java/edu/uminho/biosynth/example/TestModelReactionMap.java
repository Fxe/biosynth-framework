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
import edu.uminho.biosynth.core.data.io.dao.GenericEntityDAO;
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
	public void test() throws IOException {
		File sbml = new File("./src/main/resources/SBML/iJR904.xml");
		SbmlTransformer transformer = new DefaultSbmlTransformerImpl();
		ContainerLoader loader = new ContainerLoader(sbml, transformer);
		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
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

}
