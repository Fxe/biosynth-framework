package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.LiteratureEntity;
import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;
import pt.uminho.sysbio.biosynthframework.util.BiosIOUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

/**
 * Manages supplementary materials of the publications
 * @author Filipe Liu
 *
 */
public class LiteratureManagerService {
  
  private static Logger logger = LoggerFactory.getLogger(LiteratureManagerService.class);
  
  private int SUP_FOLDER_INDEX = 0;
  private int SUP_FILE_INDEX   = 1;
  private int SUP_URL_INDEX    = 2;
  private int SUP_MD5_INDEX    = 3;
  private int SUP_SIZE_INDEX   = 4;
//  private int cellTypeIndex   = 5;
//  private int cellTagIndex    = 6;
//  private int cellNameIndex   = 7;

  private final int LIT_FOLDER_INDEX = 0;
  private final int LIT_PUBMID_INDEX = 1;
  private final int LIT_DOI_INDEX    = 2;
  private final int LIT_JOURNAL_INDEX    = 3;
  private final int LIT_JOURNAL_A_INDEX  = 4;
  private final int LIT_TITLE_INDEX    = 5;
  
  private File root;
  private File indexFile;
  
  private Map<String, LiteratureEntity> records = new HashMap<>();
  private Map<String, LiteratureEntity> pmidToRecord = new HashMap<>();
  
  public Map<String, Function<String, List<SupplementaryMaterialEntity>>> linkScanner = new HashMap<>();
  
  @Autowired
  public LiteratureManagerService(Resource path) {
    try {
      root = path.getFile();
      if (!root.exists() || !root.isDirectory()) {
        throw new IOException("invalid path");
      }
      this.indexFile = new File(root.getAbsolutePath() + "/models.xlsx");
      if (!indexFile.exists() || !indexFile.isFile()) {
        throw new IOException("missing index file: models.xlsx");
      }
      
      logger.info("manager started at: {}", root.getAbsolutePath());
      
      initialize();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    //"Science", "Sci Rep", "Res. Microbiol.", "Proc. Natl. Acad. Sci. U.S.A.", 
    //"Plant Physiol.", "Plant J.", 
    //"Nat. Methods", "Nat. Biotechnol.", 
    //"Mol. Syst. Biol.", "Mol. Ecol.", "Mol Biosyst", "Microb. Cell Fact.", 
    //"Metabolomics", "Metabolites", "J. Theor. Biol.", "J. Biotechnol.", 
    //"J. Biol. Chem.", "J. Bacteriol.", "Integr Biol (Camb)", 
    //"Genome Res.", "Genome Biol.", "Gene", "Front Microbiol", 
    //"Environ. Microbiol.", "Database (Oxford)", "Cell Syst", 
    //"Biotechnol. Bioeng.", 
    //"Biotechnol J", "Biotechnol Biofuels", "Bioresour. Technol.", 
    //"Appl. Microbiol. Biotechnol.", "Appl. Environ. Microbiol.", 
    //"Antonie Van Leeuwenhoek"
    
    //Mol. Syst. Biol.
    //J. Bacteriol.
    //Genome Biol.
    //Microb. Cell Fact.
    //Mol Biosyst
    
    PlosLinkScanner plosLinkScanner = new PlosLinkScanner();
    SpringerLinkScanner springerLinkScanner = new SpringerLinkScanner();
    RscLinkScanner rscLinkScanner = new RscLinkScanner();
    MolSysBioLinkScanner molSysBioLinkScanner = new MolSysBioLinkScanner();
    AsmLinkScanner asmLinkScanner = new AsmLinkScanner();
    linkScanner.put("BMC Genomics", springerLinkScanner);
    linkScanner.put("BMC Microbiol.", springerLinkScanner);
    linkScanner.put("BMC Syst Biol", springerLinkScanner);
    linkScanner.put("Microb. Cell Fact.", springerLinkScanner);
    linkScanner.put("Genome Biol.", springerLinkScanner);
    linkScanner.put("PLoS ONE", plosLinkScanner);
    linkScanner.put("PLoS Comput. Biol.", plosLinkScanner);
    linkScanner.put("J. Bacteriol.", asmLinkScanner);
    linkScanner.put("Mol. Syst. Biol.", molSysBioLinkScanner);
    linkScanner.put("Mol Biosyst", rscLinkScanner);
  }
  
  public void initialize() throws IOException {
    try (InputStream is = new FileInputStream(indexFile)){
      final XSSFWorkbook workbook;
      workbook = new XSSFWorkbook(is);
      Sheet literatureSheet = workbook.getSheet("literature");
      Sheet supplementarySheet = workbook.getSheet("supplementary");
      initializeLiterature(literatureSheet);
      initializeSupplementary(supplementarySheet);
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public String getString(Cell cell) {
    if (cell == null) {
      return null;
    }
    String string = null;

    switch (cell.getCellTypeEnum()) {
    case STRING: 
      string = cell.getStringCellValue(); 
      break;
    case NUMERIC: 
      double cvalue = cell.getNumericCellValue();
      string = String.format("%.0f", cvalue);
      break;
    default: break;
    }
    return string;
  }
  
  public Long getLong(Cell cell) {
    if (cell == null) {
      return null;
    }
    Long l = null;

    switch (cell.getCellTypeEnum()) {
      case NUMERIC: 
        double cvalue = cell.getNumericCellValue();
        l = (long) cvalue;
        break;
      default: break;
    }
    return l;
  }
  
  public void initializeLiterature(Sheet sheet) throws IOException {
    int rowStart = sheet.getFirstRowNum();
    int rowEnd = sheet.getLastRowNum();
//    Row headerRow = sheet.getRow(rowStart);
    for (int rowIndex = rowStart + 1; rowIndex <= rowEnd; rowIndex++) {
      Row row = sheet.getRow(rowIndex);
      LiteratureEntity record = getLiteratureRecord(row);
      if (record.getEntry() != null) {
        if (!this.records.containsKey(record.getEntry())) {
          this.records.put(record.getEntry(), record);
          if (record.getPubmedEntry() != null) {
            this.pmidToRecord.put(record.getPubmedEntry(), record);
          }
        } else {
          logger.warn("Duplicate entry detected. Discarded: {}", record);
        }
      } else {
        logger.warn("No entry assigned. Discarded: {}", record);
      }
    }
  }
  
  public LiteratureEntity getLiteratureRecord(Row row) {
    LiteratureEntity record = new LiteratureEntity();
    String folder = getString(row.getCell(LIT_FOLDER_INDEX));
    String pubmid = getString(row.getCell(LIT_PUBMID_INDEX));
    String doi = getString(row.getCell(LIT_DOI_INDEX));
    String journal = getString(row.getCell(LIT_JOURNAL_INDEX));
    String journal2 = getString(row.getCell(LIT_JOURNAL_A_INDEX));
    String title = getString(row.getCell(LIT_TITLE_INDEX));
    
    record.setDoiEntry(doi);
    record.setPubmedEntry(pubmid);
    record.setJournal(journal);
    record.setDescription(title);
    record.setJournalAbbreviation(journal2);
    
    if (folder != null) {
      record.setFolder(new File(this.root.getAbsoluteFile() + "/" + folder));
    } else if (record.getPubmedEntry() != null) {
      record.setFolder(new File(this.root.getAbsoluteFile() + "/" + pubmid));
    } else {
      logger.warn("no supplementary material folder for: {}", record);
    }
    
    if (record.getFolder() != null) {
      record.setEntry(record.getFolder().getName());
    }
    
    return record;
  }
  
  public SupplementaryMaterialEntity getSupplementaryMaterialRecord(Row row) {
    String folder = getString(row.getCell(SUP_FOLDER_INDEX));
    String file = getString(row.getCell(SUP_FILE_INDEX));
    String url = getString(row.getCell(SUP_URL_INDEX));
    String md5 = getString(row.getCell(SUP_MD5_INDEX));
    Long size = getLong(row.getCell(SUP_SIZE_INDEX));
    
    SupplementaryMaterialEntity record = new SupplementaryMaterialEntity();
    record.setEntry(file);
    record.setSize(size);
    record.setMd5(md5);
    
    if (url != null) {
      record.setUrl(url);
    }
    
    if (folder != null) {
      record.setFolder(new File(this.root + "/" + folder));
    }
    
    record.setFile(file);
    
    return record;
  }
  
  public void initializeSupplementary(Sheet sheet) throws IOException {
    int rowStart = sheet.getFirstRowNum();
    int rowEnd = sheet.getLastRowNum();
//    Row headerRow = sheet.getRow(rowStart);
    for (int rowIndex = rowStart + 1; rowIndex <= rowEnd; rowIndex++) {
      Row row = sheet.getRow(rowIndex);
      SupplementaryMaterialEntity supplementaryMaterial = getSupplementaryMaterialRecord(row);
      if (supplementaryMaterial.getFolder() != null) {
        String folder = supplementaryMaterial.getFolder().getName();
        if (this.records.containsKey(folder)) {
          this.records.get(folder).getSupplementaryMaterials().add(supplementaryMaterial);
        } else {
          logger.warn("No literature record. Discard supplementary material record - {}, {}.", folder, supplementaryMaterial.getUrl());
        }
      } else {
        logger.warn("No folder. Discard supplementary material record - {}.", supplementaryMaterial.getUrl());
      }
    }
  }

  public File getFolder(Row row) {
    String folderName = null;
    Cell cell = row.getCell(SUP_FOLDER_INDEX);
    if (cell != null) {
      switch (cell.getCellTypeEnum()) {
        case STRING: 
          folderName = cell.getStringCellValue(); 
          break;
        case NUMERIC: 
          double cvalue = cell.getNumericCellValue();
          folderName = String.format("%.0f", cvalue);
          break;
        default: break;
      }
    }
    if (folderName == null) {
      return null;
    }
    return new File(this.root.getAbsolutePath() + "/" + folderName);
  }
  
  
  public LiteratureEntity getLiteratureByEntry(String e) {
    LiteratureEntity lit = this.records.get(e);
    if (lit.getSupplementaryMaterials().isEmpty()) {
      List<SupplementaryMaterialEntity> l = listSupplementaryMaterials(lit);
      lit.getSupplementaryMaterials().addAll(l);
    }
    return lit;
  }
  
  public LiteratureEntity getLiteratureRecordByPubmedId(String pmid) {
    LiteratureEntity lit = this.pmidToRecord.get(pmid);
    if (lit.getSupplementaryMaterials().isEmpty()) {
      List<SupplementaryMaterialEntity> l = listSupplementaryMaterials(lit);
      lit.getSupplementaryMaterials().addAll(l);
    }
    return lit;
  }
  
  public List<SupplementaryMaterialEntity> listSupplementaryMaterials(LiteratureEntity lit) {
    File folder = lit.getFolder();
    Set<String> files = new HashSet<>();
    if (folder.exists() && folder.isDirectory()) {
      BiosIOUtils.folderScan(files, folder, new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isFile();
        }
      });
    }
    
    List<SupplementaryMaterialEntity> supplementaryMaterials = new ArrayList<>();
    for (String f : files) {
      SupplementaryMaterialEntity sup = getProperties(new File(f));
      supplementaryMaterials.add(sup);
    }
    
    return supplementaryMaterials;
  }
  
  public InputStream getSupplementaryMaterialStream(LiteratureEntity lit, String sup) {
    File file = new File(lit.getFolder().getAbsolutePath() + "/" + sup);
    if (file.exists() && file.isFile()) {
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        logger.error("this should never happend - {}", e.getMessage());
      }
    }
    return null;
  }
  
  public List<SupplementaryMaterialEntity> getSupplementaryMaterial(LiteratureEntity lit) {
    return getSupplementaryMaterial(lit.getDoiEntry(), lit.getJournalAbbreviation(), lit.getFolder());
  }
  
  public List<SupplementaryMaterialEntity> getSupplementaryMaterial(String doi, String journal, File folder) {
    List<SupplementaryMaterialEntity> result = new ArrayList<> ();
    List<SupplementaryMaterialEntity> urls = fetchSupplementaryMaterialUrls(doi, journal);
    if (urls == null) {
      return null;
    }
    for (SupplementaryMaterialEntity sup : urls) {
      SupplementaryMaterialEntity sm = getSupplementaryMaterial(folder, sup.getUrl().toExternalForm());
      if (sm != null) {
        result.add(sm);
      }
    }
    return result;
  }
  
  public SupplementaryMaterialEntity getSupplementaryMaterial(LiteratureEntity lit, String url) {    
    return getSupplementaryMaterial(lit.getFolder(), url);
  };
  
  public SupplementaryMaterialEntity getSupplementaryMaterial(File folder, String url) {
    try {
      URI uri = new URI(url);
      if (!folder.exists()) {
        folder.mkdirs();
        logger.info("created {}", folder);
      }
      String filename = getFileNameFromString(url);
      File result = LiteratureManagerService.download(uri, filename, folder);
      if (result.exists()) {
        SupplementaryMaterialEntity entity = getProperties(result);
        entity.setLiterature(true);
        entity.setUrl(url);
        entity.setFolder(new File(folder.getAbsolutePath()));
        return entity;
      }
      
      return null;
    } catch (IOException | URISyntaxException e) {
      logger.warn("fail {}", e.getMessage());
    }
    
    return null;
  };
  
  public void fetchSupplementaryMaterial(LiteratureEntity lit) {
    
  };
  
  public List<SupplementaryMaterialEntity> fetchSupplementaryMaterialUrls(LiteratureEntity entity) {
    if (entity == null) {
      logger.warn("null entity");
      return null;
    }
    if (DataUtils.empty(entity.getDoiEntry())) {
      logger.warn("LiteratureEntity without DOI - {}", entity.getEntry());
      return null;
    }
    if (DataUtils.empty(entity.getJournalAbbreviation())) {
      logger.warn("LiteratureEntity without Journal Abbreviation - {}", entity.getEntry());
      return null;
    }
    
    return fetchSupplementaryMaterialUrls(entity.getDoiEntry(), entity.getJournalAbbreviation());
  };
  
  public List<SupplementaryMaterialEntity> fetchSupplementaryMaterialUrls(String doi, String journal) {
    if (linkScanner.containsKey(journal)) {
      Function<String, List<SupplementaryMaterialEntity>> scanner = linkScanner.get(journal);
      try {
        URI uri = new URI("https://doi.org/" + doi);
        String html = BiosIOUtils.download(uri);
        List<SupplementaryMaterialEntity> links = scanner.apply(html);
        return links;
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    } else {
      logger.warn("no scanner for Journal - {}", journal);
      return null;
    }
    
    return null;
  };
  
  private static String DIGEST_ALGORITHM = "MD5";
  
  public void getSupplementaryMaterialEntity() {
    
  }
  
  public SupplementaryMaterialEntity getProperties(File file) {
    SupplementaryMaterialEntity entity = new SupplementaryMaterialEntity();
    
    
    String md5 = null;
    long size = file.length();
    try {
      md5 = BiosIOUtils.digest(DIGEST_ALGORITHM, file);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    entity.setEntry(file.getName());
    entity.setMd5(md5);
    entity.setSize(size);
    entity.setName(file.getName());
    entity.setFile(new File(file.getAbsolutePath()));
    
    return entity;
  }
  
  public static String getFileNameFromString(String str) {
    String[] p = str.split("/");
    str = p[p.length - 1];
    if (str.indexOf('?') > 0) {
      str = str.substring(0, str.indexOf('?'));
    }
    return str;
  }
  
  public static File download(URI uri, String fileName, File folder) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpUriRequest request = new HttpGet(uri);
    HttpResponse response = client.execute(request);
    Header[] headers = response.getAllHeaders();
//    fileName = null;
    
    logger.info("{} -> {}", uri, fileName);
    
    String detectedName = fileName;
    for (Header h : headers) {
//      System.out.println(h.getName() + " -> " + h.getValue());
      if (h.getName().equals("Content-Disposition")) {
        String[] p = h.getValue().split("filename=");
        detectedName = p[1].trim();
      }
      if (h.getName().equals("X-SmartBan-URL")) {
        String s = getFileNameFromString(h.getValue());
        detectedName = s.trim();
      }
    }
    
    detectedName = detectedName.replaceAll("\"", "");
    
    if (fileName == null) {
      fileName = detectedName;
    } else if (!detectedName.equals(fileName)) {
      logger.warn("filename changed {} -> {}", fileName, detectedName);
      fileName = detectedName;
    }
    
    if (DataUtils.empty(fileName)) {
      logger.warn("unable to detect filename");
      return null;
    }
    
    File result = new File(folder.getAbsolutePath() + "/" + fileName);
    logger.warn("{} -> {}", uri, result);
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      InputStream is = entity.getContent();
      FileUtils.copyToFile(is, result);
      is.close();
    }
    
    return result;
  }
  
  public Set<String> getAllLiteratureEntries() {
    return new HashSet<>(this.records.keySet());
  }
  

}
