package pt.uminho.sysbio.biosynth.integration.etl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.EtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionPropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.StoichiometryPair;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;

public abstract class AbstractReactionTransform<R extends GenericReaction>
implements EtlTransform<R, GraphReactionEntity> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractReactionTransform.class);

  protected final EtlDictionary<String, String, String> dictionary;
  protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
      new AnnotationPropertyContainerBuilder();

  protected static final String REACTION_PROPERTY_LABEL = GlobalLabel.ReactionProperty.toString();
  protected static final String REACTION_LABEL = GlobalLabel.Reaction.toString();
  protected static final String REACTION_NAME_LABEL = ReactionPropertyLabel.Name.toString();
  protected static final String METABOLITE_LABEL = GlobalLabel.Metabolite.toString();
  protected static final String PROPERTY_UNIQUE_KEY = "key";
  protected static final String REACTION_NAME_RELATIONSHIP_TYPE = ReactionRelationshipType.has_name.toString();

  protected final String majorLabel;

  public AbstractReactionTransform(String majorLabel, EtlDictionary<String, String, String> dictionary) {
    this.majorLabel = majorLabel;
    this.dictionary = dictionary;
  }

  @Override
  public GraphReactionEntity etlTransform(R entity) {
    GraphReactionEntity centralReactionEntity = new GraphReactionEntity();
    centralReactionEntity.setUniqueKey("entry");
    this.configureProperties(centralReactionEntity, entity);
    this.setupLeftMetabolites(centralReactionEntity, entity);
    this.setupRightMetabolites(centralReactionEntity, entity);
    this.configureCrossreferences(centralReactionEntity, entity);
    this.configureNameLink(centralReactionEntity, entity);
    this.configureAdditionalPropertyLinks(centralReactionEntity, entity);

    return centralReactionEntity;
  }

  @Override
  public GraphReactionEntity apply(R t) {
    if (t == null) {
      logger.warn("null entity");
      return null;
    }
    String e = t.getEntry();
    if (e == null) {
      logger.warn("invalid entity missing entry");
      return null;
    }
    return etlTransform(t);
  }

  protected Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> buildPair(
      AbstractGraphNodeEntity node, AbstractGraphEdgeEntity edge) {
    Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p =
        new ImmutablePair<>(edge, node);
    return p;
  }



  public AbstractGraphNodeEntity buildSomeNode(Map<String, Object> properties, String majorLabel, String...labels) {
    AbstractGraphNodeEntity node = new AbstractGraphNodeEntity();
    node.setMajorLabel(majorLabel);
    for (String label : labels) {
      node.addLabel(label);
    }
    node.setProperties(properties);
    return node;
  }

  public AbstractGraphEdgeEntity buildSomeEdge(Map<String, Object> properties, String...labels) {
    AbstractGraphEdgeEntity edge = new AbstractGraphEdgeEntity();
    for (String label : labels) {
      edge.labels.add(label);
    }
    if (properties != null) edge.properties = properties;
    return edge;
  }

  protected void configureProperties(GraphReactionEntity centralReactionEntity, R reaction) {
    //		System.out.println(reaction);
    centralReactionEntity.setMajorLabel(majorLabel);
    centralReactionEntity.addLabel(REACTION_LABEL);
    centralReactionEntity.addProperty("entry", reaction.getEntry());
    centralReactionEntity.addProperty("description", reaction.getDescription());
    centralReactionEntity.addProperty("name", reaction.getName());
    centralReactionEntity.addProperty("source", reaction.getSource());
    centralReactionEntity.addProperty("translocation", reaction.isTranslocation());

    //Use java reflextion to extract properties of the node
    try {
      Map<String, Object> propertyContainer = 
          this.propertyContainerBuilder.extractProperties(
              reaction, reaction.getClass());

      for (String key : propertyContainer.keySet()) {
        centralReactionEntity.addProperty(key, propertyContainer.get(key));
      }
    } catch (IllegalAccessException e) {
      logger.error(e.getMessage());
    }
  }

  private Map<GraphMetaboliteProxyEntity, Map<String, Object>> getMetabolitesByReflexion(
      R reaction, 
      String field) {

    Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();

    try {
      Method method = reaction.getClass().getMethod(field);
      List<?> right = List.class.cast(method.invoke(reaction));
      for (Object stoichiometryObject : right) {
        //				System.out.println(stoichiometryObject.getClass().getSimpleName());
        Map<String, Object> stoichiometryProperties = 
            this.propertyContainerBuilder.extractProperties(
                stoichiometryObject, 
                stoichiometryObject.getClass());

        StoichiometryPair stoichiometryPair = StoichiometryPair.class.cast(stoichiometryObject);
        GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
        entity.setEntry(stoichiometryPair.getCpdEntry());
        //NO CLUE WHY I DID THIS ! Biocyc ?
        //				entity.setEntry(String.format("%s:%s", reaction.getSource(), stoichiometryPair.getCpdEntry()));

        entity.setMajorLabel(resolveComponentLabel(stoichiometryPair.getCpdEntry()));
        entity.addLabel(METABOLITE_LABEL);
        //				Map<String, Object> propertyContainer = new HashMap<> ();
        //				propertyContainer.put("stoichiometry", stoichiometryPair.getStoichiometry());
        result.put(entity, stoichiometryProperties);
      }
    } catch (NoSuchMethodException e) {
      logger.error(e.getMessage());
    } catch (InvocationTargetException | IllegalAccessException e) {
      logger.error(e.getMessage());
    }

    return result;
  }

  /**
   * Reaction may contain components with distinct labels
   * ex.: Kegg Reaction may contain glycans and compounds label
   * resolver must be defined in the upper level
   * @param entry of the component
   * @return
   */
  protected abstract String resolveComponentLabel(String entry);

  protected void setupLeftMetabolites(GraphReactionEntity centralReactionEntity, R reaction) {
    logger.debug("Reading Left Components");
    Map<GraphMetaboliteProxyEntity, Map<String, Object>> left =
        this.getMetabolitesByReflexion(reaction, "getLeft");

    centralReactionEntity.setLeft(left);
  }

  protected void setupRightMetabolites(GraphReactionEntity centralReactionEntity, R reaction) {
    logger.debug("Reading Right Components");
    Map<GraphMetaboliteProxyEntity, Map<String, Object>> right =
        this.getMetabolitesByReflexion(reaction, "getRight");

    centralReactionEntity.setRight(right);
  }
  
  protected GenericCrossreference configureCrossreference(GenericCrossreference xref) {
    return xref;
  }

  protected abstract void configureAdditionalPropertyLinks(GraphReactionEntity centralReactionEntity, R reaction);

  protected  void configureCrossreferences(
      GraphReactionEntity centralReactionEntity, 
      R reaction) {

    try {
      Method method = reaction.getClass().getMethod("getCrossreferences");
      List<?> xrefs = List.class.cast(method.invoke(reaction));
      for (Object xrefObject : xrefs) {
        GenericCrossreference xref = GenericCrossreference.class.cast(xrefObject);
        xref = configureCrossreference(xref);
        switch (xref.getType()) {
        case DATABASE:
          ReactionMajorLabel majorLabel = ReactionMajorLabel.valueOf(this.dictionary.translate(xref.getRef(), xref.getValue()));
          Map<String, Object> relationshipProperteis = 
              this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass());
          centralReactionEntity.addConnectedEntity(
              this.buildPair(
                  new SomeNodeFactory()
                  .withEntry(xref.getValue())
                  .buildGraphReactionProxyEntity(majorLabel), 
                  new SomeNodeFactory()
                  .withProperties(relationshipProperteis)
                  .buildReactionEdge(ReactionRelationshipType.has_crossreference_to)));
          //						GraphReactionProxyEntity proxyEntity = new GraphReactionProxyEntity();
          //						proxyEntity.setEntry(xref.getValue());
          //						proxyEntity.addLabel(REACTION_LABEL);
          //						proxyEntity.setMajorLabel(BioDbDictionary.translateDatabase(xref.getRef()));
          //						centralReactionEntity.getCrossreferences().add(proxyEntity);
          break;
        case MODEL:
          centralReactionEntity.addConnectedEntity(
              this.buildPair(
                  new SomeNodeFactory()
                  .withEntry(xref.getRef())
                  .withMajorLabel(GlobalLabel.MetabolicModel)
                  .buildGenericNodeEntity(), 
                  new SomeNodeFactory()
                  .withProperties(this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass()))
                  .buildReactionEdge(ReactionRelationshipType.included_in)));
          break;
        case GENE:
          centralReactionEntity.addConnectedEntity(
              this.buildPair(
                  new SomeNodeFactory()
                  .withEntry(xref.getValue())
                  .withMajorLabel(GlobalLabel.valueOf(BioDbDictionary.translateDatabase(xref.getRef())))
                  .withLabel(GlobalLabel.Gene)
                  .buildGenericNodeEntity(), 
                  new SomeNodeFactory()
                  .withProperties(this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass()))
                  .buildReactionEdge(ReactionRelationshipType.has_gene)));
          break;
        case ECNUMBER:
          centralReactionEntity.addConnectedEntity(
              this.buildPair(new SomeNodeFactory()
                  .withEntry(xref.getValue())
                  .withMajorLabel(GlobalLabel.EnzymeCommission)
                  .buildGenericNodeEntity(), 
                  new SomeNodeFactory()
                  .withProperties(this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass()))
                  .buildEdge(ReactionRelationshipType.has_ec_number)));
              
          break;
        default:
          logger.warn(String.format("Ignored type <%s>[%s:%s]", xref.getType(), xref.getRef(), xref.getValue()));
          break;
        }


      }
    } catch (NoSuchMethodException e) {
      logger.error(e.getMessage());
    } catch (InvocationTargetException | IllegalAccessException e) {
      logger.error(e.getMessage());
    }
  }

  protected void configureNameLink(GraphReactionEntity centralReactionEntity,
      R entity) {
    this.configureNameLink(centralReactionEntity, entity.getName());

    //		Map<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> link = new HashMap<> ();
    //		AbstractGraphEdgeEntity edge = buildSomeEdge(null, REACTION_NAME_RELATIONSHIP_TYPE);
    //		Map<String, Object> properties = new HashMap<> ();
    //		properties.put(PROPERTY_UNIQUE_KEY, entity.getName());
    //		AbstractGraphNodeEntity node = buildSomeNode(properties, null, REACTION_NAME_LABEL, REACTION_PROPERTY_LABEL);
    //		
    //		link.put(edge, node);
    //		centralReactionEntity.links.add(link);
  }
  protected void configureNameLink(GraphReactionEntity centralReactionEntity,
      String name) {
    this.configureGenericPropertyLink(centralReactionEntity, name, ReactionPropertyLabel.Name, ReactionRelationshipType.has_name);
  }
  protected void configureGenericPropertyLink(
      GraphReactionEntity graphReactionEntity, 
      Object value, ReactionPropertyLabel label, 
      ReactionRelationshipType relationship) {
    if (value != null) {
      graphReactionEntity.addConnectedEntity(
          this.buildPair(
              new SomeNodeFactory().buildGraphReactionPropertyEntity(label, value), 
              new SomeNodeFactory().buildReactionEdge(relationship)));
    }
  }

  //	public CentralMetaboliteEntity 
}
