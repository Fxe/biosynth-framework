package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.Neo4jSignatureLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jLayoutLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.GenomeDatabase;
import pt.uminho.sysbio.biosynthframework.neo4j.LiteratureDatabase;
import pt.uminho.sysbio.biosynthframework.neo4j.OntologyDatabase;

public class HelperNeo4jConfigInitializer {

  private final static Logger logger = LoggerFactory.getLogger(HelperNeo4jConfigInitializer.class);

  private static final String[] NEO_DATA_CONSTRAINTS = {
      
      "CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.id IS UNIQUE",
      "CREATE CONSTRAINT ON (cpd:BiGG) ASSERT cpd.internalId IS UNIQUE",
      String.format("CREATE CONSTRAINT ON (o : %s) ASSERT o.entry IS UNIQUE", GlobalLabel.Database),
      String.format("CREATE CONSTRAINT ON (mmd : %s) ASSERT mmd.entry IS UNIQUE", GlobalLabel.MetabolicModel),
      String.format("CREATE CONSTRAINT ON (cmp : %s) ASSERT cmp.entry IS UNIQUE", GlobalLabel.SubcellularCompartment),
      String.format("CREATE CONSTRAINT ON (rpr : %s) ASSERT rpr.entry IS UNIQUE", GlobalLabel.KeggReactionPair),
      String.format("CREATE CONSTRAINT ON (pwy : %s) ASSERT pwy.entry IS UNIQUE", GlobalLabel.KeggPathway),
      String.format("CREATE CONSTRAINT ON (ecn : %s) ASSERT ecn.entry IS UNIQUE", GlobalLabel.EnzymeCommission),
//      String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.UniProt),
      String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.UniProtAccession),
      String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.UniProtProtein),
      String.format("CREATE CONSTRAINT ON (sgd : %s) ASSERT sgd.entry IS UNIQUE", GlobalLabel.SGD),
//      String.format("CREATE CONSTRAINT ON (exp : %s) ASSERT exp.entry IS UNIQUE", GlobalLabel.ExPASy),
      String.format("CREATE CONSTRAINT ON (tax : %s) ASSERT tax.entry IS UNIQUE", GlobalLabel.NcbiTaxonomy),
      String.format("CREATE CONSTRAINT ON (gne : %s) ASSERT gne.entry IS UNIQUE", GlobalLabel.NCBIGene),
      
      String.format("CREATE CONSTRAINT ON (mss : %s) ASSERT mss.entry IS UNIQUE", GlobalLabel.ModelSeedSubsystem),
      String.format("CREATE CONSTRAINT ON (msr : %s) ASSERT msr.entry IS UNIQUE", GlobalLabel.ModelSeedRole),
      
      String.format("CREATE CONSTRAINT ON (prt : %s) ASSERT prt.entry IS UNIQUE", GlobalLabel.MetaCycProtein),
      
      String.format("CREATE CONSTRAINT ON (org : %s) ASSERT org.entry IS UNIQUE", GlobalLabel.KeggOrganism),
      String.format("CREATE CONSTRAINT ON (kgn : %s) ASSERT kgn.entry IS UNIQUE", GlobalLabel.KeggGene),
      //		String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.BrendaEnzyme),
      String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.EnzymePortal),
      String.format("CREATE CONSTRAINT ON (lit : %s) ASSERT lit.entry IS UNIQUE", LiteratureDatabase.Patent),
      String.format("CREATE CONSTRAINT ON (lit : %s) ASSERT lit.entry IS UNIQUE", LiteratureDatabase.PubMed),
      String.format("CREATE CONSTRAINT ON (lit : %s) ASSERT lit.entry IS UNIQUE", LiteratureDatabase.CiteXplore),
      
      String.format("CREATE CONSTRAINT ON (phe : %s) ASSERT phe.key IS UNIQUE", GlobalLabel.Phenotype),
      //		String.format("CREATE CONSTRAINT ON (pro : %s) ASSERT pro.entry IS UNIQUE", GlobalLabel.EnzymePortal),
      String.format("CREATE INDEX ON :%s(proxy)", GlobalLabel.Metabolite),
      String.format("CREATE INDEX ON :%s(proxy)", GlobalLabel.Reaction),
      String.format("CREATE INDEX ON :%s(proxy)", GlobalLabel.Gene),
      
      String.format("CREATE INDEX ON :%s(locus)", GlobalLabel.UniProtProtein),
      String.format("CREATE INDEX ON :%s(locus)", GlobalLabel.NCBIGene),
      String.format("CREATE INDEX ON :%s(old_locus)", GlobalLabel.NCBIGene),
      String.format("CREATE INDEX ON :%s(accession)", GlobalLabel.NCBIGene),
      
      String.format("CREATE INDEX ON :%s(universalEntry)", MetaboliteMajorLabel.BiGG2),
      String.format("CREATE INDEX ON :%s(abbreviation)", MetaboliteMajorLabel.ModelSeed),
      
      
  };

  private static final String[] NEO_META_CONSTRAINTS = {
      String.format("CREATE CONSTRAINT ON (iid : %s) ASSERT iid.entry IS UNIQUE", 
          IntegrationNodeLabel.IntegrationSet),
      String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE",
          IntegrationNodeLabel.IntegratedCluster),
      String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.%s IS UNIQUE", 
          IntegrationNodeLabel.IntegratedMember, Neo4jDefinitions.MEMBER_REFERENCE),
      String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", 
          IntegrationNodeLabel.MetaboliteClusterMetaProperty, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT),
      String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", 
          IntegrationNodeLabel.ReactionClusterMetaProperty, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT),
  };

  private static final String[] NEO_CURA_CONSTRAINTS = {
      String.format("CREATE CONSTRAINT ON (xid : %s) ASSERT xid.entry IS UNIQUE", CurationLabel.CurationSet),
      String.format("CREATE CONSTRAINT ON (oid : %s) ASSERT oid.entry IS UNIQUE", CurationLabel.CurationOperation),
      String.format("CREATE CONSTRAINT ON (usr : %s) ASSERT usr.username IS UNIQUE", CurationLabel.CurationUser),
      String.format("CREATE CONSTRAINT ON (cid : %s) ASSERT cid.entry IS UNIQUE", IntegrationNodeLabel.IntegratedCluster),
      String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.reference_eid IS UNIQUE", IntegrationNodeLabel.IntegratedMember),
      String.format("CREATE CONSTRAINT ON (uid : %s) ASSERT uid.entry IS UNIQUE", CurationLabel.UniversalMetabolite),
      String.format("CREATE CONSTRAINT ON (uid : %s) ASSERT uid.entry IS UNIQUE", CurationLabel.UniversalSpecie),
      String.format("CREATE CONSTRAINT ON (uid : %s) ASSERT uid.entry IS UNIQUE", CurationLabel.UniversalReaction),
  };

  private static final String[] NEO_STRU_CONSTRAINTS = {
      String.format("CREATE CONSTRAINT ON (sig : %s) ASSERT sig.key IS UNIQUE", Neo4jSignatureLabel.Signature),
      String.format("CREATE CONSTRAINT ON (eid : %s) ASSERT eid.%s IS UNIQUE", IntegrationNodeLabel.IntegratedMember, Neo4jDefinitions.MEMBER_REFERENCE),
      String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", Neo4jSignatureLabel.ChemicalStructure, Neo4jDefinitions.SHA256),
  };
  
  private static final String[] NEO_LAYO_CONSTRAINTS = {
      String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.entry IS UNIQUE", Neo4jLayoutLabel.MetabolicLayout),
      String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", Neo4jLayoutLabel.ReactionReference, Neo4jDefinitions.MEMBER_REFERENCE),
      String.format("CREATE CONSTRAINT ON (n : %s) ASSERT n.%s IS UNIQUE", Neo4jLayoutLabel.MetaboliteReference, Neo4jDefinitions.MEMBER_REFERENCE),
  };

  public static GraphDatabaseService initializeNeo4jDataDatabaseConstraints(String databasePath) {
    GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File(databasePath));
    return initializeNeo4jDataDatabaseConstraints(graphDatabaseService);
  }
  
  public static GraphDatabaseService initializeNeo4jDataDatabaseConstraints(GraphDatabaseService graphDatabaseService) {
    

    for (MetaboliteMajorLabel label : MetaboliteMajorLabel.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (cpd:%s) ASSERT cpd.entry IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }

    for (MetabolitePropertyLabel label : MetabolitePropertyLabel.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (p:%s) ASSERT p.key IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }

    for (ReactionMajorLabel label : ReactionMajorLabel.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (rxn:%s) ASSERT rxn.entry IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }

    for (MetabolicModelLabel label : MetabolicModelLabel.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (n:%s) ASSERT n.entry IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }
    
    for (LiteratureDatabase label : LiteratureDatabase.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (n:%s) ASSERT n.entry IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }
    
    for (OntologyDatabase label : OntologyDatabase.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (n:%s) ASSERT n.entry IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }

    for (GenomeDatabase label : GenomeDatabase.values()) {
      String cypherQuery = String.format("CREATE CONSTRAINT ON (n:%s) ASSERT n.entry IS UNIQUE", label);
      logger.trace("Execute Constraint: " + cypherQuery);
      graphDatabaseService.execute(cypherQuery);
    }
    
    for (String query: NEO_DATA_CONSTRAINTS) {
      graphDatabaseService.execute(query);
    }

    return graphDatabaseService;
  }

  public static GraphDatabaseService initializeNeo4jMetaDatabaseConstraints(String databasePath) {
    GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File(databasePath));

    for (String query: NEO_META_CONSTRAINTS) {
      graphDatabaseService.execute(query);
      logger.trace("Execute Constraint: " + query);
    }

    return graphDatabaseService;
  }

  public static GraphDatabaseService initializeNeo4jCuraDatabaseConstraints(String databasePath) {
    GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(new File(databasePath));

    for (String query: NEO_CURA_CONSTRAINTS) {
      graphDatabaseService.execute(query);
      logger.trace("Execute Constraint: " + query);
    }

    return graphDatabaseService;
  }

  public static GraphDatabaseService executeNeo4jLayoDatabaseConstraints(
      GraphDatabaseService service) {

    for (String query: NEO_LAYO_CONSTRAINTS) {
      service.execute(query);
      logger.trace("Execute Constraint: " + query);
    }

    return service;
  }

  public static GraphDatabaseService initializeNeo4jStruDatabaseConstraints(String path) {
    GraphDatabaseService service = new GraphDatabaseFactory().newEmbeddedDatabase(new File(path));
    
    for (String query: NEO_STRU_CONSTRAINTS) {
      service.execute(query);
      logger.trace("Execute Constraint: " + query);
    }

    return service;
  }

  public static GraphDatabaseService initializeNeo4jDatabase(String path) {
    File dbPath = new File(path);
    GraphDatabaseService graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
    
    Transaction tx = graphDatabaseService.beginTx();
    
    Iterable<ConstraintDefinition> it = graphDatabaseService.schema().getConstraints();

    if (!it.iterator().hasNext()) {
      logger.info("initialized constraints...");
      initializeNeo4jDataDatabaseConstraints(graphDatabaseService);
    }
    
    tx.success();
    tx.close();
    
    BiodbGraphDatabaseService service = new BiodbGraphDatabaseService(graphDatabaseService);
    service.databasePath = path;
    File edata = new File(path + "/../" + dbPath.getName() + "_" + Neo4jDefinitions.EXTERNAL_DATA_FOLDER);
    if (!edata.exists()) {
      edata.mkdir();
      logger.info("created external data folder at: {}", edata);
    }
    service.databasePath = edata.getAbsolutePath();
    return service;
  }

  public static GraphDatabaseService s(String mg) {
    //		org.neo4j.
    //		GraphDatabaseService graphDatabaseService = new Remote
    return null;
  }


}
