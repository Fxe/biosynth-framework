package pt.uminho.sysbio.biosynth.integration.etl;

import edu.uminho.biosynth.core.components.Metabolite;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;

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
