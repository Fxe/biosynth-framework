package edu.uminho.biosynth.core.data.service;

import org.springframework.stereotype.Service;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;


public class KeggService extends AbstractMetaboliteService<KeggCompoundMetaboliteEntity> {
	
	public KeggService(IGenericDao dao) {
		super(dao, KeggCompoundMetaboliteEntity.class);
		super.setServiceId("kegg");
	}
}
