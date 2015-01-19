package pt.uminho.sysbio.biosynth.integration.io.dao;

import pt.uminho.sysbio.biosynth.integration.curation.CurationCluster;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;

public interface IntegrationCurationDao {
	public Long saveCurationSet(CurationSet curationSet);
	public CurationSet getCurationSet(long id);
	public CurationSet getCurationSet(String entry);
	
	public Long saveCurationCluster(CurationCluster curationCluster);
	public CurationCluster getCurationCluster(long id);
	public CurationCluster getCurationCluster(String entry);
}
