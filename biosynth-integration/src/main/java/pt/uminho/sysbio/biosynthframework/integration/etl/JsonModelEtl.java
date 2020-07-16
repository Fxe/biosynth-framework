package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.util.HashMap;
import java.util.HashSet;
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
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.model.JsonModel;
import pt.uminho.sysbio.biosynthframework.model.JsonModel.JsonGene;
import pt.uminho.sysbio.biosynthframework.model.JsonModel.JsonMetabolite;
import pt.uminho.sysbio.biosynthframework.model.JsonModel.JsonReaction;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class JsonModelEtl {
  
  private static final Logger logger = LoggerFactory.getLogger(JsonModelEtl.class);
  
  private final BiodbGraphDatabaseService service;
  
  public JsonModelEtl(BiodbGraphDatabaseService service) {
    this.service = service;
  }
  
  public Map<String, Object> getProperties(JsonModel model) {
    Map<String, Object> properties = new HashMap<>();
    if (!DataUtils.empty(model.version)) {
      properties.put("version", model.version);
    }
    if (!DataUtils.empty(model.id)) {
      properties.put("id", model.id);
    }
    return properties;
  }
  
  public Map<String, Object> getProperties(JsonGene gene) {
    Map<String, Object> properties = new HashMap<>();
    if (!DataUtils.empty(gene.id)) {
      properties.put("id", gene.id);
    }
    if (!DataUtils.empty(gene.name)) {
      properties.put("name", gene.name);
    }
    return properties;
  }
  
  public Map<String, Object> getProperties(JsonMetabolite metabolite) {
    Map<String, Object> properties = new HashMap<>();
    if (!DataUtils.empty(metabolite.id)) {
      properties.put("id", metabolite.id);
    }
    if (!DataUtils.empty(metabolite.name)) {
      properties.put("name", metabolite.name);
    }
    if (!DataUtils.empty(metabolite.compartment)) {
      properties.put("compartment", metabolite.compartment);
    }
    if (!DataUtils.empty(metabolite.formula)) {
      properties.put("formula", metabolite.formula);
    }
    return properties;
  }
  
  public Map<String, Object> getProperties(JsonReaction jreaction) {
    Map<String, Object> properties = new HashMap<>();
    if (!DataUtils.empty(jreaction.id)) {
      properties.put("id", jreaction.id);
    }
    if (!DataUtils.empty(jreaction.name)) {
      properties.put("name", jreaction.name);
    }
    if (!DataUtils.empty(jreaction.ecn)) {
      properties.put("ecn", jreaction.ecn);
    }
    if (!DataUtils.empty(jreaction.equation)) {
      properties.put("equation", jreaction.equation);
    }
    if (!DataUtils.empty(jreaction.gene_reaction_rule)) {
      properties.put("gene_reaction_rule", jreaction.gene_reaction_rule);
    }
    if (!DataUtils.empty(jreaction.protein)) {
      properties.put("protein", jreaction.protein);
    }
    if (!DataUtils.empty(jreaction.subsystem)) {
      properties.put("subsystem", jreaction.subsystem);
    }
    if (!DataUtils.empty(jreaction.lower_bound)) {
      properties.put("lower_bound", jreaction.lower_bound);
    }
    if (!DataUtils.empty(jreaction.upper_bound)) {
      properties.put("upper_bound", jreaction.upper_bound);
    }
    if (!DataUtils.empty(jreaction.objective_coefficient)) {
      properties.put("objective_coefficient", jreaction.objective_coefficient);
    }
    return properties;
  }
  
  public static String buildEntry(String id, String modelEntry, String element) {
    String entry = String.format("%s_%s@%s", element, id, modelEntry);
    return entry;
  }
  
  public BiosMetabolicModelNode saveMetabolicModel(JsonModel model, String modelEntry) {
    Map<String, Object> properties = getProperties(model);
//    for (String a : mmd.getSbmlAttributes().keySet()) {
//      properties.put("sbml_" + a, mmd.getSbmlAttributes().get(a));
//    }

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
  
  public Node saveCompartmentNode(String entry, String id, String name) {
    Map<String, Object> properties = new HashMap<>();
    properties.put("name", name);
    properties.put("id", id);

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
  
  public Node saveGeneNode(String entry, JsonGene jgene) {
    Map<String, Object> properties = getProperties(jgene);
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
  
  public Map<String, Node> loadModelCompartments(JsonModel model, BiosMetabolicModelNode modelNode) {
    String modelEntry = modelNode.getEntry();
    Map<String, Node> cmpNodes = new HashMap<>();
    for (String id : model.compartments.keySet()) {
      String entry = buildEntry(id, modelEntry, "JsonCompartment");
      Node cmpNode = saveCompartmentNode(entry, id, null);

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
  
  private Map<String, Node> loadModelGenes(JsonModel model, BiosMetabolicModelNode modelNode) {
    String modelEntry = modelNode.getEntry();
    Map<String, Node> nodes = new HashMap<>();
    for (JsonGene jgene : model.genes) {
      String entry = buildEntry(jgene.id, modelEntry, "JsonGene");
      Node geneNode = saveGeneNode(entry, jgene);
      String id = jgene.id;
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
  
  public Node saveModelMetaboliteSpecie(String entry, JsonMetabolite jmetabolite) {
    Map<String, Object> properties = getProperties(jmetabolite);

    Node node = null;
    try {
      node = service.getOrCreateNode(MetabolicModelLabel.MetaboliteSpecie, 
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
  
  public Node saveModelReaction(String entry, JsonReaction jreaction) {
    Map<String, Object> properties = getProperties(jreaction);
    Node rxnNode = null;
    try {
      rxnNode = service.getOrCreateNode(MetabolicModelLabel.ModelReaction, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          entry);
      Neo4jUtils.setPropertiesMap(properties, rxnNode);
      rxnNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }

    return rxnNode;
  }
  
  public Map<String, Node> loadModelSpecies(
      JsonModel model, BiosMetabolicModelNode modelNode, Map<String, Node> cmpNodes) {
    String modelEntry = modelNode.getEntry();
    Map<String, Node> species = new HashMap<>();
    
    for (JsonMetabolite jmetabolite : model.metabolites) {
      String spiEntry = buildEntry(jmetabolite.id, modelEntry, "JsonMetabolite");
      String id = jmetabolite.id;
      Node spiNode = saveModelMetaboliteSpecie(spiEntry, jmetabolite);
      if (!Neo4jUtils.exitsRelationshipBetween(modelNode, spiNode, Direction.BOTH)) {
        modelNode.createRelationshipTo(spiNode, MetabolicModelRelationshipType.has_metabolite_species);
      }

      String cmpId = jmetabolite.compartment;
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
  
  public Node makeGprNode(BiosMetabolicModelNode modelNode, Node rxnNode, String gpr, Set<Node> gprGeneNodes) {
    String modelEntry = modelNode.getEntry();
    logger.debug("Genes: {}", gprGeneNodes);
    String key = gpr + "@" + modelEntry;
    if (gpr.length() > 30000) {
      key = buildEntry(Long.toString(rxnNode.getId()), modelEntry, "gpr");
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
  
  public Long loadModel(JsonModel model, String modelEntry) {
    BiosMetabolicModelNode modelNode = saveMetabolicModel(model, modelEntry);
    
    if (modelNode == null) {
      logger.warn("unable to create model: {}", modelEntry);
      return null;
    }

    final Map<String, Node> cmpNodes = loadModelCompartments(model, modelNode);
    final Map<String, Node> geneNodes = loadModelGenes(model, modelNode);
    final Map<String, Node> spiNodes = loadModelSpecies(model, modelNode, cmpNodes);

    for (JsonReaction jreaction : model.reactions) {
      String entry = buildEntry(jreaction.id, modelEntry, "JsonReaction");
      Node mrxnNode = saveModelReaction(entry, jreaction);
      System.out.println(mrxnNode.getId());
      System.out.println(DataUtils.toString(mrxnNode.getAllProperties(), "\n", ": "));
      if (!Neo4jUtils.exitsRelationshipBetween(modelNode, mrxnNode, Direction.BOTH)) {
        modelNode.createRelationshipTo(mrxnNode, MetabolicModelRelationshipType.has_model_reaction);
      }
      for (String metaboliteId : jreaction.metabolites.keySet()) {
        String spiId = metaboliteId;
        Double value = jreaction.metabolites.get(metaboliteId);
        Map<String, Object> properties = new HashMap<>();
        properties.put("stoichiometry", value);
        if (!DataUtils.empty(spiId) && spiNodes.containsKey(spiId)) {
          if (value < 0) {
            createStoichiometryLink(mrxnNode, spiNodes.get(spiId), 
                MetabolicModelRelationshipType.left_component, properties);
          } else if (value > 0) {
            createStoichiometryLink(mrxnNode, spiNodes.get(spiId), 
                MetabolicModelRelationshipType.right_component, properties);
          } else {
            logger.warn("invalid stoichiometry value: {}", value);
          }
        } else {
          logger.warn("invalid species reference: {}", spiId);
        }
      }

      final Set<Node> gprGeneNodes = new HashSet<>();
      if (!DataUtils.empty(jreaction.gene_reaction_rule)) {
        System.out.println(jreaction.gene_reaction_rule);
        makeGprNode(modelNode, mrxnNode, jreaction.gene_reaction_rule, gprGeneNodes);
      }
    }

    return modelNode.getId();
  }
  
  public Relationship createStoichiometryLink(Node rxnNode, Node spiNode, RelationshipType t, Map<String, Object> p) {
    Relationship r = null;
    if (!Neo4jUtils.exitsRelationshipBetween(rxnNode, spiNode, Direction.BOTH)) {
      logger.info("[LINK] [{}] -[{}]-> [{}]", rxnNode, t, spiNode);
      r = rxnNode.createRelationshipTo(spiNode, t);
      Neo4jUtils.setPropertiesMap(p, r);
      Neo4jUtils.setTimestamps(r);
    }

    return r;
  }
}

