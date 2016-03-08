package pt.uminho.sysbio.biosynth.integration.model;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteGprs {
	private static final Logger LOGGER = LoggerFactory.getLogger(DeleteGprs.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String db_linux = "/home/chris/neo4j-community-2.1.6/data/graph.db";
//		String db_windows = "D:/Dropbox/liudb/graph.db";
		GraphDatabaseService graphDatabaseService = GPRHelperNeo4jConfigInitializer
				.initializeNeo4jDatabase(args[1]);
		GPRIntegration gprIntegration = new GPRIntegration();
		Transaction tx = graphDatabaseService.beginTx();
		Map<String, Integer> deletionInfo = new HashMap<String, Integer>();
		deletionInfo = gprIntegration.clearGpr(args[0], graphDatabaseService);
		for (String key : deletionInfo.keySet()){
			System.out.println(key + ": " + deletionInfo.get(key));
		}
		tx.success();
		tx.close();
		graphDatabaseService.shutdown();

	}

}
