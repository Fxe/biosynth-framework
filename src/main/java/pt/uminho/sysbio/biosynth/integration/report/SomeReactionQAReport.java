package pt.uminho.sysbio.biosynth.integration.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

/**
 * Reaction QA
 * @author Filipe
 *
 */
public class SomeReactionQAReport implements IntegrationSetReporter {
	
	Neo4jIntegrationMetadataDaoImpl neo4jIntegrationMetadataDaoImpl;
	GraphDatabaseService graphMetaService;
	
	public SomeReactionQAReport(GraphDatabaseService graphMetaService) {
		this.graphMetaService = graphMetaService;
		neo4jIntegrationMetadataDaoImpl = new Neo4jIntegrationMetadataDaoImpl(graphMetaService);
	}
	
	@Override
	public void generateReport(IntegrationSet integrationSet) {
		long iid = integrationSet.getId();
		Set<Long> cids = neo4jIntegrationMetadataDaoImpl.getAllIntegratedClusterIdsByType(iid, IntegrationNodeLabel.ReactionCluster.toString());
		System.out.println(cids.size());
		Map<String, Integer> frequencyCount = new HashMap<> ();
		
		for (Long cid : cids) {
			IntegratedCluster cidEntry = neo4jIntegrationMetadataDaoImpl.getIntegratedClusterById(cid);
			System.out.println(cidEntry.getId());
			Node cidNode = graphMetaService.getNodeById(cidEntry.getId());
			System.out.println(Neo4jUtils.getLabels(cidNode));
			for (IntegratedClusterMeta qaTag : cidEntry.getMeta()) {
				CollectionUtils.increaseCount(frequencyCount, qaTag.getMetaType(), 1);
			}
		}
		
		System.out.println(frequencyCount);
	}

	
}
