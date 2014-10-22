package edu.uminho.biosynth.core.data.integration.chimera.service;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CurationEdgeType;

public interface IntegrationCurationService {
	public void setCurationInformation(IntegratedCluster integratedCluster, String type, String description);
	public void setCurationInformation(long id1, long id2, CurationEdgeType type, String description);
}
