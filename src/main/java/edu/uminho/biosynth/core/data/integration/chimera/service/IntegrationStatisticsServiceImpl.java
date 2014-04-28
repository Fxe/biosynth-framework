package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class IntegrationStatisticsServiceImpl implements IntegrationStatisticsService {

	private static final Logger LOGGER = Logger.getLogger(IntegrationStatisticsServiceImpl.class);
	
	@Autowired
	private ChimeraDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	
	@Override
	public int countTotalMetaboliteMembers() {
		return this.data.countByLabel("Compound");
	}

	@Override
	public int countIntegratedMetaboliteMembers(IntegrationSet integrationSet) {
//		this.data.countByLabel("Compound");
		return 0;
	}

	@Override
	public Map<String, Integer> countTotalMetaboliteMembersByMajor() {
		Map<String, Integer> count = new HashMap<> ();
		for (String major : this.data.getAllMajorMetaboliteLabels()) {
			count.put(major, this.data.countByLabel(major));
		}
		return count;
	}

	@Override
	public Map<String, Integer> countIntegratedMetaboliteMembersByMajor(IntegrationSet integrationSet) {
		// TODO Auto-generated method stub
		return null;
	}

}
