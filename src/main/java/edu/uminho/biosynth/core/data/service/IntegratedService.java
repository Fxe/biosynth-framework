package edu.uminho.biosynth.core.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.integration.IntegratedMetabolite;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.service.cascade.CascadeByReference;
import edu.uminho.biosynth.core.data.service.cascade.ICascadeStrategy;
import edu.uminho.biosynth.core.data.service.mapping.MapperService;

public class IntegratedService implements IMetaboliteService<IntegratedMetabolite> {
	
	private final static Logger LOGGER = Logger.getLogger(IntegratedService.class.getName());
	
	List<?> reactionServices = new ArrayList<> ();
	Map<String, List<IMetaboliteService<? super GenericMetabolite>>> metaboliteServices = new HashMap<> ();
	ICascadeStrategy metaboliteCasdadeStrategy = new CascadeByReference();
	
	List<?> pathwayServices = new ArrayList<> ();
	
	public void registerMetaboliteService(IMetaboliteService<? super GenericMetabolite> service) {
		String type = service.getClass().getName();
		if (!metaboliteServices.containsKey(type)) {
			metaboliteServices.put(type, new ArrayList<IMetaboliteService<? super GenericMetabolite>> ());
		}
		this.metaboliteServices.get(type).add(service);
	}

	@Override
	public IntegratedMetabolite getMetaboliteByEntry(String entry) {
		List<GenericMetabolite> res = new ArrayList<> ();
		for (List<IMetaboliteService<? super GenericMetabolite>> services : metaboliteServices.values()) {
			for (IMetaboliteService<?> s : services) {
				Object queryResult = s.getMetaboliteByEntry(entry);
				if (queryResult != null)
					res.add((GenericMetabolite) queryResult);
			}
		}
//		System.out.println(res);
		if (res.size() < 1) return null;
		
		List<GenericMetabolite> cascadedList = new ArrayList<> ();
		for (int i = 0; i < res.size(); i++) {
			GenericMetabolite cpd = res.get(i);
			LOGGER.log(Level.INFO, "Applying " + this.metaboliteCasdadeStrategy.getClass().getName() + " to " + cpd.getEntry());
			List<?> cascadeResult = metaboliteCasdadeStrategy.cascade(cpd, cpd.getClass(), metaboliteServices);
			for (int j = 0; j < cascadeResult.size(); j++) {
				cascadedList.add((GenericMetabolite) cascadeResult.get(j));
			}
		}
		
		System.out.println(MapperService.filterEntityEntry(cascadedList));
		
		IntegratedMetabolite integratedMetabolite = null;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		SessionFactory sessionFactory;
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl(sessionFactory);
		MnxService mnxSrv = new MnxService(dao);
		BiggService biggService = new BiggService(dao);
		BiocycService biocycSrv = new BiocycService(dao);
		KeggService keggSrv = new KeggService(dao);
		IntegratedService service = new IntegratedService();
		service.registerMetaboliteService((IMetaboliteService) keggSrv);
		service.registerMetaboliteService((IMetaboliteService) mnxSrv);
		service.registerMetaboliteService((IMetaboliteService) biocycSrv);
		service.registerMetaboliteService((IMetaboliteService) biggService);
		
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		System.out.println(service.getMetaboliteByEntry("MNXM754"));
		
		tx.commit();
		sessionFactory.close();
	}
}
