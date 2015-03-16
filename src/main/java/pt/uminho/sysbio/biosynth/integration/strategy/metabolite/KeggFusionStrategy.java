package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class KeggFusionStrategy extends AbstractNeo4jClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(KeggFusionStrategy.class);
	
	public KeggFusionStrategy(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		this.initialNodeLabel = MetaboliteMajorLabel.LigandCompound;
	}

	@Override
	public Set<Long> execute() {
		Set<Long> cluster = new HashSet<> ();
		cluster.add(initialNode.getId());
		
		for (Relationship relationship : initialNode.getRelationships(MetaboliteRelationshipType.has_crossreference_to)) {
			Node xref = relationship.getOtherNode(initialNode);
			cluster.add(xref.getId());
		}
		
		return cluster;
	}

}
