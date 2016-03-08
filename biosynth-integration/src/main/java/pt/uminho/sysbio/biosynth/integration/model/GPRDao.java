package pt.uminho.sysbio.biosynth.integration.model;

import java.io.Serializable;
import java.util.List;


public interface GPRDao<G extends GPR>{
	public G getGPRById(String tag, Serializable id);

	public G getGPRByEntry(String tag, String entry);

	public G saveGPR(String tag, G gpr);
	
	
	public List<Long> getGlobalAllGPRIds();

	public List<Long> getAllGPRIds(String tag);
	
	public List<String> getAllGPREntries(String tag);
}
