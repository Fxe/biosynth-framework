package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.util.HashMap;
import java.util.Map;

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
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.sbml.SbmlSBaseObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlCompartment;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class TheStaticModelLoader {
  
  private static final Logger logger = LoggerFactory.getLogger(TheStaticModelLoader.class);
  
  public static Node saveMetabolicModel(XmlSbmlModel mmd, String modelEntry, BiodbGraphDatabaseService service) {
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
    
    return node;
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
  
  public static Node saveModelReaction(String entry, XmlSbmlModel mmd, XmlSbmlReaction rxn, BiodbGraphDatabaseService service) {
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
//      Node mmdNode = graphDatabaseService.getNodeById(getNeo4jId(mmd));
//      mmdNode.createRelationshipTo(rxnNode, MetabolicModelRelationshipType.has_reaction);
      
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
    
    return rxnNode;
  }
  
  public static void loadModel(XmlSbmlModel xmodel, String modelEntry, BiodbGraphDatabaseService service) {
    Node modelNode = service.getMetabolicModel(modelEntry);
    if (modelNode == null) {
      modelNode = saveMetabolicModel(xmodel, modelEntry, service);
    }
    
    System.out.println(modelNode.getId());
    System.out.println(DataUtils.toString(modelNode.getAllProperties(), "\n", ": "));
    
    Map<String, Node> cmpNodes = loadModelCompartments(xmodel, modelNode, modelEntry, service);
    Map<String, Node> spiNodes = new HashMap<>();
    
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String spiEntry = buildEntry(xspi, modelEntry, "species");
      String id = xspi.getAttributes().get("id");
      Node spiNode = saveModelMetaboliteSpecie(spiEntry, xmodel, xspi, service);
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
        if (spiNodes.put(id, spiNode) != null) {
          logger.warn("duplicate: {}", id);
        }
      }
    }
    
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String entry = buildEntry(xrxn, modelEntry, "reaction");
      Node mrxnNode = saveModelReaction(entry, xmodel, xrxn, service);
      System.out.println(mrxnNode.getId());
      System.out.println(DataUtils.toString(mrxnNode.getAllProperties(), "\n", ": "));
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
    }
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
