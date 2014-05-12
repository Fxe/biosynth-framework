package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.IntegratedClusterMetaGenerator;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMeta;

public class IntegrationMetaServiceImpl implements IntegrationMetaService {

	private ChimeraDataDao data;
	private ChimeraMetadataDao meta;
	
	private List<IntegratedClusterMetaGenerator> generators = new ArrayList<> ();
	
	@Override
	public List<IntegratedClusterMeta> generateMeta(
			IntegratedCluster integratedCluster) {
		
		
		integratedCluster.listAllIntegratedMemberIds();
		
//		data.get
		
		return null;
	}

	@Override
	public void updatedMeta(IntegratedCluster integratedCluster) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IntegratedClusterMeta> getMeta(
			IntegratedCluster integratedCluster) {
		// TODO Auto-generated method stub
		return null;
	}

}
