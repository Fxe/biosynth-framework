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

import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationDataDao;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class IntegrationStatisticsServiceImpl implements IntegrationStatisticsService {

	private static final Logger LOGGER = Logger.getLogger(IntegrationStatisticsServiceImpl.class);
	
	@Autowired
	private IntegrationDataDao data;
	@Autowired
	private IntegrationMetadataDao meta;
	
	public IntegrationDataDao getData() { return data;}
	public void setData(IntegrationDataDao data) { this.data = data;}

	public IntegrationMetadataDao getMeta() { return meta;}
	public void setMeta(IntegrationMetadataDao meta) { this.meta = meta;}
	
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
		for (GraphPropertyEntity propertyEntity : this.data.collectAllPropertyFromIds(
				property, 
				uniqueKey, 
				integratedCluster.listAllIntegratedMemberIds().toArray(new Long[0]))) {
//			objects.add(propertyEntity.getUniqueKeyValue());
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
	@Override
	public Map<String, Integer> getIntegratedClusterDatabaseFreq(
			IntegratedCluster integratedCluster) {
		Map<String, Integer> res = new HashMap<> ();
		
		Set<String> majors = new HashSet<> ();
		for (MetaboliteMajorLabel m : MetaboliteMajorLabel.values()) {
			majors.add(m.toString());
		}
		
		for (Long eid : integratedCluster.listAllIntegratedMemberIds()) {
			Set<String> labels = this.data.collectEntityLabels(eid);
			if (labels.contains("KEGG")) {
				labels.remove("KEGG");
				Map<String, Object> props = this.data.getEntryProperties(eid);
				String entry = (String) props.get("entry");
				switch (entry.charAt(0)) {
					case 'C':
						labels.add(MetaboliteMajorLabel.LigandCompound.toString());
						break;
					case 'D':
						labels.add(MetaboliteMajorLabel.LigandDrug.toString());
						break;
					case 'G':
						labels.add(MetaboliteMajorLabel.LigandGlycan.toString());
						break;
					default:
						labels.add("KEGG");
						break;
				}
			}
			
			labels.retainAll(majors);
			
			if (labels.size() > 1) {
				LOGGER.warn("Multiple major labels found for " + eid);
			} else if (labels.isEmpty()) {
				LOGGER.warn("No major labels found for " + eid);
				labels.add("Unknown");
			}
			String major = labels.iterator().next();
			if (!res.containsKey(major)) {
				res.put(major, 1);
			} else {
				res.put(major, res.get(major) + 1);
			}
		}
		
		return res;
	}
}
