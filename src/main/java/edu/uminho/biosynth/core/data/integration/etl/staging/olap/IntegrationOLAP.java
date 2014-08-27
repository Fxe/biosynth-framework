package edu.uminho.biosynth.core.data.integration.etl.staging.olap;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.criterion.Restrictions;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import edu.uminho.biosynth.core.components.representation.basic.graph.BinaryGraph;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultGraphImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class IntegrationOLAP {

	private static Logger LOG = Logger.getLogger(IntegrationOLAP.class);
	
	private boolean destroyInactiveClusters = true;
	
	private IGenericDao dao;
	private IKeyGenerator<Long> idGenerator;
	
	private BinaryGraph<Long, Long> graph = new DefaultGraphImpl<>();
	private Map<Long, Set<Long>> clusters = new HashMap<> ();
	
	private int numberOfRecords;

	public IKeyGenerator<Long> getIdGenerator() { return idGenerator;}
	public void setIdGenerator(IKeyGenerator<Long> idGenerator) { this.idGenerator = idGenerator;}
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	public void reset() {
		this.graph.clear();
		
		this.clusters.clear();
		this.idGenerator.reset();
		
		Long initialCluster = idGenerator.generateKey();
		Set<Long> initialElements = new HashSet<> ();
		
		//OPTIMIZATION: GET ONLY IDS VS FINDALL (SPEED ?)
		for (MetaboliteStga cpdId : dao.findAll(MetaboliteStga.class)) {
			initialElements.add( cpdId.getId());
		}
		
		this.numberOfRecords = initialElements.size();
		this.clusters.put( initialCluster, initialElements);
	}
	
	public void slice(String property, Long clustersId) {
		//SLICE ONLY EXISTING CLUSTERS WITH SIZE > 1
		if (this.clusters.containsKey(clustersId) && this.clusters.get(clustersId).size() > 1) {
			this.slice(property, this.clusters.get(clustersId));
			
			if (destroyInactiveClusters) {
				this.clusters.remove(clustersId);
			}
		}
	}
	
	//Set<Integer> to describe a olap cube is pretty stupid ! must redesign this part
	public Set<Serializable> slice(String property, Serializable slice, Set<Long> cube) {
		Set<Serializable> res = new HashSet<> ();
		List<MetaboliteStga> elements = null;
		if (cube.size() == numberOfRecords) {
			LOG.info("SLICE ALL " + property);
			elements = this.dao.findAll(MetaboliteStga.class);
		} else {
			LOG.info("SLICING CUBE (" + cube.size() + ") " + property);
			elements = this.dao.criteria(MetaboliteStga.class, Restrictions.in("id", cube));
		}
		for (MetaboliteStga sliceElements : elements) {
			Serializable id = sliceElements.getMetaboliteFormulaDim().getId();
			if (slice.equals(id)) res.add(sliceElements.getId());
		}
		return res;
	}
	
	public Set<Serializable> getDimensionElements(String property, Set<Long> cube) {
		Set<Serializable> res = new HashSet<> ();
		
		try {
			Class<?> klass = Class.forName(property);
			List<?> elements = null;
			if (cube.size() == numberOfRecords) {
				LOG.info("SLICE ALL " + property);
				elements = this.dao.findAll(klass);
			} else {
				LOG.info("SLICE " + cube + " " + property);
	//			Set<Integer> aux = new HashSet<> ();
	//			aux.add(2269);
				String property_ = StringUtils.uncapitalize(klass.getSimpleName());
				//this is really bad :( must find alternative ! perhaps using criteria + join ? or reflexion
				Query query = this.dao.createQuery("SELECT DISTINCT cpd." + property_ + ".id FROM MetaboliteStga cpd WHERE cpd.id IN (:cpdIdList)");
				query.setParameterList("cpdIdList", cube);
				for (Object id : query.list()) {
					res.add((Serializable) id);
					System.out.println(id);
				}
//				elements = this.dao.criteria(klass, Restrictions.in("id", query.list()));
//				System.out.println(elements);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException " + e.getMessage());
		}
		
		return res;
	}
	
	public void slice(String property, Set<Long> metaboliteIdSet) {
		
		
		
		try {
//			System.out.println(dimension.get);
			Class<?> klass = Class.forName(property);
			List<?> elements = null;
			if (metaboliteIdSet.size() == numberOfRecords) {
				LOG.info("SLICE ALL " + property);
				elements = this.dao.findAll(klass);
			} else {
				LOG.info("SLICE " + metaboliteIdSet + " " + property);
//				Set<Integer> aux = new HashSet<> ();
//				aux.add(2269);
				String property_ = StringUtils.uncapitalize(klass.getSimpleName());
				//this is really bad :( must find alternative ! perhaps using criteria + join ? or reflexion
				Query query = this.dao.createQuery("SELECT cpd." + property_ + ".id FROM MetaboliteStga cpd WHERE cpd.id IN (:cpdIdList)");
				query.setParameterList("cpdIdList", metaboliteIdSet);
				for (Object id : query.list()) {
					System.out.println(id);
				}
				elements = this.dao.criteria(klass, Restrictions.in("id", query.list()));
				System.out.println(elements);
			}
			
			Method getMetaboliteStgas = klass.getDeclaredMethod("getMetaboliteStgas");
			
			
			
			for (Object clustersNew : elements) {
				PersistentSet cpdInClusers = (PersistentSet) getMetaboliteStgas.invoke(clustersNew);
				
				Long clusterId = idGenerator.generateKey();
				Set<Long> clusterElements = new HashSet<> ();
				for (Object obj : cpdInClusers) {
					MetaboliteStga cpd = MetaboliteStga.class.cast(obj);
					if (metaboliteIdSet.contains(cpd.getId())) clusterElements.add( cpd.getId());
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
	
	public Set<Long> getClusters() {
		Set<Long> clusterIdSet = new HashSet<> (this.clusters.keySet());
		
		return clusterIdSet;
	}
	
	public Set<Long> getCluster(Integer clusterId) {
		return this.clusters.get(clusterId);
	}
	
//	public void 
	
	public void dice() {
		
	}
	
	public void someMethod() {
//		resetMe();
		Long clusterId = 0L;
		
		for (MetaboliteFormulaDim dimElement: dao.findAll(MetaboliteFormulaDim.class)) {
			Set<Long> clusterElements = new HashSet<> ();
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
		for (Long id : this.clusters.keySet()) {
			sb.append("Cluster ").append(id).append(" -> ").append( this.clusters.get(id).size()).append('\n');
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		SessionFactory sessionFactory_stga;
		IGenericDao dao_stga;
		Transaction tx_stga;
		
//		Configuration config = new Configuration().configure("hibernate_debug_mysql.cfg.xml");

		sessionFactory_stga = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_staging_example_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		IntegrationOLAP olap = new IntegrationOLAP();
		IKeyGenerator<Long> generator= new IKeyGenerator<Long>() {
			
			private long seq = 0;
			
			@Override
			public void reset() {
				this.seq = 0;
			}
			
			@Override
			public Long generateKey() {
				return seq++;
			}

			@Override
			public void generateFromLastElement(Long key) {
				System.out.println("bzbzbbzbzb");
			}

			@Override
			public Long getCurrentKey() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		olap.setDao(dao_stga);
		olap.setIdGenerator(generator);
		olap.reset();
		
		System.out.println(olap);
		
		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim", 0L);
		
		System.out.println(olap);
		
		System.out.println(olap.getCluster(3));

		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim", 3L);
		
		System.out.println(olap);
		
		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteSmilesDim", 2L);

		System.out.println(olap);
		
		for (Long clusterId : olap.getClusters()) {
			
		}
		
		tx_stga.commit();
	}
}
