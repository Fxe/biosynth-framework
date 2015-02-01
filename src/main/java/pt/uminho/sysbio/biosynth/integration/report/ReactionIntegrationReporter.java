package pt.uminho.sysbio.biosynth.integration.report;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;

public class ReactionIntegrationReporter implements IntegrationSetReporter {

	private final static Logger LOGGER = LoggerFactory.getLogger(ReactionIntegrationReporter.class);
	
	Neo4jIntegrationMetadataDaoImpl neo4jIntegrationMetadataDaoImpl;
	GraphDatabaseService graphDataService;
	GraphDatabaseService graphMetaService;
	
	public ReactionIntegrationReporter(
			GraphDatabaseService graphDataService, 
			GraphDatabaseService graphMetaService) {
		
		this.graphDataService = graphDataService;
		this.graphMetaService = graphMetaService;
		neo4jIntegrationMetadataDaoImpl = new Neo4jIntegrationMetadataDaoImpl(graphMetaService);
	}
	
	public boolean isGeneric(Node node) {
		if (!node.hasLabel(GlobalLabel.Metabolite)) {
			throw new RuntimeException("invalid " + node + " " + Neo4jUtils.getLabels(node));
		}
		
		for (Relationship relationship : node
				.getRelationships(MetaboliteRelationshipType.has_molecular_formula)) {
			
			String status = (String) relationship.getProperty("DCS-status", null);
			if (status == null) {
				LOGGER.warn("no status !");
				return true;
			} else {
				if (status.equals("CORRUPT")) {
					return true;
				} else {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean isGenericReactionNode(Node node) {
		if (!node.hasLabel(GlobalLabel.Reaction)) {
			throw new RuntimeException("invalid " + node + " " + Neo4jUtils.getLabels(node));
		}
		
		boolean generic = true;
		
		Set<Long> cpdIds = Neo4jUtils.collectNodeRelationshipNodeIds(node, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
		for (Long cpdId : cpdIds) {
			Node cpdNode = graphDataService.getNodeById(cpdId);
			generic &= !isGeneric(cpdNode);
		}
		
//		for (Relationship relationship : node
//				.getRelationships(MetaboliteRelationshipType.HasMolecularFormula)) {
//			
//			String status = (String) relationship.getProperty("DCS-status", null);
//			if (status == null) {
//				LOGGER.warn("no status !");
//				return true;
//			} else {
//				if (status.equals("CORRUPT")) {
//					return true;
//				} else {
//					return false;
//				}
//			}
//		}
		
//		LOGGER.debug(String.format("%s - %s, %s => %s", node, Neo4jUtils.getLabels(node), node.getProperty("equation", null), !generic?"GENERIC":"NON-GENERIC"));
		
		return !generic;
	}
	
	@Override
	public void generateReport(IntegrationSet integrationSet) {
		long iid = integrationSet.getId();
//		IntegrationSet integrationSet = neo4jIntegrationMetadataDaoImpl.getIntegrationSet(iid);
		System.out.println(integrationSet);
		
		Set<Long> cidSet = neo4jIntegrationMetadataDaoImpl.getAllIntegratedClusterIdsByType(iid, 
				IntegrationNodeLabel.ReactionCluster.toString());
		
		Set<Long> eidSet = new HashSet<> ();
		for (long cid : cidSet) {
			Node cidNode = graphMetaService.getNodeById(cid);
			for (Relationship relationship : cidNode.getRelationships(IntegrationRelationshipType.Integrates)) {
				Node eidNode = relationship.getOtherNode(cidNode);
				if (eidNode.hasLabel(IntegrationNodeLabel.IntegratedMember)) {
					eidSet.add((long)eidNode.getProperty("id"));	
				}
			}
		}
		
		System.out.println("Reaction EIDS: " + eidSet.size());
		
		for (Label label : ReactionMajorLabel.values()) {
			int totalNonProxy = 0;
			int totalIntegrated = 0;
			int totalGenerics = 0;
			int totalIntegratedGenerics = 0;
			int totalNonGenerics = 0;
			int totalIntegratedNonGenerics = 0;
			for (Node node : GlobalGraphOperations
					.at(graphDataService)
					.getAllNodesWithLabel(label)) {
				long nodeId = node.getId();
				if (node.hasLabel(GlobalLabel.Reaction)) {
					boolean generic = isGenericReactionNode(node);
					if (generic) {
						totalGenerics++;
					} else {
						totalNonGenerics++;
					}
					if (!(boolean)node.getProperty("proxy", true)) {
						totalNonProxy++;
						if (eidSet.contains(nodeId)) {
							totalIntegrated++;
//							if (reactionDep) totalIntegratedWithReaction++;
							if (generic) {
								totalIntegratedGenerics++;
							} else {
								totalIntegratedNonGenerics++;
							}
						}
					}
				}
			}
			
			String out = String.format("%s\t%d\t%d\t%d\t%d\t%d\t%d", 
					label, 
					totalNonProxy, totalIntegrated, 
					totalGenerics, totalIntegratedGenerics,
					totalNonGenerics, totalIntegratedNonGenerics
					);
			
			if (totalNonProxy > 0) System.out.println(out);
		}
	}

}
