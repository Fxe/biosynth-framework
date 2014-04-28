package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.Set;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;

public interface SplitStrategy {
	public Set<Long> execute(IntegratedCluster integratedCluster);
}
