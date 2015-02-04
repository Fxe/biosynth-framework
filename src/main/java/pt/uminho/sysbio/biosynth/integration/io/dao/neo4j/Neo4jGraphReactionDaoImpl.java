package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionProxyEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;

public class Neo4jGraphReactionDaoImpl 
extends AbstractNeo4jDao
implements ReactionHeterogeneousDao<GraphReactionEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphReactionDaoImpl.class);
	private static final Label REACTION_LABEL = GlobalLabel.Reaction;
	private static final RelationshipType LEFT_RELATIONSHIP = ReactionRelationshipType.left_component;
	private static final RelationshipType RIGHT_RELATIONSHIP = ReactionRelationshipType.right_component;
	private static final RelationshipType CROSSREFERENCE_RELATIONSHIP = 
			ReactionRelationshipType.has_crossreference_to;
	
	
	public Neo4jGraphReactionDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public GraphReactionEntity getReactionById(String tag, Serializable id) {
		Node node = graphDatabaseService.getNodeById(Long.parseLong(id.toString()));
		if (!node.hasLabel(GlobalLabel.Reaction)) return null;
		
		LOGGER.debug("Found " + node);
		
		GraphReactionEntity reactionEntity = new GraphReactionEntity();
		
		reactionEntity.setId(node.getId());
//		reactionEntity.setEntry( (String) node.getProperty("entry", null));
		reactionEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
		
		reactionEntity.setLeft(getReactionMetabolites(node, ReactionRelationshipType.left_component));
		reactionEntity.setRight(getReactionMetabolites(node, ReactionRelationshipType.right_component));
		
		String majorLabel = (String) reactionEntity.getProperty("major-label", null);
		if (majorLabel == null) LOGGER.warn("Major label not found for %s:%s", node, Neo4jUtils.getLabels(node));
		reactionEntity.setMajorLabel(majorLabel);
		
		return reactionEntity;
	}
	
	private Map<GraphMetaboliteProxyEntity, Map<String, Object>> getReactionMetabolites(Node node, ReactionRelationshipType relationshipType) {
		Map<GraphMetaboliteProxyEntity, Map<String, Object>> map = new HashMap<> ();
		
		for (Relationship relationship : node.getRelationships(relationshipType)) {
			Node other = relationship.getOtherNode(node);
			
			Map<String, Object> propertyContainer = 
					Neo4jUtils.getPropertiesMap(relationship);
			
			Long id = other.getId();
			String entry = (String) other.getProperty("entry", null);
			String majorLabel = (String) other.getProperty("major-label", null);
			GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
			entity.setId(id);
			entity.setEntry(entry);
			entity.setMajorLabel(majorLabel);
			
			map.put(entity, propertyContainer);
		}
		
		return map;
	}

	@Override
	public GraphReactionEntity getReactionByEntry(String tag, String entry) {
		ReactionMajorLabel majorLabel = ReactionMajorLabel.valueOf(tag);
		
		List<Node> nodes = IteratorUtil.asList(graphDatabaseService
				.findNodesByLabelAndProperty(majorLabel, "entry", entry));
		
		if (nodes.isEmpty()) return null;
		if (nodes.size() > 1) LOGGER.warn("Multiple Records for: " + entry);
		
		Node node = nodes.get(0);
		
		LOGGER.debug("Found " + node);
		
		GraphReactionEntity reactionEntity = new GraphReactionEntity();
		
		reactionEntity.setId(node.getId());
		reactionEntity.setEntry( (String) node.getProperty("entry", null));
		reactionEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
		
		reactionEntity.setLeft(getReactionMetabolites(node, ReactionRelationshipType.left_component));
		reactionEntity.setRight(getReactionMetabolites(node, ReactionRelationshipType.right_component));
//		System.out.println(Neo4jUtils.getPropertiesMap(node));
		return reactionEntity;
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
			for (Map<AbstractGraphEdgeEntity, AbstractGraphEntity> l : reaction.links) {
				AbstractGraphEdgeEntity relationship = l.keySet().iterator().next();
				AbstractGraphEntity entity = l.get(relationship);
				LOGGER.debug(String.format("Resolving Additional Link %s", entity));
				this.createOrLinkToNode(node, relationship, entity);
			}
			
			node.setProperty("major-label", reaction.getMajorLabel());
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
			
			node.setProperty("major-label", reaction.getMajorLabel());
			node.setProperty("proxy", false);
			
			reaction.setId(node.getId());
		}
		return reaction;
	}
	
	private void createOrLinkToNode(Node srcNode, AbstractGraphEdgeEntity edge, AbstractGraphEntity dst) {
		Node dstNode = getOrCreateNode(dst.getMajorLabel(), dst.uniqueKey, dst.getProperty(dst.uniqueKey, null));
		for (String label : dst.getLabels()) {
			dstNode.addLabel(DynamicLabel.label(label));
		}
		for (String key : dst.getProperties().keySet()) {
			dstNode.setProperty(key, dst.getProperties().get(key));
		}
		Relationship relationship = srcNode.createRelationshipTo(dstNode, DynamicRelationshipType.withName(edge.labels.iterator().next()));
		for (String key : edge.properties.keySet()) {
			relationship.setProperty(key, edge.properties.get(key));
		}
	}
	
	private Node getOrCreateNode(String uniqueLabel, String uniqueProperty, Object value) {
		String cypherQuery = String.format("MERGE (n:%s {%s:{value}}) RETURN n AS NODE", uniqueLabel, uniqueProperty);
		Map<String, Object> params = new HashMap<> ();
		params.put(uniqueProperty, value);
		
		LOGGER.debug("Execution Engine: " + cypherQuery + " with " + params);
		Node node = Neo4jUtils.getExecutionResultGetSingle("NODE", executionEngine.execute(cypherQuery, params));
		return node;
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
			proxyNode.setProperty("major-label", proxy.getMajorLabel());
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
			Map<String, Object> relationshipPropertyContainer) {
		
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
			for (String key : relationshipPropertyContainer.keySet()) {
				relationship.setProperty(key, relationshipPropertyContainer.get(key));
			}
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Link to new proxy %s -> %s", proxy.getMajorLabel(), proxyNode));
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("entry", proxy.getEntry());
			proxyNode.setProperty("proxy", true);
			proxyNode.setProperty("major-label", proxy.getMajorLabel());
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			for (String key : relationshipPropertyContainer.keySet()) {
				relationship.setProperty(key, relationshipPropertyContainer.get(key));
			}
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

//	@Override
//	protected GraphReactionEntity nodeToObject(Node node) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
