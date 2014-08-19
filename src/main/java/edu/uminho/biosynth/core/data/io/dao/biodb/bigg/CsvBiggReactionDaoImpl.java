package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

/**
 * 
 * @author Filipe Liu
 *
 */

@Repository
public class CsvBiggReactionDaoImpl implements ReactionDao<BiggReactionEntity> {
	
	private static final Logger LOGGER = Logger.getLogger(CsvBiggReactionDaoImpl.class);
	
	private Resource csvFile;
	
	public Resource getCsvFile() { return csvFile;}
	public void setCsvFile(Resource csvFile) { this.csvFile = csvFile;}
	
	private Map<Long, String> idToEntry = new HashMap<> ();
	private Map<String, BiggReactionEntity> cachedData = new HashMap<> ();

	@Override
	public BiggReactionEntity getReactionById(Serializable id) {
		if (idToEntry.isEmpty()) {
			this.initialize();
		}
		
		if (!this.idToEntry.containsKey(id)) return null;
		String entry = this.idToEntry.get(id);
		return this.cachedData.get(entry);
	}
	
	@Override
	public BiggReactionEntity getReactionByEntry(String entry) {
		if (idToEntry.isEmpty()) {
			this.initialize();
		}
		
		if (!this.cachedData.containsKey(entry)) return null;
		
		return this.cachedData.get(entry);
	}
	
	/**
	 * CsvBiggReactionDaoImpl is read only.
	 * 
	 * @return none
	 * 
	 * @throws RuntimeException Not Supported Operation
	 */
	@Override
	public BiggReactionEntity saveReaction(BiggReactionEntity reaction) {
		throw new RuntimeException("Not Supported Operation");
	}
	
	@Override
	public Set<Serializable> getAllReactionIds() {
		if (idToEntry.isEmpty()) {
			this.initialize();
		}
		
		return new HashSet<Serializable> (this.idToEntry.keySet());
	}
	
	@Override
	public Set<String> getAllReactionEntries() {
		if (!idToEntry.isEmpty()) {
			return new HashSet<String> (this.idToEntry.values());
		}
		
		return new HashSet<String> (this.idToEntry.values());
	}
	
	public void initialize() {
		this.cachedData.clear();
		this.idToEntry.clear();
		
		try {
			List<BiggReactionEntity> res = new ArrayList<> ();
			InputStream in = csvFile.getInputStream();
			res = DefaultBiggReactionParser.parseReactions(in);
			
			for (BiggReactionEntity rxn : res) {
				this.cachedData.put(rxn.getEntry(), rxn);
				this.idToEntry.put(rxn.getId(), rxn.getEntry());
			}
		
			in.close();
		} catch (IOException e) {
			LOGGER.error(String.format("IOException - [%s]", e.getMessage()));
		}
	}
}
