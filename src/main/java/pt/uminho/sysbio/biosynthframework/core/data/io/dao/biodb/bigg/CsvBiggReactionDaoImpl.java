package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

/**
 * 
 * @author Filipe Liu
 *
 */

@Repository
public class CsvBiggReactionDaoImpl implements ReactionDao<BiggReactionEntity> {
	
	private static final Logger LOGGER = Logger.getLogger(CsvBiggReactionDaoImpl.class);
	
	private Resource csvFile;
	
	private BiggEquationParser biggEquationParser;
	private BiggReactionParser biggReactionParser;
	
	public Resource getCsvFile() { return csvFile;}
	public void setCsvFile(Resource csvFile) { this.csvFile = csvFile;}
	
	public BiggEquationParser getBiggEquationParser() { return biggEquationParser;}
	public void setBiggEquationParser(BiggEquationParser biggEquationParser) { this.biggEquationParser = biggEquationParser;}
	
	public BiggReactionParser getBiggReactionParser() { return biggReactionParser;}
	public void setBiggReactionParser(BiggReactionParser biggReactionParser) { this.biggReactionParser = biggReactionParser;}



	private Map<Long, String> idToEntry = new HashMap<> ();
	private Map<String, BiggReactionEntity> cachedData = new HashMap<> ();

	@Override
	public BiggReactionEntity getReactionById(Long id) {
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
	public Set<Long> getAllReactionIds() {
		if (idToEntry.isEmpty()) {
			this.initialize();
		}
		
		return new HashSet<Long> (this.idToEntry.keySet());
	}
	
	@Override
	public Set<String> getAllReactionEntries() {
		if (!idToEntry.isEmpty()) {
			return new HashSet<String> (this.idToEntry.values());
		}

		this.initialize();
		
		return new HashSet<String> (this.idToEntry.values());
	}
	
	public void initialize() {
		this.cachedData.clear();
		this.idToEntry.clear();
		
		if (this.biggEquationParser == null || this.biggReactionParser == null) {
			LOGGER.error("Missing Parsers BiggEquationParser:%s, BiggReactionParser:%s");
			return;
		}
		
		this.biggReactionParser.setEquationParser(biggEquationParser);
		
		try {
			List<BiggReactionEntity> res = new ArrayList<> ();
			InputStream in = csvFile.getInputStream();
			res = this.biggReactionParser.parseReactions(in);
			
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
