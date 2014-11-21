package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class Neo4jReactionDao extends AbstractNeo4jDao implements ReactionDao<DefaultReaction>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jReactionDao.class);
	
	public Neo4jReactionDao(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}

	@Override
	public DefaultReaction getReactionById(Long id) {
		Node node = graphDatabaseService.getNodeById(id);
		
		LOGGER.trace(String.format("%s -> %s", 
				node, node != null ? IteratorUtil.asCollection(node.getLabels()) : "null"));
		
		if (node == null || !node.hasLabel(GlobalLabel.Reaction)) return null;
		
		DefaultReaction rxn = new DefaultReaction();
		rxn.setEntry((String) node.getProperty("entry"));
		rxn.setId(node.getId());
		rxn.setName((String) node.getProperty("name", null));
		
		Map<String, Double> left = this.loadStoichiometryMap(node, ReactionRelationshipType.Left);
		Map<String, Double> right = this.loadStoichiometryMap(node, ReactionRelationshipType.Right);
		
		rxn.setReactantStoichiometry(left);
		rxn.setProductStoichiometry(right);
		
		return rxn;
	}

	@Override
	public DefaultReaction getReactionByEntry(String entry) {
		Node node = graphDatabaseService.findNodesByLabelAndProperty(ReactionMajorLabel.BiGG, "entry", entry).iterator().next();
		
		return this.getReactionById(node.getId());
	}

	@Override
	public DefaultReaction saveReaction(DefaultReaction reaction) {
		// TODO Auto-generated method stub
		return null;
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

	
	private Map<String, Double> loadStoichiometryMap(Node reactionNode, ReactionRelationshipType relationshipType) {
		Map<String, Double> stoichiometryMap = new HashMap<> ();

		
		for (Long nodeId : Neo4jUtils.collectNodeRelationshipNodeIds(reactionNode, relationshipType)) {
			Node node = graphDatabaseService.getNodeById(nodeId);
			
			if (node == null || !node.hasLabel(GlobalLabel.Metabolite)) {
				LOGGER.warn(String.format(
						"Reaction[%d] is connected to invalid metabolite[%d]", 
						reactionNode.getId(), nodeId));
			} else {
				stoichiometryMap.put(Long.toString(node.getId()), 1d);
			}
//			integratedEntry = integratedEntry == null ? node.getProperty("entry").toString() : integratedEntry;
			
//			LOGGER.debug(String.format("%s translated to %s", node.getProperty("entry"), integratedEntry));
		}
		
		return stoichiometryMap;
	}
}
