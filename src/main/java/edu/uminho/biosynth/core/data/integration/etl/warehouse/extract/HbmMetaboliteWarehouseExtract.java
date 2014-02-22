package edu.uminho.biosynth.core.data.integration.etl.warehouse.extract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.data.integration.etl.IEtlExtract;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class HbmMetaboliteWarehouseExtract implements IEtlExtract<MetaboliteStga>{

	private IGenericDao dao;
	
	public IGenericDao getDao() { return dao;}
	public void setDao(IGenericDao dao) { this.dao = dao;}
	
	@Override
	public MetaboliteStga extract(Serializable id) {
		return dao.find(MetaboliteStga.class, id);
	}
	
	public List<MetaboliteStga> extract(Serializable...ids) {
		List<MetaboliteStga> cpdList = new ArrayList<> ();
		for (Serializable id : ids) {
			cpdList.add( this.extract(id));
		}
		return cpdList;
	}

	@Override
	public List<MetaboliteStga> extractAll() {
		return this.dao.findAll(MetaboliteStga.class);
	}

}
