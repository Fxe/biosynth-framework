package pt.uminho.sysbio.biosynthframework.util;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;

public class GraphUtils {
  public static<V, E> void merge(Graph<V, E> src, Graph<V, E> dst) {
    for (V v : src.vertexSet()) {
      dst.addVertex(v);
    }
    for (E e : src.edgeSet()) {
      dst.addEdge(src.getEdgeSource(e), src.getEdgeTarget(e));
    }
  }
  
  public static<V, E> boolean addVertexIfNotExists(Graph<V, E> g, V v) {
    if (!g.containsVertex(v)) {
      g.addVertex(v);
      return true;
    }
    return false;
  }
  
  public static<V, E> List<Set<V>> getConnectedComponents(
      UndirectedGraph<V, E> g) {
    ConnectivityInspector<V, E> ccInspector = new ConnectivityInspector<>(g);
    List<Set<V>> ccList = ccInspector.connectedSets();
    
    return ccList;
  }
}
