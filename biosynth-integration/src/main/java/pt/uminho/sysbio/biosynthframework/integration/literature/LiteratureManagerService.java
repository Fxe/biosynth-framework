package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
  private final int LIT_TITLE_INDEX    = 5;
  
  private File root;
  private File indexFile;
  
  private Map<String, LiteratureEntity> records = new HashMap<>();
  private Map<String, LiteratureEntity> pmidToRecord = new HashMap<>();
  
  public Map<String, Function<String, List<String>>> linkScanner = new HashMap<>();
  
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
    String title = getString(row.getCell(LIT_TITLE_INDEX));
    
    record.setDoiEntry(doi);
    record.setPubmedEntry(pubmid);
    record.setJournal(journal);
    record.setDescription(title);
    
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
  
  public LiteratureEntity getLiteratureRecordByPubmedId(String pmid) {
    return this.pmidToRecord.get(pmid);
  }
  
  public void getSupplementaryMaterial(String id) {
    
  };
  
  public void fetchSupplementaryMaterial(String id) {
    
  };
  
  public Set<String> getAllLiteratureEntries() {
    return new HashSet<>(this.records.keySet());
  }
  
  public LiteratureEntity getLiteratureByEntry(String e) {
    return this.records.get(e);
  }
}
