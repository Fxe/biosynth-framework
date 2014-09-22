package edu.uminho.biosynth.core.data.io.dao.biodb.chebi;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class HbmChebiMetaboliteDaoImpl implements MetaboliteDao<ChebiMetaboliteEntity> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	public HbmChebiMetaboliteDaoImpl() { }
	public HbmChebiMetaboliteDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public ChebiMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpd = this.getSession().get(ChebiMetaboliteEntity.class, id);
		return ChebiMetaboliteEntity.class.cast(cpd);
	}

	@Override
	public ChebiMetaboliteEntity getMetaboliteByEntry(String entry) {
		ChebiMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(ChebiMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = ChebiMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public ChebiMetaboliteEntity saveMetabolite(ChebiMetaboliteEntity metabolite) {
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
		Query query = this.getSession().createQuery("SELECT cpd.id FROM ChebiMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM ChebiMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

	@Override
	public Serializable save(ChebiMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
