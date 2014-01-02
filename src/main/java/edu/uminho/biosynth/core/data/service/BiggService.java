package edu.uminho.biosynth.core.data.service;

import edu.uminho.biosynth.core.components.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;

public class BiggService extends AbstractMetaboliteService<BiggMetaboliteEntity>{

	public BiggService(IGenericEntityDao dao) {
		super(dao, BiggMetaboliteEntity.class);
	}

}
