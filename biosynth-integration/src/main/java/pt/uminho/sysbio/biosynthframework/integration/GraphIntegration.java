package pt.uminho.sysbio.biosynthframework.integration;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface GraphIntegration {
  public Graph<Long, DefaultEdge> integrate(long id);
}
