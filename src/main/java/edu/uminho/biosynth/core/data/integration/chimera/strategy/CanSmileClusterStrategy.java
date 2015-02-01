package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.PropertyRelationshipType;

public class CanSmileClusterStrategy extends AbstractNeo4jClusteringStrategy {

	public CanSmileClusterStrategy(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CanSmileClusterStrategy.class);
	
	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
		if (!this.initialNode.hasLabel(MetabolitePropertyLabel.CanSMILES)) {
			throw new RuntimeException();
		}
	}

	@Override
	public Set<Long> execute() {
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
							MetaboliteRelationshipType.HasInChI, 
							MetaboliteRelationshipType.HasSMILES));
			
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
		return nodes;
	}

	@Override
	public String toString() {
		return String.format("%s - Pivot[%s:%s]", CanSmileClusterStrategy.class, this.initialNode, this.initialNode.getProperty("can"));
	}
}
