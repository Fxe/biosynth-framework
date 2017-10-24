package pt.uminho.sysbio.biosynth.integration.report;

import java.util.Collection;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Iterators;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class MetadataContentReporter implements GlobalReporter {

	private GraphDatabaseService graphDatabaseService;
	
	public MetadataContentReporter(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}
	
	@Override
	public void generateReport() {
		System.out.println("System Labels:");
		for (Label label : graphDatabaseService.getAllLabels()) {
			System.out.println(label);
		}
		
		System.out.println("Total Nodes: " + Iterators.asSet(graphDatabaseService.getAllNodes()).size());
		
		for (Label label : IntegrationNodeLabel.values()) {
			Collection<Node> nodes = Iterators.asCollection(graphDatabaseService.findNodes(label));
			int total;
			
			total = nodes.size();
			System.out.println(label + "\t" + total);
		}
		
		System.out.println("---- Integration Sets ----");
		Collection<Node> nodes = Iterators
				.asCollection(graphDatabaseService
						.findNodes(IntegrationNodeLabel.IntegrationSet));
		for (Node iidNode : nodes) {
			System.out.println("\t>" + Neo4jUtils.getPropertiesMap(iidNode));
		}
	}
}
