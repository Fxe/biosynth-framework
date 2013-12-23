package edu.uminho.biosynth.core.data.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.integration.IntegratedMetabolite;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class IntegratedService implements IMetaboliteService<IntegratedMetabolite> {
	
	public static 
	
	List<?> reactionServices = new ArrayList<> ();
	List<IMetaboliteService<?>> metaboliteServices = new ArrayList<> ();
	List<?> pathwayServices = new ArrayList<> ();
	
	public void registerMetaboliteService(IMetaboliteService<?> service) {
		this.metaboliteServices.add(service);
	}

	@Override
	public IntegratedMetabolite getMetaboliteByEntry(String entry) {
		List<GenericMetabolite> res = new ArrayList<> ();
		for (IMetaboliteService<?> s : metaboliteServices) {
			Object queryResult = s.getMetaboliteByEntry(entry);
			if (queryResult != null)
				res.add((GenericMetabolite) queryResult);
		}
		System.out.println(res);
		if (res.size() < 1) return null;
		
		IntegratedMetabolite integratedMetabolite = new IntegratedMetabolite();
		// TODO Auto-generated method stub
		return integratedMetabolite;
	}

	@Override
	public IntegratedMetabolite getMetaboliteById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IntegratedMetabolite> getAllMetabolites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countNumberOfMetabolites() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		SessionFactory sessionFactory;
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl(sessionFactory);
		KeggService keggSrv = new KeggService(dao);
		IntegratedService service = new IntegratedService();
		service.registerMetaboliteService(keggSrv);
		
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		System.out.println(service.getMetaboliteByEntry("C00755"));
		
		tx.commit();
		sessionFactory.close();
	}
}
