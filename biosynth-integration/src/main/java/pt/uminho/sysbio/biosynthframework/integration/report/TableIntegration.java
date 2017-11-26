package pt.uminho.sysbio.biosynthframework.integration.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.Dataset;

public class TableIntegration {
  
  private final GraphDatabaseService graphDataService;
  private final BiodbService biodbService;
  
  public TableIntegration(GraphDatabaseService graphDataService, BiodbService biodbService) {
    this.biodbService = biodbService;
    this.graphDataService = graphDataService;
  }
   
  public Dataset<String, String, Set<Long>> lookup(String[] ucomp) {
//    Transaction dataTx = graphDataService.beginTx();
//    Transaction metaTx = graphMetaService.beginTx();
    
//    BiodbService biodbService = new Neo4jBiodbDataServiceImpl(
//        DATA_NEO4J_DAO, META_NEO4J_DAO, CURA_NEO4J_DAO, INTEGRATION_SET);
    
    Map<Long, Long> umap = biodbService.getMetaboliteUnificationMap();
    
//    GraphDatabaseService service = graphMetaService;
//    Node itgNode = service.getNodeById(INTEGRATION_SET);
//    Map<Long, Long> uniMap = new HashMap<> ();
//    for (Node ctrNode : Neo4jUtils.collectNodeRelationshipNodes(
//        itgNode, integrationType)) {
//      long ctrId = ctrNode.getId();
//      for (Node refNode : Neo4jUtils.collectNodeRelationshipNodes(
//          ctrNode, IntegrationRelationshipType.Integrates)) {
//        long refId = (long) refNode.getProperty(Neo4jDefinitions.MEMBER_REFERENCE);
//        uniMap.put(refId, ctrId);
//      }
//    }
    System.out.println(umap.size());
    Dataset<String, String, Set<Long>> result = new Dataset<>();
    Map<String, Map<String, Set<Long>>> ucompMap = new HashMap<> ();
    for (String cpdEntry : ucomp) {
      ucompMap.put(cpdEntry, new HashMap<String, Set<Long>> ());
//      System.out.println("-------------> " + cpdEntry);
      Long cpdId = biodbService.getIdByEntryAndDatabase(
          cpdEntry, MetaboliteMajorLabel.BiGG2.toString());
//      System.out.println(cpdId);
//      Node refNode = Neo4jUtils.getUniqueResult(graphMetaService.findNodesByLabelAndProperty(
//          IntegrationNodeLabel.IntegratedMember, 
//          Neo4jDefinitions.MEMBER_REFERENCE, cpdId));
//      if (refNode != null) {
//        Node ctrNode = refNode.getSingleRelationship(
//            IntegrationRelationshipType.Integrates, 
//            Direction.BOTH).getOtherNode(refNode);
//        
//        for (Relationship r : ctrNode.getRelationships()) {
//          System.out.println(r.getType().name());
//        }
//      }
      
      Long ctrId = umap.get(cpdId);
//      System.out.println(refNode + " " + ctrId);
      if (ctrId != null) {
        Set<Long> refs = biodbService.getMembersByCtrId(ctrId);
        for (long refId : refs) {
          String db = biodbService.getDatabaseById(refId);
//          System.out.println(db + " " + biodbService.getEntryById(refId) + " " + biodbService.getNamePropertyById(refId));
          if (!ucompMap.get(cpdEntry).containsKey(db)) {
            ucompMap.get(cpdEntry).put(db, new HashSet<Long> ());
          }
          ucompMap.get(cpdEntry).get(db).add(refId);
        }
      } else {
        Node cpdNode = graphDataService.getNodeById(cpdId);
        for (Node ref : Neo4jUtils.collectNodeRelationshipNodes(cpdNode, 
            MetabolicModelRelationshipType.has_crossreference_to)) {
          if (ref.hasLabel(GlobalLabel.Metabolite)) {
            boolean proxy = (boolean) ref.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
            if (!proxy) {
//              String entry = (String) ref.getProperty("entry");
//              String name = (String) ref.getProperty("name", "");
//              System.out.println(entry + " " + name);
              long refId = ref.getId();
              String db = biodbService.getDatabaseById(refId);
              //            System.out.println(db + " " + biodbService.getEntryById(refId) + " " + biodbService.getNamePropertyById(refId));
              if (!ucompMap.get(cpdEntry).containsKey(db)) {
                ucompMap.get(cpdEntry).put(db, new HashSet<Long> ());
              }
              ucompMap.get(cpdEntry).get(db).add(refId);
            }
          }
        }
      }
//      System.out.println("-------------");
    }
    
//    dataTx.failure(); dataTx.close();
//    metaTx.failure(); metaTx.close();
    result.dataset.putAll(ucompMap);
    return result;
  }
}
