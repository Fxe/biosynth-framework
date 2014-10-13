package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class HbmSeedMetaboliteDaoImpl implements MetaboliteDao<SeedMetaboliteEntity> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

	@Override
	public SeedMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpd = this.getSession().get(SeedMetaboliteEntity.class, id);
		return SeedMetaboliteEntity.class.cast(cpd);
	}

	@Override
	public SeedMetaboliteEntity saveMetabolite(
			SeedMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return metabolite;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM SeedMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public Serializable save(SeedMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		this.getSession().save(metabolite);
		return null;
	}

	@Override
	public SeedMetaboliteEntity getMetaboliteByEntry(String entry) {
		SeedMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(SeedMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = SeedMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM SeedMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
