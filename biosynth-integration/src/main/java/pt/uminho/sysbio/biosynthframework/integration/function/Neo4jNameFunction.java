package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;

public class Neo4jNameFunction implements BiFunction<Long, Long, Double>{
  
  private static Logger logger = LoggerFactory.getLogger(Neo4jNameFunction.class);
  
  private final GraphDatabaseService graphDataService;
  
  public double kappa = 1;
  public double alpha = 0.3;
  public double beta = 0.15;
  public double gamma = 0.1;
  public int rho = 3;
  
  public Neo4jNameFunction(GraphDatabaseService graphDataService) {
    this.graphDataService = graphDataService;
  }
  
  public String fixName(String n) {
    String fix = n;
    
    Map<String, String> replace = new HashMap<>();
    replace.put("<i>", "");
    replace.put("</i>", "");
    
    boolean fixing = true;
    while (fixing) {
      fixing = false;
      for (String k : replace.keySet()) {
        if (fix.contains(k)) {
          fix = fix.replace(k, replace.get(k));
          fixing = true;
        }
      }
    }
    
    
    if (!n.equals(fix)) {
      logger.debug("fixed {} -> {}", n, fix);
    }
    
    return fix;
  }
  
  @Override
  public Double apply(Long t, Long u) {
    Node a = graphDataService.getNodeById(t);
    Node b = graphDataService.getNodeById(u);
    
    
    Map<Long, String> namesA = new HashMap<> ();
    Map<Long, String> namesB = new HashMap<> ();
    Map<Long, String> namesExactA = new HashMap<> ();
    Map<Long, String> namesExactB = new HashMap<> ();
    Set<Long> iupacA = new HashSet<> ();
    Set<Long> iupacB = new HashSet<> ();
    
    int miupac = 0;
    int mexact = 0;
    int mmod = 0;
    int mmax = 0;
    
    for (Relationship r : a.getRelationships(MetaboliteRelationshipType.has_name)) {
      BiodbPropertyNode nameNode = new BiodbPropertyNode(r.getOtherNode(a));
      long propertyId = nameNode.getId();
      String k = (String) nameNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
      k = fixName(k);
      namesA.put(propertyId, k);
      if ("CORRECTED".equals(r.getProperty("DCS-status", null))) {
        k = (String) r.getProperty("DCS-original");
      }
      namesExactA.put(propertyId, k);
      if (nameNode.hasLabel(MetabolitePropertyLabel.IUPACName)) {
        iupacA.add(propertyId);
      }
    }
    
    for (Relationship r : b.getRelationships(MetaboliteRelationshipType.has_name)) {
      BiodbPropertyNode nameNode = new BiodbPropertyNode(r.getOtherNode(b));
      long propertyId = nameNode.getId();
      String k = (String) nameNode.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
      k = fixName(k);
      namesB.put(propertyId, k);
      if ("CORRECTED".equals(r.getProperty("DCS-status", null))) {
        k = (String) r.getProperty("DCS-original");
      }
      namesExactB.put(propertyId, k);
      if (nameNode.hasLabel(MetabolitePropertyLabel.IUPACName)) {
        iupacB.add(propertyId);
      }
    }
    
    logger.debug("A:{}", namesA);
    logger.debug("B:{}", namesB);
    
    mmax = Math.max(namesA.size(), namesA.size());
    Set<Long> iupacAB = Sets.intersection(iupacA, iupacB);
    miupac = iupacAB.size();
    namesExactA.keySet().removeAll(iupacAB);
    namesExactB.keySet().removeAll(iupacAB);
    Set<Long> exactAB = new HashSet<> ();
    Set<String> exactStringAB = Sets.intersection(new HashSet<>(namesExactA.values()), 
                                                  new HashSet<>(namesExactB.values()));
    for (long id : namesExactA.keySet()) {
      String k = namesExactA.get(id);
      if (exactStringAB.contains(k)) {
        exactAB.add(id);
      }
    }
    
    mexact = exactAB.size();
    
    namesA.keySet().removeAll(iupacAB);
    namesB.keySet().removeAll(iupacAB);
    namesA.keySet().removeAll(exactAB);
    namesB.keySet().removeAll(exactAB);
    mmod = Sets.intersection(new HashSet<>(namesA.values()), 
                             new HashSet<>(namesB.values())).size();
    
    logger.debug("IUPAC A/B: {}/{}, MAX: {}, Matches (IUPAC: {}, Exact: {}, Mod: {})", iupacA.size(), iupacB.size(), mmax, miupac, mexact, mmod);
    
    double neg = 0.0;
    
    if (mmax >= rho && (miupac + mexact + mmod) < 1) {
      neg = -1 * kappa;
    }
    
    return (miupac * alpha + mexact * beta + mmod * gamma) + neg;
  }
}
