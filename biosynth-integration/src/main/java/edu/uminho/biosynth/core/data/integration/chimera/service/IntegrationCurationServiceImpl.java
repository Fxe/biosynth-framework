package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CurationEdge;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CurationEdgeType;

@Service
public class IntegrationCurationServiceImpl implements IntegrationCurationService {

	@Autowired
	private IntegrationMetadataDao integrationMetadataDao;
	
	@Override
	public void setCurationInformation(long id1, long id2, CurationEdgeType type,
			String description) {
		
		CurationEdge curationEdge = new CurationEdge();
		curationEdge.setSrc(id1);
		curationEdge.setDst(id2);
		curationEdge.setType(type);
		curationEdge.setDescription(description);
		
		integrationMetadataDao.saveCurationEdge(curationEdge);
	}

	@Override
	public void setCurationInformation(IntegratedCluster integratedCluster,
			String type, String description) {
		
		List<Long> list = integratedCluster.listAllIntegratedMemberIds();
		if (list.size() > 1) {
			Iterator<Long> iterator = list.iterator();
			
			Long prev = iterator.next();
			while (iterator.hasNext()) {
				Long actual = iterator.next();
				this.setCurationInformation(prev, actual, CurationEdgeType.Identity, "Equal Entities");
				prev = actual;
			}
		}
	}

}
