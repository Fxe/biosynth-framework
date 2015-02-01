package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;

public class HelperNeo4jConfigInitializer {

	private final static Logger LOGGER = LoggerFactory.getLogger(HelperNeo4jConfigInitializer.class);
	
	private static final String[] NEO_DATA_CONSTRAINTS = {
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.id IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.internalId IS UNIQUE",
		
		"CREATE CONSTRAINT ON (c:Compartment) ASSERT c.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (m:Model) ASSERT m.entry IS UNIQUE",
		"CREATE INDEX ON :Metabolite(proxy)",
		"CREATE INDEX ON :Reaction(proxy)",
	};
	
	private static final String[] NEO_META_CONSTRAINTS = {
		String.format("CREATE CONSTRAINT ON (iid : %s) ASSERT iid.entry IS UNIQUE", IntegrationNodeLabel.IntegrationSet),
		String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE", IntegrationNodeLabel.IntegratedCluster),
		String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.reference_eid IS UNIQUE", IntegrationNodeLabel.IntegratedMember),
	};
	
	private static final String[] NEO_CURA_CONSTRAINTS = {
		String.format("CREATE CONSTRAINT ON (xid : %s) ASSERT xid.entry IS UNIQUE", CurationLabel.CurationSet),
		String.format("CREATE CONSTRAINT ON (oid : %s) ASSERT oid.entry IS UNIQUE", CurationLabel.CurationOperation),
		String.format("CREATE CONSTRAINT ON (usr : %s) ASSERT usr.username IS UNIQUE", CurationLabel.CurationUser),
		String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE", IntegrationNodeLabel.IntegratedCluster),
		String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.reference_eid IS UNIQUE", IntegrationNodeLabel.IntegratedMember),
	};
	
	public static GraphDatabaseService initializeNeo4jDataDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
		
		for (MetaboliteMajorLabel label : MetaboliteMajorLabel.values()) {
			String cypherQuery = String.format("CREATE CONSTRAINT ON (cpd:%s) ASSERT cpd.entry IS UNIQUE", label);
			LOGGER.trace("Execute Constraint: " + cypherQuery);
			engine.execute(cypherQuery);
		}
		
		for (MetabolitePropertyLabel label : MetabolitePropertyLabel.values()) {
			String cypherQuery = String.format("CREATE CONSTRAINT ON (p:%s) ASSERT p.key IS UNIQUE", label);
			LOGGER.trace("Execute Constraint: " + cypherQuery);
			engine.execute(cypherQuery);
		}
		
		for (ReactionMajorLabel label : ReactionMajorLabel.values()) {
			String cypherQuery = String.format("CREATE CONSTRAINT ON (rxn:%s) ASSERT rxn.entry IS UNIQUE", label);
			LOGGER.trace("Execute Constraint: " + cypherQuery);
			engine.execute(cypherQuery);
		}
		
		for (String query: NEO_DATA_CONSTRAINTS) {
			engine.execute(query);
		}
		
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService initializeNeo4jMetaDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
		for (String query: NEO_META_CONSTRAINTS) {
			engine.execute(query);
		}
		
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService initializeNeo4jCuraDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
		for (String query: NEO_CURA_CONSTRAINTS) {
			engine.execute(query);
		}
		
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService initializeNeo4jDatabase(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService s(String mg) {
//		org.neo4j.
//		GraphDatabaseService graphDatabaseService = new Remote
		return null;
	}
}
