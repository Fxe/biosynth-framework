package pt.uminho.sysbio.biosynthframework.integration.etl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.EtlDictionary;
import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;

public abstract class AbstractBiosEntityTransform<SRC, DST> implements EtlTransform<SRC, DST> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBiosEntityTransform.class);

  protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
      new AnnotationPropertyContainerBuilder();
  protected final EtlDictionary<String, String, String> dictionary;

  public AbstractBiosEntityTransform(EtlDictionary<String, String, String> dictionary) {
    this.dictionary = dictionary;
  }

  @Override
  public abstract DST apply(SRC arg0);

  @Override
  public abstract DST etlTransform(SRC srcObject);

  protected GenericCrossreference configureReference(GenericCrossreference xref) {
    logger.trace("{}", xref);
    return xref;
  }

//  protected  void configureCrossreferences(
//      DST centralReactionEntity, 
//      SRC reaction) {
//
//    try {
//      Method method = reaction.getClass().getMethod("getCrossreferences");
//      List<?> xrefs = List.class.cast(method.invoke(reaction));
//      for (Object xrefObject : xrefs) {
//        GenericCrossreference xref = GenericCrossreference.class.cast(xrefObject);
//        xref = configureReference(xref);
//        switch (xref.getType()) {
//        case DATABASE:
//          ReactionMajorLabel majorLabel = ReactionMajorLabel.valueOf(this.dictionary.translate(xref.getRef(), xref.getValue()));
//          Map<String, Object> relationshipProperteis = 
//              this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass());
//          centralReactionEntity.addConnectedEntity(
//              this.buildPair(
//                  new SomeNodeFactory()
//                  .withEntry(xref.getValue())
//                  .buildGraphReactionProxyEntity(majorLabel), 
//                  new SomeNodeFactory()
//                  .withProperties(relationshipProperteis)
//                  .buildReactionEdge(ReactionRelationshipType.has_crossreference_to)));
//          break;
//        case MODEL:
//          centralReactionEntity.addConnectedEntity(
//              this.buildPair(
//                  new SomeNodeFactory()
//                  .withEntry(xref.getRef())
//                  .withMajorLabel(GlobalLabel.MetabolicModel)
//                  .buildGenericNodeEntity(), 
//                  new SomeNodeFactory()
//                  .withProperties(this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass()))
//                  .buildReactionEdge(ReactionRelationshipType.included_in)));
//          break;
//        case GENE:
//          centralReactionEntity.addConnectedEntity(
//              this.buildPair(
//                  new SomeNodeFactory()
//                  .withEntry(xref.getValue())
//                  .withMajorLabel(GlobalLabel.valueOf(BioDbDictionary.translateDatabase(xref.getRef())))
//                  .withLabel(GlobalLabel.Gene)
//                  .buildGenericNodeEntity(), 
//                  new SomeNodeFactory()
//                  .withProperties(this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass()))
//                  .buildReactionEdge(ReactionRelationshipType.has_gene)));
//          break;
//        case ECNUMBER:
//          centralReactionEntity.addConnectedEntity(
//              this.buildPair(new SomeNodeFactory()
//                  .withEntry(xref.getValue())
//                  .withMajorLabel(GlobalLabel.EnzymeCommission)
//                  .buildGenericNodeEntity(), 
//                  new SomeNodeFactory()
//                  .withProperties(this.propertyContainerBuilder.extractProperties(xrefObject, xrefObject.getClass()))
//                  .buildEdge(ReactionRelationshipType.has_ec_number)));
//
//          break;
//        default:
//          logger.warn(String.format("Ignored type <%s>[%s:%s]", xref.getType(), xref.getRef(), xref.getValue()));
//          break;
//        }
//      }
//    } catch (NoSuchMethodException e) {
//      logger.error(e.getMessage());
//    } catch (InvocationTargetException | IllegalAccessException e) {
//      logger.error(e.getMessage());
//    }    
//  }

  protected Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> buildPair(
      AbstractGraphNodeEntity node, AbstractGraphEdgeEntity edge) {
    Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p =
        new ImmutablePair<>(edge, node);
    return p;
  }

  protected GraphRelationshipEntity buildRelationhipEntity(
      String relationShipType) {
    GraphRelationshipEntity relationshipEntity =
        new GraphRelationshipEntity();
    relationshipEntity.setMajorLabel(relationShipType);

    return relationshipEntity;
  }
}
