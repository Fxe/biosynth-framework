package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlCompartment;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;

public class Neo4jXmlSbmlModelDao extends AbstractNeo4jDao {
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jXmlSbmlModelDao.class);
  
  private static final String NEO4JID = "neo4jId";
  
  public Neo4jXmlSbmlModelDao(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }
  
  public static String extractString(String string) {
    String[] split = string.replace("</p>", "").split(":");
    if (split.length > 1) {
      String formula = split[1].trim();
      if (!formula.isEmpty()) {
        return formula;
      }
    }
    
    return null;
  }
  
  public static Map<String, String> parseNotes(List<String> notes) {
    Map<String, String> data = new HashMap<> ();
    Map<String, String> fields = new HashMap<> ();
    fields.put("FORMULA:", "formula");
    fields.put("GENE_ASSOCIATION:", "gene_association");
    fields.put("PROTEIN_ASSOCIATION:", "protein_association");
    fields.put("SUBSYSTEM:", "subsystem");
    fields.put("PROTEIN_CLASS:", "protein_class");
    
    for (String line : notes) {
      boolean detected = false;
      for (String field : fields.keySet()) {
        detected = true;
        if (line.toUpperCase().contains(field)) {
          String str = extractString(line);
          if (str != null) {
            data.put(fields.get(field), str);
          }
        }
      }
      
      if (!detected) {
        logger.warn("Unknown field: {}", line);
      }
    }
    
    return data;
  }
  
  protected static Map<String, Object> getProperties(XmlObject xmlObject) {
    Map<String, Object> properties = new HashMap<> ();
    
    for (String a : xmlObject.getAttributes().keySet()) {
      properties.put(a, xmlObject.getAttributes().get(a));
    }
    
    return properties;
  }
  
  protected static long getNeo4jId(XmlObject xmlObject) {
    return Long.parseLong(xmlObject.getAttributes().get(NEO4JID));
  }
  
  protected static void mergeNotes(Map<String, String> notes, Node node) {
    for (String k : notes.keySet()) {
      if (node.hasProperty(k)) {
        node.setProperty("notes_" + k, notes.get(k));
      } else {
        node.setProperty(k, notes.get(k));
      }
    }
  }
  
  protected static void mergeParameters(XmlSbmlReaction rxn, Node node) {
    Set<String> units = new HashSet<> ();
    Map<String, String> fields = new HashMap<> ();
    fields.put("LOWER_BOUND", "lowerBound");
    fields.put("UPPER_BOUND", "upperBound");
//    fields.put("PROTEIN_ASSOCIATION:", "protein_association");
//    fields.put("SUBSYSTEM:", "subsystem");
//    fields.put("PROTEIN_CLASS:", "protein_class");
    //<parameter id="OBJECTIVE_COEFFICIENT" value="0" units="mmol_per_gDW_per_hr" constant="false"/>
    //OBJECTIVE_COEFFICIENT = 0
    //OBJECTIVE_COEFFICIENT_units = "mmol_per_gDW_per_hr"
    //OBJECTIVE_COEFFICIENT_constant = "false"
    for (XmlObject o : rxn.getListOfParameters()) {
      String id = o.getAttributes().get("id");
      //translate known ids
      if (fields.containsKey(id)) {
        id = fields.get(id);
      }
      String value = o.getAttributes().get("value");
      if (NumberUtils.isNumber(value)) {
        node.setProperty(id, Double.parseDouble(value));
      } else {
        node.setProperty(id, value);
      }
      units.add(o.getAttributes().get("units"));
    }
    units.remove(null);
    if (units.size() > 1) {
      logger.warn("multiple units {}", units);
    }
    if (!units.isEmpty()) {
      node.setProperty("units", units.iterator().next());
    }
  }
  
  public XmlSbmlModel saveMetabolicModel(XmlSbmlModel mmd) {
    
    String modelEntry = mmd.getAttributes().get("id");
    
    try {
      Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.MetabolicModel, "entry", modelEntry, executionEngine);
      Map<String, Object> properties = getProperties(mmd);
      properties.remove("id");
      
      for (String a : mmd.getSbmlAttributes().keySet()) {
        properties.put("sbml_" + a, mmd.getSbmlAttributes().get(a));
      }
      
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
//      mmd.getAttributes().p
      mmd.getAttributes().put(NEO4JID, Long.toString(node.getId()));
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    
    return mmd;
  }
  
  public XmlSbmlCompartment saveCompartment(XmlSbmlModel mmd, XmlSbmlCompartment cmp) {
    String modelEntry = mmd.getAttributes().get("id");
    String cmpEntry = cmp.getAttributes().get("id");
    
    try {
      String entry = String.format("%s@%s", cmpEntry, modelEntry);
      Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.SubcellularCompartment, "entry", entry, executionEngine);
      Map<String, Object> properties = getProperties(cmp);
      properties.remove("id");
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      Node mmdNode = graphDatabaseService.getNodeById(getNeo4jId(mmd));
      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_compartment);
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    return cmp;
  }
  
  public XmlSbmlSpecie saveModelMetaboliteSpecie(XmlSbmlModel mmd, XmlSbmlSpecie spi) {
    String modelEntry = mmd.getAttributes().get("id");
    String spiEntry = spi.getAttributes().get("id");
    
    try {
      String entry = String.format("%s@%s", spiEntry, modelEntry);
      Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.MetaboliteSpecie, "entry", entry, executionEngine);
      Map<String, Object> properties = getProperties(spi);
      properties.remove("id");
      Neo4jUtils.setPropertiesMap(properties, node);
      mergeNotes(parseNotes(spi.getNotes()), node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      Node mmdNode = graphDatabaseService.getNodeById(getNeo4jId(mmd));
      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_specie);
      String spiCmp = spi.getAttributes().get("compartment");
      String cmpEntry = String.format("%s@%s", spiCmp, modelEntry);
      Node cmpNode = Neo4jUtils.getUniqueResult(graphDatabaseService
          .findNodesByLabelAndProperty(GlobalLabel.SubcellularCompartment, "entry", cmpEntry));
      node.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.in_compartment);
      
      //FIXME: temp hammer to fix typo ... and field names
      if (node.hasProperty("compartment")) {
        node.setProperty("comparment", node.getProperty("compartment"));
      }
      if (node.hasProperty("gene_association")) {
        node.setProperty("geneRule", node.getProperty("compartment"));
      }
      
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    return spi;
  }
  
  public XmlSbmlReaction saveModelReaction(XmlSbmlModel mmd, XmlSbmlReaction rxn) {
    String modelEntry = mmd.getAttributes().get("id");
    String rxnEntry = rxn.getAttributes().get("id");
    
    try {
      String entry = String.format("%s@%s", rxnEntry, modelEntry);
      Node rxnNode = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.ModelReaction, "entry", entry, executionEngine);
      Map<String, Object> properties = getProperties(rxn);
      properties.remove("id");
      mergeNotes(parseNotes(rxn.getNotes()), rxnNode);
      mergeParameters(rxn, rxnNode);
      Neo4jUtils.setPropertiesMap(properties, rxnNode);
      rxnNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      Node mmdNode = graphDatabaseService.getNodeById(getNeo4jId(mmd));
      mmdNode.createRelationshipTo(rxnNode, MetabolicModelRelationshipType.has_reaction);

      for (XmlObject  l : rxn.getListOfReactants()) {
        String spiEntry = l.getAttributes().get("species");
        createStoichiometryLink(spiEntry, modelEntry, rxnNode, 
            getProperties(l), MetabolicModelRelationshipType.left_component);
      }
      for (XmlObject r : rxn.getListOfProducts()) {
        String spiEntry = r.getAttributes().get("species");
        createStoichiometryLink(spiEntry, modelEntry, rxnNode, 
            getProperties(r), MetabolicModelRelationshipType.right_component);
      }
      
      if (rxnNode.hasProperty("subsystem")) {
        String ssysEntry = String.format("%s@%s", rxnNode.getProperty("subsystem"), modelEntry);
        Node subsysNode = Neo4jUtils.getOrCreateNode(
            MetabolicModelLabel.ModelSubsystem, "entry", ssysEntry, executionEngine);
        Relationship subsysToModel = subsysNode.getSingleRelationship(
            MetabolicModelRelationshipType.has_subsystem, Direction.BOTH);
        if (subsysToModel == null) {
          mmdNode.createRelationshipTo(subsysNode, MetabolicModelRelationshipType.has_subsystem);
        }
        rxnNode.createRelationshipTo(subsysNode, MetabolicModelRelationshipType.in_subsystem);
      }
      
      //FIXME: temp hammer to fix typo ... and field names
      if (rxnNode.hasProperty("compartment")) {
        rxnNode.setProperty("comparment", rxnNode.getProperty("compartment"));
      }
      if (rxnNode.hasProperty("gene_association")) {
        rxnNode.setProperty("geneRule", rxnNode.getProperty("gene_association"));
      }
    } catch (Exception e) {
      logger.error("{}", e.getMessage());
      e.printStackTrace();
      return null;
    }
    
    return rxn;
  }
  
  public void createStoichiometryLink(String spiEntry, String mmdEntry, Node rxn, Map<String, Object> properties, MetabolicModelRelationshipType r) {
    String spiEntry_ = String.format("%s@%s", spiEntry, mmdEntry);
    Node spiNode = Neo4jUtils.getUniqueResult(graphDatabaseService
        .findNodesByLabelAndProperty(MetabolicModelLabel.MetaboliteSpecie, "entry", spiEntry_));
    
    Relationship relationship = rxn.createRelationshipTo(spiNode, r);
    Neo4jUtils.setPropertiesMap(properties, relationship);
    //XXX: another hammer fix
    if (relationship.hasProperty("species")) {
      relationship.setProperty("cpdEntry", relationship.getProperty("species"));
    }
  }
  
//  public CPD saveModelMetabolite(XmlSbmlModel mmd, CPD cpd);
}
