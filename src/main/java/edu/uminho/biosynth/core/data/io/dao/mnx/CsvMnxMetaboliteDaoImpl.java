package edu.uminho.biosynth.core.data.io.dao.mnx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.IMetaboliteDao;

public class CsvMnxMetaboliteDaoImpl implements IMetaboliteDao<MnxMetaboliteEntity> {

	private File metaboliteCsvFile;
	private File crossreferenceCsvFile;
	
	private Map<String, Integer> entrySeekPosition = new HashMap<> ();
	private Map<String, List<MnxMetaboliteCrossReferenceEntity>> xrefMap;
	
	private String seekFileLine(File file, int pos) throws IOException{
		if (pos < 0) return null;
		
		String res = null;
		
		int line = 0;
		BufferedReader br = new BufferedReader(new FileReader(metaboliteCsvFile));
		String readLine = null;
		while ( (readLine = br.readLine()) != null && line < pos) {
			line++;
		}
		res = readLine;
		br.close();
		return res;
	}

	public File getMetaboliteCsvFile() {
		return metaboliteCsvFile;
	}

	public void setMetaboliteCsvFile(File metaboliteCsvFile) {
		this.metaboliteCsvFile = metaboliteCsvFile;
	}

	public File getCrossreferenceCsvFile() {
		return crossreferenceCsvFile;
	}

	public void setCrossreferenceCsvFile(File crossreferenceCsvFile) {
		this.crossreferenceCsvFile = crossreferenceCsvFile;
	}

	public void parseMetaboliteCrossReference(Map<String, List<MnxMetaboliteCrossReferenceEntity>> xrefMap, String record) {
		/* Example Record
		 * REFERENCE : ENTRY | MAPS TO  | 
		 * bigg:14glucan     | MNXM2905 | inferred | 1,4-alpha-D-glucan
		 */
		String[] fields = record.trim().split("\t");
		String key = fields[1].trim();
		String reference = fields[0].trim().split(":")[0].trim();
		String entry = fields[0].trim().split(":")[1].trim();
		MnxMetaboliteCrossReferenceEntity xref = new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, reference, entry);
		if (fields.length > 3) xref.setDescription(fields[3].trim());
		xref.setEvidence(fields[2].trim());
		if ( !xrefMap.containsKey(key)) {
			xrefMap.put(key, new ArrayList<MnxMetaboliteCrossReferenceEntity> ());
		}
		
		xrefMap.get(key).add(xref);
	}
	
	public Map<String, List<MnxMetaboliteCrossReferenceEntity>> parseMetaboliteCrossReferences() throws FileNotFoundException, IOException {
		Map<String, List<MnxMetaboliteCrossReferenceEntity>> xrefMap = new TreeMap<> ();
		
		BufferedReader br = new BufferedReader(new FileReader(crossreferenceCsvFile));
		String readLine = null;
		while ( (readLine = br.readLine()) != null) {
			if (readLine.trim().charAt(0) != '#') {
				try {
					parseMetaboliteCrossReference(xrefMap, readLine);
				} catch (ArrayIndexOutOfBoundsException e) {
					br.close();
					System.err.println(readLine);
					throw e;
				}
			}
		}
		
		br.close();
		return xrefMap;
	}

	public MnxMetaboliteEntity parseMetabolite(String record) {
		/* Example Record
		 * ENTRY | NAME | FORMULA | CHARGE | MASS  | InChI        | SMILES | ORIGINAL REFERENCE
		 * MNXM1 | H(+) | H       | 1      | 1.008 | InChI=1S/p+1 | [H+]   | chebi:15378
		 */
		String[] values = record.split("\t");
//		System.out.println(values.length);
		MnxMetaboliteEntity cpd = new MnxMetaboliteEntity();
		cpd.setEntry(values[0].trim());
		cpd.setName(values[1].trim());
		cpd.setFormula(values[2].trim());
		cpd.setCharge(values[3].trim().length() > 0 ? Integer.parseInt(values[3].trim()) : null);
		String mass = values[4].trim();
		double massValue;
		try {
			massValue = mass.trim().length() > 0 ? Double.parseDouble(mass) : null;
		} catch (NumberFormatException nfEx) {
			massValue = Double.MAX_VALUE;
		}
		cpd.setMass(massValue);
		cpd.setInChI(values[5].trim());
		cpd.setSmiles(values[6].trim());
		if (values.length > 7) cpd.setOriginalSource(values[7].trim());
		cpd.setMetaboliteClass("METABOLITE");
		return cpd;
	}
	
	
	public List<MnxMetaboliteEntity> parseMetabolites() throws FileNotFoundException, IOException {
		List<MnxMetaboliteEntity> mnxMetabolites = new ArrayList<> ();
		
		BufferedReader br = new BufferedReader(new FileReader(metaboliteCsvFile));
		String readLine = null;
		while ( (readLine = br.readLine()) != null) {
			if (readLine.trim().charAt(0) != '#') {
				try {
					mnxMetabolites.add(parseMetabolite(readLine));
				} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
					br.close();
					System.err.println(readLine);
					throw e;
				}
			}
		}
		
		br.close();
		return mnxMetabolites;
	}
	
	@Override
	public MnxMetaboliteEntity getMetaboliteInformation(Serializable id) {
		if (!this.entrySeekPosition.containsKey(id)) {
			this.getAllMetaboliteIds();
		}
		MnxMetaboliteEntity cpd = null;
		try {
			if (this.entrySeekPosition.containsKey(id)) {
				if (this.xrefMap == null) {
					this.xrefMap = parseMetaboliteCrossReferences();
				}
				String record = this.seekFileLine(metaboliteCsvFile, this.entrySeekPosition.get(id));
				cpd = this.parseMetabolite(record);
				for (MnxMetaboliteCrossReferenceEntity xref: this.xrefMap.get(id)) {
					xref.setMnxMetaboliteEntity(cpd);
					cpd.addCrossReference(xref);
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return cpd;
	}

	@Override
	public MnxMetaboliteEntity saveMetaboliteInformation(
			MnxMetaboliteEntity metabolite) {
		throw new RuntimeException("Unsupported Operation");
	}

	@Override
	public MnxMetaboliteEntity find(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Serializable> getAllMetaboliteIds() {
		List<Serializable> idList = new ArrayList<> ();
		int line = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(metaboliteCsvFile));
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

	@Override
	public List<MnxMetaboliteEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(MnxMetaboliteEntity entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
