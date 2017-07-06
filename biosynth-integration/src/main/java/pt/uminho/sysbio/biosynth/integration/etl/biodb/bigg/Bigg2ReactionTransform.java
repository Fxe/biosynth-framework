package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionMetaboliteEntity;

public class Bigg2ReactionTransform extends AbstractReactionTransform<Bigg2ReactionEntity> {

  private static final String BIGG2_REACTION_LABEL = ReactionMajorLabel.BiGG2Reaction.toString();
  private static final String BIGG2_REACTION_METABOLITE_LABEL = MetaboliteMajorLabel.BiGG2.toString();

  public Bigg2ReactionTransform() {
    super(BIGG2_REACTION_LABEL, new BiobaseMetaboliteEtlDictionary<>(Bigg2MetaboliteEntity.class));
  }

  @Override
  protected void configureAdditionalPropertyLinks(
      GraphReactionEntity entity,
      Bigg2ReactionEntity reaction) {
    
    for (String model : reaction.getModels()) {
      entity.addConnectedEntity(buildPair(
          new SomeNodeFactory()
          .withEntry(model)
          .withMajorLabel(GlobalLabel.MetabolicModel)
          .buildGenericNodeEntity(), 
          new SomeNodeFactory()
          .buildReactionEdge(ReactionRelationshipType.included_in)));
    }
    
    if (reaction.getEcNumber() != null && 
        !reaction.getEcNumber().trim().isEmpty()) {
      entity.addConnectedEntity(buildPair(
          new SomeNodeFactory()
              .withEntry(reaction.getEcNumber())
              .withMajorLabel(GlobalLabel.EnzymeCommission)
              .buildGenericNodeEntity(), 
          new SomeNodeFactory()
              .buildReactionEdge(ReactionRelationshipType.has_ec_number)));
    }
  }

//  @Override
//  protected void configureNameLink(GraphReactionEntity centralReactionEntity,
//      Bigg2ReactionEntity entity) {
////    for (String name : entity.getSynonyms()) {
////      this.configureNameLink(centralReactionEntity, name);
////    }
//    super.configureNameLink(centralReactionEntity, entity);
//  }

  @Override
  protected String resolveComponentLabel(String entry) {
    return BIGG2_REACTION_METABOLITE_LABEL;
  }
  
  private void processReagent(Bigg2ReactionMetaboliteEntity reagent, Map<GraphMetaboliteProxyEntity, Map<String, Object>> result) {
    try {
        Map<String, Object> stoichiometryProperties = 
                this.propertyContainerBuilder.extractProperties(
                        reagent, 
                        reagent.getClass());
        GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
        entity.setEntry(reagent.getCpdEntry());
        entity.setMajorLabel(resolveComponentLabel(reagent.getCpdEntry()));
        entity.addLabel(METABOLITE_LABEL);
        result.put(entity, stoichiometryProperties);
    } catch (IllegalAccessException e) {
//        logger.error(e.getMessage());
        throw new RuntimeException(e);
    }
}
  
  @Override
  protected void setupLeftMetabolites(GraphReactionEntity centralReactionEntity, Bigg2ReactionEntity rxn) {
      Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
      for (Bigg2ReactionMetaboliteEntity reagent : rxn.getMetabolites()) {
          if (reagent.getStoichiometry() < 0) processReagent(reagent, result);
      }
      centralReactionEntity.setLeft(result);
  };
  
  @Override
  protected void setupRightMetabolites(GraphReactionEntity centralReactionEntity, Bigg2ReactionEntity rxn) {
      Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
      for (Bigg2ReactionMetaboliteEntity reagent : rxn.getMetabolites()) {
          if (reagent.getStoichiometry() > 0) processReagent(reagent, result);
      }
      centralReactionEntity.setRight(result);
  };
}
