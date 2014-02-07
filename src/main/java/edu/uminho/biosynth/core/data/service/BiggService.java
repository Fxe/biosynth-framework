package edu.uminho.biosynth.core.data.service;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class BiggService extends AbstractMetaboliteService<BiggMetaboliteEntity>{

	public BiggService(IGenericDao dao) {
		super(dao, BiggMetaboliteEntity.class);
		super.setServiceId("bigg");
	}

}
