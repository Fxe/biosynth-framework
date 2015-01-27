package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.curation.CurationOperation;
import pt.uminho.sysbio.biosynth.integration.curation.CurationSet;
import pt.uminho.sysbio.biosynth.integration.curation.CurationUser;

public interface IntegrationCurationDao {
	public Long saveCurationSet(CurationSet curationSet);
	public CurationSet getCurationSet(long xid);
	public CurationSet getCurationSet(String entry);
	public Set<Long> getAllCurationSetIds();
	
	public CurationUser getCurationUserById(long id);
	public CurationUser getCurationUserByUsername(String username);
	public Long saveCurationUser(CurationUser curationUser);
	
	public Long saveCurationOperation(CurationOperation curationOperation);
	public CurationOperation getCurationOperationById(long oid);
	public CurationOperation getCurationOperationByEntry(String entry);
	public Set<CurationOperation> getCurationOperationByMembers(Set<Long> eidSet);

	public Set<Long> getAllCurationOperationIds(long xid);
}
