package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.BFunction;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.MultiNodeTree;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelReactionNode;
import pt.uminho.sysbio.biosynthframework.sbml.SbmlSBaseObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlCompartment;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.SbmlUtils;

public class TheStaticModelLoader {

  private static final Logger logger = LoggerFactory.getLogger(TheStaticModelLoader.class);

  public static BiosMetabolicModelNode saveMetabolicModel(XmlSbmlModel mmd, String modelEntry, BiodbGraphDatabaseService service) {
    Map<String, Object> properties = getProperties(mmd);
    for (String a : mmd.getSbmlAttributes().keySet()) {
      properties.put("sbml_" + a, mmd.getSbmlAttributes().get(a));
    }

    //    return null;
    Node node = null;
    try {
      node = service.getOrCreateNode(GlobalLabel.MetabolicModel, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          modelEntry);

      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      //      mmd.getAttributes().put(NEO4JID, Long.toString(node.getId()));
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }

    return service.getMetabolicModel(node.getId());
  }

  public static Map<String, Node> loadModelCompartments(XmlSbmlModel xmodel, Node modelNode, String modelEntry, BiodbGraphDatabaseService service) {
    Map<String, Node> cmpNodes = new HashMap<>();
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      String entry = buildEntry(xcmp, modelEntry, "compartment");
      Node cmpNode = saveCompartmentNode(entry, xcmp, service);
      String id = xcmp.getAttributes().get("id");
      if (!Neo4jUtils.exitsRelationshipBetween(modelNode, cmpNode, Direction.BOTH)) {
        modelNode.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.has_model_compartment);
      }
      if (!DataUtils.empty(id)) {
        if (cmpNodes.put(id, cmpNode) != null) {
          logger.warn("duplicate: {}", id);
        }
      }
    }

    return cmpNodes;
  }

  private static Map<String, Node> loadModelGenes(XmlSbmlModel xmodel, Node modelNode, String modelEntry,
      BiodbGraphDatabaseService service) {
    Map<String, Node> nodes = new HashMap<>();
    for (XmlObject xo : xmodel.getListOfGeneProducts()) {
      String entry = buildEntry(xo, modelEntry, "geneProduct");
      Node geneNode = saveGeneNode(entry, xo, service);
      String id = xo.getAttributes().get("id");
      if (!Neo4jUtils.exitsRelationshipBetween(modelNode, geneNode, Direction.BOTH)) {
        logger.info("[LINK] {} -[{}]-> {}", modelNode, MetabolicModelRelationshipType.has_gpr_gene, geneNode);
        modelNode.createRelationshipTo(geneNode, MetabolicModelRelationshipType.has_gpr_gene);
      }
      if (!DataUtils.empty(id)) {
        if (nodes.put(id, geneNode) != null) {
          logger.warn("duplicate: {}", id);
        }
      }
    }

    return nodes;
  }

  public static Node saveCompartmentNode(String entry, XmlSbmlCompartment cmp, BiodbGraphDatabaseService service) {
    Map<String, Object> properties = getProperties(cmp);
    includeNotes(cmp, properties);
    includeAnnotation(cmp, properties);

    //    String modelEntry = mmd.getAttributes().get("id");
    //    String cmpEntry = cmp.getAttributes().get("id");
    Node node = null;
    try {
      node = service.getOrCreateNode(MetabolicModelLabel.ModelCompartment, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          entry);
      //      node = Neo4jUtils.getOrCreateNode(GlobalLabel.SubcellularCompartment, "entry", entry, service);

      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    return node;
  }

  public static Node saveGeneNode(String entry, XmlObject xo, BiodbGraphDatabaseService service) {
    Map<String, Object> properties = getProperties(xo);
    //    includeNotes(xo, properties);
    //    includeAnnotation(xo, properties);

    Node node = null;
    try {
      node = service.getOrCreateNode(MetabolicModelLabel.ModelGene, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          entry);
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    return node;
  }

  public static Node saveModelMetaboliteSpecie(String entry, XmlSbmlModel mmd, XmlSbmlSpecie spi, BiodbGraphDatabaseService service) {
    Map<String, Object> properties = getProperties(spi);
    includeNotes(spi, properties);
    includeAnnotation(spi, properties);

    Node node = null;
    try {
      node = service.getOrCreateNode(MetabolicModelLabel.MetaboliteSpecie, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          entry);
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      //      Node mmdNode = graphDatabaseService.getNodeById(getNeo4jId(mmd));
      //      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_specie);
      //      String spiCmp = spi.getAttributes().get("compartment");
      //      String cmpEntry = String.format("%s@%s", spiCmp, modelEntry);
      //      Node cmpNode = Neo4jUtils.getUniqueResult(graphDatabaseService
      //          .findNodes(GlobalLabel.SubcellularCompartment, "entry", cmpEntry));
      //      if (cmpNode != null) {
      //        node.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.in_compartment);
      //      }

    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    return node;
  }

  public static BiosModelReactionNode saveModelReaction(String entry, XmlSbmlModel mmd, XmlSbmlReaction rxn, BiodbGraphDatabaseService service) {
    Map<String, Object> properties = getProperties(rxn);
    includeNotes(rxn, properties);
    includeAnnotation(rxn, properties);
    Node rxnNode = null;
    try {
      rxnNode = service.getOrCreateNode(MetabolicModelLabel.ModelReaction, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          entry);
      Neo4jUtils.setPropertiesMap(properties, rxnNode);
      rxnNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      //      if (rxnNode.hasProperty("subsystem")) {
      //        String ssysEntry = String.format("%s@%s", rxnNode.getProperty("subsystem"), modelEntry);
      //        Node subsysNode = Neo4jUtils.getOrCreateNode(
      //            MetabolicModelLabel.ModelSubsystem, "entry", ssysEntry, graphDatabaseService);
      //        Relationship subsysToModel = subsysNode.getSingleRelationship(
      //            MetabolicModelRelationshipType.has_subsystem, Direction.BOTH);
      //        if (subsysToModel == null) {
      //          mmdNode.createRelationshipTo(subsysNode, MetabolicModelRelationshipType.has_subsystem);
      //        }
      //        rxnNode.createRelationshipTo(subsysNode, MetabolicModelRelationshipType.in_subsystem);
      //      }
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }

    return new BiosModelReactionNode(rxnNode, null);
  }
  
  public static Map<String, Node> loadModelSpecies(
      XmlSbmlModel model, BiosMetabolicModelNode modelNode, String modelEntry, Map<String, Node> cmpNodes, BiodbGraphDatabaseService service) {
    Map<String, Node> species = new HashMap<>();
    
    for (XmlSbmlSpecie xspi : model.getSpecies()) {
      String spiEntry = buildEntry(xspi, modelEntry, "species");
      String id = xspi.getAttributes().get("id");
      Node spiNode = saveModelMetaboliteSpecie(spiEntry, model, xspi, service);
      if (!Neo4jUtils.exitsRelationshipBetween(modelNode, spiNode, Direction.BOTH)) {
        modelNode.createRelationshipTo(spiNode, MetabolicModelRelationshipType.has_metabolite_species);
      }

      //      System.out.println(spiNode.getId());
      //      System.out.println(DataUtils.toString(spiNode.getAllProperties(), "\n", ": "));
      String cmpId = xspi.getAttributes().get("compartment");
      if (!DataUtils.empty(cmpId) && cmpNodes.containsKey(cmpId)) {
        Node cmpNode = cmpNodes.get(cmpId);
        if (!Neo4jUtils.exitsRelationshipBetween(spiNode, cmpNode, Direction.BOTH)) {
          logger.info("[LINK] [{}] -[{}]-> [{}]", spiNode, MetabolicModelRelationshipType.in_compartment, cmpId);
          spiNode.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.in_compartment);
        }
      } else {
        logger.warn("invalid compartment reference: {}", cmpId);
      }

      if (!DataUtils.empty(id) && spiNode != null) {
        if (species.put(id, spiNode) != null) {
          logger.warn("duplicate: {}", id);
        }
      }
    }
    
    return species;
  }

  public static Long loadModel(XmlSbmlModel xmodel, String modelEntry, BiodbGraphDatabaseService service) {
    BiosMetabolicModelNode modelNode = saveMetabolicModel(xmodel, modelEntry, service);

    final Map<String, Node> cmpNodes = loadModelCompartments(xmodel, modelNode, modelEntry, service);
    final Map<String, Node> geneNodes = loadModelGenes(xmodel, modelNode, modelEntry, service);
    final Map<String, Node> spiNodes = loadModelSpecies(xmodel, modelNode, modelEntry, cmpNodes, service);

    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String entry = buildEntry(xrxn, modelEntry, "reaction");
      BiosModelReactionNode mrxnNode = saveModelReaction(entry, xmodel, xrxn, service);
      logger.info("[{}] [{}]{} {}", modelEntry, mrxnNode.getId(), mrxnNode.getSid(), mrxnNode.getAllProperties().keySet());
//      System.out.println(mrxnNode.getId());
//      System.out.println(DataUtils.toString(mrxnNode.getAllProperties(), "\n", ": "));
      if (!Neo4jUtils.exitsRelationshipBetween(modelNode, mrxnNode, Direction.BOTH)) {
        modelNode.createRelationshipTo(mrxnNode, MetabolicModelRelationshipType.has_model_reaction);
      }
      
      for (XmlObject  l : xrxn.getListOfReactants()) {
        String spiId = l.getAttributes().get("species");
        if (!DataUtils.empty(spiId) && spiNodes.containsKey(spiId)) {
          createStoichiometryLink(mrxnNode, spiNodes.get(spiId), 
              MetabolicModelRelationshipType.left_component, getProperties(l));
        } else {
          logger.warn("invalid species reference: {}", spiId);
        }
      }
      for (XmlObject r : xrxn.getListOfProducts()) {
        String spiId = r.getAttributes().get("species");
        if (!DataUtils.empty(spiId) && spiNodes.containsKey(spiId)) {
          createStoichiometryLink(mrxnNode, spiNodes.get(spiId), 
              MetabolicModelRelationshipType.right_component, getProperties(r));
        } else {
          logger.warn("invalid species reference: {}", spiId);
        }
      }

      for (XmlObject m : xrxn.getListOfModifiers()) {
        if (m.getAttributes().containsKey("species")) {
          String spiId = m.getAttributes().get("species");
          if (!DataUtils.empty(spiId) && spiNodes.containsKey(spiId)) {
            logger.info("[LINK] [{}] -[{}]-> [{}]", mrxnNode, MetabolicModelRelationshipType.has_modifier_to, spiNodes.get(spiId));
            Relationship r = mrxnNode.createRelationshipTo(
                spiNodes.get(spiId), MetabolicModelRelationshipType.has_modifier_to);
            Neo4jUtils.setCreatedTimestamp(r);
            Neo4jUtils.setUpdatedTimestamp(r);
            Neo4jUtils.setPropertiesMapWild(m.getAttributes(), r);
          } else {
            logger.warn("invalid species reference: {}", spiId);
          }
        }
      }

      MultiNodeTree<Object> gprTree = xrxn.getGpr();
      final Set<Node> gprGeneNodes = new HashSet<>();
      if (gprTree != null) {
        BFunction<Object, String> f = 
            new BFunction<Object, String>() {

          @Override
          public String apply(Object t) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) t;
            if (data.containsKey("geneProduct")) {
              String geneProduct = data.get("geneProduct");
              if (geneNodes.containsKey(geneProduct)) {
                Node geneNode = geneNodes.get(geneProduct);
                gprGeneNodes.add(geneNode);
              } else {
                logger.warn("invalid gene reference: {}", geneProduct);
              }
              return data.get("geneProduct");
            }
            return t.toString();
          }
        };
        List<String> gprs = SbmlUtils.gprTreeToString(gprTree, f);
        if (gprs != null && gprs.size() == 1) {
          String gprStr = gprs.iterator().next();
          System.out.println(gprStr);
          makeGprNode(modelNode, modelEntry, mrxnNode, gprStr, gprGeneNodes, service);
        }
      }
    }



    return modelNode.getId();
  }


  public static Node makeGprNode(Node modelNode, String modelEntry, Node rxnNode, String gpr, 
      Set<Node> gprGeneNodes, BiodbGraphDatabaseService service) {

    logger.debug("Genes: {}", gprGeneNodes);
    String key = gpr + "@" + modelEntry;
    if (gpr.length() > 30000) {
      key = rxnNode.getId() + "@" + modelEntry;
    }
    
    logger.info("[MERGE] ModelGPR -> {}", key);
    Node gprNode = service.getOrCreateNode(MetabolicModelLabel.ModelGPR, 
        Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, key);

    if (gpr.length() > 30000) {
      gprNode.addLabel(GlobalLabel.EXTERNAL_DATA);
      Map<String, Object> edata = new HashMap<>();
      edata.put("egpr", gpr);
      service.exportExternalProperties(gprNode, edata);
    }

    //      gprNode.setProperty("lexicographic_string", lgpr);
    Neo4jUtils.setUpdatedTimestamp(gprNode);
    if (!Neo4jUtils.exitsRelationshipBetween(rxnNode, gprNode, Direction.BOTH)) {
      logger.info("[LINK] {} -[{}]-> {}", rxnNode, MetabolicModelRelationshipType.has_gpr, gprNode);
      rxnNode.createRelationshipTo(gprNode, MetabolicModelRelationshipType.has_gpr);
    }

    for (Node geneNode : gprGeneNodes) {
      if (!Neo4jUtils.exitsRelationshipBetween(geneNode, gprNode, Direction.BOTH)) {
        logger.info("[LINK] {} -> {}", gprNode, geneNode);
        gprNode.createRelationshipTo(geneNode, MetabolicModelRelationshipType.has_gpr_gene);
      }
    }
    
    return gprNode;
  }

  public static Relationship createStoichiometryLink(Node rxnNode, Node spiNode, RelationshipType t, Map<String, Object> p) {
    Relationship r = null;
    if (!Neo4jUtils.exitsRelationshipBetween(rxnNode, spiNode, Direction.BOTH)) {
      logger.info("[LINK] [{}] -[{}]-> [{}]", rxnNode, t, spiNode);
      r = rxnNode.createRelationshipTo(spiNode, t);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
      Neo4jUtils.setPropertiesMap(p, r);
    }

    return r;
  }

  public static void includeNotes(SbmlSBaseObject sbase, Map<String, Object> properties) {
    String notes = sbase.getNotes();
    if (!DataUtils.empty(notes)) {
      properties.put("notes", notes);
    }
  }
  public static void includeAnnotation(SbmlSBaseObject sbase, Map<String, Object> properties) {
    String annotation = sbase.getAnnotation();
    if (!DataUtils.empty(annotation)) {
      properties.put("annotation", annotation);
    }
  }

  public static String buildEntry(XmlObject xo, String modelEntry, String element) {
    int l = xo.lineNumber;
    int c = xo.columnNumber;
    String entry = String.format("%s_%d_%d@%s", element, l, c, modelEntry);
    return entry;
  }

  public static Map<String, Object> getProperties(XmlObject xmlObject) {
    Map<String, Object> properties = new HashMap<> ();
    for (String k : xmlObject.getAttributes().keySet()) {
      properties.put(k, xmlObject.getAttributes().get(k));
    }
    return properties;
  }
}
