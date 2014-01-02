package edu.uminho.biosynth.core.data.service;

import edu.uminho.biosynth.core.components.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;

public class BiocycService extends AbstractMetaboliteService<BioCycMetaboliteEntity> {

	public BiocycService(IGenericEntityDao dao) {
		super(dao, BioCycMetaboliteEntity.class);
	}
}
