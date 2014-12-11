package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class MetaboliteClusterQualityScreener implements EtlQualityScreen<IntegratedCluster> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetaboliteClusterQualityScreener.class);
	
	private MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao;
	
	@Autowired
	public MetaboliteClusterQualityScreener(
			MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteDao) {
		
		this.metaboliteDao = metaboliteDao;
	}
	
	@Override
	public void evaluate(IntegratedCluster entity) {
		Set<MetaboliteQualityLabel> qualityLabels = this.yay(entity);
		
		System.out.println(qualityLabels);
	}
	
	public Set<MetaboliteQualityLabel> yay(IntegratedCluster integratedCluster) {
		
		List<GraphMetaboliteEntity> cpdList = new ArrayList<> ();
	
		LOGGER.debug("Load Metabolites ...");
		for (IntegratedClusterMember member : integratedCluster.getMembers()) {
			Long referenceId = member.getMember().getReferenceId();
			GraphMetaboliteEntity cpd = metaboliteDao.getMetaboliteById("", referenceId);
			
			if (cpd != null) {
				cpdList.add(cpd);
			}
		}
		
		return this.yay(cpdList);
	}
	
	public Set<MetaboliteQualityLabel> yay(List<GraphMetaboliteEntity> cpdList) {
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		if (cpdList.isEmpty()) {
			LOGGER.warn("String empty cluster.");	
			return qualityLabels;
		}
		
		qualityLabels.addAll(verifyFormulas(cpdList));
		qualityLabels.addAll(verifyChemicalCharges(cpdList));
		qualityLabels.addAll(verifyStructure(cpdList));
		qualityLabels.addAll(verifyCrossreference(cpdList));
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyFormulas(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking formulas ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.MolecularFormula.toString());
		
		if (occurenceMap.size() > 1) qualityLabels.add(MetaboliteQualityLabel.FORMULA_MISMATCH);
//		for (GraphMetaboliteEntity cpd: cpdList) {
//			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> property : 
//				cpd.getPropertyEntities()) {
//				if (property.getLeft().getLabels().contains(MetabolitePropertyLabel.MolecularFormula.toString())) {
//					System.out.println(property.getLeft());
//				}
//			}
//		}
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyChemicalCharges(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking chemical charge ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.Charge.toString());
		
		if (occurenceMap.size() > 1) qualityLabels.add(MetaboliteQualityLabel.CHARGE_MISMATCH);
		
		return qualityLabels;
	}
	
	public Set<MetaboliteQualityLabel> verifyStructure(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking structure ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		Map<Object, Integer> occurenceMap = this.collectPropertyKeys(cpdList, MetabolitePropertyLabel.InChI.toString());
		
//		for (GraphMetaboliteEntity cpd: cpdList) {
//			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> property : 
//				cpd.getPropertyEntities()) {
//				if (property.getLeft().getLabels().contains(MetabolitePropertyLabel.InChI.toString())) {
//					System.out.println(property.getLeft());
//				}
//			}
//		}
		
		for (GraphMetaboliteEntity cpd: cpdList) {
			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> property : 
				cpd.getPropertyEntities()) {
				if (property.getLeft().getLabels().contains(MetabolitePropertyLabel.SMILES.toString())) {
					System.out.println(property.getLeft());
				}
			}
		}
		
		return qualityLabels;
	}
	
	private Map<Object, Integer> collectPropertyKeys(List<GraphMetaboliteEntity> cpdList, String majorLabel) {
		Map<Object, Integer> occurenceMap = new HashMap<> ();
		
		for (GraphMetaboliteEntity cpd: cpdList) {
			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> property : 
				cpd.getPropertyEntities()) {
				if (property.getLeft().getLabels().contains(majorLabel)) {
					LOGGER.debug("Found: " + property.getLeft());
					
					GraphPropertyEntity propertyEntity = property.getLeft(); 
					CollectionUtils.increaseCount(occurenceMap, propertyEntity.getProperty("key", null), 1);
				}
			}
		}
		
		LOGGER.debug("Total: " + occurenceMap);
		
		return occurenceMap;
	}
	
	public Set<MetaboliteQualityLabel> verifyCrossreference(List<GraphMetaboliteEntity> cpdList) {
		LOGGER.debug("Checking cross-reference ...");
		Set<MetaboliteQualityLabel> qualityLabels = new HashSet<> ();
		
		for (GraphMetaboliteEntity cpd : cpdList) {
			for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyEntity : cpd.getCrossreferences()) {
				System.out.println(proxyEntity);
			}
		}
		
		return qualityLabels;
	}
}
