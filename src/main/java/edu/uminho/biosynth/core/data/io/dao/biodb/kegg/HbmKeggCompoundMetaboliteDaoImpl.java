package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class HbmKeggCompoundMetaboliteDaoImpl implements MetaboliteDao<KeggCompoundMetaboliteEntity> {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteById(Serializable id) {
		return KeggCompoundMetaboliteEntity.class.cast(this.getSession().get(KeggCompoundMetaboliteEntity.class, id));
	}

	@Override
	public KeggCompoundMetaboliteEntity getMetaboliteByEntry(String entry) {
		KeggCompoundMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(KeggCompoundMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = KeggCompoundMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public KeggCompoundMetaboliteEntity saveMetabolite(
			KeggCompoundMetaboliteEntity metabolite) {

		this.getSession().save(metabolite);
		return metabolite;
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		this.getSession().save(metabolite);
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM KeggCompoundMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM KeggCompoundMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

	@Deprecated
	@Override
	public Serializable save(KeggCompoundMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<KeggCompoundMetaboliteEntity> getAllMetabolites() {
		List<KeggCompoundMetaboliteEntity> cpdList = new ArrayList<> ();
		List<?> res = this.getSession().createCriteria(KeggCompoundMetaboliteEntity.class).list();
		for (Object o: res) {
			cpdList.add(KeggCompoundMetaboliteEntity.class.cast(o));
		}
		return cpdList;
	}

}
