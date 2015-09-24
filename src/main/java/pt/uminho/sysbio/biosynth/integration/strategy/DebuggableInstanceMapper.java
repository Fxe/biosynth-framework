package pt.uminho.sysbio.biosynth.integration.strategy;

import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public interface DebuggableInstanceMapper extends ClusteringStrategy {
  Object getDebugInformation();
}
