package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.annotations.BiosReport;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelReactionNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jMetabolicModelIntegrationReporter extends AbstractNeo4jReporter {

  public Neo4jMetabolicModelIntegrationReporter(GraphDatabaseService service) {
    super(service);
  }
  
  public static ExternalReference selectReactionReference(BiosModelReactionNode rxnNode) {
    String database = null;
    String value = null;
    for (Node node : Neo4jUtils.collectNodeRelationshipNodes(rxnNode, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (node.hasLabel(ReactionMajorLabel.ModelSeedReaction)) {
        database = ReactionMajorLabel.ModelSeedReaction.toString();
        value = (String) node.getProperty("entry");
      }
    }
    if (database == null) {
      for (Node node : Neo4jUtils.collectNodeRelationshipNodes(rxnNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        if (node.hasLabel(ReactionMajorLabel.LigandReaction)) {
          database = ReactionMajorLabel.LigandReaction.toString();
          value = (String) node.getProperty("entry");
        }
      }
    }
    if (database == null) {
      for (Node node : Neo4jUtils.collectNodeRelationshipNodes(rxnNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        if (node.hasLabel(ReactionMajorLabel.BiGGReaction)) {
          database = ReactionMajorLabel.BiGGReaction.toString();
          value = (String) node.getProperty("entry");
        }
      }
    }
    if (database == null) {
      value = rxnNode.getSid();
      database = "Model";
    }
    return new ExternalReference(value, database);
  }
  
  public static ExternalReference selectSpeciesReference(BiosModelSpeciesNode spiNode) {
    String database = null;
    String value = null;
    for (Node node : Neo4jUtils.collectNodeRelationshipNodes(spiNode, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (node.hasLabel(MetaboliteMajorLabel.ModelSeed)) {
        database = MetaboliteMajorLabel.ModelSeed.toString();
        value = (String) node.getProperty("entry");
      }
    }
    if (database == null) {
      for (Node node : Neo4jUtils.collectNodeRelationshipNodes(spiNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        if (node.hasLabel(MetaboliteMajorLabel.BiGGMetabolite)) {
          database = MetaboliteMajorLabel.BiGGMetabolite.toString();
          value = (String) node.getProperty("entry");
        }
      }
    }
    if (database == null) {
      for (Node node : Neo4jUtils.collectNodeRelationshipNodes(spiNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        if (node.hasLabel(MetaboliteMajorLabel.LigandCompound)) {
          database = MetaboliteMajorLabel.LigandCompound.toString();
          value = (String) node.getProperty("entry");
        }
      }
    }
    if (database == null) {
      for (Node node : Neo4jUtils.collectNodeRelationshipNodes(spiNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        if (node.hasLabel(MetaboliteMajorLabel.MetaCyc)) {
          database = MetaboliteMajorLabel.MetaCyc.toString();
          value = (String) node.getProperty("entry");
        }
      }
    }
    if (database == null) {
      for (Node node : Neo4jUtils.collectNodeRelationshipNodes(spiNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        if (node.hasLabel(MetaboliteMajorLabel.BiGG)) {
          database = MetaboliteMajorLabel.BiGG.toString();
          value = (String) node.getProperty("entry");
        }
      }
    }
    if (database == null) {
      value = spiNode.getSid();
      database = "Model";
    }
    return new ExternalReference(value, database);
  }

  @BiosReport
  public Dataset<String, String, Integer> reportRxn(Set<String> models) {
    Dataset<String, String, Integer> report = new Dataset<>();
    
    for (String modelEntry : models) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(modelEntry);
      if (modelNode != null && !modelNode.isProxy()) {
        Map<String, Integer> count = new HashMap<>();
        for (BiosModelReactionNode mrxnNode : modelNode.getModelReactions()) {
          ExternalReference ref = selectReactionReference(mrxnNode);
          CollectionUtils.increaseCount(count, ref.source, 1);
        }
        report.dataset.put(modelEntry, count);
      }
    }
    
    
    return report; 
  }
  
  @BiosReport
  public Dataset<String, String, Integer> reportRxnBasic(Set<String> models) {
    Dataset<String, String, Integer> report = new Dataset<>();
    
    for (String modelEntry : models) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(modelEntry);
      if (modelNode != null && !modelNode.isProxy()) {
        Map<String, Integer> count = new HashMap<>();
        for (BiosModelReactionNode mrxnNode : modelNode.getModelReactions()) {
          if (!mrxnNode.isTranslocation()) {
            ExternalReference ref = selectReactionReference(mrxnNode);
            CollectionUtils.increaseCount(count, ref.source, 1);            
          }
        }
        report.dataset.put(modelEntry, count);
      }
    }
    
    
    return report; 
  }
  
  @BiosReport
  public Dataset<String, String, Integer> reportSpi(Set<String> models) {
    Dataset<String, String, Integer> report = new Dataset<>();
    
    for (String modelEntry : models) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(modelEntry);
      if (modelNode != null && !modelNode.isProxy()) {
        Map<String, Integer> count = new HashMap<>();
        for (BiosModelSpeciesNode mrxnNode : modelNode.getMetaboliteSpecies()) {
          ExternalReference ref = selectSpeciesReference(mrxnNode);
          CollectionUtils.increaseCount(count, ref.source, 1);
        }
        report.dataset.put(modelEntry, count);
      }
    }
    
    return report; 
  }
  
  @BiosReport
  public Dataset<String, String, Integer> reportSpi() {
    Set<String> models = new HashSet<>();
    for (BiosMetabolicModelNode m : service.listMetabolicModels()) {
      models.add(m.getEntry());
    }
    return reportSpi(models);
  }
  
  @BiosReport
  public Dataset<String, String, Integer> reportRxn() {
    Set<String> models = new HashSet<>();
    for (BiosMetabolicModelNode m : service.listMetabolicModels()) {
      models.add(m.getEntry());
    }
    return reportRxn(models);
  }
}
