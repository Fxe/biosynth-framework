package pt.uminho.sysbio.biosynthframework.genome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

//import org.jgrapht.Graph;
//import org.jgrapht.GraphPath;
//import org.jgrapht.UndirectedGraph;
//import org.jgrapht.alg.DijkstraShortestPath;
//import org.jgrapht.graph.DefaultEdge;
//import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import pt.uminho.sysbio.biosynthframework.util.GraphUtils;

public class ClosestPhylogenyMatcher {
  
  private static final Logger logger = LoggerFactory.getLogger(ClosestPhylogenyMatcher.class);
  
//  public Graph<Long, DefaultEdge> taxaGraph;
//  public Graph<Long, DefaultEdge> taxaGraphValid;
  public Map<Long, Long> mergeMap = new HashMap<>();
  
  private Set<Long> validTax = new HashSet<>();
  
  public Set<Long> bestMatch = new HashSet<>();
  
  public boolean addLineage(long txid) {
    if (mergeMap.containsKey(txid)) {
      txid = mergeMap.get(txid);
    }
//    if (taxaGraph.containsVertex(txid)) {
//      logger.debug("[START] add lineage for: {}", txid);
//      long prev = txid;
//      Set<DefaultEdge> visited = new HashSet<>();
//      while (prev != 1) {
//        GraphUtils.addVertexIfNotExists(taxaGraphValid, prev);
//        Set<DefaultEdge> edges = taxaGraph.edgesOf(prev);
//        Set<DefaultEdge> edges_ = new HashSet<>();
//        for (DefaultEdge e : edges) {
//          long s = taxaGraph.getEdgeTarget(e);
//          if (s == prev) {
//            edges_.add(e);
//          }
//        }
//        logger.trace("{} / {}", edges.size(), edges_.size());
//        //only edges that I am source
//        for (DefaultEdge e : edges_) {
//          if (!visited.contains(e)) {
//            long s = taxaGraph.getEdgeSource(e);
//            long t = taxaGraph.getEdgeTarget(e);
//            long other = s == prev ? t : s;
//            GraphUtils.addVertexIfNotExists(taxaGraphValid, other);
//            taxaGraphValid.addEdge(other, prev);
//            prev = other;
////            System.out.println(s + " " + t);
//            visited.add(e);
//          }
//        }
//      }
//      logger.debug("[DONE!] add lineage for: {}", txid);
//      return true;
//    }
    return false;
  }
  
  public void setValidTaxIds(Set<Long> validTax) {
    this.validTax.clear();
    this.validTax.addAll(validTax);
//    taxaGraphValid = new SimpleGraph<>(DefaultEdge.class);
    for (long id : validTax) {
      addLineage(id);
    }
  }
  
  public double distance(long ncbiTaxId) {
    if (mergeMap.containsKey(ncbiTaxId)) {
      ncbiTaxId = mergeMap.get(ncbiTaxId);
    }
    Map<Integer, Set<Long>> distanceMap = new TreeMap<> ();
//    if (!taxaGraphValid.containsVertex(ncbiTaxId)) {
//      if (!addLineage(ncbiTaxId)) {
//        return -1;
//      }
//    }
    
    for (long txId : validTax) {
//      if (taxaGraphValid.containsVertex(txId)) {
//        DijkstraShortestPath<Long, DefaultEdge> shortestAlgo = 
//            new DijkstraShortestPath<Long, DefaultEdge>(
//                taxaGraphValid, ncbiTaxId, txId);
//        
//        GraphPath<Long, DefaultEdge> path = shortestAlgo.getPath();
//        int distance = path.getEdgeList().size();
//        if (!distanceMap.containsKey(distance)) {
//          distanceMap.put(distance, new HashSet<> ());
//        }
//        distanceMap.get(distance).add(txId);
//      } else {
//        logger.warn("txid not in graph: {}", txId);
//      }

    }
    
    int minDistance = distanceMap.keySet().iterator().next();
    
    bestMatch = distanceMap.get(minDistance);

    return minDistance;
  }
}
