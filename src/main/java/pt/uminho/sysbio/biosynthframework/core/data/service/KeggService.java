package pt.uminho.sysbio.biosynthframework.core.data.service;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;


public class KeggService extends AbstractMetaboliteService<KeggCompoundMetaboliteEntity> {
	
	public KeggService(IGenericDao dao) {
		super(dao, KeggCompoundMetaboliteEntity.class);
		super.setServiceId("kegg");
	}
}
