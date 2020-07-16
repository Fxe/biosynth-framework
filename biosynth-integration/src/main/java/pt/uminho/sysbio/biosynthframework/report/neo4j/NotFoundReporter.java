package pt.uminho.sysbio.biosynthframework.report.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;

public class NotFoundReporter extends AbstractNeo4jReporter {

  private static final Logger logger = LoggerFactory.getLogger(NotFoundReporter.class);
  
  public NotFoundReporter(GraphDatabaseService service) {
    super(service);
  }

  public void aaa() {
    for (Node node : service.listNodes(MetaboliteMajorLabel.NOTFOUND)) {
      System.out.println(node.getAllProperties());
      for (Relationship r : node.getRelationships()) {
        Node sn = r.getStartNode();
        Node en = r.getEndNode();
        String t = r.getType().name();
        logger.info("{} -[{}]-> {}", sn.getAllProperties(), t, en.getAllProperties());
      }
    }
  }
  
  public void aaa2() {
    for (BiodbMetaboliteNode node : service.listMetabolites(MetaboliteMajorLabel.MetaCyc)) {
      if (node.isProxy()) {
        System.out.println(node.getAllProperties());
        for (Relationship r : node.getRelationships()) {
          Node sn = r.getStartNode();
          Node en = r.getEndNode();
          String t = r.getType().name();
          logger.info("{} -[{}]-> {}", sn.getAllProperties(), t, en.getAllProperties());
        }
      }
    }
  }
}
