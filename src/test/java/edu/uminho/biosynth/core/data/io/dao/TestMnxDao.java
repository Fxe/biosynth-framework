package edu.uminho.biosynth.core.data.io.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.mnx.MnxReactionEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionProductEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionReactantEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestMnxDao {

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
	public void testLoadMetabolite() {
		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		MnxMetaboliteEntity cpd = dao.find(MnxMetaboliteEntity.class, 58);
		System.out.println(cpd);
		tx.commit();
	}
	
	@Test
	public void testLoadReaction() {
		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		MnxReactionEntity rxn = dao.find(MnxReactionEntity.class, 585);
		System.out.println(rxn);
		tx.commit();
	}
	
	private List<String> consumes(String cpd) {
//		System.out.println("consumers of: " + cpd);
		List<String> consumers = new ArrayList<> ();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MnxReactionReactantEntity.class)
				.add(Restrictions.eq("cpdEntry", cpd));
			
		@SuppressWarnings("unchecked")
		List<MnxReactionReactantEntity> result = (List<MnxReactionReactantEntity>) criteria.list();
		for (MnxReactionReactantEntity reactant : result) {
			consumers.add(reactant.getMnxReactionEntity().getEntry());
		}
		return consumers;
	}
	
	private List<String> produces(String cpd) {
//		System.out.println("producers of: " + cpd);
		List<String> producers = new ArrayList<> ();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MnxReactionProductEntity.class)
				.add(Restrictions.eq("cpdEntry", cpd));
			
		@SuppressWarnings("unchecked")
		List<MnxReactionProductEntity> result = (List<MnxReactionProductEntity>) criteria.list();
		for (MnxReactionProductEntity product : result) {
			producers.add(product.getMnxReactionEntity().getEntry());
		}
		return producers;
	}
	
	private List<String> numberOfProductsByEntry(int n) {
//		System.out.println("producers with " + n + " products");
		List<String> producers = new ArrayList<> ();
//		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MnxReactionProductEntity.class)
//				.setProjection( Projections.projectionList()
//						.add(Projections.groupProperty("mnxReactionEntity"))
//						.add(Projections.rowCount()));
		Query query = sessionFactory.getCurrentSession()
				.createQuery("SELECT count(*), a.mnxReactionEntity.entry FROM MnxReactionProductEntity a GROUP BY a.mnxReactionEntity");
		
		@SuppressWarnings("unchecked")
		List<Object[]> omg = query.list();
		for (Object[] o : omg) {
			if ((long) o[0] == n) producers.add((String) o[1]);
		}
//		System.out.println(query.scroll().get(0));
//		System.out.println(criteria.list());
		return producers;
	}
	
	private List<String> numberOfConsumersByEntry(int n) {
//		System.out.println("consumers with " + n + " reactants");
		List<String> producers = new ArrayList<> ();
//		Criteria c = sessionFactory.getCurrentSession().createCriteria(MnxReactionEntity.class)
//				.add(Restrictions.eq("id", "xpto"));
//		c.list();
		
//		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MnxReactionProductEntity.class)
//				.setProjection( Projections.projectionList()
//						.add(Projections.groupProperty("mnxReactionEntity"))
//						.add(Projections.rowCount()));
//		Query query2 = sessionFactory.getCurrentSession()
//				.createQuery("SELECT count(*), a.crossReferences.crrossReferences FROM MnxReactionEntity a GROUP BY a.mnxReactionEntity");
		Query query = sessionFactory.getCurrentSession()
				.createQuery("SELECT count(*), a.mnxReactionEntity.entry FROM MnxReactionReactantEntity a GROUP BY a.mnxReactionEntity");
		
		@SuppressWarnings("unchecked")
		List<Object[]> omg = query.list();
		for (Object[] o : omg) {
			if ((long) o[0] == n) producers.add(o[1].toString());
		}
//		System.out.println(query.scroll().get(0));
//		System.out.println(criteria.list());
		return producers;
	}
	
	private List<String> findReactionByReactantsAndProducts(List<String> reactants, List<String> products) {
		List<String> reactionsWithNReactants = numberOfConsumersByEntry(reactants.size());
		List<String> reactionsWithNProducts = numberOfProductsByEntry(products.size());
		
		
		List<String> consumers = consumes(reactants.remove(0));
		for (String reactantId : reactants) {
			consumers.retainAll( consumes(reactantId));
		}
//		System.out.println(consumers);
		consumers.retainAll(reactionsWithNReactants);
//		System.out.println(consumers);
		
		List<String> producers = produces(products.remove(0));
		for (String productId : products) {
			producers.retainAll( produces(productId));
		}
//		System.out.println(producers);
		producers.retainAll(reactionsWithNProducts);
//		System.out.println(producers);
		
		List<String> finalAnswer = new ArrayList<> (consumers);
		finalAnswer.retainAll(producers);
		
		return finalAnswer;
	}

	@Test
	public void testFindProducer() {
		
		
//		GenericEntityDAO dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		
		List<String> set1 = new ArrayList<> ();
		set1.add("MNXM20"); set1.add("MNXM268");
		List<String> set2 = new ArrayList<> ();
		set2.add("MNXM263"); set2.add("MNXM89557");

		List<String> finalAnswer = findReactionByReactantsAndProducts(set1, set2);
		
//		MnxReactionEntity rxn = dao.find(MnxReactionEntity.class, 58);
//		System.out.println(rxn);
		tx.commit();
		assertEquals(true, finalAnswer.contains("MNXR30562"));
	}
}
