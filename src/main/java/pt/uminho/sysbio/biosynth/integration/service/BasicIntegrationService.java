package pt.uminho.sysbio.biosynth.integration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;

@Transactional(readOnly=true, value="neo4jMetaTransactionManager")
public class BasicIntegrationService implements IntegrationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicIntegrationService.class);
	
	
	protected IntegrationMetadataDao meta;
	
	public IntegrationMetadataDao getMeta() { return meta;}
	public void setMeta(IntegrationMetadataDao meta) { this.meta = meta;}
	
	@Autowired
	public BasicIntegrationService(IntegrationMetadataDao meta) {
		this.meta = meta;
	}

	@Override
	public IntegrationSet getIntegrationSetByEntry(String entry) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(entry);
		
		if (integrationSet == null) {
			LOGGER.warn(String.format("Integration Set [%s] not found", entry));
			return null;
		}
		
		return integrationSet;
	}
	
	@Override
	public IntegrationSet getIntegrationSetById(Long id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		
		if (integrationSet == null) {
			LOGGER.warn(String.format("Integration Set [%d] not found", id));
			return null;
		}
		
		return integrationSet;
	}
	@Override
	public IntegrationSet createIntegrationSet(String name, String description) {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(name);
		integrationSet.setDescription(description);
		meta.saveIntegrationSet(integrationSet);
		return integrationSet;
	}

	@Override
	public void resetIntegrationSet(IntegrationSet integrationSet) {
		LOGGER.info(String.format("Reset atempt to Integration Set %s", integrationSet));
		
		List<Long> clusters = new ArrayList<> (integrationSet.getIntegratedClustersMap().keySet());
		for (Long clusterId : clusters){
			IntegratedCluster cluster = integrationSet.getIntegratedClustersMap().remove(clusterId);
			LOGGER.info(String.format("Removing cluster %s", cluster));
			this.meta.deleteCluster(cluster);
		}
		
//		this.meta.saveIntegrationSet(currentIntegrationSet);
	}

	@Override
	public void deleteIntegrationSet(IntegrationSet integrationSet) {
		this.meta.deleteIntegrationSet(integrationSet);
	}

	@Override
	public List<IntegrationSet> getAllIntegrationSets() {
		List<IntegrationSet> res = new ArrayList<> ();
		for (Long id : this.meta.getAllIntegrationSetsId()) {
			res.add(this.meta.getIntegrationSet(id));
		}
		return res;
	}
	
	@Override
	public List<String> getAllIntegrationSetsEntries() {
		return this.meta.getAllIntegrationSetsEntries();
	}
	@Override
	public List<Long> getAllIntegrationSetsIds() {
		return this.meta.getAllIntegrationSetsId();
	}

	@Override
	public IntegratedCluster getIntegratedClusterById(long id) {
		IntegratedCluster integratedCluster = meta.getIntegratedClusterById(id);
		return integratedCluster;
	}
	
	@Override
	public IntegratedCluster getIntegratedClusterByEntry(String entry, long iid) {
		IntegratedCluster integratedCluster = meta.getIntegratedClusterByEntry(entry, iid);
		return integratedCluster;
	}
	@Override
	public IntegratedMember getIntegratedMemberById(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<IntegrationSet, Set<IntegratedCluster>> findIntegratedClusterByMemberReferenceId(
			long refId) {
		Map<IntegrationSet, Set<IntegratedCluster>> a = new HashMap<> ();
		for (long iidId : this.meta.getAllIntegrationSetsId()) {
			IntegrationSet integrationSet = this.meta.getIntegrationSet(iidId);
			List<IntegratedCluster> ctr = this.meta.getIntegratedClusterByMemberIds(iidId, new Long[]{refId});
			System.out.println(integrationSet);
			System.out.println(ctr.size());
			a.put(integrationSet, new HashSet<> (ctr));
		}
		
		return a;
	}

}
