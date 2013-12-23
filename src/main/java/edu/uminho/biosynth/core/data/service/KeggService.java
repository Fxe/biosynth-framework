package edu.uminho.biosynth.core.data.service;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;

public class KeggService implements IMetaboliteService<KeggMetaboliteEntity> {

	private IGenericEntityDao dao;
	
	public KeggService(IGenericEntityDao dao) {
		this.dao = dao;
	}
	
	@Override
	public KeggMetaboliteEntity getMetaboliteByEntry(String entry) {
		List<KeggMetaboliteEntity> result = 
				this.dao.criteria(KeggMetaboliteEntity.class, Restrictions.eq("entry", entry));
		if (result == null || result.size() < 1) return null;
		if (result.size() > 1) throw new RuntimeException("Unique field error, duplicate entry " + entry);
		return result.iterator().next();
	}

	@Override
	public KeggMetaboliteEntity getMetaboliteById(int id) {
		return this.dao.find(KeggMetaboliteEntity.class, id);
	}

	@Override
	public List<KeggMetaboliteEntity> getAllMetabolites() {
		return this.dao.findAll(KeggMetaboliteEntity.class);
	}

	@Override
	public int countNumberOfMetabolites() {
		// TODO Auto-generated method stub
		return 0;
	}
}
