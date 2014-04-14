package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

public class HbmBiggReactionDaoImpl implements ReactionDao<BiggReactionEntity> {

	private static final Logger LOGGER = Logger.getLogger(HbmBiggReactionDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public BiggReactionEntity getReactionById(Serializable id) {
		Object cpd = this.getSession().get(BiggReactionEntity.class, id);
		return BiggReactionEntity.class.cast(cpd);
	}

	@Override
	public BiggReactionEntity getReactionByEntry(String entry) {
		BiggReactionEntity rxn = null;
		Criteria criteria = this.getSession().createCriteria(BiggReactionEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) {
			LOGGER.warn(String.format("Entry uniqueness fail multiple records found for [%s]", entry));
		}
		
		for (Object o: res) {
			rxn = BiggReactionEntity.class.cast(o);
		}
		
		return rxn;
	}

	@Override
	public BiggReactionEntity saveReaction(BiggReactionEntity reaction) {
		this.getSession().save(reaction);
		return reaction;
	}

	@Override
	public Set<Serializable> getAllReactionIds() {
		Query query = this.getSession().createQuery("SELECT rxn.id FROM BiggReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<Serializable> res = new HashSet<> (query.list());
		return res;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		Query query = this.getSession().createQuery("SELECT rxn.entry FROM BiggReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<String> res = new HashSet<> (query.list());
		return res;
	}

}
