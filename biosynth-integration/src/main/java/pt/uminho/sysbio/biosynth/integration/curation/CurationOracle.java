package pt.uminho.sysbio.biosynth.integration.curation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class CurationOracle {
  
  private static final Logger logger = LoggerFactory.getLogger(CurationOracle.class);
  
  public UndirectedGraph<Long, DefaultEdge> graphEq = 
      new SimpleGraph<>(DefaultEdge.class);
  public UndirectedGraph<Long, DefaultEdge> graphNeq = 
      new SimpleGraph<>(DefaultEdge.class);
  //Hierarchy network
  public Graph<Long, DefaultEdge> graphHierarchy = 
      new SimpleDirectedGraph<>(DefaultEdge.class);
  
  public Map<Long, Long> not = new HashMap<> ();
  public Map<Long, Long> equals = new HashMap<> ();
  
  public void isNot(long a, long b) {
    not.put(a, b);
    not.put(b, a);
  }
  
  public boolean isNotEqual(long a, long b) {
    Set<Long> neq = getNotEquals(a);
    return neq.contains(b);
  }
  
  public boolean isEqual(long a, long b) {
    Set<Long> eq = getEquals(a);
    return eq.contains(b);
  }
  
  /**
   * 
   * @param parent ID
   * @param child ID
   * @return if network was modified
   */
  public boolean addChild(long parent, long child) {
    logger.warn("hierarchy not working properly ... yet!");
    if (!graphHierarchy.containsVertex(parent)) {
      graphHierarchy.addVertex(parent);
    }
    if (!graphHierarchy.containsVertex(child)) {
      graphHierarchy.addVertex(child);
    }
    
    graphEq.addEdge(parent, child);
    
    return false;
  }
  
  public boolean addIsEqual(long a, long b) {
    if (hasDisjointConstraint(a, b)) {
      logger.warn("Invalid Operation {} and {}, must be disjoint", a, b);
      return false;
    }
    //check is it is already equals
    if (!isEqual(a, b)) {
      if (!graphEq.containsVertex(a)) {
        graphEq.addVertex(a);
      }
      if (!graphEq.containsVertex(b)) {
        graphEq.addVertex(b);
      }
      graphEq.addEdge(a, b);
      
      return true;
    }
    
    return false;
  }
  
  public boolean addIsNotEqual(long a, long b) {
    if (isNotEqual(a, b)) {
      logger.warn("Already added {} =/= {}", a, b);
      return false;
    }
     //check is it is already equals
    if (!isEqual(a, b)) {
      if (!graphNeq.containsVertex(a)) {
        graphNeq.addVertex(a);
      }
      if (!graphNeq.containsVertex(b)) {
        graphNeq.addVertex(b);
      }
      graphNeq.addEdge(a, b);
      
      return true;
    }
    
    return false;
  }
  
  public Set<Long> getNotEquals(long id) {
    Set<Long> neq = new HashSet<> ();
    if (graphNeq.containsVertex(id)) {
      Set<DefaultEdge> edges = graphNeq.edgesOf(id);
      for (DefaultEdge e : edges) {
        long dst = graphNeq.getEdgeTarget(e);
        long src = graphNeq.getEdgeSource(e);
        neq.add(src);
        neq.add(dst);
      }
    }
    neq.remove(id);
    return neq;
  }
  
  public Set<Long> getEquals(long id) {
    Set<Long> equals = new HashSet<> ();
    Set<Long> toVisit = new HashSet<> ();
    toVisit.add(id);
    if (graphEq.containsVertex(id)) {
      while (!toVisit.isEmpty()) {
        long pivot = toVisit.iterator().next();
        equals.add(pivot);
        Set<DefaultEdge> edges = graphEq.edgesOf(pivot);
        for (DefaultEdge e : edges) {
          long dst = graphEq.getEdgeTarget(e);
          long src = graphEq.getEdgeSource(e);
          toVisit.add(src);
          toVisit.add(dst);
        }
        toVisit.removeAll(equals);
      }
      
    }
    return equals;
  }
  
  public boolean hasKnowledge(long id) {
    return not.containsKey(id) || graphEq.containsVertex(id);
  }
  
  public long decideSingle(long id, Set<Long> candidates) {
    logger.debug("{} => {}", id, candidates.size());
    if (candidates.isEmpty()) {
      return id;
    }
    //check if knowledge something about
    if (hasKnowledge(id)) {
      /* Iskashli Progridka
       * Selection methods
       * if oracles knows that a ==> b
       * selects a
       * otherwise applies exclusion
       * 
       */
      logger.info("Found data about {}", id);
      //select element(s) that belongs to set
      Set<Long> eq = getEquals(id);
      logger.info("{} == {}", id, eq);
      Set<Long> r = Sets.intersection(eq, candidates);
      if (!r.isEmpty()) {
        return r.iterator().next();
      }
      //removing elements that does not belongs to set
    }
    return candidates.iterator().next();
  }

  public Set<Long> decide(long id, Set<Long> candidates) {
    logger.debug("{} => {}", id, candidates.size());
    if (candidates.isEmpty()) {
      Set<Long> idSet = new HashSet<> ();
      idSet.add(id);
      return idSet;
    }
    //check if knowledge something about
    if (hasKnowledge(id)) {
      /* Iskashli Progridka
       * Selection methods
       * if oracles knows that a ==> b
       * selects a
       * otherwise applies exclusion
       * 
       */
      logger.info("Found data about {}", id);
      //select element(s) that belongs to set
      Set<Long> eq = getEquals(id);
      logger.info("{} == {}", id, eq);
      Set<Long> r = Sets.intersection(eq, candidates);
      if (!r.isEmpty()) {
        return r;
      }
      //removing elements that does not belongs to set
    }
    return candidates;
  }
  
  public boolean hasDisjointConstraint(long a, long b) {
    Set<Long> A = getEquals(a);
    Set<Long> B = getEquals(b);
    Set<Long> neqA = new HashSet<> ();
    for (long i : A) {
      Set<Long> neq = getNotEquals(i);
      neqA.addAll(neq);
    }
    
    boolean AdisjointB = !Sets.intersection(neqA, B).isEmpty();
    
    return AdisjointB;
  }
  
  public static void main(String[] args) {
    CurationOracle oracle = new CurationOracle();
    System.out.println("1 -> 2, " + oracle.isEqual(1, 2));
    oracle.addIsEqual(1, 3);
    oracle.addIsEqual(2, 4);
    oracle.addIsEqual(6, 4);
    oracle.addIsNotEqual(1, 6);
    System.out.println("1 -> 3, " + oracle.isEqual(1, 3));
    System.out.println("4 -> 2, " + oracle.isEqual(4, 2));
    System.out.println("4 -> 6, " + oracle.isEqual(4, 6));
    System.out.println("2 -> 6, " + oracle.isEqual(2, 6));
    System.out.println("1 -> 6, " + oracle.isEqual(1, 6));
    System.out.println("S(3) must be disjoint S(6), " + oracle.hasDisjointConstraint(3, 6));
    oracle.addIsEqual(3, 6);
    System.out.println("1 -> 6, " + oracle.isEqual(1, 6));
    oracle.addIsNotEqual(1, 6);
  }

  public static<V, E> Set<Pair<V, V>> getEdgeAsPairs(Graph<V, E> g) {
    Set<Pair<V, V>> set = new HashSet<> ();
    for (E e : g.edgeSet()) {
      V src = g.getEdgeSource(e);
      V dst = g.getEdgeTarget(e);
      set.add(new ImmutablePair<>(src, dst));
    }
    return set;
  }

  public Set<Pair<Long, Long>> getNeqConstraints() {
    return getEdgeAsPairs(graphNeq);
  }
  
  public Set<Pair<Long, Long>> getEqConstraints() {
    return getEdgeAsPairs(graphEq);
  }

  public String toTsv() {
    final char SEP = '\t';
    StringBuilder sb = new StringBuilder();
    sb.append("Set Number").append(SEP)
      .append("ID A").append(SEP)
      .append("ID B");
//    for (graphEq.)
    ConnectivityInspector<Long, DefaultEdge> eqCCInspector = 
        new ConnectivityInspector<>(graphEq);
    int counter = 0;
    for (Set<Long> s : eqCCInspector.connectedSets()) {
      Iterator<Long> it = s.iterator();
      long s0 = it.next();
      while (it.hasNext()) {
        sb.append('\n');
        long sx = it.next();
        sb.append(counter).append(SEP)
          .append(s0).append(SEP)
          .append(sx);
        s0 = sx;
      }
      System.out.println(s);
      counter++;
    }
    // TODO Auto-generated method stub
    return sb.toString();
  }
}
