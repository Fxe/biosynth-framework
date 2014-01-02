package edu.uminho.biosynth.core.data.service;

import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;

public class KeggService extends AbstractMetaboliteService<KeggMetaboliteEntity> {
	
	public KeggService(IGenericEntityDao dao) {
		super(dao, KeggMetaboliteEntity.class);
	}
}
