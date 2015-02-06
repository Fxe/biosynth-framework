package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;

public class AbstractNeo4jGraphDao<E extends AbstractGraphNodeEntity> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jGraphDao.class);
	
	protected GraphDatabaseService graphDatabaseService;
	
	public AbstractNeo4jGraphDao(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}
	
	protected<T extends AbstractGraphNodeEntity> AbstractGraphNodeEntity getGraphNodeEntity(long id, Class<T> clazz) {
		AbstractGraphNodeEntity nodeEntity = null;
		Node node = null;
		try {
			nodeEntity = clazz.newInstance();
			Neo4jMapper.nodeToAbstractGraphNodeEntity(nodeEntity, node);
		} catch (InstantiationException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return nodeEntity;
	}
	
	protected void saveGraphEntity(AbstractGraphNodeEntity entity) {
		Node node = null;
		
		if (entity.getId() != null) {
			LOGGER.trace("Entity id[%s] non zero looking for existing node");
			node = graphDatabaseService.getNodeById(entity.getId());
		}
		
		
		if (node == null) {
			LOGGER.trace("Generating new node");
			node = graphDatabaseService.createNode();
		}
		LOGGER.trace("Selected node: " + node);
		entity.setId(node.getId());
		
		LOGGER.debug("Setup node labels ...");
		for (String label : entity.getLabels()) {
			LOGGER.trace("Add label: " + label);
			node.addLabel(DynamicLabel.label(label));
		}
		
		
		LOGGER.debug("Setup node properties ...");
		Neo4jUtils.setPropertiesMap(entity.getProperties(), node);
		if (entity.getMajorLabel() != null) {
			LOGGER.trace("Setup major label property: " + entity.getMajorLabel());
			node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, entity.getMajorLabel());
		}
		
		LOGGER.debug("Setup node connected entities ...");
		for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> pair : entity.getConnectedEntities()) {
			AbstractGraphEdgeEntity edgeEntity = pair.getLeft();
			AbstractGraphNodeEntity nodeEntity = pair.getRight();
			
			this.saveGraphEntity(nodeEntity);
			
			Node otherNode = graphDatabaseService.getNodeById(nodeEntity.getId());
			String relationshipType = edgeEntity.getLabels().iterator().next();
			Relationship relationship = node.createRelationshipTo(otherNode, DynamicRelationshipType.withName(relationshipType));
			LOGGER.trace(String.format("Connected %s:%s -[:%s]-> %s:%s", 
					node, Neo4jUtils.getLabels(node), relationshipType, 
					otherNode, Neo4jUtils.getLabels(otherNode)));
			Neo4jUtils.setPropertiesMap(edgeEntity.getProperties(), relationship);
		}
	}
}
