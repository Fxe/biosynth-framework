package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Set;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.curation.CurationOperation;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.curation.CurationRelationship;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;
import pt.uminho.sysbio.biosynth.integration.etl.MetaboliteQualityLabel;
import pt.uminho.sysbio.biosynth.integration.etl.ReactionQualityLabel;

public class Neo4jMapper {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jMapper.class);
	
	public static CurationOperation nodeToCurationCluster(Node node) {
		if (node == null) return null;
		LOGGER.debug(String.format("Mapping %s:%s to CurationCluster", node, Neo4jUtils.getLabels(node)));
		
		if (!node.hasLabel(CurationLabel.CurationMetabolite)) {
			LOGGER.warn(String.format("Invalid node. Expected %s got %s", 
					CurationLabel.CurationMetabolite, Neo4jUtils.getLabels(node)));;
		}
		
		CurationOperation curationCluster = new CurationOperation();
		Set<Node> a = Neo4jUtils.collectNodeRelationshipNodes(node, CurationRelationship.Curation);
		if (a.isEmpty()) {
			LOGGER.warn(String.format("Unable to load curation set for ") + node);
			return null;
		}
		
		CurationSet curationSet = null;
		for (Node curationSetNode : a) {
			if (curationSet != null) LOGGER.warn(":(:(:(:(:");
			curationSet = nodeToCurationSet(curationSetNode);
		}
		
		curationCluster.setCurationSet(curationSet);
		
		Set<Node> cidIdSet = Neo4jUtils.collectNodeRelationshipNodes(node, CurationRelationship.CurationOperation);
		for (Node cidNode : cidIdSet) {
			IntegratedCluster integratedCluster = Neo4jMapper.nodeToIntegratedCluster(cidNode);
			for (Node eidNode : Neo4jUtils.collectNodeRelationshipNodes(cidNode, IntegrationRelationshipType.Integrates)) {
				IntegratedMember integratedMember = Neo4jMapper.nodeToIntegratedMember(eidNode);
				IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
				integratedClusterMember.setCluster(integratedCluster);
				integratedClusterMember.setMember(integratedMember);
				integratedCluster.getMembers().add(integratedClusterMember);
			}
			curationCluster.getIntegratedClusters().add(integratedCluster);
		}
		
		return curationCluster;
	}
	
	public static CurationSet nodeToCurationSet(Node node) {
		if (!node.hasLabel(CurationLabel.CurationSet)) {
			LOGGER.warn(String.format("Invalid node. Expected %s got %s", 
					CurationLabel.CurationSet, Neo4jUtils.getLabels(node)));;
			return null;
		}
		
		CurationSet curationSet = new CurationSet();
		curationSet.setId(node.getId());
		curationSet.setEntry((String) node.getProperty("entry"));
		
		return curationSet;
	}
	
//	public CurationSet nodeToCurationSet(Node node) {
//	if (node == null) {
//		LOGGER.debug("Invalid curation set node: null");
//		return null;
//	}
//	
//	if (!node.hasLabel(CurationLabel.CurationSet)) {
//		LOGGER.debug(String.format("Invalid curation set node: ", Neo4jUtils.getLabels(node)));
//		return null;
//	}
//	
//	CurationSet curationSet = new CurationSet();
//	curationSet.setId(node.getId());
//	curationSet.setEntry((String)node.getProperty("entry"));
//	
//	return curationSet;
//}
	
	public static IntegrationSet nodeToIntegrationSet(Node node) {
		if (node == null || !node.hasLabel(IntegrationNodeLabel.IntegrationSet)) return null;
		
		LOGGER.debug("Loading integration set: " + node);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setId(node.getId());
		integrationSet.setDescription((String) node.getProperty("description"));
		integrationSet.setName((String) node.getProperty("entry"));
		return integrationSet;
	}
	
	public static IntegratedCluster nodeToIntegratedCluster(Node node) {
		if (node == null) return null;
		LOGGER.debug(String.format("Mapping %s:%s to IntegratedCluster", node, Neo4jUtils.getLabels(node)));
//		if (node == null || !node.hasLabel(IntegrationNodeLabel.MetaboliteCluster)) return null;
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		String clusterType = node.getLabels().iterator().next().toString();
		integratedCluster.setId(node.getId());
		integratedCluster.setClusterType(clusterType);
		integratedCluster.setDescription((String) node.getProperty("description", ""));
		integratedCluster.setEntry((String) node.getProperty("entry"));
		
		for (Label label : node.getLabels()) {
			try {
				MetaboliteQualityLabel qLabel = MetaboliteQualityLabel.valueOf(label.toString());
				IntegratedClusterMeta integratedClusterMeta = new IntegratedClusterMeta();
				integratedClusterMeta.setMetaType(qLabel.toString());
				integratedCluster.getMeta().put(integratedClusterMeta.getMetaType(), integratedClusterMeta);
			} catch (Exception e) {
				LOGGER.trace("not metabolite label .. " + label);
			}
			
			try {
				ReactionQualityLabel qLabel = ReactionQualityLabel.valueOf(label.toString());
				IntegratedClusterMeta integratedClusterMeta = new IntegratedClusterMeta();
				integratedClusterMeta.setMetaType(qLabel.toString());
				integratedCluster.getMeta().put(integratedClusterMeta.getMetaType(), integratedClusterMeta);
			} catch (Exception e) {
				LOGGER.trace("not reaction label .. " + label);
			}
		}
		
		return integratedCluster;
	}
	
	public static IntegratedMember nodeToIntegratedMember(Node node) {
		if (node == null) return null;
		LOGGER.debug(String.format("Mapping %s:%s to IntegratedMember", node, Neo4jUtils.getLabels(node)));
//		if (node == null || !node.hasLabel(IntegrationNodeLabel.MetaboliteMember)) return null;
		
		IntegratedMember integratedMember = new IntegratedMember();
		String memberType = node.getLabels().iterator().next().toString();
		integratedMember.setId(node.getId());
		integratedMember.setReferenceId((Long)node.getProperty("id", null));
		integratedMember.setEntry((String) node.getProperty("entry", null));
		integratedMember.setMemberType(memberType);
		integratedMember.setDescription((String) node.getProperty("description", null));
		return integratedMember;
	}

	public static CurationOperation nodeToCurationMetabolite(Node oidNode) {
		if (oidNode == null) {
			LOGGER.trace("Invalid curation set node: null");
			return null;
		}
		LOGGER.debug(String.format("Mapping %s:%s to CurationMetabolite", oidNode, Neo4jUtils.getLabels(oidNode)));
		if (!oidNode.hasLabel(CurationLabel.CurationMetabolite)) {
			LOGGER.debug(String.format("Invalid curation set node: ", Neo4jUtils.getLabels(oidNode)));
			return null;
		}
		
		CurationOperation curationCluster = new CurationOperation();
		curationCluster.setId(oidNode.getId());
		curationCluster.setEntry((String)oidNode.getProperty("entry"));
		
		return curationCluster;
	}
}
