package pt.uminho.sysbio.biosynthframework.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(GraphUtils.class);
  
  public static<V, E> Set<V> getConnectedVertex(V s, UndirectedGraph<V, E> g) {
    if (!g.containsVertex(s)) {
      return null;
    }
    
    logger.debug("get connected set: {}", s);
    
    Set<V> result = new TreeSet<>();
    result.add(s);
    
    Set<E> visitedEdges = new HashSet<>(g.edgesOf(s));
    Set<E> move = new HashSet<> (g.edgesOf(s));
    while (!move.isEmpty()) {
      Set<E> next = new HashSet<> ();
      for (E e : move) {
        result.add(g.getEdgeSource(e));
        result.add(g.getEdgeTarget(e));
        for (E e_ : g.edgesOf(g.getEdgeSource(e))) {
          if (!visitedEdges.contains(e_)) {
            next.add(e_);
          }
        }
        for (E e_ : g.edgesOf(g.getEdgeTarget(e))) {
          if (!visitedEdges.contains(e_)) {
            next.add(e_);
          }
        }
        logger.trace("Now: {}, V: {}, Next: {}", move, result, next);
      }
      visitedEdges.addAll(move);
      move.clear();
      move.addAll(next);
    }
    
    
    return result;
  }
  
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
  
  public static <V> void addConnectedSet(Graph<V, ?> g, Collection<V> v) {
    Iterator<V> it = v.iterator();
    if (it.hasNext()) {
      V pivot = it.next();
      addVertexIfNotExists(g, pivot);
      while (it.hasNext()) {
        V e = it.next();
        addVertexIfNotExists(g, e);
        g.addEdge(pivot, e);
      }
    }
  }
  
  public static<V, E> List<Set<V>> getConnectedComponents(
      UndirectedGraph<V, E> g) {
    ConnectivityInspector<V, E> ccInspector = new ConnectivityInspector<>(g);
    List<Set<V>> ccList = ccInspector.connectedSets();
    
    return ccList;
  }
}
