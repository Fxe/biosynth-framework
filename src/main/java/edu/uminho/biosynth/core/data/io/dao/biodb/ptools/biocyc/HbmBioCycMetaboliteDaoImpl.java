package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.AbstractHibernateDao;

@Repository
public class HbmBioCycMetaboliteDaoImpl 
extends AbstractHibernateDao implements MetaboliteDao<BioCycMetaboliteEntity>{

	private String pgdb = "META";
	
	public String getPgdb() { return pgdb;}
	public void setPgdb(String pgdb) { this.pgdb = pgdb;}

	@Override
	public BioCycMetaboliteEntity getMetaboliteById(Serializable id) {
		Object cpdObj = this.getSession().get(BioCycMetaboliteEntity.class, id);
		return BioCycMetaboliteEntity.class.cast(cpdObj);
	}

	@Override
	public BioCycMetaboliteEntity saveMetabolite(
			BioCycMetaboliteEntity metabolite) {
		this.getSession().save(metabolite);
		return metabolite;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		Query query = this.getSession().createQuery("SELECT cpd.id FROM BioCycMetaboliteEntity cpd WHERE cpd.source = :source");
		query.setParameter("source", pgdb);
		@SuppressWarnings("unchecked")
		List<Serializable> res = query.list();
		return res;
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
		
		BioCycMetaboliteEntity cpd = null;
		Criteria criteria = this.getSession().createCriteria(BioCycMetaboliteEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			cpd = BioCycMetaboliteEntity.class.cast(o);
		}
		return cpd;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		Query query = this.getSession().createQuery("SELECT cpd.entry FROM BioCycMetaboliteEntity cpd WHERE cpd.source = :source");
		query.setParameter("source", pgdb);
		@SuppressWarnings("unchecked")
		List<String> res = query.list();
		return res;
	}

}
