package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class KeggMetaboliteDaoImpl {

//	public KeggMetaboliteDaoImpl(SessionFactory sessionFactory) {
//		super(sessionFactory);
//	}
//
//	@Override
//	public KeggMetaboliteEntity getEntityById(int id) {
//		return (KeggMetaboliteEntity) this.sessionFactory.getCurrentSession().load(KeggMetaboliteEntity.class, id);
//	}
//
//	@Override
//	public KeggMetaboliteEntity getEntityByEntry(String entry) {
//		 Query query = sessionFactory.getCurrentSession().createQuery("from KEGG_METABOLITE where ENTRY = :ENTRY");
//		 query.setParameter("ENTRY", entry);
//		 return (KeggMetaboliteEntity) query.list().get(0);
//	}
//
//	@Override
//	public boolean contains(int id) {
//		KeggMetaboliteEntity cpd = (KeggMetaboliteEntity) this.sessionFactory.getCurrentSession().get(KeggMetaboliteEntity.class, id);
//
//		return true;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<KeggMetaboliteEntity> findAll() {
//		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(KeggMetaboliteEntity.class);
//		return criteria.list();
//	}
//
//	@Override
//	public void save(KeggMetaboliteEntity entity) {
//		// TODO Auto-generated method stub
//		
//	}

}
