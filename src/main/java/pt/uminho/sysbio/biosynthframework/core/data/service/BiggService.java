package pt.uminho.sysbio.biosynthframework.core.data.service;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;

@Deprecated
public class BiggService extends AbstractMetaboliteService<BiggMetaboliteEntity>{

	public BiggService(IGenericDao dao) {
		super(dao, BiggMetaboliteEntity.class);
		super.setServiceId("bigg");
	}

}
