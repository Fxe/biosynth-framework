package edu.uminho.biosynth.core.data.service;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class BiocycService extends AbstractMetaboliteService<BioCycMetaboliteEntity> {
	
	public BiocycService(IGenericDao dao) {
		super(dao, BioCycMetaboliteEntity.class);
		super.setServiceId("biocyc");
	}
	
	public BiocycService(IGenericDao dao, String serviceId) {
		super(dao, BioCycMetaboliteEntity.class);
		super.setServiceId(serviceId);
	}
}
