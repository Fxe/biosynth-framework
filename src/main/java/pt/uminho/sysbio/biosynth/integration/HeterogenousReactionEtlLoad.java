package pt.uminho.sysbio.biosynth.integration;

import pt.uminho.sysbio.biosynth.integration.etl.EtlLoad;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;
import edu.uminho.biosynth.core.components.Reaction;

public class HeterogenousReactionEtlLoad<R extends Reaction> implements EtlLoad<R> {

	private final ReactionHeterogeneousDao<R> heterogeneousDao;
	
	public HeterogenousReactionEtlLoad(ReactionHeterogeneousDao<R> heterogeneousDao) {
		this.heterogeneousDao = heterogeneousDao;
	}
	
	@Override
	public void etlLoad(R reaction) {
		this.heterogeneousDao.saveReaction("", reaction);
	}

}
