package pt.uminho.sysbio.biosynth.integration.etl;

import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynthframework.Metabolite;

public class HeterogenousMetaboliteEtlLoad<M extends Metabolite> implements EtlLoad<M> {
	
	private final MetaboliteHeterogeneousDao<M> heterogeneousDao;

	public HeterogenousMetaboliteEtlLoad(MetaboliteHeterogeneousDao<M> heterogeneousDao) {
		this.heterogeneousDao = heterogeneousDao;
	}
	
	@Override
	public void etlLoad(M destinationObject) {
		heterogeneousDao.saveMetabolite("", destinationObject);
	}
}
