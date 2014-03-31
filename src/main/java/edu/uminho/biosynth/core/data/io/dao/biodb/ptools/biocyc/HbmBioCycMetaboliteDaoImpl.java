package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

@Repository
public class HbmBioCycMetaboliteDaoImpl implements IMetaboliteDao<BioCycMetaboliteEntity>{

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public BioCycMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpd = this.getSession().get(BioCycMetaboliteEntity.class, id);
		return BioCycMetaboliteEntity.class.cast(cpd);
	}

	@Override
	public BioCycMetaboliteEntity saveMetabolite(
			BioCycMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return metabolite;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM BioCycMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
	}

	@Override
	public BioCycMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BioCycMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(BioCycMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable saveMetabolite(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioCycMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM BioCycMetaboliteEntity cpd");
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
