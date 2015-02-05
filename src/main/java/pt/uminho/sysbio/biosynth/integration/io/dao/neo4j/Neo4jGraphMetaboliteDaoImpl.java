package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
public class Neo4jGraphMetaboliteDaoImpl 
extends AbstractNeo4jDao
implements MetaboliteHeterogeneousDao<GraphMetaboliteEntity>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphMetaboliteDaoImpl.class);
	protected static final Label METABOLITE_LABEL = GlobalLabel.Metabolite;
	protected static final RelationshipType CROSSREFERENCE_RELATIONSHIP = 
			MetaboliteRelationshipType.has_crossreference_to;
	
	@Autowired
	public Neo4jGraphMetaboliteDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}
	
	@Override
	public GraphMetaboliteEntity getMetaboliteById(String tagsss, Serializable id) {
		Node node = graphDatabaseService.getNodeById(Long.parseLong(id.toString()));
//		System.out.println(node);
//		System.out.println(IteratorUtil.asCollection(node.getLabels()));
		if (!node.hasLabel(GlobalLabel.Metabolite)) return null;
		
		LOGGER.debug("Found " + node);
		
		GraphMetaboliteEntity metaboliteEntity = new GraphMetaboliteEntity();
		
		metaboliteEntity.setId(node.getId());
		metaboliteEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
		metaboliteEntity.setPropertyEntities(Neo4jUtils.getPropertyEntities(node));
		metaboliteEntity.setCrossreferences(Neo4jUtils.getCrossreferences(node));
		
		metaboliteEntity.setMajorLabel((String) metaboliteEntity.getProperty("major-label", null));
		
		for (Label label : node.getLabels()) metaboliteEntity.addLabel(label.toString());
		
		return metaboliteEntity;
	}

	@Override
	public GraphMetaboliteEntity getMetaboliteByEntry(String tag, String entry) {
		MetaboliteMajorLabel majorLabel = MetaboliteMajorLabel.valueOf(tag);
		
		List<Node> nodes = IteratorUtil.asList(graphDatabaseService
				.findNodesByLabelAndProperty(majorLabel, "entry", entry));
		
		if (nodes.isEmpty()) return null;
		if (nodes.size() > 1) LOGGER.warn("Multiple Records for: " + entry);
		
		Node node = nodes.get(0);
		
		LOGGER.debug(String.format("Found %s for %s:%s", node, tag, entry));
		
		GraphMetaboliteEntity metaboliteEntity = new GraphMetaboliteEntity();
		
		metaboliteEntity.setProperties(Neo4jUtils.getPropertiesMap(node));
		
		metaboliteEntity.setId(node.getId());
		return metaboliteEntity;
	}

	@Override
	public GraphMetaboliteEntity saveMetabolite(String tag, GraphMetaboliteEntity metabolite) {
		boolean create = true;
		System.out.println(metabolite.getMajorLabel());
		System.out.println(metabolite.getEntry());
		for (Node node : graphDatabaseService.findNodesByLabelAndProperty(
				DynamicLabel.label(metabolite.getMajorLabel()), 
				"entry", 
				metabolite.getEntry())) {
			create = false;
			LOGGER.debug(String.format("Found Previous node with entry:%s", metabolite.getEntry()));
			LOGGER.debug(String.format("MODE:UPDATE %s", node));
			//SCD Track changes
			Neo4jUtils.applyProperties(node, metabolite.getProperties());
			
			for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair : metabolite.getCrossreferences()) {
				this.createOrLinkToProxy(node, proxyPair);
			}
			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair 
					: metabolite.getPropertyEntities()) {
				this.createOrLinkToProperty(node, pair);
			}
			
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, metabolite.getMajorLabel());
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
			
			metabolite.setId(node.getId());
		}
		
		if (create) {
			Node node = graphDatabaseService.createNode();
			LOGGER.debug("Create " + node);
			
			node.addLabel(DynamicLabel.label(metabolite.getMajorLabel()));
			for (String label : metabolite.getLabels())
				node.addLabel(DynamicLabel.label(label));
			
			Neo4jUtils.applyProperties(node, metabolite.getProperties());
			
			for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair : metabolite.getCrossreferences()) {
				this.createOrLinkToProxy(node, proxyPair);
			}
			
			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair 
					: metabolite.getPropertyEntities()) {
				this.createOrLinkToProperty(node, pair);
			}
			
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, metabolite.getMajorLabel());
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
			
			metabolite.setId(node.getId());
		}
		
		return metabolite;
	}
	
	private void createOrLinkToProperty(Node parent, 
			Pair<GraphPropertyEntity, GraphRelationshipEntity> propertyLinkPair) {
		
		LOGGER.debug(String.format("Resolving %s => %s", propertyLinkPair.getRight(), propertyLinkPair.getLeft()));
		/*
		 * Create or Update Link + Property
		 * a) - If Property exists
		 *      1 - Create Link To Property
		 * b) - If Property does not exists
		 *      1 - Create Property
		 *      2 - Add Major Label
		 *      3 - Add Labels
		 *      4 - Add Data
		 *      5 - Create Link To Property
		 */
		boolean create = true;
		GraphPropertyEntity propertyEntity = propertyLinkPair.getLeft();
		GraphRelationshipEntity relationshipEntity = propertyLinkPair.getRight();
		for (Node propertyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(propertyEntity.getMajorLabel()), 
						propertyEntity.uniqueProperty, 
						propertyEntity.getUniqueKey())) {
			LOGGER.debug("Link To Node/Proxy " + propertyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			Relationship relationship = parent.createRelationshipTo(propertyNode, 
					DynamicRelationshipType.withName((
							relationshipEntity.getMajorLabel())));
			for (String key : relationshipEntity.getProperties().keySet()) {
				relationship.setProperty(key, relationshipEntity.getProperties().get(key));
			}
		}
		
		//b) - If Property does not exists
		if (create) {
			//1 - Create Property
			Node propertyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Create Property %s -> %s", propertyEntity.getMajorLabel(), propertyNode));
			//2 - Add Major Label
			propertyNode.addLabel(DynamicLabel.label(propertyEntity.getMajorLabel()));
			
			//3 - Add Labels
			for (String label : propertyEntity.getLabels()) {
				propertyNode.addLabel(DynamicLabel.label(label));
				LOGGER.trace(String.format("Add label [%s] -> %s", label, propertyNode));
			}
			
			//4 - Add Data
			for (String key : propertyEntity.getProperties().keySet()) {
				Object value = propertyEntity.getProperties().get(key);
				propertyNode.setProperty(key, value);
				LOGGER.trace(String.format("Add key:value [%s] -> %s", key, value, propertyNode));
			}
			//5 - Create Link To Property
			Relationship relationship = parent.createRelationshipTo(propertyNode, 
					DynamicRelationshipType.withName((
							relationshipEntity.getMajorLabel())));
			for (String key : relationshipEntity.getProperties().keySet()) {
				relationship.setProperty(key, relationshipEntity.getProperties().get(key));
			}
		}
	}
	
	private void createOrLinkToProxy(Node parent, Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> proxyPair) {
		boolean create = true;
		GraphMetaboliteProxyEntity proxy = proxyPair.getLeft();
		GraphRelationshipEntity relationshipEntity = proxyPair.getRight();
		RelationshipType relationshipType = DynamicRelationshipType.withName(relationshipEntity.getMajorLabel());
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(
						DynamicLabel.label(proxy.getMajorLabel()), 
						"entry", 
						proxy.getEntry())) {
			LOGGER.debug("Link To Node/Proxy " + proxyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			Neo4jUtils.setPropertiesMap(relationshipEntity.getProperties(), relationship);
		}
		
		if (create) {
			Node proxyNode = graphDatabaseService.createNode();
			LOGGER.debug(String.format("Create Property %s -> %s", proxy.getMajorLabel(), proxyNode));
			proxyNode.addLabel(DynamicLabel.label(proxy.getMajorLabel()));
			
			for (String label : proxy.getLabels())
				proxyNode.addLabel(DynamicLabel.label(label));
			
			proxyNode.setProperty("entry", proxy.getEntry());
			proxyNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, proxy.getMajorLabel());
			proxyNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, true);
			Relationship relationship = parent.createRelationshipTo(proxyNode, relationshipType);
			Neo4jUtils.setPropertiesMap(relationshipEntity.getProperties(), relationship);
		}
	}

//	@Override
//	protected GraphMetaboliteEntity nodeToObject(Node node) {
//		// TODO Auto-generated method stub
//		return null;
//	}



	@Override
	public List<Long> getGlobalAllMetaboliteIds() {
		List<Long> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(METABOLITE_LABEL)) {
			
			result.add(node.getId());
		}
		return result;
	}

	@Override
	public List<Long> getAllMetaboliteIds(String tag) {
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
	public List<String> getAllMetaboliteEntries(String tag) {
		//TODO: verify label if valid
		
		List<String> result = new ArrayList<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(tag))) {
			
			result.add((String)node.getProperty("entry"));
		}
		return result;
	}

}
