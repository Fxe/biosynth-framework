package pt.uminho.sysbio.biosynth.integration.etl;

import edu.uminho.biosynth.core.components.Metabolite;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class DefaultMetaboliteEtlLoad<M extends Metabolite> implements EtlLoad<M> {
	
	private final MetaboliteDao<M> dao;

	public DefaultMetaboliteEtlLoad(MetaboliteDao<M> dao) {
		this.dao = dao; 
	}
	
	@Override
	public void etlLoad(M entity) {
		dao.saveMetabolite(entity);
	}
}
