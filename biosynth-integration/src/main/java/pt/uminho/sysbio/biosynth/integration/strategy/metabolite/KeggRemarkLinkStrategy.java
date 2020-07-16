package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class KeggRemarkLinkStrategy extends AbstractNeo4jClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(KeggRemarkLinkStrategy.class);
	
	public KeggRemarkLinkStrategy(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		
		this.initialNodeLabel = GlobalLabel.Metabolite;
	}

	@Override
	public Set<Long> execute() {
		Set<Long> cluster = new HashSet<> ();
		
		cluster.add(initialNode.getId());
		
		String remark = (String) initialNode.getProperty("remark", null);
		LOGGER.trace(String.format("%s - %s", Neo4jUtils.getLabels(initialNode), remark));
		if (remark == null) return cluster;
		remark = remark.toUpperCase();
		
		Map<MetaboliteMajorLabel, Set<String>> extraLinks = new HashMap<> ();
		extraLinks.put(MetaboliteMajorLabel.LigandCompound, new HashSet<String> ());
		extraLinks.put(MetaboliteMajorLabel.LigandDrug, new HashSet<String> ());
		extraLinks.put(MetaboliteMajorLabel.LigandGlycan, new HashSet<String> ());
		
		if (remark.contains("SAME AS")) {
			Set<String> entries = new HashSet<> ();
			// extract entries
			Pattern pattern = Pattern.compile("[CGD][0-9]+");
			Matcher matcher = pattern.matcher(remark);
			
			while (matcher.find()) {
				String entry = matcher.group();
				entries.add(entry);
				LOGGER.trace(String.format("Extracted %s", entry));
			}
			
			for (String entry : entries) {
				char initial = entry.charAt(0);
				switch (initial) {
					case 'C': extraLinks.get(MetaboliteMajorLabel.LigandCompound).add(entry); break;
					case 'D': extraLinks.get(MetaboliteMajorLabel.LigandDrug).add(entry); break;
					case 'G': extraLinks.get(MetaboliteMajorLabel.LigandGlycan).add(entry); break;
					
					default: LOGGER.warn("Wut ? " + entry);break;
				}
			}
			
			for (Label label : extraLinks.keySet()) {
				for (String entry : extraLinks.get(label)) {
					Node keggNode = Neo4jUtils.getUniqueResult(db.findNodes(label, "entry", entry));
					if (keggNode != null) {
						LOGGER.trace(String.format("Found %s - %s", keggNode, Neo4jUtils.getLabels(keggNode)));
						cluster.add(keggNode.getId());
					} else {
						LOGGER.trace(String.format("Resource not found %s", entry));
					}
				}
			}
			
			
		}
		
		return cluster;
	}

	
}
