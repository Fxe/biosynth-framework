package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class HbmBiggMetaboliteDaoImpl implements IMetaboliteDao<BiggMetaboliteEntity> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public BiggMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpd = this.getSession().get(BiggMetaboliteEntity.class, id);
		return BiggMetaboliteEntity.class.cast(cpd);
	}

	@Override
	public BiggMetaboliteEntity saveMetabolite(BiggMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return metabolite;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM BiggMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public BiggMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public List<BiggMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public Serializable save(BiggMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		this.getSession().save(metabolite);
		return null;
	}

	@Override
	public BiggMetaboliteEntity getMetaboliteByEntry(String entry) {
		BiggMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(BiggMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = BiggMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM BiggMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
