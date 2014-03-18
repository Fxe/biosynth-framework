package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	public IntegratedCluster createCluster(List<Long> ids, String description,
			IntegrationSet integrationSet) {
		
		IntegratedCluster cluster = new IntegratedCluster();
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
		this.getSession().delete(cluster);
	}

	@Override
	public void deleteIntegrationSet(IntegrationSet integrationSet) {
		this.getSession().delete(integrationSet);
	}

}
