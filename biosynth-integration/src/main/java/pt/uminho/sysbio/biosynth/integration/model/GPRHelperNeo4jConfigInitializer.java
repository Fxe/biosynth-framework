package pt.uminho.sysbio.biosynth.integration.model;


import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;

public class GPRHelperNeo4jConfigInitializer {
	private static final String[] NEO_DATA_CONSTRAINTS = {
		"CREATE CONSTRAINT ON (gene:Leaf) ASSERT gene.entry IS UNIQUE",
		String.format("CREATE INDEX ON :%s(proxy)", MetabolicModelLabel.ModelGene),
	};
	

	
	public static GraphDatabaseService initializeNeo4jDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File(databasePath));
		
		for (String query: NEO_DATA_CONSTRAINTS) {
		  graphDatabaseService.execute(query);
		}
		
		return graphDatabaseService;
	}
	

	
	
	public static GraphDatabaseService initializeNeo4jDatabase(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File(databasePath));
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService s(String mg) {
//		org.neo4j.
//		GraphDatabaseService graphDatabaseService = new Remote
		return null;
	}
}
