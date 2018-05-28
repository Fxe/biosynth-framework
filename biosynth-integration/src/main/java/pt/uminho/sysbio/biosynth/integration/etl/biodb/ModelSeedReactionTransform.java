package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionReagentEntity;

public class ModelSeedReactionTransform extends AbstractReactionTransform<ModelSeedReactionEntity>{

  private static final Logger logger = LoggerFactory.getLogger(ModelSeedReactionTransform.class);
  
  public static Map<String, String> getDbMapping() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("KEGG", ReactionMajorLabel.LigandReaction.toString());
    mapping.put("PlantCyc", ReactionMajorLabel.PlantCycReaction.toString());
    return mapping;
  }
  
  public ModelSeedReactionTransform() {
    super(ReactionMajorLabel.ModelSeedReaction.toString(), 
        new BiobaseMetaboliteEtlDictionary<>(ModelSeedMetaboliteEntity.class, getDbMapping()));
  }

  @Override
  protected String resolveComponentLabel(String entry) {
    return majorLabel;
  }

  @Override
  protected void configureAdditionalPropertyLinks(
      GraphReactionEntity centralReactionEntity,
      ModelSeedReactionEntity rxn) {
    for (String ecn : rxn.getEc()) {
      logger.debug("Add EC number: " + ecn);
      centralReactionEntity.addConnectedEntity(
          this.buildPair(
              new SomeNodeFactory()
              .withEntry(ecn)
              .withMajorLabel(GlobalLabel.EnzymeCommission)
              .buildGenericNodeEntity(), 
              new SomeNodeFactory().buildReactionEdge(
                  ReactionRelationshipType.has_ec_number)));
    }
  }
  
  private void processReagent(ModelSeedReactionReagentEntity reagent, Map<GraphMetaboliteProxyEntity, Map<String, Object>> result) {
    try {
      Map<String, Object> stoichiometryProperties = 
          this.propertyContainerBuilder.extractProperties(
              reagent, 
              reagent.getClass());
      GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
      entity.setEntry(reagent.getCpdEntry());
      entity.setMajorLabel(MetaboliteMajorLabel.ModelSeed.toString());
      entity.addLabel(METABOLITE_LABEL);
      result.put(entity, stoichiometryProperties);
    } catch (IllegalAccessException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }
  
  @Override
  protected GenericCrossreference configureCrossreference(GenericCrossreference xref) {
    if (xref.getRef().startsWith("KEGG")) {
      xref.setRef("KEGG");
    }
    if (xref.getRef().equals("MetaCyc") || xref.getRef().equals("PlantCyc")) {
      String value = xref.getValue();
      if (value.endsWith(".cp") ||
          value.endsWith(".ce") ||
          value.endsWith(".cd") ||
          value.endsWith(".cr") ||
          value.endsWith(".cv") ||
          value.endsWith(".cx")) {
        xref.setValue(value.substring(0, value.length() - 3));
      }
      if (value.endsWith(".c") || 
          value.endsWith(".x") ||
          value.endsWith(".m") ||
          value.endsWith(".e") ||
          value.endsWith(".g") ||
          value.endsWith(".n") ||
          value.endsWith(".r") ||
          value.endsWith(".w") ||
          value.endsWith(".p") ||
          value.endsWith(".v")) {
        xref.setValue(value.substring(0, value.length() - 2));
      }
//      System.out.println(xref.getValue());
    }
    
    return super.configureCrossreference(xref);
  }

  @Override
  protected void setupLeftMetabolites(GraphReactionEntity centralReactionEntity, ModelSeedReactionEntity reaction) {
      Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
      for (ModelSeedReactionReagentEntity reagent : reaction.getReagents()) {
          if (reagent.getCoefficient() < 0) {
            processReagent(reagent, result);
          }
      }
      centralReactionEntity.setLeft(result);
  };
  
  @Override
  protected void setupRightMetabolites(GraphReactionEntity centralReactionEntity, ModelSeedReactionEntity reaction) {
      Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
      for (ModelSeedReactionReagentEntity reagent : reaction.getReagents()) {
          if (reagent.getCoefficient() > 0) {
            processReagent(reagent, result);
          }
      }
      centralReactionEntity.setRight(result);
  };
}
