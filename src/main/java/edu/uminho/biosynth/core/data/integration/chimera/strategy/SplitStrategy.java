package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;

public interface SplitStrategy {
	public Set<Long> execute(IntegratedCluster integratedCluster);
}
