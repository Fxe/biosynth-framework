package edu.uminho.biosynth.core.data.integration.etl.warehouse.load;

import edu.uminho.biosynth.core.data.integration.etl.IEtlLoad;
import edu.uminho.biosynth.core.data.integration.etl.warehouse.components.MetaboliteFact;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class HbmMetaboliteWarehouseLoad implements IEtlLoad<MetaboliteFact> {

	private IGenericDao dao;
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	@Override
	public void etlLoad(MetaboliteFact destinationObject) {
		dao.save(destinationObject);
	}
	
}
