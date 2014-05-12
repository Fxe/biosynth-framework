package edu.uminho.biosynth.core.data.integration.chimera;

import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMeta;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;

public interface IntegratedClusterMetaGenerator {
	public List<IntegratedClusterMeta> generateMeta(IntegratedMetaboliteEntity integratedMetaboliteEntity);
}
