package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class InchiIntegrationStategyImpl extends AbstractNeo4jClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(InchiIntegrationStategyImpl.class);
	
	@Autowired
	public InchiIntegrationStategyImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		
		this.initialNodeLabel = GlobalLabel.Metabolite;
	}

	@Override
	public Set<Long> execute() {
		
		Set<Long> cluster = new HashSet<> ();
		cluster.add(initialNode.getId());
		
		Set<Long> inchiNodeIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(initialNode, MetaboliteRelationshipType.has_inchi);
		
		LOGGER.debug("Found " + inchiNodeIdSet);
		
		if (inchiNodeIdSet.isEmpty()) return cluster;

		for (Long inchiNodeId : inchiNodeIdSet) {
			Node inichiNode = db.getNodeById(inchiNodeId);
			
			LOGGER.debug("Traverse Inchi: " + inichiNode.getProperty("key", null));
			
			for (Path position: db.traversalDescription()
					.depthFirst()
					.relationships(MetaboliteRelationshipType.has_inchi)
					.evaluator(Evaluators.all()).traverse(inichiNode)) {
				
				if ( position.startNode().hasLabel(GlobalLabel.Metabolite)) {
					long id = position.startNode().getId();
					LOGGER.debug("Added: " + id);
					
					cluster.add(id);
				}
				if (position.endNode().hasLabel(GlobalLabel.Metabolite)) {
					long id = position.endNode().getId();
					LOGGER.debug("Added: " + id);
					
					cluster.add(id);
				}
			}
		}
		
		return cluster;
	}

}
