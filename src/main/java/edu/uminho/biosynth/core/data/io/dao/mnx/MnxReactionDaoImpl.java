package edu.uminho.biosynth.core.data.io.dao.mnx;

import org.hibernate.SessionFactory;

import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

@Deprecated
public class MnxReactionDaoImpl extends GenericEntityDaoImpl {

	public MnxReactionDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

//	@Override
//	public void save(MnxReactionEntity entity) {
//		this.sessionFactory.getCurrentSession().save(entity);
////		for (MnxReactionReactantEntity reactant : entity.getLeft()) {
////			reactant.setMnxReactionEntity(entity);
////			this.sessionFactory.getCurrentSession().save(reactant);
////		}
////		for (MnxReactionProductEntity product : entity.getRight()) {
////			product.setMnxReactionEntity(entity);
////			this.sessionFactory.getCurrentSession().save(product);
////		}
////		for (MnxReactionCrossReferenceEntity crossReference : entity.getCrossReferences()) {
////			crossReference.setMnxReactionEntity(entity);
////			this.sessionFactory.getCurrentSession().save(crossReference);
////		}
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<MnxReactionEntity> findAll() {
//		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(MnxReactionEntity.class);
//		return criteria.list();
//	}
//
//	@Override
//	public MnxReactionEntity getEntityById(int id) {
//		return (MnxReactionEntity) this.sessionFactory.getCurrentSession().load(MnxReactionEntity.class, id);
//	}
//
//	@Override
//	public MnxReactionEntity getEntityByEntry(String entry) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean contains(int id) {
//		// TODO Auto-generated method stub
//		return false;
//	}
	
}
