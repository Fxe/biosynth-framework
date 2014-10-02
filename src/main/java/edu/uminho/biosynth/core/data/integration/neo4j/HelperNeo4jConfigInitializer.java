package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class HelperNeo4jConfigInitializer {

	private static final String[] contraints = {
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.id IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BioPath) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:HMDB) ASSERT cpd.entry IS UNIQUE",
		"CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.entry IS UNIQUE",
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
		"CREATE CONSTRAINT ON (n:Name) ASSERT n.name IS UNIQUE",
		"CREATE CONSTRAINT ON (f:Formula) ASSERT f.formula IS UNIQUE",
		"CREATE CONSTRAINT ON (i:InChI) ASSERT i.inchi IS UNIQUE",
		"CREATE CONSTRAINT ON (s:CanSMILES) ASSERT s.can IS UNIQUE",
		"CREATE CONSTRAINT ON (if:IsotopeFormula) ASSERT if.formula IS UNIQUE",
		"CREATE CONSTRAINT ON (s:SMILES) ASSERT s.smiles IS UNIQUE",
		"CREATE CONSTRAINT ON (c:Charge) ASSERT c.charge IS UNIQUE",
		"CREATE CONSTRAINT ON (c:Compartment) ASSERT c.compartment IS UNIQUE",
		"CREATE CONSTRAINT ON (m:Model) ASSERT m.id IS UNIQUE",
		"CREATE INDEX ON :Compound(proxy)",
		
		"CREATE CONSTRAINT ON (rxn:LigandReaction) ASSERT rxn.entry IS UNIQUE",
	};
	
	public static GraphDatabaseService initializeNeo4jDatabaseConstraints(String databasePath) {
		GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);
		ExecutionEngine engine = new ExecutionEngine(graphDatabaseService);
		for (String query: contraints) {
			engine.execute(query);
		}
		
		return graphDatabaseService;
	}
	
	public static GraphDatabaseService s(String mg) {
//		org.neo4j.
//		GraphDatabaseService graphDatabaseService = new Remote
		return null;
	}
}
