package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.PropertyRelationshipType;

/**
 * No use. Formulas should be corrected prior.
 * 
 * @author Filipe
 *
 */
@Deprecated
public class IsoFormulaClusterStrategy implements ClusteringStrategy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IsoFormulaClusterStrategy.class);
	
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
		if (!this.initialNode.hasLabel(MetabolitePropertyLabel.IsotopeFormula)) {
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
			
			for (Relationship r: isoNode.getRelationships(MetaboliteRelationshipType.has_molecular_formula)) {
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

}
