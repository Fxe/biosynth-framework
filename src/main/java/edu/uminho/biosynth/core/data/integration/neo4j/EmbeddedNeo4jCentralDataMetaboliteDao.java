package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

/**
 * Embedded Neo4j DAO 
 * 
 * @author Filipe Liu
 *
 */
public class EmbeddedNeo4jCentralDataMetaboliteDao implements MetaboliteDao<CentralDataMetaboliteEntity> {

	private static final Logger LOGGER = Logger.getLogger(EmbeddedNeo4jCentralDataMetaboliteDao.class);
	
	@Autowired
	private GraphDatabaseService graphDatabaseService;
	
	protected boolean exists(Label label, String key, String value) {
		return !IteratorUtil.asCollection(graphDatabaseService
				.findNodesByLabelAndProperty(label, key, value)).isEmpty();
	}
	
	protected Node getOrCreateNode(Label label, String key, String value) {
		Collection<Node> nodes = IteratorUtil.asCollection(graphDatabaseService
				.findNodesByLabelAndProperty(label, key, value));
		
		if (nodes.isEmpty()) {
			return graphDatabaseService.createNode();
		}
		
		if (nodes.size() > 1) {
			LOGGER.warn(String.format("Label %s with property %s:%s does not hold uniqueness", label, key, value));
		}
		
		return nodes.iterator().next();
	}
	
	/**
	 * 
	 * @param node the graph node to be updated.
	 * @param properties the properties to be updated.
	 */
	private void updateNode(Node node, Map<String, Object> properties) {
		for (String key : properties.keySet()) {
			node.setProperty(key, properties.get(key));
		}
	}
	
	private void updateRelationship(Relationship relationship, Map<String, Object> properties) {
		for (String key : properties.keySet()) {
			relationship.setProperty(key, properties.get(key));
		}
	}
	
	@Override
	public CentralDataMetaboliteEntity getMetaboliteById(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataMetaboliteEntity saveMetabolite(
			CentralDataMetaboliteEntity metabolite) {
		
		Label major = DynamicLabel.label(metabolite.getMajorLabel());
		Node node = this.getOrCreateNode(major, "entry", metabolite.getEntry());
		node.setProperty("proxy", false);
		
		node.setProperty("entry", metabolite.getEntry());
		for (String label : metabolite.getLabels()) {
			Label l = DynamicLabel.label(label);
			node.addLabel(l);
		}
		updateNode(node, metabolite.getProperties());
		
		metabolite.setId(node.getId());
		
		for (CentralDataMetabolitePropertyEntity propertyEntity : metabolite.getPropertyEntities()) {
			Label propertyMajor = DynamicLabel.label(propertyEntity.getMajorLabel());
			String uniqueKey = propertyEntity.getUniqueKey();
			String uniqueValue = (String) propertyEntity.getProperties().get(uniqueKey);
			Node propertyNode = this.getOrCreateNode(propertyMajor, uniqueKey, uniqueValue);
			this.updateNode(propertyNode, propertyEntity.getProperties());
			propertyEntity.setId(propertyNode.getId());
			RelationshipType relationshipType = DynamicRelationshipType.withName(propertyEntity.getRelationshipMajorLabel());
			node.createRelationshipTo(propertyNode, relationshipType);
		}
		
		for (CentralDataMetaboliteProxyEntity xref : metabolite.getCrossreferences()) {
			Label xrefMajor = DynamicLabel.label(xref.getMajorLabel());
			
			Node xrefNode = this.getOrCreateNode(xrefMajor, "entry", xref.getEntry());
			if ( !xrefNode.hasProperty("proxy")) xrefNode.setProperty("proxy", true);
			
			RelationshipType relationshipType = CompoundRelationshipType.HasCrossreferenceTo;
			Relationship relationship = node.createRelationshipTo(xrefNode, relationshipType);
			this.updateRelationship(relationship, xref.getProperties());
		}
		
		return metabolite;
	}

	@Override
	public Serializable saveMetabolite(Object metabolite) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllMetaboliteEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CentralDataMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(CentralDataMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
