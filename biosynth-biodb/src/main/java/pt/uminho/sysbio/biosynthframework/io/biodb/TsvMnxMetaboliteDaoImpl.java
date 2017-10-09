package pt.uminho.sysbio.biosynthframework.io.biodb;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class TsvMnxMetaboliteDaoImpl implements MetaboliteDao<MnxMetaboliteEntity> {

  private final static Logger logger = LoggerFactory.getLogger(TsvMnxMetaboliteDaoImpl.class);

  public static String DEFAULT_CHEM_PROP_TSV = "chem_prop.tsv";
  public static String DEFAULT_CHEM_XREF_TSV = "chem_xref.tsv";

  private File metaboliteCsvFile;
  private File crossreferenceCsvFile;

  private Map<Serializable, Integer> entrySeekPosition = new HashMap<> ();
  private Map<String, MnxMetaboliteEntity> cachedData = new HashMap<> ();
  private Map<Serializable, List<MnxMetaboliteCrossreferenceEntity>> xrefMap = new HashMap<> ();

  public TsvMnxMetaboliteDaoImpl() { }

  public TsvMnxMetaboliteDaoImpl(String path) {
    this(new File(path + "/" + DEFAULT_CHEM_PROP_TSV), 
        new File(path + "/" + DEFAULT_CHEM_XREF_TSV));
  }

  public TsvMnxMetaboliteDaoImpl(File chemPropTsv, File chemXrefTsv) {
    this.setCrossreferenceCsvFile(chemXrefTsv);
    this.setMetaboliteCsvFile(chemPropTsv);
  }

  private boolean bulkAccess = false;

  public boolean isBulkAccess() {
    return bulkAccess;
  }

  public void setBulkAccess(boolean bulkAccess) {
    this.bulkAccess = bulkAccess;
  }

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
    this.xrefMap.clear();
    try {
      this.xrefMap = parseMetaboliteCrossReferences();
    } catch (IOException e) {
      //TODO: LOGGER
    }
  }

  public void parseMetaboliteCrossReference(Map<Serializable, List<MnxMetaboliteCrossreferenceEntity>> xrefMap, String record) {
    /* Example Record
     * REFERENCE : ENTRY | MAPS TO  | 
     * bigg:14glucan     | MNXM2905 | inferred | 1,4-alpha-D-glucan
     */
    String[] fields = record.trim().split("\t");
    String key = fields[1].trim();
    String reference = null; 
    String entry = fields[0].trim();
    
    if (entry.contains(":")) {
      entry = entry.split(":")[1].trim();
      reference = fields[0].trim().split(":")[0].trim();
    }
    
    MnxMetaboliteCrossreferenceEntity xref = new MnxMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, reference, entry);
    if (fields.length > 3) xref.setDescription(fields[3].trim());
    xref.setEvidence(fields[2].trim());
    if ( !xrefMap.containsKey(key)) {
      xrefMap.put(key, new ArrayList<MnxMetaboliteCrossreferenceEntity> ());
    }

    xrefMap.get(key).add(xref);
  }

  public Map<Serializable, List<MnxMetaboliteCrossreferenceEntity>> parseMetaboliteCrossReferences() throws FileNotFoundException, IOException {
    Map<Serializable, List<MnxMetaboliteCrossreferenceEntity>> xrefMap = new TreeMap<> ();
    
    BufferedReader br = new BufferedReader(new FileReader(crossreferenceCsvFile));
    String line = null;
    while ( (line = br.readLine()) != null) {
      if (line.trim().charAt(0) != '#') {
        try {
          parseMetaboliteCrossReference(xrefMap, line);
        } catch (ArrayIndexOutOfBoundsException e) {
          br.close();
          System.err.println(line);
          throw e;
        }
      }
    }

    br.close();
    return xrefMap;
  }
  
  public static int INDEX_ENTRY = 0;
  public static int INDEX_NAME = 1;
  public static int INDEX_FORMULA = 2;
  public static int INDEX_CHARGE = 3;
  public static int INDEX_MASS = 4;
  public static int INDEX_INCHI = 5;
  public static int INDEX_SMILES = 6;
  public static int INDEX_ORIGINAL_REFERENCE = 7;
  public static int INDEX_INCHI_KEY = 8;

  public MnxMetaboliteEntity parseMetabolite(String record) {
    /* Example Record
     * ENTRY   | NAME     | FORMULA | CHARGE | MASS     | InChI                  | SMILES                 | ORIGINAL REFERENCE
     * MNXM1   | H(+)     | H       | 1      | 1.008    | InChI=1S/p+1           | [H+]                   | chebi:15378
     * MNXM328 | agmatine | C5H16N4 | 2      | 132.2062 | InChI=1S/C5H14N4/c6... | NC(=[NH2+])NCCCC[NH3+] | chebi:58145 | QYPPJABKJHAVHS-UHFFFAOYSA-P
     * MNXM329 | atROL    | NA      | NA     |          |                        | NA                     | reactome:2395761 | NA
     */
    String[] values = record.split("\t");
    
    String chargeStr = DataUtils.getArray(values, INDEX_CHARGE);
    String smilesStr = DataUtils.getArray(values, INDEX_SMILES);
    String inchikStr = DataUtils.getArray(values, INDEX_INCHI_KEY);
    String inchiStr  = DataUtils.getArray(values, INDEX_INCHI);
    String origsrStr = DataUtils.getArray(values, INDEX_ORIGINAL_REFERENCE);
    Integer charge = null;
    if (!DataUtils.empty(chargeStr) && !chargeStr.equals("NA")) {
      charge = Integer.parseInt(chargeStr);
    }
    if (!DataUtils.empty(smilesStr) && smilesStr.equals("NA")) {
      smilesStr = null;
    }
    //		System.out.println(values.length);
    MnxMetaboliteEntity cpd = new MnxMetaboliteEntity();
    
    
    cpd.setCharge(charge);
    String mass = DataUtils.getArray(values, INDEX_MASS, null);
    Double massValue;
    try {
      massValue = mass.trim().length() > 0 ? Double.parseDouble(mass) : null;
    } catch (NumberFormatException nfEx) {
      logger.warn("invalid mass [{}] - {}", mass, record);
      massValue = null;
    }
    
    cpd.setEntry(DataUtils.trim(values[INDEX_ENTRY]));
    cpd.setName(DataUtils.trim(values[INDEX_NAME]));
    cpd.setFormula(DataUtils.trim(values[INDEX_FORMULA]));
    cpd.setMass(massValue);
    cpd.setInChI(DataUtils.trim(inchiStr));
    cpd.setSmiles(DataUtils.trim(smilesStr));
    cpd.setInchikey(DataUtils.trim(inchikStr));
    cpd.setOriginalSource(DataUtils.trim(origsrStr));
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
  public MnxMetaboliteEntity getMetaboliteById(Serializable id) {
    if(this.bulkAccess && cachedData.isEmpty()) {
      try {
        for (MnxMetaboliteEntity cpd: this.parseMetabolites()) {
          this.cachedData.put(cpd.getEntry(), cpd);
        }
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }

    if (cachedData.containsKey(id)) {
      MnxMetaboliteEntity cpd = cachedData.get(id);
      for (MnxMetaboliteCrossreferenceEntity xref: this.xrefMap.get(id)) {
        xref.setMnxMetaboliteEntity(cpd);
        cpd.addCrossReference(xref);
      }

      return cpd;
    }

    if (!this.entrySeekPosition.containsKey(id)) {
      this.getAllMetaboliteIds();
    }
    MnxMetaboliteEntity cpd = null;
    try {
      if (this.entrySeekPosition.containsKey(id.toString())) {
        String record = this.seekFileLine(metaboliteCsvFile, this.entrySeekPosition.get(id));
        cpd = this.parseMetabolite(record);
        for (MnxMetaboliteCrossreferenceEntity xref: this.xrefMap.get(id)) {
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
  public MnxMetaboliteEntity saveMetabolite(
      MnxMetaboliteEntity metabolite) {
    throw new RuntimeException("Unsupported Operation");
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
  public Serializable save(MnxMetaboliteEntity entity) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public Serializable saveMetabolite(Object entity) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public MnxMetaboliteEntity getMetaboliteByEntry(String entry) {
    if(this.bulkAccess && cachedData.isEmpty()) {
      try {
        for (MnxMetaboliteEntity cpd: this.parseMetabolites()) {
          this.cachedData.put(cpd.getEntry(), cpd);
        }
      } catch (IOException e) {
        logger.error("{}", e.getMessage());
      }
    }

    if (cachedData.containsKey(entry)) {
      MnxMetaboliteEntity cpd = cachedData.get(entry);
      for (MnxMetaboliteCrossreferenceEntity xref: this.xrefMap.get(entry)) {
        xref.setMnxMetaboliteEntity(cpd);
        cpd.addCrossReference(xref);
      }

      return cpd;
    }

    if (!this.entrySeekPosition.containsKey(entry)) {
      this.getAllMetaboliteIds();
    }
    MnxMetaboliteEntity cpd = null;
    try {
      if (this.entrySeekPosition.containsKey(entry.toString())) {
        String record = this.seekFileLine(metaboliteCsvFile, this.entrySeekPosition.get(entry));
        cpd = this.parseMetabolite(record);
        for (MnxMetaboliteCrossreferenceEntity xref: this.xrefMap.get(entry)) {
          xref.setMnxMetaboliteEntity(cpd);
          cpd.addCrossReference(xref);
        }
      }
    } catch (IOException e) {
      logger.error("{}", e.getMessage());
    }
    return cpd;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    List<String> idList = new ArrayList<> ();
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

}
