package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.Reaction;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class DefaultReactionEtlExtract<R extends Reaction> implements EtlExtract<R> {

	private final ReactionDao<R> reactionDao;
	
	public DefaultReactionEtlExtract(ReactionDao<R> reactionDao) {
		this.reactionDao = reactionDao;
	}
	
	@Override
	public R extract(Serializable id) {
		R entity = null;
		
		if (id instanceof String) {
			entity = this.reactionDao.getReactionByEntry((String) id);
		} else if (id instanceof Long) {
			entity = this.reactionDao.getReactionById((Long) id);
		} else {
			
		}
		return entity;
	}

	@Override
	public List<Serializable> getAllKeys() {
		List<Serializable> keys = new ArrayList<> ();
		for (String entry : this.reactionDao.getAllReactionEntries()) keys.add(entry);
		return keys;
	}

	@Override
	public List<R> extractAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
