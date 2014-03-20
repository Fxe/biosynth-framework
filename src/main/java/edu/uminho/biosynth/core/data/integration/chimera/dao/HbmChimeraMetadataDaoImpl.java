package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

public class HbmChimeraMetadataDaoImpl implements ChimeraMetadataDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public List<Serializable> getAllIntegrationSetsId() {
		Query query = this.getSession().createQuery("SELECT i.id FROM IntegrationSet i");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}



	@Override
	public void mergeCluster(List<Long> ids, Serializable integrationId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveIntegrationSet(IntegrationSet integrationSet) {
		this.getSession().save(integrationSet);
	}

	@Override
	public IntegrationSet getIntegrationSet(Serializable id) {
		return IntegrationSet.class.cast(this.getSession().get(IntegrationSet.class, id));
	}

	@Override
	public IntegratedCluster createCluster(String name, List<Long> ids, String description,
			IntegrationSet integrationSet) {
		
		IntegratedCluster cluster = new IntegratedCluster();
		cluster.setName(name);
		cluster.setIntegrationSet(integrationSet);
		for (Long id: ids) {
			IntegratedClusterMember member = new IntegratedClusterMember();
			member.setDescription(description);
			member.setId(id);
			member.setIntegratedCluster(cluster);
			cluster.getMemberMap().put(id, member);
		}
		
		this.getSession().save(cluster);
		
		return cluster;
	}

	@Override
	public void deleteCluster(IntegratedCluster cluster) {
		Query query;
		
		query = this.getSession().createQuery("DELETE FROM IntegratedClusterMember m WHERE m.integratedCluster.id = :cid");
		query.setParameter("cid", cluster.getId());
		query.executeUpdate();
		
		query = this.getSession().createQuery("DELETE FROM IntegratedCluster m WHERE m.id = :cid");
		query.setParameter("cid", cluster.getId());
		query.executeUpdate();
	}

	@Override
	public void deleteIntegrationSet(IntegrationSet integrationSet) {
		this.getSession().delete(integrationSet);
	}

	@Override
	public IntegrationSet getIntegrationSet(String id) {
		IntegrationSet integratedSet = null;
		
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(IntegrationSet.class);
		criteria.add(Restrictions.eq("name", id));
		List<?> res = criteria.list();
		for (Object o : res) {
			integratedSet = IntegrationSet.class.cast(o);
		}
		
		return integratedSet;
	}

}
