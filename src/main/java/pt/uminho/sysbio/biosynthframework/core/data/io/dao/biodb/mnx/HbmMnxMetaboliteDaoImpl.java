package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.mnx;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class HbmMnxMetaboliteDaoImpl implements MetaboliteDao<MnxMetaboliteEntity> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public MnxMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpd = this.getSession().get(MnxMetaboliteEntity.class, id);
		return MnxMetaboliteEntity.class.cast(cpd);
	}

	@Override
	public MnxMetaboliteEntity saveMetabolite(MnxMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return metabolite;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM MnxMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public Serializable save(MnxMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		this.getSession().save(metabolite);
		return null;
	}

	@Override
	public MnxMetaboliteEntity getMetaboliteByEntry(String entry) {
		MnxMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(MnxMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = MnxMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM MnxMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
