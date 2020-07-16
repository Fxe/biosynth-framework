package pt.uminho.sysbio.biosynthframework.neo4j.integration;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalMetaboliteNode;

public class Neo4jMapper {
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jMapper.class);
  
  private final BiodbGraphDatabaseService service;
  
  public Neo4jMapper(BiodbGraphDatabaseService service) {
    this.service = service;
  }
  
  public CompartmentalizedStoichiometry<Long, Integer> getModelSeedCS(BiodbReactionNode rxnNode) {
    CompartmentalizedStoichiometry<Long, Integer> cstoich = new CompartmentalizedStoichiometry<>();
    for (Relationship r : rxnNode.getRelationships(ReactionRelationshipType.left_component)) {
      BiodbMetaboliteNode cpdNode = new BiodbMetaboliteNode(r.getOtherNode(rxnNode), null);
      Integer compartment = (Integer) r.getProperty("compartment", null);
      Double value = (Double) r.getProperty("stoichiometry", null);
      if (compartment == null || value == null) {
        logger.warn("[L] bad data: compartment: {}, value: {}", compartment, value);
      }
      long id = cpdNode.getId();
      if (cpdNode.getUniversalMetabolite() != null) {
        id = cpdNode.getUniversalMetabolite().getId();
      }
//      System.out.println(r.getAllProperties() + " " + onode.getAllProperties());
      cstoich.addLeft(id, compartment, value);
    }
    for (Relationship r : rxnNode.getRelationships(ReactionRelationshipType.right_component)) {
      BiodbMetaboliteNode cpdNode = new BiodbMetaboliteNode(r.getOtherNode(rxnNode), null);
      Integer compartment = (Integer) r.getProperty("compartment", null);
      Double value = (Double) r.getProperty("stoichiometry", null);
      if (compartment == null || value == null) {
        logger.warn("[R] bad data: compartment: {}, value: {}", compartment, value);
      }
      long id = cpdNode.getId();
      if (cpdNode.getUniversalMetabolite() != null) {
        id = cpdNode.getUniversalMetabolite().getId();
      }
//      System.out.println(r.getAllProperties() + " " + onode.getAllProperties());
      cstoich.addRight(id, compartment, value);
    }
    
    return cstoich;
  }
  
  public CompartmentalizedStoichiometry<Long, Long> mapToUniversals(CompartmentalizedStoichiometry<Long, Long> aa) {
    CompartmentalizedStoichiometry<Long, Long> ustoich = new CompartmentalizedStoichiometry<>();
//    logger.debug("[{}] {}", mrxnNode.getSid(), aa);
    for (Pair<Long, Long> p : aa.stoichiometry.keySet()) {
      long spiId = p.getLeft();
      double value = aa.stoichiometry.get(p);
      BiosModelSpeciesNode spiNode = new BiosModelSpeciesNode(service.getNodeById(spiId), null);
      Set<Long> ucps = new HashSet<>();
      for (BiodbMetaboliteNode cpdNode : spiNode.getReferences()) {
        BiosUniversalMetaboliteNode ucpdNode = cpdNode.getUniversalMetabolite();
        if (ucpdNode != null) {
          ucps.add(ucpdNode.getId());
        }
      }
      long id = spiNode.getId();
      if (ucps.size() == 1) {
        logger.debug("{} -> {}", id, ucps.iterator().next());
        id = ucps.iterator().next();
      } else if (ucps.size() > 1) {
        logger.warn("{} {}", ucps, spiNode.getAllProperties());
      }
      
//      logger.debug("[{}] {} {} -> {}", mrxnNode.getSid(), p, value, id);
      ustoich.add(id, p.getRight(), value);
    }
    
    return ustoich;
  }
}
