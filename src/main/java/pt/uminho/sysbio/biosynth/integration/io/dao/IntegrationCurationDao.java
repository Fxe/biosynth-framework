package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.curation.CurationOperation;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;

public interface IntegrationCurationDao {
	public Long saveCurationSet(CurationSet curationSet);
	public CurationSet getCurationSet(long xid);
	public CurationSet getCurationSet(String entry);
	public Set<Long> getAllCurationSetIds();
	
	public Long saveCurationCluster(CurationOperation curationCluster);
	public CurationOperation getCurationCluster(long oid);
	public CurationOperation getCurationCluster(String entry);
	public Set<CurationOperation> getCurationClustersByMembers(Set<Long> eidSet);
	public Set<Long> getAllCurationOperationIds(long xid);
}
