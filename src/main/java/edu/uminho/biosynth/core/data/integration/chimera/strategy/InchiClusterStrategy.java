package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluators;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

/**
 * See 
 * {@link pt.uminho.sysbio.biosynth.integration.strategy.metabolite.InchiIntegrationStategyImpl InchiIntegrationStategyImpl}
 * 
 * @author Filipe
 *
 */
@Deprecated
public class InchiClusterStrategy implements ClusteringStrategy {
	
	@Autowired
	private GraphDatabaseService db;
	
	private Node initialNode;
	
	public Node getInitialNode() { return initialNode;}
	public void setInitialNode(Node initialNode) { this.initialNode = initialNode;}

	public GraphDatabaseService getDb() { return db;}
	public void setDb(GraphDatabaseService db) { this.db = db;}
	
	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
		if (!this.initialNode.hasLabel(MetabolitePropertyLabel.InChI)) {
			throw new RuntimeException("Invalid Node - not a inchi node");
		}
	}

	@Override
	public Set<Long> execute() {
		if (!this.initialNode.hasLabel(MetabolitePropertyLabel.InChI)) {
			throw new RuntimeException("Invalid Node - not a inchi node");
		}

		Set<Long> nodes = new HashSet<> ();
		for (Path position: db.traversalDescription()
				.depthFirst()
				.relationships(MetaboliteRelationshipType.has_inchi)
				.evaluator(Evaluators.all()).traverse(initialNode)) {
			
			if (position.startNode().getId() != initialNode.getId() && 
					position.startNode().hasLabel(CompoundNodeLabel.Compound)) {
				nodes.add(position.startNode().getId());
			}
			if (position.endNode().getId() != initialNode.getId() && 
					position.endNode().hasLabel(CompoundNodeLabel.Compound)) {
				nodes.add(position.endNode().getId());
			}
		}
		
		return nodes;
	}
	
	@Override
	public String toString() {
		if (!this.initialNode.hasLabel(MetabolitePropertyLabel.InChI)) {
			throw new RuntimeException("Invalid Node - not a inchi node");
		}
		return "InchiClusterStrategy " + initialNode.getProperty("inchi");
	}

}
