package pt.uminho.sysbio.biosynthframework.core.data.service;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;

@Deprecated
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
