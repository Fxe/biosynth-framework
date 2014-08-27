package edu.uminho.biosynth.integration.dao;

import java.io.Serializable;
import java.util.Set;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.data.integration.neo4j.AbstractNeo4jDao;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;
import edu.uminho.biosynth.integration.CentralReactionEntity;

public class Neo4jCentralReactionDaoImpl 
extends AbstractNeo4jDao<CentralReactionEntity> 
implements ReactionDao<CentralReactionEntity> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jCentralReactionDaoImpl.class);
	
	@Override
	public CentralReactionEntity getReactionById(Serializable id) {
		Node node = null;
		return nodeToObject(node);
	}

	@Override
	public CentralReactionEntity getReactionByEntry(String entry) {
		Node node = null;
		return nodeToObject(node);
	}

	@Override
	public CentralReactionEntity saveReaction(CentralReactionEntity reaction) {
		boolean update = false;
		
		for (Node node : graphDatabaseService.findNodesByLabelAndProperty(
				DynamicLabel.label(reaction.getMajorLabel()), 
				"entry", 
				reaction.getEntry())) {
			update = true;
			LOGGER.debug("Update " + node);
			
			reaction.setId(node.getId());
		}
		
		if (!update) {
			Node node = graphDatabaseService.createNode();
			LOGGER.debug("Create " + node);
			
			node.addLabel(DynamicLabel.label(reaction.getMajorLabel()));
			for (String label : reaction.getLabels())
				node.addLabel(DynamicLabel.label(label));
			
			for (String key : reaction.getProperties().keySet())
				node.setProperty(key, reaction.getProperties().get(key));
			
			reaction.setId(node.getId());
		}

		return reaction;
	}

	@Override
	public Set<Serializable> getAllReactionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CentralReactionEntity nodeToObject(Node node) {
		if (node == null) return null;
		
		
		
		return null;
	}

}
