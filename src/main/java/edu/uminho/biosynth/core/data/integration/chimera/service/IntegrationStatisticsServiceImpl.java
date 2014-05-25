package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataMetabolitePropertyEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundPropertyLabel;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class IntegrationStatisticsServiceImpl implements IntegrationStatisticsService {

	private static final Logger LOGGER = Logger.getLogger(IntegrationStatisticsServiceImpl.class);
	
	@Autowired
	private IntegrationDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	
	public IntegrationDataDao getData() { return data;}
	public void setData(IntegrationDataDao data) { this.data = data;}

	public ChimeraMetadataDao getMeta() { return meta;}
	public void setMeta(ChimeraMetadataDao meta) { this.meta = meta;}
	
	@Override
	public int countTotalMetaboliteMembers() {
		return this.data.countByLabel("Compound");
	}

	@Override
	public int countIntegratedMetaboliteMembers(IntegrationSet integrationSet) {
//		this.data.countByLabel("Compound");
		return this.meta.getAllIntegratedMembersId(integrationSet, true).size();
	}

	@Override
	public Map<String, Integer> countTotalMetaboliteMembersByMajor() {
		Map<String, Integer> count = new HashMap<> ();
		for (String major : this.data.getAllMajorMetaboliteLabels()) {
			count.put(major, this.data.countByLabel(major));
		}
		return count;
	}

	@Override
	public Map<String, Integer> countIntegratedMetaboliteMembersByMajor(IntegrationSet integrationSet) {
		Map<String, Integer> count = new HashMap<> ();
		Set<Long> allIds = new HashSet<> (this.meta.getAllIntegratedMembersId(integrationSet, true));
		for (String major : this.data.getAllMajorMetaboliteLabels()) {
			Set<Long> aux_ = new HashSet<> (allIds);
			aux_.retainAll( this.data.getEntitiesByLabel(major));
			count.put(major, aux_.size());
		}
		
		return count;
	}
	
	public int countProperties(IntegratedCluster integratedCluster, String property, String uniqueKey) {
//		int count = 0;
		
		Set<Object> objects = new HashSet<> ();
		for (CentralDataMetabolitePropertyEntity propertyEntity : this.data.collectAllPropertyFromIds(
				property, 
				uniqueKey, 
				integratedCluster.listAllIntegratedMemberIds().toArray(new Long[0]))) {
			objects.add(propertyEntity.getUniqueKeyValue());
		}
		
		return objects.size();
	}
	
	public Map<Integer, Integer> propertieHistogram(IntegrationSet integrationSet, String property, String uniqueKey) {
		Map<Integer, Integer> count = new HashMap<> ();
		
		for (Long cid : integrationSet.getIntegratedClustersMap().keySet()) {
			IntegratedCluster integratedCluster = integrationSet.getIntegratedClustersMap().get(cid);
			int c = countProperties(integratedCluster, property, uniqueKey);
			
			if (!count.containsKey(c)) count.put(c, 0);
			int prev = count.get(c);
			count.put(c, ++prev);
			
		}
		return count;
	}

	@Override
	public Map<Integer, Integer> getIntegratedClusterPropertyFrequency(
			IntegrationSet integrationSet, String property) {
		
		IntegrationSet integrationSet_ = this.meta.getIntegrationSet(integrationSet.getId());
		Map<Integer, Integer> frequency = new HashMap<> ();
		
		for (Long cid : integrationSet_.getIntegratedClustersMap().keySet()) {
			System.out.println(cid);
			List<Long> eids = integrationSet_.getIntegratedClustersMap().get(cid).listAllIntegratedMemberIds();
			Set<Long> propIdList = this.data.collectEntityProperties(eids, property);
			for (Long omg : propIdList) System.out.println(this.data.getMetaboliteProperty(omg));
			System.out.println("==================================");
		}
		return frequency;
	}
}
