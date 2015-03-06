package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.curation.CurationOperation;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;
import pt.uminho.sysbio.biosynth.integration.curation.CurationUser;
import pt.uminho.sysbio.biosynth.integration.etl.MetaboliteQualityLabel;
import pt.uminho.sysbio.biosynth.integration.etl.ReactionQualityLabel;
import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModel;
import pt.uminho.sysbio.biosynthframework.DefaultMetaboliteSpecie;
import pt.uminho.sysbio.biosynthframework.DefaultModelMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.DefaultSubcellularCompartmentEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;

public class Neo4jMapper {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jMapper.class);
	
	public static CurationOperation nodeToCurationOperation(Node oidNode) {
		if (oidNode == null) return null;
		LOGGER.debug(String.format("Mapping %s:%s to CurationOperation", oidNode, Neo4jUtils.getLabels(oidNode)));
		
		if (!oidNode.hasLabel(CurationLabel.CurationOperation)) {
			LOGGER.error(String.format("Invalid node. Expected %s got %s", 
					CurationLabel.CurationOperation, Neo4jUtils.getLabels(oidNode)));;
			return null;
		}
		
		CurationOperation curationOperation = new CurationOperation();
		curationOperation.setId(oidNode.getId());
		curationOperation.setEntry((String) oidNode.getProperty("entry"));
		curationOperation.setCreatedAt((long) oidNode.getProperty("created_at"));
		curationOperation.setClusterType((String) oidNode.getProperty("cluster_type"));
		curationOperation.setOperationType((String) oidNode.getProperty("operation_type"));
		
		return curationOperation;
	}
	
	public static CurationOperation nodeToCurationMetabolite(Node oidNode) {
		if (oidNode == null) {
			LOGGER.debug("Invalid curation set node: null");
			return null;
		}
		LOGGER.debug(String.format("Mapping %s:%s to CurationMetabolite", oidNode, Neo4jUtils.getLabels(oidNode)));
		if (!oidNode.hasLabel(CurationLabel.CurationMetabolite)) {
			LOGGER.debug(String.format("Invalid curation metabolite node: ", Neo4jUtils.getLabels(oidNode)));
			return null;
		}
		
		CurationOperation curationCluster = nodeToCurationOperation(oidNode);
		
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
	
	public static IntegrationSet nodeToIntegrationSet(Node node) {
		if (node == null || !node.hasLabel(IntegrationNodeLabel.IntegrationSet)) return null;
		
		LOGGER.debug("Loading integration set: " + node);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setId(node.getId());
		integrationSet.setDescription((String) node.getProperty("description", null));
		integrationSet.setEntry((String) node.getProperty("entry"));
		integrationSet.setName((String) node.getProperty("name", null));
		return integrationSet;
	}
	
	public static IntegratedCluster nodeToIntegratedCluster(Node node) {
		if (node == null) return null;
		LOGGER.debug(String.format("Mapping %s:%s to IntegratedCluster", node, Neo4jUtils.getLabels(node)));
//		if (node == null || !node.hasLabel(IntegrationNodeLabel.MetaboliteCluster)) return null;
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		if (node.hasLabel(IntegrationNodeLabel.MetaboliteCluster) &&
				node.hasLabel(IntegrationNodeLabel.ReactionCluster)) {
			LOGGER.warn("Invalid type definition: MetaboliteCluster and ReactionCluster are exclusive");
			integratedCluster.setClusterType("ERROR");
		} else if (node.hasLabel(IntegrationNodeLabel.MetaboliteCluster)) {
			integratedCluster.setClusterType(IntegrationNodeLabel.MetaboliteCluster);
		} else if (node.hasLabel(IntegrationNodeLabel.ReactionCluster)) {
			integratedCluster.setClusterType(IntegrationNodeLabel.ReactionCluster);
		} else {
			LOGGER.warn(String.format("Invalid type definition %s expected MetaboliteCluster or ReactionCluster", Neo4jUtils.getLabels(node)));
			integratedCluster.setClusterType("ERROR");
		}
//		String clusterType = node.getLabels().iterator().next().toString();
		integratedCluster.setId(node.getId());
		
		integratedCluster.setDescription((String) node.getProperty("description", null));
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
		LOGGER.trace(String.format("Properties: ", Neo4jUtils.getPropertiesMap(node)));
		IntegratedMember integratedMember = new IntegratedMember();
		String memberType = node.getLabels().iterator().next().toString();
		integratedMember.setId(node.getId());
		integratedMember.setReferenceId((Long)node.getProperty(Neo4jDefinitions.MEMBER_REFERENCE, null));
		integratedMember.setEntry((String) node.getProperty("entry", null));
		integratedMember.setMemberType(memberType);
		integratedMember.setDescription((String) node.getProperty("description", null));
		integratedMember.setSource((String) node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, null));
		return integratedMember;
	}

	public static CurationUser nodeToCurationUser(Node usrNode) {
		if (usrNode == null) {
			LOGGER.debug("Invalid curation user node: null");
			return null;
		}
		LOGGER.debug(String.format("Mapping %s:%s to CurationUser", usrNode, Neo4jUtils.getLabels(usrNode)));
		if (!usrNode.hasLabel(CurationLabel.CurationUser)) {
			LOGGER.debug(String.format("Invalid curation user node: ", Neo4jUtils.getLabels(usrNode)));
			return null;
		}
		
		CurationUser curationUser = new CurationUser();
		curationUser.setId(usrNode.getId());
		curationUser.setUsername((String) usrNode.getProperty("username"));
		
		return curationUser;
	}

	public static void updateNodeWithIntegratedCluster(Node cidNode,
			IntegratedCluster cid) {
		// TODO Auto-generated method stub
		
	}
	
	public static void updateNodeWithIntegratedMember(Node cidNode,
			IntegratedMember eid) {
		throw new RuntimeException("Not implemented !!! ups :)");
	}

	public static void nodeToAbstractGraphNodeEntity(
			AbstractGraphNodeEntity nodeEntity, Node node) {
		nodeEntity.setId(node.getId());
		nodeEntity.setMajorLabel((String) node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
		nodeEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
		for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
			
		}
	}

	public static OptfluxContainerMetabolicModelEntity nodeToMetabolicModel(Node node) {
		OptfluxContainerMetabolicModelEntity mmd = new OptfluxContainerMetabolicModelEntity();
		mmd.setId(node.getId());
		mmd.setEntry((String) node.getProperty("entry"));
		return mmd;
	}

	public static DefaultMetaboliteSpecie nodeToMetaboliteSpecie(Node node) {
		DefaultMetaboliteSpecie spi = new DefaultMetaboliteSpecie();
		spi.setId(node.getId());
		spi.setEntry(((String) node.getProperty("entry")).split("@")[0]);
		spi.setName((String) node.getProperty("name", null));
		spi.setComparment((String) node.getProperty("comparment", null));
		spi.setFormula((String) node.getProperty("formula", null));
		return spi;
	}

	public static DefaultSubcellularCompartmentEntity nodeToSubcellularCompartment(
			Node node) {
		DefaultSubcellularCompartmentEntity cmp = new DefaultSubcellularCompartmentEntity();
		cmp.setId(node.getId());
		cmp.setEntry(((String) node.getProperty("entry")).split("@")[0]);
		cmp.setName((String) node.getProperty("name", null));
		return cmp;
	}

	public static OptfluxContainerReactionEntity nodeToModelReaction(Node node) {
		OptfluxContainerReactionEntity rxn = new OptfluxContainerReactionEntity();
		rxn.setId(node.getId());
		rxn.setEntry(((String) node.getProperty("entry")).split("@")[0]);
		rxn.setName((String) node.getProperty("name", null));
		rxn.setGeneRule((String) node.getProperty("geneRule", null));
		rxn.setLowerBound((Double) node.getProperty("lowerBound", null));
		rxn.setUpperBound((Double) node.getProperty("upperBound", null));
		return rxn;
	}

	public static DefaultModelMetaboliteEntity nodeToModelMetabolite(Node node) {
		DefaultModelMetaboliteEntity cpd = new DefaultModelMetaboliteEntity();
		cpd.setId(node.getId());
		cpd.setEntry(((String) node.getProperty("entry")).split("@")[0]);
		cpd.setName((String) node.getProperty("name", null));
		cpd.setFormula((String) node.getProperty("formula", null));
		return cpd;
	}
}
