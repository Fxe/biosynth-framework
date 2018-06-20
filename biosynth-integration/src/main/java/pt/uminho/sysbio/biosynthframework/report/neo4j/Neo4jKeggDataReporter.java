package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jKeggDataReporter extends AbstractNeo4jReporter {

  public Neo4jKeggDataReporter(GraphDatabaseService service) {
    super(service);
  }
  
  public void reportRemark(MetaboliteMajorLabel db,  Dataset<MetaboliteMajorLabel, String, Integer> result) {
    
    Map<String, String> scanFields = new HashMap<> ();
    scanFields.put("same as:", "same as");
    scanFields.put("atc code:", "atc code");
    scanFields.put("therapeutic category:", "therapeutic category");
    scanFields.put("chemical group:", "chemical group");
    scanFields.put("drug group:", "drug group:");
    
    for (String f : scanFields.values()) {
      result.add(db, f, 0);
    }
    
    for (Node cpdNode : service.listNodes(db) ){
      boolean proxy = (boolean) cpdNode.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
      if (proxy) {
        CollectionUtils.increaseCount(result.dataset.get(db), "proxy", 1);
      } else {
        String remark = (String) cpdNode.getProperty("remark", null);
        if (remark != null) {
          
          for (String f : scanFields.keySet()) {
            if (remark.toLowerCase().trim().startsWith(f)) {
              CollectionUtils.increaseCount(result.dataset.get(db), scanFields.get(f), 1);
            }
          }
          
//          if (remark.toLowerCase().trim().startsWith("same as:")) {
//            CollectionUtils.increaseCount(result.dataset.get(db), "same as", 1);
//          } else if (remark.toLowerCase().trim().startsWith("atc code:")){
//            CollectionUtils.increaseCount(result.dataset.get(db), "atc code", 1);
//          } else if (remark.toLowerCase().trim().startsWith("therapeutic category:")){
//            CollectionUtils.increaseCount(result.dataset.get(db), "therapeutic category", 1);
//          } else if (remark.toLowerCase().trim().startsWith("chemical group:")){
//            CollectionUtils.increaseCount(result.dataset.get(db), "chemical group", 1);
//          } else {
//            System.err.println(cpdNode.getProperty("entry") + remark);
//          }
        }
      }
    }
  }

  public void reportRemark() {
    Dataset<MetaboliteMajorLabel, String, Integer> result = new Dataset<>();

    reportRemark(MetaboliteMajorLabel.LigandGlycan, result);
    reportRemark(MetaboliteMajorLabel.LigandCompound, result);
    reportRemark(MetaboliteMajorLabel.LigandDrug, result);

    DataUtils.printData(result.dataset);
  }
}
