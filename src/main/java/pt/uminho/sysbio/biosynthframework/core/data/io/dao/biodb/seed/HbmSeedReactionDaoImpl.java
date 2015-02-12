package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class HbmSeedReactionDaoImpl implements ReactionDao<SeedReactionEntity>{

	private SessionFactory sessionFactory;
	
	public HbmSeedReactionDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public SeedReactionEntity getReactionById(Long id) {
		Object o = sessionFactory.getCurrentSession().get(SeedReactionEntity.class, id);
		return SeedReactionEntity.class.cast(o);
	}

	@Override
	public SeedReactionEntity getReactionByEntry(String entry) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(SeedReactionEntity.class);
		Object o = criteria.add(Restrictions.eq("entry", entry)).uniqueResult();
		if (o == null) return null;
		return (SeedReactionEntity) o;
	}

	@Override
	public SeedReactionEntity saveReaction(SeedReactionEntity reaction) {
		sessionFactory.getCurrentSession().save(reaction);
		return reaction;
	}

	@Override
	public Set<Long> getAllReactionIds() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("SELECT rxn.id FROM SeedReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<Long> res = new HashSet<> (query.list());
		return res;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		Query query = this.sessionFactory.getCurrentSession().createQuery("SELECT rxn.entry FROM SeedReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<String> res = new HashSet<> (query.list());
		return res;
	}

}
