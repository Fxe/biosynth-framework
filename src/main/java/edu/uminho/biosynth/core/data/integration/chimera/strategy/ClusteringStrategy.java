package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.List;

public interface ClusteringStrategy {
	public void setInitialNode(Long id);
	public List<Long> execute();
}
