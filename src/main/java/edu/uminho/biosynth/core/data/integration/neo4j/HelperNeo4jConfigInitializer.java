package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.Neo4jSignatureLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.LiteratureMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;

public class HelperNeo4jConfigInitializer {

	private final static Logger LOGGER = LoggerFactory.getLogger(HelperNeo4jConfigInitializer.class);
	
	private static final String[] NEO_DATA_CONSTRAINTS = {
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.id IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.internalId IS UNIQUE",
		
		String.format("CREATE CONSTRAINT ON (mmd : %s) ASSERT mmd.entry IS UNIQUE", GlobalLabel.MetabolicModel),
		String.format("CREATE CONSTRAINT ON (cmp : %s) ASSERT cmp.entry IS UNIQUE", GlobalLabel.SubcellularCompartment),
		String.format("CREATE CONSTRAINT ON (rpr : %s) ASSERT rpr.entry IS UNIQUE", GlobalLabel.KeggReactionPair),
		String.format("CREATE CONSTRAINT ON (pwy : %s) ASSERT pwy.entry IS UNIQUE", GlobalLabel.KeggPathway),
		String.format("CREATE CONSTRAINT ON (ecn : %s) ASSERT ecn.entry IS UNIQUE", GlobalLabel.EnzymeCommission),
		String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.UniProt),
//		String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.BrendaEnzyme),
		String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.EnzymePortal),
		String.format("CREATE CONSTRAINT ON (lit : %s) ASSERT lit.entry IS UNIQUE", LiteratureMajorLabel.Patent),
		String.format("CREATE CONSTRAINT ON (lit : %s) ASSERT lit.entry IS UNIQUE", LiteratureMajorLabel.PubMed),
		String.format("CREATE CONSTRAINT ON (lit : %s) ASSERT lit.entry IS UNIQUE", LiteratureMajorLabel.CiteXplore),
//		String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.EnzymePortal),
		String.format("CREATE INDEX ON :%s(proxy)", GlobalLabel.Metabolite),
		String.format("CREATE INDEX ON :%s(proxy)", GlobalLabel.Reaction),
	};
	
	private static final String[] NEO_META_CONSTRAINTS = {
		String.format("CREATE CONSTRAINT ON (iid : %s) ASSERT iid.entry IS UNIQUE", IntegrationNodeLabel.IntegrationSet),
		String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE", IntegrationNodeLabel.IntegratedCluster),
		String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.%s IS UNIQUE", IntegrationNodeLabel.IntegratedMember, Neo4jDefinitions.MEMBER_REFERENCE),
		String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", IntegrationNodeLabel.MetaboliteClusterMetaProperty, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT),
		String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", IntegrationNodeLabel.ReactionClusterMetaProperty, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT),
	};
	
	private static final String[] NEO_CURA_CONSTRAINTS = {
		String.format("CREATE CONSTRAINT ON (xid : %s) ASSERT xid.entry IS UNIQUE", CurationLabel.CurationSet),
		String.format("CREATE CONSTRAINT ON (oid : %s) ASSERT oid.entry IS UNIQUE", CurationLabel.CurationOperation),
		String.format("CREATE CONSTRAINT ON (usr : %s) ASSERT usr.username IS UNIQUE", CurationLabel.CurationUser),
		String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE", IntegrationNodeLabel.IntegratedCluster),
		String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.reference_eid IS UNIQUE", IntegrationNodeLabel.IntegratedMember),
	};
	
	private static final String[] NEO_STRU_CONSTRAINTS = {
		String.format("CREATE CONSTRAINT ON (sig : %s) ASSERT sig.key IS UNIQUE", Neo4jSignatureLabel.Signature),
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
		
		for (MetabolicModelLabel label : MetabolicModelLabel.values()) {
			String cypherQuery = String.format("CREATE CONSTRAINT ON (n:%s) ASSERT n.entry IS UNIQUE", label);
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
			LOGGER.trace("Execute Constraint: " + query);
		}
		
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService initializeNeo4jCuraDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
		for (String query: NEO_CURA_CONSTRAINTS) {
			engine.execute(query);
			LOGGER.trace("Execute Constraint: " + query);
		}
		
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService initializeNeo4jStruDatabaseConstraints(
			String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
		for (String query: NEO_STRU_CONSTRAINTS) {
			engine.execute(query);
			LOGGER.trace("Execute Constraint: " + query);
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
