package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.curation.CurationOperation;
import pt.uminho.sysbio.biosynth.integration.curation.CurationRelationship;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationCurationDao;

public class Neo4jIntegrationCurationDaoImpl implements IntegrationCurationDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jIntegrationCurationDaoImpl.class);
	
	private GraphDatabaseService graphDatabaseService;
	
	public Neo4jIntegrationCurationDaoImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}
	
	@Override
	public Set<Long> getAllCurationSetIds() {
		Set<Long> ids = new HashSet<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(CurationLabel.CurationSet)) {
			ids.add(node.getId());
		}
		return ids;
	}
	
	public Node getOrCreateMetaboliteMemberNode(long referenceId) {
		return generateMemberNode(referenceId, IntegrationNodeLabel.MetaboliteMember);
	}
	
	public Node getOrCreateReactionMemberNode(long referenceId) {
		return generateMemberNode(referenceId, IntegrationNodeLabel.ReactionMember);
	}
	
	public Node generateMemberNode(long referenceId, IntegrationNodeLabel label) {
		Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(
						IntegrationNodeLabel.IntegratedMember, 
						"id", referenceId));
		if (node == null) {
			node = graphDatabaseService.createNode();
			node.addLabel(IntegrationNodeLabel.IntegratedMember);
			node.addLabel(label);
			//XXX: reference id - may be changed to reference-id in future
			node.setProperty("id", referenceId);
		}
		
		return node;
	}
	
	@Override
	public Long saveCurationSet(CurationSet curationSet) {
		
		if (curationSet.getEntry() == null) {
			LOGGER.debug("Invalid set: null entry");
			return null;
		}
		
		Node node = graphDatabaseService.createNode();
		node.setProperty("entry", curationSet.getEntry());
		node.addLabel(CurationLabel.CurationSet);
		
		curationSet.setId(node.getId());
		return node.getId();
	}

	@Override
	public CurationSet getCurationSet(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		return Neo4jMapper.nodeToCurationSet(node);
	}

	@Override
	public CurationSet getCurationSet(String entry) {
		Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(CurationLabel.CurationSet, "entry", entry));
		if (node == null) return null;
		return Neo4jMapper.nodeToCurationSet(node);
	}

	@Override
	public Long saveCurationCluster(CurationOperation curationCluster) {
		if (curationCluster.getClusterType() == null
				|| curationCluster.getOperationType() == null) {
			LOGGER.warn("Missing type definition");
			return null;
		}
		
		if (curationCluster.getClusterType().equals(CurationLabel.CurationMetabolite.toString())) {
			return saveMetaboliteCurationCluster(curationCluster);
		}
		
		if (curationCluster.getClusterType().equals(CurationLabel.CurationReaction.toString())) {
			LOGGER.warn("Not implemented for: " + curationCluster.getClusterType());
			return null;
		}
		
		LOGGER.warn("Not implemented for: " + curationCluster.getClusterType());
		return null;
	}
	
	public Set<Node> findMatchingClusterByNodes(Set<Node> eidNodeSet) {
		Set<Long> eids = new HashSet<> ();
		for (Node eid : eidNodeSet) {
			eids.add(eid.getId());
		}
		
		return findMatchingCluster(eids);
	}
	
	public Set<Node> findMatchingCluster(Set<Long> eids) {
		Set<Set<Long>> cidSets = new HashSet<> ();
		for (Long eid : eids) {
			Node node = graphDatabaseService.getNodeById(eid);
			Set<Long> cids = Neo4jUtils.collectNodeRelationshipNodeIds(node, IntegrationRelationshipType.Integrates);
			cidSets.add(cids);
		}
		
		Set<Long> matchSet = new HashSet<> (cidSets.iterator().next());
		for (Set<Long> cidSet : cidSets) {
			matchSet.retainAll(cidSet);
		}
		
		Set<Node> result = new HashSet<> ();
		for (Long cid : matchSet) result.add(graphDatabaseService.getNodeById(cid));
		
		return result;
	}
	
	public void a(IntegratedCluster integratedCluster) {
		
	}
	
	public Long saveMetaboliteCurationCluster(CurationOperation curationCluster) {
		CurationSet curationSet = curationCluster.getCurationSet();
		
		if (curationSet == null) {
			LOGGER.warn("Invalid Curation Cluster: No curation set assigned.");
			return null;
		}
		
		if (curationCluster.getClusterRelationship() == null) {
			if (curationCluster.getIntegratedClusters().size() < 2) {
				LOGGER.warn("No relationship specified: ignored");
			} else {
				LOGGER.warn("No relationship specified: multiple clusters found");
				return null;
			}
		}
		
		Long curaSetId = curationSet.getId();
		if (curaSetId == null) {
			curaSetId = this.saveCurationSet(curationSet);
		}
		Set<Node> eids = new HashSet<> ();
		Set<Node> eidNodeSet = new HashSet<> ();
		//scaffold members to curation
		for (IntegratedClusterMember integratedClusterMember 
				: curationCluster.getMembers()) {
			
			IntegratedMember integratedMember = integratedClusterMember.getMember();
			Node eidNode = this.getOrCreateMetaboliteMemberNode(integratedMember.getReferenceId());
			eidNodeSet.add(eidNode);
			eids.add(eidNode);
			LOGGER.debug(String.format("Link to member node %s:%s", eidNode, Neo4jUtils.getLabels(eidNode)));
		}
		List<Node> cidArray = new ArrayList<> ();
		
		//check if [EID]->CC exists
		Set<Node> nodes = this.findMatchingClusterByNodes(eids);
		if (nodes.isEmpty()) {
			LOGGER.debug("Create replicate proxy cluster");
			Node cidNode = graphDatabaseService.createNode();
			cidNode.addLabel(IntegrationNodeLabel.IntegratedCluster);
			cidNode.addLabel(IntegrationNodeLabel.MetaboliteCluster);
			cidNode.setProperty("entry", "aaaaaaaaaaa");
			cidArray.add(cidNode);
			for (Node eidNode : eidNodeSet) {
				cidNode.createRelationshipTo(eidNode, IntegrationRelationshipType.Integrates);
			}
		} else {
			LOGGER.debug("Found existing match set: " + nodes);
		}
		
		
		//if cid array size > 1 must estabilish relationship between cids
		if (cidArray.size() > 1) {
			CurationRelationship relationship = 
					CurationRelationship.valueOf(curationCluster.getClusterRelationship());
			Node prev = cidArray.get(0);
			for (int i = 1; i < cidArray.size(); i++) {
				Node next = cidArray.get(i);
				prev.createRelationshipTo(next, relationship);
				prev = next;
			}
		} else {
			
		}
		
		Node curationNode = graphDatabaseService.createNode();
		curationNode.addLabel(CurationLabel.CurationMetabolite);
		curationNode.setProperty("entry", curationCluster.getEntry());
		for (Node cidNode : cidArray) {
			LOGGER.debug(String.format("Link %s -> %s", curationNode, cidNode));
			Relationship relationship = curationNode.createRelationshipTo(
					cidNode, CurationRelationship.CurationOperation);
			relationship.setProperty("operation", "accept");
		}
		
		
		Node curationSetNode = graphDatabaseService.getNodeById(curaSetId);
		curationSetNode.createRelationshipTo(curationNode, CurationRelationship.Curation);
		
		//if exists add user
		//if not create CC and link and add user
		//copy cluster
		//link cluster
		
		curationCluster.setId(curationNode.getId());
		
		return curationNode.getId();
	}

	public CurationOperation assembleNode(Node oidNode) {
		CurationOperation curationOperation = Neo4jMapper.nodeToCurationMetabolite(oidNode);
		for (Node cidNode : Neo4jUtils
				.collectNodeRelationshipNodes(
						oidNode, CurationRelationship.CurationOperation)) {
			IntegratedCluster integratedCluster = Neo4jMapper.nodeToIntegratedCluster(cidNode);
			curationOperation.getIntegratedClusters().add(integratedCluster);
		}
		
		return curationOperation;
	}
	
	@Override
	public CurationOperation getCurationCluster(long oid) {
		Node oidNode = graphDatabaseService.getNodeById(oid);
		return assembleNode(oidNode);
	}

	@Override
	public CurationOperation getCurationCluster(String entry) {
		Node oidNode = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(
						CurationLabel.CurationOperation, "entry", entry));
		
		return assembleNode(oidNode);
	}

	@Override
	public Set<CurationOperation> getCurationClustersByMembers(Set<Long> eidSet) {
		Set<CurationOperation> curationClusters = new HashSet<> ();
		
		Set<Node> nodes = new HashSet<> ();
		for (Long eid : eidSet) {
			Node eidNode = Neo4jUtils.getUniqueResult(
					graphDatabaseService.findNodesByLabelAndProperty(
							IntegrationNodeLabel.IntegratedMember, "id", eid));
			
			if (eidNode != null) {
				LOGGER.debug(eidNode + " " + Neo4jUtils.getLabels(eidNode));
				
				for (Relationship relationship : 
					eidNode.getRelationships(IntegrationRelationshipType.Integrates)) {
					Node cidNode = relationship.getOtherNode(eidNode);
					LOGGER.debug(cidNode + " " + Neo4jUtils.getLabels(cidNode));
					Set<Node> curationNodes = Neo4jUtils.collectNodeRelationshipNodes(cidNode, CurationRelationship.CurationOperation);
					LOGGER.debug(curationNodes.toString());
					
					nodes.addAll(curationNodes);
				}
			}
		}
		
		for (Node xidNode : nodes) {
			CurationOperation curationCluster = Neo4jMapper.nodeToCurationCluster(xidNode);
			curationClusters.add(curationCluster);
		}
		
		return curationClusters;
	}

	@Override
	public Set<Long> getAllCurationOperationIds(long xid) {
		Set<Long> ids = new HashSet<> ();
		Node xidNode = graphDatabaseService.getNodeById(xid);
		for (Relationship relationship : 
			xidNode.getRelationships(CurationRelationship.Curation)) {
			
			Node oidNode = relationship.getOtherNode(xidNode);
			LOGGER.debug(String.format("Found %s:%s", oidNode, Neo4jUtils.getLabels(oidNode)));
			ids.add(oidNode.getId());
		}
		return ids;
	}


	


}
