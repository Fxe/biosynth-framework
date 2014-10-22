package pt.uminho.sysbio.biosynth.integration;

import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;
import pt.uminho.sysbio.biosynthframework.Reaction;

public class IntegratedReactionFactory<R extends Reaction> {
	
	private ReactionHeterogeneousDao<R> heterogeneousDao;
	
	public IntegratedReactionFactory<R> withHeterogeneousDao(ReactionHeterogeneousDao<R> heterogeneousDao) {
		this.heterogeneousDao = heterogeneousDao;
		return this;
	}
	
	public IntegratedReactionFactory<R> withIntegratedCluster(IntegratedCluster integratedCluster) {
		integratedCluster.getEntry();
		integratedCluster.getDescription();
		integratedCluster.getClusterType();
		integratedCluster.getId();
		integratedCluster.getIntegrationSet().getName(); //source
		
		for (IntegratedClusterMember integratedClusterMember : integratedCluster.getMembers()) {
			IntegratedMember integratedMember = integratedClusterMember.getMember();
			Long eid = integratedMember.getId();
//			heterogeneousDao.getMetaboliteById(tag, eid);
		}
		
		return this;
	}
	
	public IntegratedReactionEntity build() {
		IntegratedReactionEntity entity = new IntegratedReactionEntity();
//		entity.setEntry(entry);
//		entity.setId(id);
//		entity.setDescription(description);
//		entity.setSource(source);
		
		
		
//		entity.setName(name);
		
		
		return null;
	}
}
