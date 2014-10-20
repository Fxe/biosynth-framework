package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.util.List;

public interface IntegrationReportDao {
	
	public List<Long> getIntegratedMetabolitesIdsByTag(String iid, String tag);
	public List<Long> getIntegratedReactionsIdsByTag(String iid, String tag);
}
