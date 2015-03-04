package pt.uminho.sysbio.biosynthframework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

public class CsvDefaultReactionDaoImpl implements ReactionDao<DefaultReaction> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CsvDefaultReactionDaoImpl.class);
	
	private static final int NAME_INDEX = 0;
	private static final int LEFT_INDEX = 1;
	private static final int RIGHT_INDEX = 2;
	private static final int LEFT_STOICH_INDEX = 3;
	private static final int RIGHT_STOICH_INDEX = 4;
	private static final int ORIENTATION_INDEX = 5;
	
	private File csvFile;
	
	private boolean initialized = false;
	private Map<String, DefaultReaction> reactionMap = new HashMap<> ();
	
	public CsvDefaultReactionDaoImpl(File csvFile) {
		this.csvFile = csvFile;
		initialize();
	}
	
	private void initialize() {
		reactionMap.clear();
		
		try {
			String data = BioSynthUtilsIO.readFromFile(csvFile);
			String[] reactionRecords = data.split("\n");
			for (String reactionString : reactionRecords) {
				try {
					if (!reactionString.trim().isEmpty()) {
						String[] fields = reactionString.split(",");
						String entry = fields[NAME_INDEX];
						String[] left = fields[LEFT_INDEX].split("\\s+");
						String[] right = fields[RIGHT_INDEX].split("\\s+");
						String[] left_stoich = fields[LEFT_STOICH_INDEX].split("\\s+");
						String[] right_stoich = fields[RIGHT_STOICH_INDEX].split("\\s+");
						boolean rev = Integer.parseInt(fields[ORIENTATION_INDEX]) != 0;
						DefaultReaction genericReaction = new DefaultReaction();
						genericReaction.setEntry(entry);
						genericReaction.setName(entry);
						
						if (left.length != left_stoich.length || right.length != right_stoich.length) {
							throw new IllegalArgumentException(String.format("Invalid format at line: %s", reactionString));
						}
						
						Map<String, Double> leftMap = new HashMap<> ();
						for (int i = 0; i < left.length; i++) {
							leftMap.put(left[i], Double.parseDouble(left_stoich[i]));
						}
						Map<String, Double> rightMap = new HashMap<> ();
						for (int i = 0; i < right.length; i++) {
							rightMap.put(right[i], Double.parseDouble(right_stoich[i]));
						}
						
						genericReaction.setReactantStoichiometry(leftMap);
						genericReaction.setProductStoichiometry(rightMap);
						genericReaction.setOrientation(rev ? Orientation.Reversible : Orientation.LeftToRight);

						this.reactionMap.put(entry, genericReaction);
					}
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage() + " @ " + reactionString);
				}
			}
			
			initialized = true;
		} catch (FileNotFoundException e) {
			LOGGER.error(String.format("File not found: %s", e.getMessage()));
			initialized = false;
		} catch (IOException e) {
			LOGGER.error(String.format("IO error: %s", e.getMessage()));
			initialized = false;
		}
		
		
		LOGGER.debug(String.format("Loaded %s reactions", this.reactionMap.size()));
	}
	
	@Override
	public DefaultReaction getReactionById(Long id) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public DefaultReaction getReactionByEntry(String entry) {
		if (!initialized) this.initialize();
		return this.reactionMap.get(entry);
	}

	@Override
	public DefaultReaction saveReaction(DefaultReaction reaction) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Set<Long> getAllReactionIds() {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public Set<String> getAllReactionEntries() {
		return this.reactionMap.keySet();
	}

}
