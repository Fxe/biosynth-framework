package edu.uminho.biosynth.core.data.service;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class KeggService extends AbstractMetaboliteService<KeggMetaboliteEntity> {
	
	public KeggService(IGenericDao dao) {
		super(dao, KeggMetaboliteEntity.class);
	}
}
