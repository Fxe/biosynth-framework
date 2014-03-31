package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

@Repository
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
	public void deleteCluster(IntegratedCluster cluster) {
		Query query;
		
		query = this.getSession().createQuery("DELETE FROM IntegratedClusterMember m WHERE m.pk.cluster.id = :cid");
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

	@Override
	public IntegratedMember getIntegratedMember(Long id) {
		return IntegratedMember.class.cast(this.getSession().get(IntegratedMember.class, id));
	}

	@Override
	public void saveIntegratedMember(IntegratedMember member) {
		IntegratedMember m = this.getIntegratedMember(member.getId());
		if (m == null) this.getSession().save(member);
//		this.getSession().saveOrUpdate(member);
	}

	@Override
	public void saveIntegratedCluster(IntegratedCluster cluster) {
		this.getSession().save(cluster);
	}

	@Override
	public List<Long> getAllIntegratedMembersId() {
		Query query = this.getSession().createQuery("SELECT m.id FROM IntegratedMember m");
		@SuppressWarnings("unchecked")
		List<Long> res = query.list();
		return res;
	}
	

	@Override
	public Map<Long, Long> getAllAssignedIntegratedMembers(Long integrationSetId) {
		Map<Long, Long> res = new HashMap<> ();
		IntegrationSet integrationSet = IntegrationSet.class.cast(this.getSession().get(IntegrationSet.class, integrationSetId));
		for (IntegratedCluster cluster: integrationSet.getIntegratedClustersMap().values()) {
			for (IntegratedClusterMember member: cluster.getMembers()) {
				res.put(member.getMember().getId(), member.getCluster().getId());
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getAllIntegratedClusterIds(Long integrationSetId) {
		Query query = this.getSession().createQuery(
				"SELECT c.id FROM IntegratedCluster c WHERE c.integrationSet.id = :sid");
		query.setParameter("sid", integrationSetId);
		return (List<Long>)query.list();
	}

}
