package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynth.integration.curation.CurationDecisionMap;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.curation.CurationOperation;
import pt.uminho.sysbio.biosynth.integration.curation.CurationRelationship;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;
import pt.uminho.sysbio.biosynth.integration.curation.CurationUser;
import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationCurationDao;

public class Neo4jIntegrationCurationDaoImpl extends AbstractNeo4jDao implements IntegrationCurationDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jIntegrationCurationDaoImpl.class);
	
	public Neo4jIntegrationCurationDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
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
	
	public Node generateCurationSetNode(CurationSet curationSet) {
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", curationSet.getEntry());
		String cypherQuery = String.format("MERGE (xid:%s {entry:{entry}}) RETURN xid AS XID", CurationLabel.CurationSet);
		LOGGER.debug(String.format("Execute:%s with %s", cypherQuery, params));
		Node node = Neo4jUtils.getExecutionResultGetSingle("XID", executionEngine.execute(cypherQuery, params));
		
		return node;
	}
	
	public Node generateUserNode(CurationUser curationUser) {
		Map<String, Object> params = new HashMap<> ();
		params.put("username", curationUser.getUsername());
		String cypherQuery = String.format("MERGE (usr:%s {username:{username}}) RETURN usr AS USER", CurationLabel.CurationUser);
		LOGGER.debug(String.format("Execute:%s with %s", cypherQuery, params));
		Node node = Neo4jUtils.getExecutionResultGetSingle("USER", executionEngine.execute(cypherQuery, params));
		
		return node;
	}
	
	private Node generateIntegratedCluster(IntegratedCluster integratedCluster) {
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", integratedCluster.getEntry());
		params.put("reference_cid", integratedCluster.getId());
		params.put("description", integratedCluster.getDescription());
		params.put("cluster_type", integratedCluster.getClusterType());
		
		String cypherQuery = String.format(
				"MERGE (cid:%s {entry:{entry}, reference_cid:{reference_cid}, cluster_type:{cluster_type}, description:{description}}) RETURN cid AS CID", 
				IntegrationNodeLabel.IntegratedCluster);
		LOGGER.debug(String.format("Execute:%s with %s", cypherQuery, params));
		Node node = Neo4jUtils.getExecutionResultGetSingle("CID", executionEngine.execute(cypherQuery, params));
		
		return node;
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
			//XXX: reference id - may be changed to reference_id in future
			node.setProperty("id", referenceId);
		}
		
		return node;
	}
	

	
	private long saveIntegratedCluster(IntegratedCluster integratedCluster) {
		Node node = generateIntegratedCluster(integratedCluster);
		return node.getId();
	}
	


	@Override
	public Long saveCurationSet(CurationSet curationSet) {
		
		if (curationSet.getEntry() == null) {
			LOGGER.debug("Invalid set: null entry");
			return null;
		}
		
		Node node = generateCurationSetNode(curationSet);
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
	public Long saveCurationOperation(CurationOperation curationCluster) {
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
	
	public Long saveMetaboliteCurationCluster(CurationOperation curationOperation) {
		CurationSet curationSet = curationOperation.getCurationSet();
		
		if (curationSet == null) {
			LOGGER.warn("Invalid Curation Cluster: No curation set assigned.");
			return null;
		}
		
		if (curationOperation.getClusterRelationship() == null) {
			if (curationOperation.getIntegratedClusters().size() < 2) {
				LOGGER.debug("No relationship specified: ignored");
			} else {
				LOGGER.warn("No relationship specified: invalid because of multiple clusters found");
				return null;
			}
		}
		
		//0: SAVE OID
		Node oidNode = graphDatabaseService.createNode();
		oidNode.addLabel(CurationLabel.CurationOperation);
		oidNode.addLabel(CurationLabel.CurationMetabolite);
		oidNode.setProperty("entry", curationOperation.getEntry());
		oidNode.setProperty("created_at", System.currentTimeMillis());
		oidNode.setProperty("operation_type", curationOperation.getOperationType());
		oidNode.setProperty("cluster_type", curationOperation.getClusterType());
		if (curationOperation.getClusterRelationship() != null)
			oidNode.setProperty("cluster_relationship", curationOperation.getClusterRelationship());
		long oid = oidNode.getId();
		curationOperation.setId(oid);
		LOGGER.debug(String.format("Curation Operation oid %s", oidNode));
		
		//1: SAVE XID
		long xid = this.saveCurationSet(curationSet);
		Node xidNode = graphDatabaseService.getNodeById(xid);
		LOGGER.debug(String.format("Curation Set xid %s", xidNode));
		xidNode.createRelationshipTo(oidNode, CurationRelationship.HAS_CURATION_OPERATION);
		LOGGER.debug(String.format("Linked %s -> %s", xidNode, oidNode));
		
		//2: SAVE USR
		LOGGER.debug("Resolving User data");
		long usr = this.saveCurationUser(curationOperation.getCurationUser());
		Node usrNode = graphDatabaseService.getNodeById(usr);
		LOGGER.debug(String.format("Curation User usr %s", usrNode));
		usrNode.createRelationshipTo(oidNode, CurationRelationship.PERFORMED_CURATION_OPERATION);
		LOGGER.debug(String.format("Linked %s -> %s", usrNode, oidNode));

		//3: scaffold members to curation
		Set<Node> eids = new HashSet<> ();
		Set<Node> eidNodeSet = new HashSet<> ();
		Map<Long, Long> referenceEidToNodeIdMap = new HashMap<> ();
		for (IntegratedClusterMember integratedClusterMember 
				: curationOperation.getMembers()) {
			
			IntegratedMember integratedMember = integratedClusterMember.getMember();
			Node eidNode = this.getOrCreateMetaboliteMemberNode(integratedMember.getReferenceId());
			integratedMember.setId(eidNode.getId());
			eidNodeSet.add(eidNode);
			eids.add(eidNode);
			LOGGER.debug(String.format("Scaffold Member Node %s:%s", eidNode, Neo4jUtils.getLabels(eidNode)));
			referenceEidToNodeIdMap.put(integratedMember.getReferenceId(), eidNode.getId());
		}
		
		//4: exclude links
		for (IntegratedMember eid : curationOperation.getExclude()) {
			Node eidNode = graphDatabaseService.getNodeById(eid.getId());
			oidNode.createRelationshipTo(eidNode, CurationRelationship.NOT_EQUAL);
		}
		
		List<Node> cidArray = new ArrayList<> ();
		
		//check if [EID]->CC exists NOT NEEDED REPLICATE ALL CIDS FOR ROLLBACK OP
//		Set<Node> nodes = this.findMatchingClusterByNodes(eids);
		for (IntegratedCluster integratedCluster : 
			curationOperation.getIntegratedClusters()) {
			long cid = this.saveIntegratedCluster(integratedCluster);
			Node cidNode = graphDatabaseService.getNodeById(cid); 
			cidArray.add(cidNode);
			LOGGER.debug(String.format("Integrated Cluster cid %s", cidNode));
			for (long referenceEid : IntegrationUtils.collectClusterMemberRerefenceEids(integratedCluster)) {
				Node eidNode = graphDatabaseService.getNodeById(referenceEidToNodeIdMap.get(referenceEid));
				cidNode.createRelationshipTo(eidNode, IntegrationRelationshipType.Integrates);
				LOGGER.debug(String.format("Linked %s -> %s", cidNode, eidNode));
			}
		}
		
//		if (nodes.isEmpty()) {
//			LOGGER.debug("Create replicate proxy cluster");
//			Node cidNode = graphDatabaseService.createNode();
//			cidNode.addLabel(IntegrationNodeLabel.IntegratedCluster);
//			cidNode.addLabel(IntegrationNodeLabel.MetaboliteCluster);
//			cidNode.setProperty("entry", "aaaaaaaaaaa");
//			cidNode.setProperty("reference_cid", value);
//			cidArray.add(cidNode);
//			for (Node eidNode : eidNodeSet) {
//				cidNode.createRelationshipTo(eidNode, IntegrationRelationshipType.Integrates);
//			}
//		} else {
//			LOGGER.debug("Found existing match set: " + nodes);
//		}
		
		
		//if cid array size > 1 must estabilish relationship between cids
		
		if (cidArray.size() > 1) {
			LOGGER.debug("Resolving CID to CID relationship ... ");
			CurationRelationship relationship = 
					CurationRelationship.valueOf(curationOperation.getClusterRelationship());
			Node prev = cidArray.get(0);
			for (int i = 1; i < cidArray.size(); i++) {
				Node next = cidArray.get(i);
				Relationship cidToCid = prev.createRelationshipTo(next, relationship);
				LOGGER.debug(String.format("Linked %s - [%s] -> %s", prev, cidToCid, next));
				prev = next;
			}
		} else {
			LOGGER.debug("Resolving CID to CID relationship ... [SKIP]");
		}

		for (Node cidNode : cidArray) {
			Relationship relationship = oidNode.createRelationshipTo(
					cidNode, CurationRelationship.OPERATES_ON);
			relationship.setProperty("operation", curationOperation.getOperationType());
			LOGGER.debug(String.format("Link %s -> %s", oidNode, cidNode));
		}
		
		return oidNode.getId();
	}



	public CurationOperation assembleCurationOperationNode(Node oidNode) {
		CurationOperation curationOperation = Neo4jMapper.nodeToCurationMetabolite(oidNode);
		for (Node cidNode : Neo4jUtils
				.collectNodeRelationshipNodes(
						oidNode, CurationRelationship.OPERATES_ON)) {
			IntegratedCluster integratedCluster = Neo4jMapper.nodeToIntegratedCluster(cidNode);
			curationOperation.getIntegratedClusters().add(integratedCluster);
		}
		
		Node xidNode = Neo4jUtils.collectUniqueNodeRelationshipNodes(oidNode, CurationRelationship.HAS_CURATION_OPERATION);
		CurationSet curationSet = Neo4jMapper.nodeToCurationSet(xidNode);
		Node usrNode = Neo4jUtils.collectUniqueNodeRelationshipNodes(oidNode, CurationRelationship.PERFORMED_CURATION_OPERATION);
		CurationUser curationUser = Neo4jMapper.nodeToCurationUser(usrNode);
		
		curationOperation.setCurationSet(curationSet);
		curationOperation.setCurationUser(curationUser);
		
		return curationOperation;
	}
	
	@Override
	public CurationOperation getCurationOperationById(long oid) {
		Node oidNode = graphDatabaseService.getNodeById(oid);
		return assembleCurationOperationNode(oidNode);
	}

	@Override
	public CurationOperation getCurationOperationByEntry(String entry) {
		Node oidNode = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(
						CurationLabel.CurationOperation, "entry", entry));
		
		return assembleCurationOperationNode(oidNode);
	}

	@Override
	public Set<CurationOperation> getCurationOperationByMembers(Set<Long> eidSet) {
		Set<CurationOperation> curationOperations = new HashSet<> ();
		
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
					Set<Node> curationNodes = Neo4jUtils.collectNodeRelationshipNodes(cidNode, CurationRelationship.OPERATES_ON);
					LOGGER.debug(curationNodes.toString());
					
					nodes.addAll(curationNodes);
				}
			}
		}
		
		for (Node oidNode : nodes) {
			CurationOperation curationOperation = this.assembleCurationOperationNode(oidNode);
			curationOperations.add(curationOperation);
		}
		
		return curationOperations;
	}

	@Override
	public Set<Long> getAllCurationOperationIds(long xid) {
		Set<Long> ids = new HashSet<> ();
		Node xidNode = graphDatabaseService.getNodeById(xid);
		for (Relationship relationship : 
			xidNode.getRelationships(CurationRelationship.HAS_CURATION_OPERATION)) {
			
			Node oidNode = relationship.getOtherNode(xidNode);
			LOGGER.debug(String.format("Found %s:%s", oidNode, Neo4jUtils.getLabels(oidNode)));
			ids.add(oidNode.getId());
		}
		return ids;
	}

	@Override
	public CurationUser getCurationUserById(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		CurationUser curationUser = Neo4jMapper.nodeToCurationUser(node);
		return curationUser;
	}

	@Override
	public CurationUser getCurationUserByUsername(String username) {
		Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(CurationLabel.CurationUser, "username", username));
		CurationUser curationUser = Neo4jMapper.nodeToCurationUser(node);
		return curationUser;
	}

	@Override
	public Long saveCurationUser(CurationUser curationUser) {
		Node node = this.generateUserNode(curationUser);
		curationUser.setId(node.getId());
		return node.getId();
	}
	

	@Override
	public CurationDecisionMap resolveMembership(Set<Long> referenceEids) {
		CurationDecisionMap decisionMap = new CurationDecisionMap();
		
		Set<Long> eids = new HashSet<> ();
		for (long referenceEid : referenceEids) {
			Node eidNode = Neo4jUtils.getUniqueResult(graphDatabaseService.findNodesByLabelAndProperty(
					IntegrationNodeLabel.IntegratedMember, "id", referenceEid));
			if (eidNode != null) {
				eids.add(eidNode.getId());
			} else {
				LOGGER.debug("Reference Eid not found: " + referenceEid);
			}
		}
		
		long setCounter = 0L;
		Set<Long> found = new HashSet<> ();
		Set<Node> opNodeSet = new HashSet<> ();
		for (long eid : eids) {
			try {
				Node eidNode = graphDatabaseService.getNodeById(eid);
				long refEid = (long) eidNode.getProperty("id");
				if (!found.contains(refEid)) {
					Set<Long> eidSet = new HashSet<> ();
					for (Path path : graphDatabaseService.traversalDescription()
							.relationships(IntegrationRelationshipType.Integrates)
							.breadthFirst()
							.traverse(eidNode)) {
						Node node = path.endNode();
						if (node.hasLabel(IntegrationNodeLabel.IntegratedMember)) eidSet.add((long) node.getProperty("id"));
						if (node.hasLabel(IntegrationNodeLabel.IntegratedCluster)) {
							Set<Node> nodes = Neo4jUtils.collectNodeRelationshipNodes(node, CurationRelationship.OPERATES_ON);
							opNodeSet.addAll(nodes);
						}
					}
					decisionMap.addMergedSet(setCounter, eidSet);
					LOGGER.debug(String.format("Set %d - %s", setCounter, eidSet));
					found.addAll(eidSet);
					setCounter++;
				}
			} catch (NotFoundException e) {
				LOGGER.warn(e.getMessage());
			}
		}
		
		LOGGER.debug("Looking for rejection sets ...");
		for (Node node : opNodeSet) {
			
			LOGGER.trace(node + " " + Neo4jUtils.getLabels(node) + " " + Neo4jUtils.getPropertiesMap(node));
			List<Node> cidNodes = new ArrayList<> (
					Neo4jUtils.collectNodeRelationshipNodes(node, CurationRelationship.OPERATES_ON));
			String opType = (String) node.getProperty("operation_type");
			switch (opType) {
				case "ACCEPT": LOGGER.debug(String.format("%s:%s TYPE: %s SKIP", node, Neo4jUtils.getLabels(node), opType)); break;
				case "SPLIT": 
					LOGGER.debug(String.format("%s:%s TYPE: %s", node, Neo4jUtils.getLabels(node), opType));
					Node prev = cidNodes.get(0);
					for (int i = 1; i < cidNodes.size(); i++) {
						Node next = cidNodes.get(i);
						Node prevEid = Neo4jUtils.collectNodeRelationshipNodes(prev, IntegrationRelationshipType.Integrates).iterator().next();
						Node nextEid = Neo4jUtils.collectNodeRelationshipNodes(next, IntegrationRelationshipType.Integrates).iterator().next();
						long refEid1 = (long) prevEid.getProperty("id");
						long refEid2 = (long) nextEid.getProperty("id");
						LOGGER.debug(String.format("%d - REJECT -> %d", refEid1, refEid2));
						decisionMap.addMemberRejectionPair(refEid1, refEid2);
					}
					break;
				case "EXCLUDE":
					LOGGER.debug(String.format("%s:%s TYPE: %s", node, Neo4jUtils.getLabels(node), opType));
					Set<Node> nodes = Neo4jUtils.collectNodeRelationshipNodes(node, CurationRelationship.NOT_EQUAL);
					for (Node node_ : nodes) {
						long refEid1 = (long) node_.getProperty("id");
						LOGGER.trace("Found rejection edge " + refEid1);
						for (Node cidNode : cidNodes) {
							Node eidNode = Neo4jUtils.collectNodeRelationshipNodes(cidNode, IntegrationRelationshipType.Integrates).iterator().next();
							long refEid2 = (long) eidNode.getProperty("id");
							LOGGER.debug(String.format("%d - REJECT -> %d", refEid1, refEid2));
							decisionMap.addMemberRejectionPair(refEid1, refEid2);
						}
					}
					break;
				default: LOGGER.warn(String.format("%s:%s TYPE: %s NOT SUPPORTED SKIP", node, Neo4jUtils.getLabels(node), opType)); break;
			}

		}
		
		return decisionMap;
	}

}
