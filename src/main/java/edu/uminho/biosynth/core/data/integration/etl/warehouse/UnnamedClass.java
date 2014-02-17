package edu.uminho.biosynth.core.data.integration.etl.warehouse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultGraphImpl;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class UnnamedClass {

	private IGenericDao dao;
	
	private IBinaryGraph<Integer, Integer> graph = new DefaultGraphImpl<>();
	private Map<Integer, Set<Integer>> clusters = new HashMap<> ();
	
	public IGenericDao getDao() {
		return dao;
	}

	public void setDao(IGenericDao dao) {
		this.dao = dao;
	}

	public void resetMe() {
		graph.clear();
		clusters.clear();
	}
	
	public void slice() {
		
	}
	
	public void dice() {
		
	}
	
	public void someMethod() {
		resetMe();
		Integer clusterId = 0;
		
		for (MetaboliteFormulaDim dimElement: dao.findAll(MetaboliteFormulaDim.class)) {
			Set<Integer> clusterElements = new HashSet<> ();
			for (MetaboliteStga cpd : dimElement.getMetaboliteStgas()) {
				clusterElements.add(cpd.getId());
			}
			clusters.put(clusterId, clusterElements);
			System.out.println(clusterId + "\t" + clusterElements.size());
			clusterId++;
			
		}
		
		System.out.println(clusters);
		System.out.println(clusters.keySet().size());
	}
	
	public static void main(String[] args) {
		SessionFactory sessionFactory_stga;
		IGenericDao dao_stga;
		Transaction tx_stga;
		
		Configuration config = new Configuration().configure("hibernate_production_staging_pgsql.cfg.xml");
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		System.out.println(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory_stga = config.buildSessionFactory(servReg);
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		
		UnnamedClass integrador = new UnnamedClass();
		
		integrador.setDao(dao_stga);
		integrador.someMethod();
		
		tx_stga.commit();
	}
}
