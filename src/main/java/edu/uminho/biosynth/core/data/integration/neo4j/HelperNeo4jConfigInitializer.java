package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;

public class HelperNeo4jConfigInitializer {

	private static final String[] NEO_DATA_CONSTRAINTS = {
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.id IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BioPath) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:HMDB) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.referenceId IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:LigandCompound) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:LigandDrug) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:LigandGlycan) ASSERT cpd.entry IS UNIQUE",
//		"CREATE CONSTRAINT ON (cpd:KEGG) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:MetaCyc) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:AraCyc) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:MaizeCyc) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:ChEBI) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:CAS) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:Seed) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:MetaNetX) ASSERT cpd.entry IS UNIQUE",
		
		"CREATE CONSTRAINT ON (n:Name) ASSERT n.key IS UNIQUE",
		"CREATE CONSTRAINT ON (f:Formula) ASSERT f.key IS UNIQUE",
		"CREATE CONSTRAINT ON (i:InChI) ASSERT i.key IS UNIQUE",
		"CREATE CONSTRAINT ON (s:CanSMILES) ASSERT s.key IS UNIQUE",
		"CREATE CONSTRAINT ON (if:IsotopeFormula) ASSERT if.key IS UNIQUE",
		"CREATE CONSTRAINT ON (s:SMILES) ASSERT s.key IS UNIQUE",
		"CREATE CONSTRAINT ON (c:Charge) ASSERT c.key IS UNIQUE",
		
		"CREATE CONSTRAINT ON (c:FIKHB) ASSERT c.key IS UNIQUE",
		
		"CREATE CONSTRAINT ON (c:Compartment) ASSERT c.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (m:Model) ASSERT m.entry IS UNIQUE",
		"CREATE INDEX ON :Metabolite(proxy)",
		
		"CREATE CONSTRAINT ON (rxn:LigandReaction) ASSERT rxn.entry IS UNIQUE",
		
	};
	
	private static final String[] NEO_META_CONSTRAINTS = {
		"CREATE CONSTRAINT ON (iid : IntegrationSet) ASSERT iid.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cid : MetaboliteCluster) ASSERT cid.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cid : MetaboliteMember) ASSERT cid.id IS UNIQUE",
		"CREATE CONSTRAINT ON (cid : ReactionCluster) ASSERT cid.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cid : ReactionMember) ASSERT cid.id IS UNIQUE",
	};
	
	private static final String[] NEO_CURA_CONSTRAINTS = {
		String.format("CREATE CONSTRAINT ON (xid : %s) ASSERT xid.entry IS UNIQUE", CurationLabel.CurationSet),
		String.format("CREATE CONSTRAINT ON (oid : %s) ASSERT oid.entry IS UNIQUE", CurationLabel.CurationOperation),
		String.format("CREATE CONSTRAINT ON (usr : %s) ASSERT usr.username IS UNIQUE", CurationLabel.CurationUser),
		String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE", IntegrationNodeLabel.IntegratedCluster),
		String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.referenceId IS UNIQUE", IntegrationNodeLabel.IntegratedMember),
	};
	
	public static GraphDatabaseService initializeNeo4jDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
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
