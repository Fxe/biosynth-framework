package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

public class HbmKeggReactionDaoImpl implements ReactionDao<KeggReactionEntity> {

	private static Logger LOGGER = LoggerFactory.getLogger(HbmKeggReactionDaoImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Override
	public KeggReactionEntity getReactionById(Long id) {
		Object object = this.getSession().get(KeggReactionEntity.class, id);
		return KeggReactionEntity.class.cast(object);
	}

	@Override
	public KeggReactionEntity getReactionByEntry(String entry) {
		KeggReactionEntity rxn = null;
		Criteria criteria = this.getSession().createCriteria(KeggReactionEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) {
			LOGGER.error("Entry uniqueness fail multiple records found for [" + entry + "]");
		}
		
		for (Object o: res) rxn = KeggReactionEntity.class.cast(o);
		
		return rxn;
	}

	@Override
	public KeggReactionEntity saveReaction(KeggReactionEntity reaction) {
		this.getSession().save(reaction);
		return reaction;
	}

	@Override
	public Set<Long> getAllReactionIds() {
		Query query = this.getSession().createQuery("SELECT rxn.id FROM KeggReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<Long> res = new HashSet<> (query.list());
		return res;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		Query query = this.getSession().createQuery("SELECT rxn.entry FROM KeggReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<String> res = new HashSet<> (query.list());
		return res;
	}

}
