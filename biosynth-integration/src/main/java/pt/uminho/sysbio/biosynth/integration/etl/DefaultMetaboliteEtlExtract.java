package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class DefaultMetaboliteEtlExtract<M extends Metabolite> implements EtlExtract<M> {

	private final MetaboliteDao<M> metaboliteDao;
	
	public DefaultMetaboliteEtlExtract(MetaboliteDao<M> metaboliteDao) {
		this.metaboliteDao = metaboliteDao;
	}

	@Override
	public M extract(Serializable id) {
		M metaboliteEntity = null;
		
		if (id instanceof String) {
			metaboliteEntity = metaboliteDao.getMetaboliteByEntry((String)id);
		} else if (id instanceof Long) {
			metaboliteEntity = metaboliteDao.getMetaboliteById((Long)id);
		} else {
			
		}

		return metaboliteEntity;
	}

	@Override
	public List<M> extractAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllKeys() {
		List<Serializable> keys = new ArrayList<> ();
		for (String entry : metaboliteDao.getAllMetaboliteEntries()) {
			keys.add(entry);
		}
		
		return keys;
	}

}
