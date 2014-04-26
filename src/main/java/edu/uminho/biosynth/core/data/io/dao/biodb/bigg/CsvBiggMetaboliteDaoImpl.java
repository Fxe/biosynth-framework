package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

/**
 * 
 * @author Filipe Liu
 *
 */
public class CsvBiggMetaboliteDaoImpl implements MetaboliteDao<BiggMetaboliteEntity>{

	@Autowired
	private File csvFile;
	
	public File getCsvFile() { return csvFile;}
	public void setCsvFile(File csvFile) { this.csvFile = csvFile;}
	
	private Map<Long, String> idToEntry = new HashMap<> ();
	private Map<String, BiggMetaboliteEntity> cachedData = new HashMap<> ();

	@Override
	public BiggMetaboliteEntity find(Serializable id) {
		for (BiggMetaboliteEntity c : this.findAll()) {
			if (c.getEntry().equals(id)) return c;
		}
		return null;
	}

	@Override
	public List<BiggMetaboliteEntity> findAll() {
		this.cachedData.clear();
		this.idToEntry.clear();
		
		List<BiggMetaboliteEntity> cpdList = null;
		try {
			cpdList = DefaultBiggMetaboliteParser.parseMetabolites(csvFile);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		for (BiggMetaboliteEntity cpd: cpdList) {
			this.cachedData.put(cpd.getEntry(), cpd);
			this.idToEntry.put(cpd.getId(), cpd.getEntry());
		}
		return cpdList;
	}

	@Override
	public Serializable save(BiggMetaboliteEntity entity) {
		throw new RuntimeException("Not Supported Operation");
	}
	
	@Override
	public List<Serializable> getAllMetaboliteIds() {
		if (!idToEntry.isEmpty()) {
			return new ArrayList<Serializable> (this.idToEntry.keySet());
		}
		
		List<Serializable> res = new ArrayList<> ();
		for (BiggMetaboliteEntity cpd : this.findAll()) {
			res.add(cpd.getId());
		}
		return res;
	}
	
	@Override
	public BiggMetaboliteEntity getMetaboliteById(Serializable id) {
//		if (!idToEntry.isEmpty()) {
//			return new ArrayList<Serializable> (this.idToEntry.keySet());
//		}
		
		for (BiggMetaboliteEntity c : this.findAll()) {
			if (c.getId().equals(id)) return c;
		}
		return null;
	}
	
	@Override
	public BiggMetaboliteEntity saveMetabolite(
			BiggMetaboliteEntity metabolite) {
		throw new RuntimeException("Not Supported Operation");
	}
	@Override
	public Serializable saveMetabolite(Object entity) {
		throw new RuntimeException("Not Supported Operation");
	}
	
	@Override
	public BiggMetaboliteEntity getMetaboliteByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> getAllMetaboliteEntries() {
		if (!idToEntry.isEmpty()) {
			return new ArrayList<String> (this.idToEntry.values());
		}
		
		List<String> res = new ArrayList<> ();
		for (BiggMetaboliteEntity cpd : this.findAll()) {
			res.add(cpd.getEntry());
		}
		return res;
	}
}
