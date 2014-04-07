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

import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class HbmKeggDrugMetaboliteDaoImpl implements IMetaboliteDao<KeggDrugMetaboliteEntity> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public KeggDrugMetaboliteEntity getMetaboliteById(Serializable id) {
		return KeggDrugMetaboliteEntity.class.cast(this.getSession().get(KeggDrugMetaboliteEntity.class, id));
	}

	@Override
	public KeggDrugMetaboliteEntity saveMetabolite(
			KeggDrugMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return null;
	}

	@Override
	public KeggDrugMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM KeggDrugMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public List<KeggDrugMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(KeggDrugMetaboliteEntity metabolite) {
		return this.getSession().save(metabolite);
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(KeggDrugMetaboliteEntity.class.cast(entity));
	}

	@Override
	public KeggDrugMetaboliteEntity getMetaboliteByEntry(String entry) {
		KeggDrugMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(KeggDrugMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = KeggDrugMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM KeggDrugMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}
	
	public List<KeggDrugMetaboliteEntity> getAllMetabolites() {
		List<KeggDrugMetaboliteEntity> cpdList = new ArrayList<> ();
		List<?> res = this.getSession().createCriteria(KeggDrugMetaboliteEntity.class).list();
		for (Object o: res) {
			cpdList.add(KeggDrugMetaboliteEntity.class.cast(o));
		}
		return cpdList;
	}

}
