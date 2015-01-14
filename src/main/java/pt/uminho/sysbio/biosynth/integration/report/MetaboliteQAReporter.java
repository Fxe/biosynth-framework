package pt.uminho.sysbio.biosynth.integration.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynth.integration.strategy.metabolite.ChebiParentClusteringStrategy;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class MetaboliteQAReporter implements IntegrationSetReporter {

	GraphDatabaseService graphDataService;
	GraphDatabaseService graphMetaService;
	Neo4jIntegrationMetadataDaoImpl neo4jIntegrationMetadataDaoImpl;
	Neo4jGraphMetaboliteDaoImpl neo4jGraphMetaboliteDaoImpl;
	
	public MetaboliteQAReporter(
			GraphDatabaseService graphDataService,
			GraphDatabaseService graphMetaService) {
		
		this.graphDataService = graphDataService;
		this.graphMetaService = graphMetaService;
		neo4jIntegrationMetadataDaoImpl = 
				new Neo4jIntegrationMetadataDaoImpl(graphMetaService);
		
		neo4jGraphMetaboliteDaoImpl = 
				new Neo4jGraphMetaboliteDaoImpl(graphDataService);
	}
	
	@Override
	public void generateReport(IntegrationSet integrationSet) {
		long iid = integrationSet.getId();

		
		Map<Integer, Integer> sizeDistributionCount = new HashMap<> ();
		Map<Integer, Integer> sizeDistributionChebiMergeCount = new HashMap<> ();
		Map<Integer, Set<String>> efnjdsg = new HashMap<> ();
		Map<String, Integer> frequencyCount = new HashMap<> ();
		
		ChebiParentClusteringStrategy chebiParentClusteringStrategy =
				new ChebiParentClusteringStrategy(graphDataService);
		
		Set<Long> cids = neo4jIntegrationMetadataDaoImpl.getAllIntegratedClusterIdsByType(iid, IntegrationNodeLabel.MetaboliteCluster.toString());
//		Long[] cids = new Long[] {26040L, 45889L};
		for (Long cid : cids) {
			IntegratedCluster cidEntry = neo4jIntegrationMetadataDaoImpl.getIntegratedClusterById(cid);
			for (String qaTag : cidEntry.getMeta().keySet()) {
				CollectionUtils.increaseCount(frequencyCount, qaTag, 1);
			}
			
			int size = cidEntry.getMembers().size();
			CollectionUtils.increaseCount(sizeDistributionCount, size, 1);
			
			if (!efnjdsg.containsKey(size)) {
				efnjdsg.put(size, new HashSet<String> ());
			}
			efnjdsg.get(size).add(String.format("%d:%s", cid, cidEntry.getEntry()));
			
			int chebiMergeSize = -1;
			Set<Long> nonChebiEids = new HashSet<> ();
			Set<Long> chebiCluster = new HashSet<> ();
			Map<Long, Long> chebiMap = new HashMap<> ();
			for (IntegratedClusterMember member : cidEntry.getMembers()) {
				long eid = member.getMember().getReferenceId();
				nonChebiEids.add(eid);
//				System.out.println(eid + " " + chebiMap.values());
				if (!chebiMap.keySet().contains(eid)) {
					GraphMetaboliteEntity entity = neo4jGraphMetaboliteDaoImpl.getMetaboliteById("", eid);
					if (entity.getMajorLabel().equals(MetaboliteMajorLabel.ChEBI.toString())) {
						chebiParentClusteringStrategy.setInitialNode(eid);
						Set<Long> res = chebiParentClusteringStrategy.execute();
						for (long eid_chebi : res) {
							chebiMap.put(eid_chebi, eid);
						}
					}
				}
			}
//			System.out.println();
			chebiCluster.addAll(chebiMap.values());
			nonChebiEids.removeAll(chebiMap.keySet());
			
//			for (long eeee : nonChebiEids) {
//				Node node = graphDataService.getNodeById(eeee);
//				System.out.println(Neo4jUtils.getLabels(node) + " " + node.getProperty("entry", ":):):)"));
//			}
//			for (long eeee : chebiCluster) {
//				Node node = graphDataService.getNodeById(eeee);
//				System.out.println(Neo4jUtils.getLabels(node) + " " + node.getProperty("entry", ":):):)"));
//			}
			chebiMergeSize = nonChebiEids.size() + chebiCluster.size();
			
			CollectionUtils.increaseCount(sizeDistributionChebiMergeCount, chebiMergeSize, 1);
//			if (cidEntry.getMeta().containsKey(MetaboliteQualityLabel.FORMULA_MISMATCH.toString())
//					|| cidEntry.getMeta().containsKey(MetaboliteQualityLabel.INCHI_MISMATCH.toString())
//					|| cidEntry.getMeta().containsKey(MetaboliteQualityLabel.INCHI_SECOND_HASH_BLOCK_MISMATCH.toString())
//					|| cidEntry.getMeta().containsKey(MetaboliteQualityLabel.CROSSREFERENCE_EXTERNAL.toString())
//					) {
//				
//				System.out.println(PrintStringUtil.toString(cidEntry));
//				break;
//			}
		}
		
		System.out.println(frequencyCount);
		System.out.println(sizeDistributionCount);
		System.out.println(sizeDistributionChebiMergeCount);
		
		for (int size : sizeDistributionCount.keySet()) {
			System.out.println(size + " : " + efnjdsg.get(size).toString().substring(0, efnjdsg.get(size).toString().length() > 50 ? 50 : efnjdsg.get(size).toString().length()));
		}
	}

}
