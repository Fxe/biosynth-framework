package edu.uminho.biosynth.core.data.integration.etl.staging.extract;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

@Deprecated
public class BiggMetaboliteStageExtract implements IMetaboliteStageExtract<BiggMetaboliteEntity>{

	private IGenericDao dao;
	
	@Override
	public BiggMetaboliteEntity extract(Serializable id) {
		return dao.find(BiggMetaboliteEntity.class, id);
	}

	@Override
	public List<BiggMetaboliteEntity> extractAll() {
		return dao.findAll(BiggMetaboliteEntity.class);
	}

	@Override
	public List<Serializable> getAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
