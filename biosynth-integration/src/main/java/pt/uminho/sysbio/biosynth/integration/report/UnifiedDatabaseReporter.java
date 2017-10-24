package pt.uminho.sysbio.biosynth.integration.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Iterators;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;

/**
 * 
 * @author Filipe Liu
 *
 */
public class UnifiedDatabaseReporter implements GlobalReporter {

	private GraphDatabaseService graphDataService;
	
	public UnifiedDatabaseReporter(GraphDatabaseService graphDatabaseService) {
		this.graphDataService = graphDatabaseService;
	}
	
	@Override
	public void generateReport() {
		System.out.println("System Labels:");
		for (Label label : graphDataService.getAllLabels()) {
			System.out.println(label);
		}
		
		
		System.out.println("Total Nodes: " + Iterators.asSet(graphDataService.getAllNodes()).size());
		
		
		
		int sum = 0;
		for (Label label : MetaboliteMajorLabel.values()) {
			Collection<Node> nodes = Iterators.asCollection(graphDataService.findNodes(label));
			int concrete = 0, proxies = 0, error = 0, total = 0;
			for (Node node : nodes) {
				if (!node.hasLabel(GlobalLabel.Reaction)) {
					total++;
					if (node.hasProperty("proxy")) {
						if ((boolean)node.getProperty("proxy")) {
							proxies++;
						} else {
							concrete++;
						}
					} else {
						error++;
					}
				} else if (!node.hasLabel(GlobalLabel.Metabolite) && !node.hasLabel(GlobalLabel.Reaction)) {
					System.out.println("ERRRRROR !" + node + " " + node.getLabels() + " " + Neo4jUtils.getPropertiesMap(node));
				}
			}
			String line = String.format("\t%d\t%d\t%d", concrete, proxies, error);
			System.out.println(label + "\t" + total + line);
			sum += total;
			
			
		}
		
		System.out.println("Total Metabolites: " + Iterators.asCollection(graphDataService.findNodes(GlobalLabel.Metabolite)).size());
		System.out.println("Total Metabolites (Counted): " + sum);
		
		sum = 0;
		for (Label label : MetabolitePropertyLabel.values()) {
			Collection<Node> nodes = Iterators.asCollection(graphDataService.findNodes(label));
			System.out.println(label + "\t" + nodes.size());
			sum += nodes.size();
		}
		
		System.out.println("Total Metabolites Property: " + Iterators.asCollection(graphDataService.findNodes(GlobalLabel.MetaboliteProperty)).size());
		System.out.println("Total Metabolites Property (Counted): " + sum);
		
		sum = 0;
		for (Label label : ReactionMajorLabel.values()) {
			Collection<Node> nodes = Iterators.asCollection(graphDataService.findNodes(label));
			int concrete = 0, proxies = 0, error = 0, total = 0;
			for (Node node : nodes) {
				if (!node.hasLabel(GlobalLabel.Metabolite)) {
					total++;
					if (node.hasProperty("proxy")) {
						if ((boolean)node.getProperty("proxy")) {
							proxies++;
						} else {
							concrete++;
						}
					} else {
						error++;
					}
				} else if (!node.hasLabel(GlobalLabel.Metabolite) && !node.hasLabel(GlobalLabel.Reaction)) {
					System.out.println("ERRRRROR !" + node);
				}
			}
			String line = String.format("\t%d\t%d\t%d", concrete, proxies, error);
			System.out.println(label + "\t" + total + line);
			sum += total;
		}

		System.out.println("Total Reactions: " + Iterators.asCollection(graphDataService.findNodes(GlobalLabel.Reaction)).size());
		System.out.println("Total Reactions (Counted): " + sum);
	}

}
