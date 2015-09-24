package pt.uminho.sysbio.biosynth.integration.etl;

import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

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
