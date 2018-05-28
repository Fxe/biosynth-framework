package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;
import pt.uminho.sysbio.biosynthframework.chemanalysis.cdk.CdkWrapper;

public class Neo4jStructureFunction implements BiFunction<Long, Long, Double> {

  private static Logger logger = LoggerFactory.getLogger(Neo4jStructureFunction.class);
  
  private final GraphDatabaseService service;
  
  public double alpha = 1;
  public double beta = 1;
  public double gamma = -0.5;
  
  public boolean removeHlayer = false;
  
  public Neo4jStructureFunction(GraphDatabaseService service) {
    this(service, false);
  }
  
  public Neo4jStructureFunction(GraphDatabaseService service, boolean removeHLayer) {
    this.service = service;
    this.removeHlayer = removeHLayer;
  }
  
  public static String removeLayer(String inchi, String layer) {
    String[] layers = inchi.split("/");
    List<String> inchiWithoutLayer = new ArrayList<> ();
    for (String l : layers) {
      if (!l.startsWith(layer)) {
        inchiWithoutLayer.add(l);
      }
    }
    String result = StringUtils.join(inchiWithoutLayer, '/');
    return result;
  }
  
  public static String removeHydrogenFromFormulaLayer(String inchi) {
    String[] layers = inchi.split("/");
    
    if (layers.length > 1) {
      CdkWrapper cdk = new CdkWrapper();
//      System.out.println("+");
      Map<String, Integer> ac = cdk.getAtomCountMap(layers[1]);
//      System.out.println("-");
//      System.out.println(layers[1] + " " +  ac);
//      System.out.println(inchi);
      if (ac != null) {
        if (ac.size() == 1 && ac.containsKey("H")) {
          logger.debug("formula contains only hydrogens: keep {}", layers[1]);
        } else if (ac.size() > 1){
          ac.remove("H");
          String f = Joiner.on("").withKeyValueSeparator("").join(ac);
          f = CdkWrapper.toIsotopeMolecularFormula(f, false);
          logger.debug("remove hydrogens from formula layer: {} -> {}", layers[1], f);
          layers[1] = f;
        }
      }
    }
    
    
    String result = StringUtils.join(layers, '/');
    return result;
  }
  
  @Override
  public Double apply(Long t, Long u) {
    Node a = service.getNodeById(t);
    Node b = service.getNodeById(u);

    Set<BiodbPropertyNode> ia = new HashSet<>();
    Set<BiodbPropertyNode> ib = new HashSet<>();
    for (Relationship r : a.getRelationships(MetaboliteRelationshipType.has_inchi)) {
      String confidence = (String) r.getProperty("confidence", "high");
      if (confidence.equals("high")) {
        Node node = r.getOtherNode(a);
        logger.debug("[{}] i+:{}", t, node.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
        ia.add(new BiodbPropertyNode(node, null));
      }
    }
    for (Relationship r : b.getRelationships(MetaboliteRelationshipType.has_inchi)) {
      String confidence = (String) r.getProperty("confidence", "high");
      if (confidence.equals("high")) {
        Node node = r.getOtherNode(b);
        logger.debug("[{}] i+:{}", u, node.getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT));
        ib.add(new BiodbPropertyNode(node, null));
      }
    }
    
    //one of the compounds has no InChI
    if (ia.size() == 0 || ib.size() == 0) {
      return 0.0;
    }
    
    
    //exact
    int ie = Sets.intersection(ia, ib).size();
    logger.trace("{}", ia);
    logger.trace("{}", ib);
    
    Map<String, Set<Node>> iap = new HashMap<>();
    Map<String, Set<Node>> ibp = new HashMap<>();
    
    
//    for (BiodbPropertyNode inchiNode : ia) {
//      Relationship r = inchiNode.getSingleRelationship(MetaboliteRelationshipType.has_inchikey, Direction.BOTH);
//      if (r != null) {
//        BiodbPropertyNode ikNode = new BiodbPropertyNode(r.getOtherNode(inchiNode));
//        System.out.println(ikNode.getValue());
//      }
//    }
//    System.out.println("--");
//    for (BiodbPropertyNode inchiNode : ib) {
//      Relationship r = inchiNode.getSingleRelationship(MetaboliteRelationshipType.has_inchikey, Direction.BOTH);
//      if (r != null) {
//        BiodbPropertyNode ikNode = new BiodbPropertyNode(r.getOtherNode(inchiNode));
//        System.out.println(ikNode.getValue());
//      }
//    }
    
    for (BiodbPropertyNode inchiNode : ia) {
      String k = inchiNode.getValue();
      String kp = removeLayer(k, "p");
      if (removeHlayer) {
        kp = removeLayer(kp, "h");
        kp = removeHydrogenFromFormulaLayer(kp);
      }
      logger.debug("A    : {}", inchiNode.getValue());
      logger.debug("A (*): {}", kp);
      if (!iap.containsKey(kp)) {
        iap.put(kp, new HashSet<Node>());
      }
      iap.get(kp).add(inchiNode);
    }
    
    for (BiodbPropertyNode inchiNode : ib) {
      String k = inchiNode.getValue();
      String kp = removeLayer(k, "p");
      if (removeHlayer) {
        kp = removeLayer(kp, "h");
        kp = removeHydrogenFromFormulaLayer(kp);
      }
      logger.debug("B    : {}", inchiNode.getValue());
      logger.debug("B (*): {}", kp);
      if (!ibp.containsKey(kp)) {
        ibp.put(kp, new HashSet<Node>());
      }
      ibp.get(kp).add(inchiNode);
    }
    
    //protonation
    int ih = Sets.intersection(iap.keySet(), ibp.keySet()).size();
    
    logger.debug("Exact: {}, Protons: {}", ie, ih);
    
//    Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
//        a, MetaboliteRelationshipType.has_smiles);
//    Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
//        b, MetaboliteRelationshipType.has_smiles);
    
    // if is defined
    
    //else
    if (ie + ih == 0) {
      return gamma;
    }
    
    return ie * alpha + ih * beta;
  }
  
  public Double dapply(Long t, Long u) {
    Node a = service.getNodeById(t);
    Node b = service.getNodeById(u);
    
//    ZTestFunctions.a(a, MetaboliteRelationshipType.has_inchi);
//    logger.info("-----------------------------");
//    ZTestFunctions.a(b, MetaboliteRelationshipType.has_inchi);
    
    Set<Node> ia = new HashSet<>();
    Set<Node> ib = new HashSet<>();
    for (Relationship r : a.getRelationships(MetaboliteRelationshipType.has_inchi)) {
      String confidence = (String) r.getProperty("confidence", "high");
      if (confidence.equals("high")) {
        Node node = r.getOtherNode(a);
        ia.add(node);
      }
    }
    
    for (Relationship r : b.getRelationships(MetaboliteRelationshipType.has_inchi)) {
      String confidence = (String) r.getProperty("confidence", "high");
      if (confidence.equals("high")) {
        Node node = r.getOtherNode(b);
        ib.add(node);
      }
    }
    
//    Set<Long> ia = Neo4jUtils.collectNodeRelationshipNodeIds(
//        a, MetaboliteRelationshipType.has_inchi);
//    Set<Long> ib = Neo4jUtils.collectNodeRelationshipNodeIds(
//        b, MetaboliteRelationshipType.has_inchi);
    
    //exact
    int ie = Sets.intersection(ia, ib).size();
    //protonation
//    int ih = 0;
//    
//    Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
//        a, MetaboliteRelationshipType.has_smiles);
//    Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
//        b, MetaboliteRelationshipType.has_smiles);
    
    // if is defined
    
    //else
    
    
    return ie * 1.0;
  }
}
