package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	public KeggDrugMetaboliteEntity getMetaboliteInformation(Serializable id) {
		return KeggDrugMetaboliteEntity.class.cast(this.getSession().get(KeggDrugMetaboliteEntity.class, id));
	}

	@Override
	public KeggDrugMetaboliteEntity saveMetaboliteInformation(
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
	public Serializable save(Object entity) {
		return this.save(KeggDrugMetaboliteEntity.class.cast(entity));
	}

}
