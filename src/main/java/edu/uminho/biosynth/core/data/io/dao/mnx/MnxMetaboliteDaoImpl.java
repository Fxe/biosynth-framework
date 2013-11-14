package edu.uminho.biosynth.core.data.io.dao.mnx;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class MnxMetaboliteDaoImpl extends GenericEntityDaoImpl<MnxMetaboliteEntity> {

	public MnxMetaboliteDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MnxMetaboliteEntity> findAll() {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(MnxMetaboliteEntity.class);
		return criteria.list();
	}

	@Override
	public MnxMetaboliteEntity getEntityById(int id) {
		return (MnxMetaboliteEntity) this.sessionFactory.getCurrentSession().load(MnxMetaboliteEntity.class, id);
	}

	@Override
	public MnxMetaboliteEntity getEntityByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save(MnxMetaboliteEntity entity) {
		this.sessionFactory.getCurrentSession().save(entity);
		for (MnxMetaboliteCrossReferenceEntity crossReference : entity.getCrossReferences()) {
			crossReference.setMnxMetaboliteEntity(entity);
			this.sessionFactory.getCurrentSession().save(crossReference);
		}
	}

}
