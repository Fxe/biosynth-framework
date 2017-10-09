package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class HbmMnxReactionDaoImpl implements ReactionDao<MnxReactionEntity> {

	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}
	
	@Autowired
	public HbmMnxReactionDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public MnxReactionEntity getReactionById(Long id) {
		Object rxn = this.getSession().get(MnxReactionEntity.class, id);
		return MnxReactionEntity.class.cast(rxn);
	}

	@Override
	public MnxReactionEntity getReactionByEntry(String entry) {
//		MnxReactionEntity rxn = null;
		Criteria criteria = this.getSession().createCriteria(MnxReactionEntity.class);
		Object res = criteria.add(Restrictions.eq("entry", entry)).uniqueResult();

		return MnxReactionEntity.class.cast(res);
	}

	@Override
	public MnxReactionEntity saveReaction(MnxReactionEntity reaction) {
		this.getSession().save(reaction);
		return reaction;
	}

	@Override
	public Set<Long> getAllReactionIds() {
		Query query = this.getSession().createQuery("SELECT rxn.id FROM MnxReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<Long> res = new HashSet<> (query.list());
		return res;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		Query query = this.getSession().createQuery("SELECT rxn.entry FROM MnxReactionEntity rxn");
		@SuppressWarnings("unchecked")
		Set<String> res = new HashSet<> (query.list());
		return res;
	}

}
