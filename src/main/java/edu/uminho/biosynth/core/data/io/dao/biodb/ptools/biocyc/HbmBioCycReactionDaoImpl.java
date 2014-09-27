package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.AbstractHibernateDao;

public class HbmBioCycReactionDaoImpl 
extends AbstractHibernateDao implements ReactionDao<BioCycReactionEntity> {

	private String pgdb = "META";
	
	public String getPgdb() { return pgdb;}
	public void setPgdb(String pgdb) { this.pgdb = pgdb;}
	
	@Override
	public BioCycReactionEntity getReactionById(Long id) {
		Object rxnObj = this.getSession().get(BioCycReactionEntity.class, id);
		return BioCycReactionEntity.class.cast(rxnObj);
	}

	@Override
	public BioCycReactionEntity getReactionByEntry(String entry) {
		BioCycReactionEntity rxn = null;
		Criteria criteria = this.getSession().createCriteria(BioCycReactionEntity.class);
		List<?> res = criteria.add(Restrictions.eq("entry", entry)).list();
		if (res.size() > 1) throw new RuntimeException("Entry uniqueness fail multiple records found for [" + entry + "]");
		for (Object o: res) {
			rxn = BioCycReactionEntity.class.cast(o);
		}
		return rxn;
	}

	@Override
	public BioCycReactionEntity saveReaction(BioCycReactionEntity reaction) {
		this.getSession().save(reaction);
		return reaction;
	}

	@Override
	public Set<Long> getAllReactionIds() {
		Query query = this.getSession().createQuery("SELECT rxn.id FROM BioCycReactionEntity rxn WHERE rxn.source = :source");
		query.setParameter("source", pgdb);
		@SuppressWarnings("unchecked")
		Set<Long> res = new HashSet<> (query.list());
		return res;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		Query query = this.getSession().createQuery("SELECT rxn.entry FROM BioCycReactionEntity rxn WHERE rxn.source = :source");
		query.setParameter("source", pgdb);
		@SuppressWarnings("unchecked")
		Set<String> res = new HashSet<> (query.list());
		return res;
	}

}
