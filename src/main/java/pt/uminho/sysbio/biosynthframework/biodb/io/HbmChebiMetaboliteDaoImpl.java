package pt.uminho.sysbio.biosynthframework.biodb.io;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;


public class HbmChebiMetaboliteDaoImpl implements MetaboliteDao<ChebiMetaboliteEntity> {

	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Autowired
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
		Criteria criteria = this.getSession().createCriteria(ChebiMetaboliteEntity.class);
		Object res = criteria.add(Restrictions.eq("entry", entry)).uniqueResult();
		if (res == null) return null;
		
		return ChebiMetaboliteEntity.class.cast(res);
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
