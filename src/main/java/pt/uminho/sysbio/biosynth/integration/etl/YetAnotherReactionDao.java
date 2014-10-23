package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class YetAnotherReactionDao implements ReactionDao<GenericReaction> {

	private static final Logger LOGGER = LoggerFactory.getLogger(YetAnotherReactionDao.class);
	
	private IntegrationMetadataDao metadataDao;
	private GraphDatabaseService graphDatabaseService;
	private IntegrationSet integrationSet;
	
	public YetAnotherReactionDao(
			IntegrationSet integrationSet,
			GraphDatabaseService graphDatabaseService,
			IntegrationMetadataDao metadataDao) {
		this.integrationSet = integrationSet;
		this.graphDatabaseService = graphDatabaseService;
		this.metadataDao = metadataDao;
	}
	
	public IntegrationMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IntegrationMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}

	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}

	public IntegrationSet getIntegrationSet() {
		return integrationSet;
	}

	public void setIntegrationSet(IntegrationSet integrationSet) {
		this.integrationSet = integrationSet;
	}

	@Override
	public GenericReaction getReactionById(Long id) {
		LOGGER.debug(String.format("Featching Node[%d]", id));
		
		Node reactionNode = graphDatabaseService.getNodeById(id);
		if (reactionNode == null || 
				!reactionNode.hasLabel(GlobalLabel.Reaction)) {
			LOGGER.debug(String.format("%s invalid label type %s", 
					reactionNode, IteratorUtil.asList(reactionNode.getLabels())));
			return null;
		}
		
		Long iid = integrationSet.getId();
		
		
		GenericReaction rxn = new GenericReaction();
		rxn.setEntry((String) reactionNode.getProperty("entry"));
		rxn.setId(reactionNode.getId());
		rxn.setName((String) reactionNode.getProperty("name", null));

		Map<String, Double> left = new HashMap<> ();
		for (Long nodeId : collectNodes(reactionNode, ReactionRelationshipType.Left)) {
			Node node = graphDatabaseService.getNodeById(nodeId);
			String integratedEntry = metadataDao.lookupClusterEntryByMemberId(iid, nodeId);
			integratedEntry = integratedEntry == null ? node.getProperty("entry").toString() : integratedEntry;
			
			LOGGER.debug(String.format("%s translated to %s", node.getProperty("entry"), integratedEntry));
			left.put(integratedEntry, 1d);
		}
		
		Map<String, Double> right = new HashMap<> ();
		for (Long nodeId : collectNodes(reactionNode, ReactionRelationshipType.Right)) {
			Node node = graphDatabaseService.getNodeById(nodeId);
			String integratedEntry = metadataDao.lookupClusterEntryByMemberId(iid, nodeId);
			integratedEntry = integratedEntry == null ? node.getProperty("entry").toString() : integratedEntry;
			
			LOGGER.debug(String.format("%s translated to %s", node.getProperty("entry"), integratedEntry));
			right.put(integratedEntry, 1d);
		}
		
		rxn.setReactantStoichiometry(left);
		rxn.setProductStoichiometry(right);
		
		return rxn;
	}

	@Override
	public GenericReaction getReactionByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenericReaction saveReaction(GenericReaction reaction) {
		throw new RuntimeException("nooo !");
	}

	@Override
	public Set<Long> getAllReactionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Set<Long> collectNodes(Node node, ReactionRelationshipType...relationshipTypes) {
		Set<Long> nodes = new HashSet<> ();
		
		for (Relationship relationship : node.getRelationships(relationshipTypes)) {
			Node other = relationship.getOtherNode(node);
			nodes.add(other.getId());
		}
		
		return nodes;
	}
	
}
