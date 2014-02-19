package edu.uminho.biosynth.core.data.integration.etl.staging.olap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultGraphImpl;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation.IAttributeEvaluator;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class IntegrationOLAP {

	private IGenericDao dao;
	private IKeyGenerator<Integer> idGenerator;
	
	private IBinaryGraph<Integer, Integer> graph = new DefaultGraphImpl<>();
	private Map<Integer, Set<Integer>> clusters = new HashMap<> ();
	
	private int numberOfRecords;
	
	private ScoreMap scoreMap = new ScoreMap();

	public IKeyGenerator<Integer> getIdGenerator() { return idGenerator;}
	public void setIdGenerator(IKeyGenerator<Integer> idGenerator) { this.idGenerator = idGenerator;}
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	public void reset() {
		this.graph.clear();
		
		this.clusters.clear();
		this.idGenerator.reset();
		
		Integer initialCluster = idGenerator.generateKey();
		Set<Integer> initialElements = new HashSet<> ();
		
		//OPTIMIZATION: GET ONLY IDS VS FINDALL (SPEED ?)
		for (MetaboliteStga cpdId : dao.findAll(MetaboliteStga.class)) {
			initialElements.add( cpdId.getId());
		}
		
		this.numberOfRecords = initialElements.size();
		this.clusters.put( initialCluster, initialElements);
	}
	
	public void slice(String property, Class<?> dimension, Integer clustersId) {
		if (this.clusters.containsKey(clustersId)) {
			this.slice(property, dimension, this.clusters.get(clustersId));
		}
	}
	
	public void slice(String property, Class<?> dimension, Set<Integer> clusters) {
		List<?> elements = null;
		if (clusters.size() == numberOfRecords) {
			elements = this.dao.findAll(dimension);
		} else {
			
		}
		
		try {
//			System.out.println(dimension.get);
			
			Class<?> klass = Class.forName(property);
			Method getMetaboliteStgas = klass.getDeclaredMethod("getMetaboliteStgas");
			
			
			
			for (Object clustersNew : elements) {

				PersistentSet cpdInClusers = (PersistentSet) getMetaboliteStgas.invoke(clustersNew);
				
				Integer clusterId = idGenerator.generateKey();
				Set<Integer> clusterElements = new HashSet<> ();
				for (Object obj : cpdInClusers) {
					MetaboliteStga cpd = MetaboliteStga.class.cast(obj);
					clusterElements.add( cpd.getId());
				}
//				for (int i = 0; i < cpdInClusers.size(); i++) ;
				
				this.clusters.put(clusterId, clusterElements);
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			System.out.println("InvocationTargetException | IllegalAccessException " + e.getMessage());
		} catch (NoSuchMethodException e) {
			System.out.println("NoSuchMethodException " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException " + e.getMessage());
		}
	}
	
//	public void 
	
	public void dice() {
		
	}
	
	public void someMethod() {
//		resetMe();
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Elements:").append( this.numberOfRecords).append('\n');
		for (Integer id : this.clusters.keySet()) {
			sb.append("Cluster ").append(id).append(" -> ").append( this.clusters.get(id).size()).append('\n');
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		SessionFactory sessionFactory_stga;
		IGenericDao dao_stga;
		Transaction tx_stga;
		
		Configuration config = new Configuration().configure("hibernate_production_staging_example_pgsql.cfg.xml");
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");
		System.out.println(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory_stga = config.buildSessionFactory(servReg);
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		
		IntegrationOLAP olap = new IntegrationOLAP();
		IKeyGenerator<Integer> generator= new IKeyGenerator<Integer>() {
			
			private int seq = 0;
			
			@Override
			public void reset() {
				this.seq = 0;
			}
			
			@Override
			public Integer generateKey() {
				return seq++;
			}
		};
		olap.setDao(dao_stga);
		olap.setIdGenerator(generator);
		olap.reset();
		
		System.out.println(olap);
		
		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim", MetaboliteFormulaDim.class, 0);
		
		System.out.println(olap);
		
		tx_stga.commit();
	}
}
