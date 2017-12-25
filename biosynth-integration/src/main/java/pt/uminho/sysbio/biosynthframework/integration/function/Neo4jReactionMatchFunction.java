package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jReactionMatchFunction implements BiFunction<Long, Long, Double> {

  private static Logger logger = LoggerFactory.getLogger(Neo4jReactionMatchFunction.class);

  private final ConnectedComponents<Long> ccs;
  
  private final BiodbGraphDatabaseService graphDataService;
  
  public double alpha = 1;
  public double  beta = 0;
  
  public Neo4jReactionMatchFunction(GraphDatabaseService graphDataService) {
    this(graphDataService, null, 1.0, 0.0);
  }
  
  public Neo4jReactionMatchFunction(GraphDatabaseService graphDataService, ConnectedComponents<Long> ccs) {
    this(graphDataService, ccs, 1.0, 0.0);
  }
  
  public Neo4jReactionMatchFunction(GraphDatabaseService graphDataService, ConnectedComponents<Long> ccs, double alpha, double beta) {
    this.graphDataService = new BiodbGraphDatabaseService(graphDataService);
    this.ccs = ccs;
    this.alpha = alpha;
    this.beta = beta;
  }
  /**
   * a and b should not be in the same reaction
   * let r  = a  ->  b
   * let r' = a' ->  b'
   * a -> b & a -> a' => 
   */
  @Override
  public Double apply(Long t, Long u) {
    BiodbMetaboliteNode a = new BiodbMetaboliteNode(graphDataService.getNodeById(t));
    BiodbMetaboliteNode b = new BiodbMetaboliteNode(graphDataService.getNodeById(u));
    
    Set<Node> rxnA = Neo4jUtils.collectNodeRelationshipNodes(a, 
        ReactionRelationshipType.left_component, ReactionRelationshipType.right_component);
    Set<Node> rxnB = Neo4jUtils.collectNodeRelationshipNodes(b, 
        ReactionRelationshipType.left_component, ReactionRelationshipType.right_component);
    
    int i = Sets.intersection(rxnA, rxnB).size();
    
    int singleSet = 0;
    int  multiSet = 0;
    
    if (ccs != null) {
      Set<Long> ccA = ccs.getConnectedComponentOf(t);
      Set<Long> ccB = ccs.getConnectedComponentOf(u);
      

      if (ccA != null && !ccA.isEmpty() && ccA.equals(ccB)) {
        MetaboliteMajorLabel dbA = a.getDatabase();
        MetaboliteMajorLabel dbB = b.getDatabase();
        
        Map<MetaboliteMajorLabel, Integer> dbCount = new HashMap<>();
        for (long id : ccA) {
          BiodbMetaboliteNode n = new BiodbMetaboliteNode(graphDataService.getNodeById(id));
          CollectionUtils.increaseCount(dbCount, n.getDatabase(), 1);
        }
        
        Integer countA = dbCount.get(dbA);
        Integer countB = dbCount.get(dbB);
        logger.debug("Databases A/B count: {}/{} {}/{}", dbA, dbB, countA, countB);
        if (countA == countB && countA == 1) {
          singleSet = 1;
        } else {
          multiSet = 1;
        }
      }
    }

    
    return -10.0 * i + alpha * singleSet + beta * multiSet;
  } 
}
