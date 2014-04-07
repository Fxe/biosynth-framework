package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundPropertyLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundRelationshipType;
import edu.uminho.biosynth.core.data.integration.neo4j.PropertyRelationshipType;

public class CanSmileClusterStrategy implements ClusteringStrategy{

	private static final Logger LOGGER = Logger.getLogger(CanSmileClusterStrategy.class);
	
	@Autowired
	private GraphDatabaseService db;
	
	private Node initialNode;
	
	public Node getInitialNode() { return initialNode;}
	public void setInitialNode(Node initialNode) { this.initialNode = initialNode;}

	public GraphDatabaseService getDb() { return db;}
	public void setDb(GraphDatabaseService db) { this.db = db;}
	
	/**
	 * @param id Id of the initial node for traversal
	 * @throws RuntimeException if id matches a node without the <b>CanSMILES</b> label
	 */
	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
		if (!this.initialNode.hasLabel(CompoundPropertyLabel.CanSMILES)) {
			throw new RuntimeException();
		}
	}

	@Override
	public List<Long> execute() {
		Set<Long> nodes = new HashSet<> ();
		Set<Long> isomorphicProperties = new HashSet<> ();
		for (Path position: db.traversalDescription()
				.depthFirst()
				.relationships(PropertyRelationshipType.Isomorphic)
				.evaluator(Evaluators.all()).traverse(initialNode)) {
			
			isomorphicProperties.add(position.endNode().getId());
		}
		for (Long isoNodeId: isomorphicProperties) {
			Node isoNode = db.getNodeById(isoNodeId);
			List<Relationship> links = IteratorUtil.asList(
					isoNode.getRelationships(
							CompoundRelationshipType.HasInChI, 
							CompoundRelationshipType.HasSMILES));
			
			for (Relationship r: links) {
				if (r.getStartNode().hasLabel(CompoundNodeLabel.Compound)) {
					nodes.add(r.getStartNode().getId());
				} else if (r.getEndNode().hasLabel(CompoundNodeLabel.Compound)) {
					nodes.add(r.getEndNode().getId());
				} else {
					LOGGER.warn(String.format("%s matches incorrect link between nodes %s -> %s", r, r.getStartNode(), r.getEndNode()));
				}
			}
		}
		return new ArrayList<Long> (nodes);
	}

	@Override
	public String toString() {
		return String.format("%s - Pivot[%s:%s]", CanSmileClusterStrategy.class, this.initialNode, this.initialNode.getProperty("can"));
	}
}
