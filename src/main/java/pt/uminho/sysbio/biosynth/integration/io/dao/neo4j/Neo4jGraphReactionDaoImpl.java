package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionProxyEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;
import edu.uminho.biosynth.core.data.integration.neo4j.AbstractNeo4jDao;

public class Neo4jGraphReactionDaoImpl 
extends AbstractNeo4jDao<GraphReactionEntity> 
implements ReactionHeterogeneousDao<GraphReactionEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphReactionDaoImpl.class);
	private static final Label REACTION_LABEL = GlobalLabel.Reaction;
	private static final RelationshipType LEFT_RELATIONSHIP = ReactionRelationshipType.Left;
	private static final RelationshipType RIGHT_RELATIONSHIP = ReactionRelationshipType.Right;
	private static final RelationshipType CROSSREFERENCE_RELATIONSHIP = 
			ReactionRelationshipType.HasCrossreferenceTo;
	
	@Override
	public GraphReactionEntity getReactionById(String tag, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphReactionEntity getReactionByEntry(String tag, String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphReactionEntity saveReaction(String tag,
			GraphReactionEntity reaction) {
		boolean create = true;
		
		for (Node node : graphDatabaseService.findNodesByLabelAndProperty(
				DynamicLabel.label(reaction.getMajorLabel()), 
				"entry", 
				reaction.getEntry())) {
			create = false;
			LOGGER.debug(String.format("Found Previous node with entry:%s", reaction.getEntry()));
			LOGGER.debug(String.format("MODE:UPDATE %s", node));
			
			for (String label : reaction.getLabels())
				node.addLabel(DynamicLabel.label(label));
			for (String key : reaction.getProperties().keySet())
				node.setProperty(key, reaction.getProperties().get(key));
			for (GraphMetaboliteProxyEntity l : reaction.getLeft().keySet()) {
				LOGGER.debug(String.format("Resolving Left Link %s", l.getEntry()));
				this.createOrLinkToMetaboliteProxy(node, l, LEFT_RELATIONSHIP, reaction.getLeft().get(l));
			}
			for (GraphMetaboliteProxyEntity r : reaction.getRight().keySet()) {
				LOGGER.debug(String.format("Resolving Right Link %s", r.getEntry()));
				this.createOrLinkToMetaboliteProxy(node, r, RIGHT_RELATIONSHIP, reaction.getRight().get(r));
			}
			for (GraphReactionProxyEntity x : reaction.getCrossreferences()) {
				LOGGER.debug(String.format("Resolving Crossreference Link %s", x.getEntry()));
				this.createOrLinkToReactionProxy(node, x, CROSSREFERENCE_RELATIONSHIP);
			}
			
			node.setProperty("proxy", false);
			
			reaction.setId(node.getId());
		}
		
		if (create) {
			Node node = graphDatabaseService.createNode();
			LOGGER.debug(String.format("MODE:CREATE %s", node));
			
			node.addLabel(DynamicLabel.label(reaction.getMajorLabel()));
			
			for (String label : reaction.getLabels())
				node.addLabel(DynamicLabel.label(label));
			for (String key : reaction.getProperties().keySet())
				node.setProperty(key, reaction.getProperties().get(key));
			for (GraphMetaboliteProxyEntity l : reaction.getLeft().keySet()) {
				LOGGER.debug(String.format("Resolving Left Link %s", l.getEntry()));
				Double stoichiometry = reaction.getLeft().get(l);
				this.createOrLinkToMetaboliteProxy(node, l, LEFT_RELATIONSHIP, stoichiometry);
			}
			for (GraphMetaboliteProxyEntity r : reaction.getRight().keySet()) {
				LOGGER.debug(String.format("Resolving Right Link %s", r.getEntry()));
				Double stoichiometry = reaction.getRight().get(r);
				this.createOrLinkToMetaboliteProxy(node, r, RIGHT_RELATIONSHIP, stoichiometry);
			}
			for (GraphReactionProxyEntity x : reaction.getCrossreferences()) {
				LOGGER.debug(String.format("Resolving Crossreference Link %s", x.getEntry()));
				this.createOrLinkToReactionProxy(node, x, CROSSREFERENCE_RELATIONSHIP);
			}
			
			node.setProperty("proxy", false);
			
			reaction.setId(node.getId());
		}
		return reaction;
	}
	
	private void createOrLinkToReactionProxy(
			Node parent, 
			GraphReactionProxyEntity proxy,
			RelationshipType relationshipType
			) {
		
		boolean create = true;
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(proxy.getMajorLabel()), 
						"entry", 
						proxy.getEntry())) {
			LOGGER.debug("Link To Node/Proxy " + proxyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			parent.createRelationshipTo(proxyNode, relationshipType);
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Create Property %s -> %s", proxy.getMajorLabel(), proxyNode));
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("entry", proxy.getEntry());
			proxyNode.setProperty("proxy", true);
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			
			for (String key : proxy.getProperties().keySet()) {
				relationship.setProperty(key, proxy.getProperties().get(key));
			}
			
		}
	}
	
	private Node createNode(Map<String, Object> nodeProperties) {
		Node node = graphDatabaseService.createNode();
		for (String key : nodeProperties.keySet()) {
			node.setProperty(key, nodeProperties.get(key));
		}
		
		return node;
	}
	
	private Relationship createRelationship(Node src, Node dst, RelationshipType relationshipType, Map<String, Object> relationshipProperties) {
		Relationship relationship = src.createRelationshipTo(dst, relationshipType);
		for (String key : relationshipProperties.keySet()) {
			relationship.setProperty(key, relationshipProperties.get(key));
		}
		
		return relationship;
	}
	
	private void createOrLinkToMetaboliteProxy(
			Node parent, 
			GraphMetaboliteProxyEntity proxy,
			RelationshipType relationshipType,
			Double stoichiometry) {
		
		boolean create = true;
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(proxy.getMajorLabel()), 
						"entry", 
						proxy.getEntry())) {
			LOGGER.debug("Link to previous Node/Proxy " + proxyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			relationship.setProperty("stoichiometry", stoichiometry);
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Link to new proxy %s -> %s", proxy.getMajorLabel(), proxyNode));
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("entry", proxy.getEntry());
			proxyNode.setProperty("proxy", true);
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			relationship.setProperty("stoichiometry", stoichiometry);
		}
	}

	@Override
	public List<Long> getGlobalAllReactionIds() {
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(REACTION_LABEL)) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<Long> getAllReactionIds(String tag) {
		//TODO: verify label if valid
		
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<String> getAllReactionEntries(String tag) {
		//TODO: verify label if valid
		
		List<String> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add((String)node.getProperty("entry"));
		}
		return result;
	}

	@Override
	protected GraphReactionEntity nodeToObject(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

}
