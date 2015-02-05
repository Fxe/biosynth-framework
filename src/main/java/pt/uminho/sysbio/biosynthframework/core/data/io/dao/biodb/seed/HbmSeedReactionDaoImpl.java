package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed;

import java.util.Set;

import org.hibernate.SessionFactory;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SeedReactionEntity saveReaction(SeedReactionEntity reaction) {
		sessionFactory.getCurrentSession().save(reaction);
		return reaction;
	}

	@Override
	public Set<Long> getAllReactionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		// TODO Auto-generated method stub
		return null;
	}

}
