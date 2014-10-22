package pt.uminho.sysbio.biosynth.integration;

import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynthframework.Metabolite;

public class IntegratedMetaboliteFactory<M extends Metabolite> {
	
	private MetaboliteHeterogeneousDao<M> heterogeneousDao = null;
	
	private Long id;
	private String entry;
	private String description;
	private String source;
	
	public IntegratedMetaboliteFactory<M> withHeterogeneousDao(MetaboliteHeterogeneousDao<M> heterogeneousDao) {
		this.heterogeneousDao = heterogeneousDao;
		return this;
	}
	
	public IntegratedMetaboliteFactory<M> withIntegratedCluster(IntegratedCluster integratedCluster) {
		if (integratedCluster == null) return this;
		
		id = integratedCluster.getId();
		entry = integratedCluster.getEntry();
		description = integratedCluster.getDescription();
		source = integratedCluster.getIntegrationSet().getName(); //source
		
		integratedCluster.getClusterType();
		
		
		
		for (IntegratedClusterMember integratedClusterMember : integratedCluster.getMembers()) {
			IntegratedMember integratedMember = integratedClusterMember.getMember();
			Long eid = integratedMember.getId();
//			heterogeneousDao.getMetaboliteById(tag, eid);
		}
		
		return this;
	}
	
	public IntegratedMetaboliteFactory<M> withar(IntegratedCluster integratedCluster) {
		
		return this;
	}
	
	public IntegratedMetaboliteEntity build() {
		IntegratedMetaboliteEntity entity = new IntegratedMetaboliteEntity();
		entity.setEntry(entry);
		entity.setId(id);
		entity.setDescription(description);
		entity.setSource(source);
		
//		entity.setName(name);
		
		return entity;
	}
}
