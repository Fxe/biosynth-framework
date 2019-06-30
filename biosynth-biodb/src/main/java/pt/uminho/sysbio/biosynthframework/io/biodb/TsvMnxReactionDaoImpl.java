package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class TsvMnxReactionDaoImpl implements ReactionDao<MnxReactionEntity> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TsvMnxReactionDaoImpl.class);
	
	private File reactionCsvFile;
	private File crossreferenceCsvFile;
	
	private Map<Serializable, Integer> entrySeekPosition = new HashMap<> ();
	private Map<String, MnxReactionEntity> cachedData = new HashMap<> ();
	private Map<Serializable, List<MnxReactionCrossReferenceEntity>> xrefMap = new HashMap<> ();
	
	private boolean bulkAccess = false;
	
	public boolean isBulkAccess() { return bulkAccess;}
	public void setBulkAccess(boolean bulkAccess) { this.bulkAccess = bulkAccess;}
	
	@Autowired
	public TsvMnxReactionDaoImpl(File reactionCsvFile, File crossreferenceCsvFile) {
		this.reactionCsvFile = reactionCsvFile;
		this.crossreferenceCsvFile = crossreferenceCsvFile;
		this.xrefMap.clear();
		try {
			this.xrefMap = parseReactionCrossReferences();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void parseReactionCrossReferences(Map<Serializable, List<MnxReactionCrossReferenceEntity>> xrefMap, String record) {
		/* Example Record
		 * REFERENCE : XREF | MNX_ID 
		 * bigg:10FTHF5GLUtl| MNXR1   
		 */
		LOGGER.trace("L:" + record);
		String[] fields = record.trim().split("\t");
		String key = fields[1].trim();
		String reference = "none";
		String entry = fields[0].trim();
		if (entry.contains(":")) {
		  reference = fields[0].trim().split(":")[0].trim();
		  entry = fields[0].trim().split(":")[1].trim();
		}
		MnxReactionCrossReferenceEntity xref = new MnxReactionCrossReferenceEntity(ReferenceType.DATABASE, reference, entry);
//		if (fields.length > 3) xref.setDescription(fields[3].trim());
//		xref.setEvidence(fields[2].trim());
		if ( !xrefMap.containsKey(key)) {
			xrefMap.put(key, new ArrayList<MnxReactionCrossReferenceEntity> ());
		}
		
		xrefMap.get(key).add(xref);
	}
	
	public Map<Serializable, List<MnxReactionCrossReferenceEntity>> parseReactionCrossReferences() throws FileNotFoundException, IOException {
		Map<Serializable, List<MnxReactionCrossReferenceEntity>> xrefMap = new TreeMap<> ();
		
		BufferedReader br = new BufferedReader(new FileReader(crossreferenceCsvFile));
		String readLine = null;
		while ( (readLine = br.readLine()) != null) {
			if (readLine.trim().charAt(0) != '#') {
				try {
					parseReactionCrossReferences(xrefMap, readLine);
				} catch (ArrayIndexOutOfBoundsException e) {
					br.close();
					System.err.println(readLine);
					throw e;
				}
			}
		}
		
		br.close();
		
//		LOGGER.debug("Parsed total " xrefMap.get);
		
		return xrefMap;
	}
	
	public MnxReactionEntity parseReaction(String record) {
		/* Example Record
		 * ENTRY | EQUATION | FORMULA | CHARGE | MASS  | InChI        | SMILES | ORIGINAL REFERENCE
		 * MNXM1 | H(+) | H       | 1      | 1.008 | InChI=1S/p+1 | [H+]   | chebi:15378
		 */
		String[] values = record.split("\t");
//		System.out.println(values.length);
		MnxReactionEntity rxn = new MnxReactionEntity();
		rxn.setEntry(values[0].trim());
//		rxn.setName(values[0].trim());
		rxn.setEquation(values[1].trim());
//		cpd.setFormula(values[2].trim());
//		cpd.setCharge(values[3].trim().length() > 0 ? Integer.parseInt(values[3].trim()) : null);
//		String mass = values[4].trim();
//		Double massValue;
//		try {
//			massValue = mass.trim().length() > 0 ? Double.parseDouble(mass) : null;
//		} catch (NumberFormatException nfEx) {
//			massValue = Double.MAX_VALUE;
//		}
//		cpd.setMass(massValue);
//		cpd.setInChI(values[5].trim());
//		cpd.setSmiles(values[6].trim());
//		if (values.length > 7) cpd.setOriginalSource(values[7].trim());
//		cpd.setMetaboliteClass("METABOLITE");
		return rxn;
	}
	
	public List<MnxReactionEntity> parseReactions() throws FileNotFoundException, IOException {
		LOGGER.debug("Parsing Reactions");
		
		List<MnxReactionEntity> mnxReactions = new ArrayList<> ();
		
		BufferedReader br = new BufferedReader(new FileReader(reactionCsvFile));
		String readLine = null;
		while ( (readLine = br.readLine()) != null) {
			if (readLine.trim().charAt(0) != '#') {
				try {
					mnxReactions.add(parseReaction(readLine));
				} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
					br.close();
					System.err.println(readLine);
					throw e;
				}
			}
		}
		
		br.close();
		return mnxReactions;
	}
	
//	private String seekFileLine(File file, int pos) throws IOException{
//		if (pos < 0) return null;
//		
//		String res = null;
//		
//		int line = 0;
//		BufferedReader br = new BufferedReader(new FileReader(reactionCsvFile));
//		String readLine = null;
//		while ( (readLine = br.readLine()) != null && line < pos) {
//			line++;
//		}
//		res = readLine;
//		br.close();
//		return res;
//	}
	
	@Override
	public MnxReactionEntity getReactionById(Long id) {
		throw new RuntimeException("Unsupported Operation");
	}
	@Override
	public MnxReactionEntity getReactionByEntry(String entry) {
		if(this.bulkAccess && cachedData.isEmpty()) {
			try {
				for (MnxReactionEntity rxn: this.parseReactions()) {
					this.cachedData.put(rxn.getEntry(), rxn);
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		if (cachedData.containsKey(entry)) {
			MnxReactionEntity rxn = cachedData.get(entry);
			
			for (MnxReactionCrossReferenceEntity xref: this.xrefMap.get(entry)) {
				xref.setMnxReactionEntity(rxn);
				rxn.addCrossReference(xref);
			}
			
			return rxn;
		}
		
		MnxReactionEntity rxn = null;
		
		return rxn;
	}
	@Override
	public MnxReactionEntity saveReaction(MnxReactionEntity reaction) {
		throw new RuntimeException("Unsupported Operation");
	}
	@Override
	public Set<Long> getAllReactionIds() {
		throw new RuntimeException("Unsupported Operation");
	}
	
	@Override
	public Set<String> getAllReactionEntries() {
		Set<String> idList = new HashSet<> ();
		int line = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(reactionCsvFile));
			String readLine = null;
			while ( (readLine = br.readLine()) != null) {
				if (readLine.trim().charAt(0) != '#') {
					String[] values = readLine.split("\t");
					String entry = values[0].trim(); 
					idList.add(entry);
					this.entrySeekPosition.put(entry, line);
				}
				line++;
			}
			br.close();
		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
		return idList;
	}
}
