package edu.uminho.biosynth.core.data.integration.etl.warehouse.extract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.etl.EtlExtract;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

@Deprecated
public class HbmMetaboliteWarehouseExtract implements EtlExtract<MetaboliteStga>{

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
	@Override
	public List<Serializable> getAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
