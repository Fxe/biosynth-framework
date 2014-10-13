package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class HbmBiggMetaboliteDaoImpl implements MetaboliteDao<BiggMetaboliteEntity> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	public HbmBiggMetaboliteDaoImpl withSessionFactory(SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
		return this;
	}
	
	@Override
	public BiggMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpd = this.getSession().get(BiggMetaboliteEntity.class, id);
		return BiggMetaboliteEntity.class.cast(cpd);
	}
	
	@Override
	public BiggMetaboliteEntity getMetaboliteByEntry(String entry) {
		Criteria criteria = this.getSession()
				.createCriteria(BiggMetaboliteEntity.class)
				.add(Restrictions.eq("entry", entry));

		return (BiggMetaboliteEntity) criteria.uniqueResult();
	}

	@Override
	public BiggMetaboliteEntity saveMetabolite(BiggMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return metabolite;
	}

	@Deprecated
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
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM BiggMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}
	
	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM BiggMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
