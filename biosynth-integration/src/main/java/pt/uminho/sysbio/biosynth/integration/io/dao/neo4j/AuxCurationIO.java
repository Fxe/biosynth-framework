package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.curation.CurationRelationship;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class AuxCurationIO {
  
  private static final Logger logger = LoggerFactory.getLogger(AuxCurationIO.class);
  
  
  
  public static String[] cmpToExt = new String[] {
      SubcellularCompartment.CYTOSOL.toString()     , "c",
      SubcellularCompartment.GOLGI.toString()       , "g",
      SubcellularCompartment.MITOCHONDRIA.toString(), "m",
      SubcellularCompartment.NUCLEUS.toString()     , "n",
      SubcellularCompartment.VACUOLE.toString()     , "v",
      SubcellularCompartment.PEROXISOME.toString()  , "x",
      SubcellularCompartment.LYSOSOME.toString()    , "l",
      SubcellularCompartment.EXTRACELLULAR.toString()     , "e",
      SubcellularCompartment.RETICULUM.toString()     , "r",
      SubcellularCompartment.BOUNDARY.toString(), "b",
  };
  
  public static Map<SubcellularCompartment, String> t = null;
  
  
  private static void buildTMap() {
    if (t == null) {
      t = new HashMap<> ();
      for (int i = 0; i < cmpToExt.length; i+=2) {
        t.put(SubcellularCompartment.valueOf(cmpToExt[i]), cmpToExt[i + 1]);
      }
    }
  }
  
  public static String tEntry(String e) {
    return e;
  }
  
  
  public static String buildUniversalSpecieEntry(String alias, SubcellularCompartment gcmp) {
    buildTMap();
    String cmpAlias = t.get(gcmp);
    String baseEntry = tEntry(alias);
    String entry = String.format("%s@%s", baseEntry, cmpAlias);
    
    return entry;
  }
  
  public static Map<Long, String> listUniversalSpecies(GraphDatabaseService service) {
    return listUniversalEntity(service, CurationLabel.UniversalSpecie);
  }
  
  public static Map<Long, String> listUniversalMetabolites(GraphDatabaseService service) {
    return listUniversalEntity(service, CurationLabel.UniversalMetabolite);
  }
  
  public static Map<Long, String> listUniversalReactions(GraphDatabaseService service) {
    return listUniversalEntity(service, CurationLabel.UniversalReaction);
  }
  
  public static Map<Long, String> listUniversalEntity(GraphDatabaseService service, Label label) {
    Map<Long, String> result = new HashMap<> ();
    
    for (Node node : GlobalGraphOperations.at(service).getAllNodesWithLabel(label)) {
      long id = node.getId();
      String entry = (String) node.getProperty("entry");
      result.put(id, entry);
    }
    
    return result;
  }
  
  public static void addSpeciesToUniversalSpecie(
      Node uspi,
      Set<Long> spiIdSet, 
      GraphDatabaseService cura, 
      GraphDatabaseService data) {
    addEntitiesToUniversalEntity(
        uspi, spiIdSet, MetabolicModelLabel.MetaboliteSpecie, cura, data);
  }
  
  public static void addMetabolitesToUniversalMetabolite(
      Node ucpd,
      Set<Long> cpdIdSet, 
      GraphDatabaseService cura, 
      GraphDatabaseService data) {
    addEntitiesToUniversalEntity(
        ucpd, cpdIdSet, GlobalLabel.Metabolite, cura, data);
  }
  
  public static void addReactionsToUniversalReaction(
      Node urxn,
      Set<Long> rxnIdSet, 
      GraphDatabaseService cura, 
      GraphDatabaseService data) {
    addEntitiesToUniversalEntity(
        urxn, rxnIdSet, GlobalLabel.Reaction, cura, data);
  }
  
  public static void addEntitiesToUniversalEntity(
      Node unode,
      Set<Long> idSet, 
      Label type,
      GraphDatabaseService cura, 
      GraphDatabaseService data) {
    Set<Long> valid = new HashSet<> ();
    //filter valid ids
    for (long id : idSet) {
      Node node = data.getNodeById(id);
      if (node.hasLabel(type)) {
        valid.add(id);
      }
    }
    
    //collect eidNodes
    Set<Node> eidNodeSet = new HashSet<> ();
    for (long id : valid) {
      Node eidNode = 
          Neo4jUtilsIntegration.getOrCreateIntegratedMemberByReferenceId(
              id, data, cura);
      eidNodeSet.add(eidNode);
    }
    
    for (Node eidNode : eidNodeSet) {
      //check if exists
      Relationship r = eidNode.getSingleRelationship(
          CurationRelationship.has_universal_entity, Direction.BOTH);
      if (r == null) {
        r = eidNode.createRelationshipTo(
            unode, CurationRelationship.has_universal_entity);
        Neo4jUtils.setCreatedTimestamp(r);
        Neo4jUtils.setUpdatedTimestamp(r);
      } else if (r.getOtherNode(eidNode).getId() != unode.getId()) {
        logger.warn("{} already binded to {}", eidNode.getId(), 
                                               r.getOtherNode(eidNode).getId());
      }
    }
  }
  

  
  public static Long createUniversalMetabolite(
      GraphDatabaseService service, String alias) {
    Node ucpdNode = getUniversalMetabolite(service, alias);
    if (ucpdNode != null) {
      logger.warn("{} already exists", alias);
      return null;
    }
    
    String entry = tEntry(alias);
    ucpdNode = service.createNode(CurationLabel.UniversalMetabolite);
    ucpdNode.setProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
    Neo4jUtils.setCreatedTimestamp(ucpdNode);
    Neo4jUtils.setUpdatedTimestamp(ucpdNode);
    
    return ucpdNode.getId();
  }
  
  public static Long createUniversalSpecie(
      GraphDatabaseService service, String alias, SubcellularCompartment gcmp) {
    Node uspiNode = getUniversalSpecie(service, alias, gcmp);
    if (uspiNode != null) {
      logger.warn("{}@{} already exists", alias, gcmp);
      return null;
    }
    
    String entry = buildUniversalSpecieEntry(alias, gcmp);
    uspiNode = service.createNode(CurationLabel.UniversalSpecie);
    uspiNode.setProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
    uspiNode.setProperty("cmp", gcmp.toString());
    Neo4jUtils.setCreatedTimestamp(uspiNode);
    Neo4jUtils.setUpdatedTimestamp(uspiNode);
    
    return uspiNode.getId();
  }
  
  public static Node getUniversalSpecie(
      GraphDatabaseService service,
      String alias, SubcellularCompartment gcmp) {
    String entry = buildUniversalSpecieEntry(alias, gcmp);
    
    logger.debug("get UniversalSpecie {}", entry);
    
    return getNode(CurationLabel.UniversalSpecie, entry, service);
  }
  
  public static Node getUniversalMetabolite(
      GraphDatabaseService service, String alias) {
    String entry = tEntry(alias);
    
    logger.debug("get UniversalMetabolite {}", entry);
    
    return getNode(CurationLabel.UniversalMetabolite, entry, service);
  }
  
  public static Node getNode(Label label, 
                             String entry, GraphDatabaseService service) {
    Node node = Neo4jUtils.getUniqueResult(
        service.findNodesByLabelAndProperty(label, 
            Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry));
    
    return node;
  }
}
