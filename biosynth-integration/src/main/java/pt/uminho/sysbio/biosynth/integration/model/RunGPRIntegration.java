package pt.uminho.sysbio.biosynth.integration.model;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.xml.sax.SAXException;


public class RunGPRIntegration {
	// args[0] = sbml
	// args[1] = db
	public static void main(String[] args) {
//		String db_linux = "/home/chris/neo4j-community-2.1.6/data/graph.db";
//		String db_windows = "D:/liudb/graph.db";
		GraphDatabaseService graphDatabaseService = GPRHelperNeo4jConfigInitializer
				.initializeNeo4jDatabaseConstraints(args[1]);
		GPRIntegration gprIntegration = new GPRIntegration();
//		try {
			Transaction tx = graphDatabaseService.beginTx();
//			gprIntegration.scaffoldGpr(args[0], graphDatabaseService);
			tx.success();
			tx.close();
			graphDatabaseService.shutdown();
//		} catch () {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
