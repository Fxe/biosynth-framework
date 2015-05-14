package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.Set;

public interface ClusteringStrategy {
  public void setInitialNode(Long id);
  public Set<Long> execute();
}
