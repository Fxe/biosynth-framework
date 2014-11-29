package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class HbmKeggGlycanMetaboliteDaoImpl implements MetaboliteDao<KeggGlycanMetaboliteEntity> {

	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	@Autowired
	public HbmKeggGlycanMetaboliteDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public KeggGlycanMetaboliteEntity getMetaboliteById(Serializable id) {
		return KeggGlycanMetaboliteEntity.class.cast(this.getSession().get(KeggGlycanMetaboliteEntity.class, id));
	}

	@Override
	public KeggGlycanMetaboliteEntity saveMetabolite(
			KeggGlycanMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM KeggGlycanMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public Serializable save(KeggGlycanMetaboliteEntity metabolite) {
		return this.getSession().save(metabolite);
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		return this.save(KeggGlycanMetaboliteEntity.class.cast(entity));
	}

	@Override
	public KeggGlycanMetaboliteEntity getMetaboliteByEntry(String entry) {
		KeggGlycanMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(KeggGlycanMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = KeggGlycanMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM KeggGlycanMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
