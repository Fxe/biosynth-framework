package pt.uminho.sysbio.biosynth.integration;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.jung.graph.Graph;

public class BFS {
  
  private static final Logger logger = LoggerFactory.getLogger(BFS.class);
  
  public static<V, E> Set<V> run(Graph<V, E> graph, V vertex) {
    Set<V> res = new HashSet<> ();
    
    if (!graph.containsVertex(vertex)) {
      logger.debug("Vertex {} not found", vertex);
      return res;
    }
    
    LinkedList<V> queue = new LinkedList<>();
    Set<V> visited = new HashSet<> ();
    
    queue.addFirst(vertex);
    visited.add(vertex);
    while (!queue.isEmpty()) {
      V v = queue.getFirst();
      queue.remove(v);
      res.add(v);
      for (V adj : graph.getNeighbors(vertex)) {
        if (!visited.contains(adj)) {
          visited.add(adj);
          queue.addFirst(adj);
        }
      }
    }
    
    return res;
  }
}
