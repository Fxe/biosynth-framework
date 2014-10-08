package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;

public class CrossreferenceTraversalStrategyImpl implements ClusteringStrategy {

	@Autowired
	private GraphDatabaseService db;
	
	private Node initialNode;
	
	public Node getInitialNode() { return initialNode;}
	public void setInitialNode(Node initialNode) { this.initialNode = initialNode;}

	public GraphDatabaseService getDb() { return db;}
	public void setDb(GraphDatabaseService db) { this.db = db;}
	
//	private RelationshipType relationshipType = CompoundRelationshipType.HasCrossreferenceTo;
	private static final RelationshipType relationshipType = MetaboliteRelationshipType.HasCrossreferenceTo;
	
	@Override
	public Set<Long> execute() {
		Set<Long> nodes = new HashSet<> ();
		for (Path position: db.traversalDescription()
				.depthFirst()
				.relationships(relationshipType)
				.evaluator(Evaluators.all()).traverse(initialNode)) {
			
			nodes.add(position.endNode().getId());
		}
		
		return nodes;
	}

	@Override
	public String toString() {
		return String.format("CrossreferenceTraversalStrategyImpl.initial(%d)", initialNode==null? -1L : initialNode.getId());
	}

	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
	}
}
