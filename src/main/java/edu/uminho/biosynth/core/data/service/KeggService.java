package edu.uminho.biosynth.core.data.service;

import org.springframework.stereotype.Service;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

@Service
public class KeggService extends AbstractMetaboliteService<KeggMetaboliteEntity> {
	
	public KeggService(IGenericDao dao) {
		super(dao, KeggMetaboliteEntity.class);
		super.setServiceId("kegg");
	}
}
