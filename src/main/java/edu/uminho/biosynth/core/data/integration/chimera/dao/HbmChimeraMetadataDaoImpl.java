package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
	
	private static final Logger LOGGER = Logger.getLogger(HbmChimeraMetadataDaoImpl.class);

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
	public IntegratedCluster saveIntegratedCluster(IntegratedCluster cluster) {
		this.getSession().save(cluster);
		
		return cluster;
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<Long> getAllIntegratedClusterIds(IntegrationSet integrationSetId) {
		if (integrationSetId == null || integrationSetId.getId() == null) return null;
		
		Query query = this.getSession().createQuery(
				"SELECT c.id FROM IntegratedCluster c WHERE c.integrationSet.id = :iid");
		query.setParameter("iid", integrationSetId.getId());
		return new HashSet<> (query.list());
	}

	@Override
	public IntegratedCluster getIntegratedClusterByEntry(String name, Long integrationSetId) {
		List<?> res = this.getSession().createCriteria(IntegratedCluster.class)
				.add(Restrictions.and(
						Restrictions.eq("name", name), 
						Restrictions.eq("integrationSet.id", integrationSetId))).list();
		
		if (res.isEmpty()) return null;
		if (res.size() > 1) {
//			for (Object o : res) System.out.println(IntegratedCluster.class.cast(o).getName());
			LOGGER.warn(String.format("Integrity fault - duplicate name [%s]", name));
		}

		return IntegratedCluster.class.cast(res.iterator().next());
	}

	@Override
	public IntegratedCluster getIntegratedClusterById(Long id) {
		return IntegratedCluster.class.cast(this.getSession().get(IntegratedCluster.class, id));
	}

	@Override
	public List<Long> getAllIntegratedClusterMembersId() {
		Query query = this.getSession().createQuery("SELECT DISTINCT m.pk.member.id FROM IntegratedClusterMember m");
		@SuppressWarnings("unchecked")
		List<Long> res = query.list();
		return res;
	}

	@Override
	public List<IntegratedCluster> getIntegratedClusterByMemberIds(
			Long... memberIds) {
		
		System.out.println("Here !");
		
		List<IntegratedCluster> integratedClusters = new ArrayList<> ();
		Query query = this.getSession().createQuery("SELECT DISTINCT m.pk.cluster.id FROM IntegratedClusterMember m WHERE m.pk.member.id IN (:ids)");
		query.setParameterList("ids", memberIds);
		
		@SuppressWarnings("unchecked")
		List<Long> clusterIds = query.list();
		
		System.out.println("Query Done");
		
		for (Long id : clusterIds) {
			IntegratedCluster integratedCluster = this.getIntegratedClusterById(id);
			integratedClusters.add(integratedCluster);
		}

		System.out.println("Done !");
		return integratedClusters;
	}

	@Override
	public String getLastClusterEntry(Long integrationSetId) {
		IntegrationSet integrationSet = IntegrationSet.class.cast(
				this.getSession().get(IntegrationSet.class, integrationSetId));
		return integrationSet.getLastClusterEntry();
	}

	@Override
	public List<IntegratedCluster> getAllIntegratedClusters(Long integrationSetId) {
		@SuppressWarnings("unchecked")
		List<IntegratedCluster> res = this.getSession().createCriteria(IntegratedCluster.class)
				.add(Restrictions.eq("integrationSet.id", integrationSetId)).list();
		return res;
	}

	@Override
	public List<IntegratedCluster> getIntegratedClustersByPage(
			Long integrationSetId, int firstResult, int maxResults) {
		Criteria criteria = this.getSession().createCriteria(IntegratedCluster.class);
		criteria.add(Restrictions.eq("integrationSet.id", integrationSetId));
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		
		@SuppressWarnings("unchecked")
		List<IntegratedCluster> list = criteria.list();
		return list;
	}

	@Override
	public void updateCluster(IntegratedCluster cluster) {
////		if (cluster.getMembers().isEmpty())
//		Query query = this.getSession().createQuery(
//				"update IntegratedCluster set description = :description where id = :id");
//		query.setParameter("description", cluster.getDescription());
//		query.setParameter("id", cluster.getId());
//		query.executeUpdate();

		this.getSession().update(cluster);
	}
	
	@Override
	public void deleteClusterMember(IntegratedClusterMember member) {
		Query query = this.getSession().createQuery(
				"delete IntegratedClusterMember where pk.cluster.id = :cid and pk.member.id = :eid");
		query.setParameter("cid", member.getCluster().getId());
		query.setParameter("eid", member.getMember().getId());
		query.executeUpdate();
	}

}
