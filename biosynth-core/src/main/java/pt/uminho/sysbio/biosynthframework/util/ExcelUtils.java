package pt.uminho.sysbio.biosynthframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;

import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.MatrixS;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.MatrixSFactory;

public class ExcelUtils {
  
  public static Dataset<String, String, Object> readDataSetFromSheet(Sheet sheet, boolean header, int key) {
    Dataset<String, String, Object> result = new Dataset<>();
    
    Map<Integer, String> headerLabel = new HashMap<>();
    
    int rowStart = sheet.getFirstRowNum();
    if (header) {
      Row row = sheet.getRow(rowStart);
      int colStart = row.getFirstCellNum();
      int colEnd = row.getLastCellNum();
      for (int colIndex = colStart; colIndex <= colEnd; colIndex++) {
        String label = ExcelUtils.getString(row, colIndex);
        if (DataUtils.empty(label)) {
          label = String.format("column_%d", colIndex);
        }
        headerLabel.put(colIndex, label);
      }
      rowStart++;
    }
    
    int rowEnd = sheet.getLastRowNum();
    Set<String> keys = new HashSet<>();
    for (int rowIndex = rowStart; rowIndex <= rowEnd; rowIndex++) {
//      logger.trace("reading row: {}", rowIndex);
      Row row = sheet.getRow(rowIndex);
      int colStart = row.getFirstCellNum();
      int colEnd = row.getLastCellNum();
      String k = ExcelUtils.getString(row, key);
      for (int colIndex = colStart; colIndex <= colEnd; colIndex++) {
        if (colIndex != key) {
//          Cell cell = row.getCell(colIndex);
          Object data = ExcelUtils.get(row, colIndex);
          if (!headerLabel.containsKey(colIndex)) {
            headerLabel.put(colIndex, String.format("column_%d", colIndex));
          }
          String field = headerLabel.get(colIndex);
          result.add(k, field, data);
          
        } else {
          if (!keys.add(k)) {
            System.out.println("DIPLICATE KEY ");
          }
        }
      }
    }
    
    return result;
  }
  
  public static Dataset<String, String, Object> readDataSetXls(
      String file, 
      String sheetName, 
      boolean header, 
      int key, String...columnNames) {
    
    Dataset<String, String, Object> result = null;
    try (InputStream is = new FileInputStream(file)) {
      result = readDataSetXls(is, sheetName, header, key, columnNames);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }
  
  public static Dataset<String, String, Object> readDataSetXls(
      InputStream is, 
      String sheetName, 
      boolean header, 
      int key, String...columnNames) throws IOException {
    
    Dataset<String, String, Object> result = null;
    
    final HSSFWorkbook workbook = new HSSFWorkbook(is);
    HSSFSheet sheet = workbook.getSheet(sheetName);
    if (sheet == null) {
      //        logger.warn("sheet not found: {}, sheet: {}", file, sheetName);
      workbook.close();
      return null;
    }
    result = readDataSetFromSheet(sheet, header, key);
    workbook.close();

    return result;
  }
  
  public static Dataset<String, String, Object> readDataSetXlsx(
      String file, 
      String sheetName, 
      boolean header, 
      int key, String...columnNames) {
    
    Dataset<String, String, Object> result = null;
    
    InputStream is = null;
    try {
      final XSSFWorkbook workbook;
      File f = new File(file);
      is = new FileInputStream(f);
      workbook = new XSSFWorkbook(is);
      XSSFSheet sheet = workbook.getSheet(sheetName);
      if (sheet == null) {
//        logger.warn("sheet not found: {}, sheet: {}", file, sheetName);
        workbook.close();
        return null;
      }
      
      result = readDataSetFromSheet(sheet, header, key);
      
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
  
  public static Dataset<String, String, Object> getTable(      String file, 
      String sheetName, 
      boolean header, 
      int key, String...columnNames) {
    return readDataSetXlsx(file, sheetName, true, key, columnNames);
  }
  
  public static String getString(Row row, int cellnum) {
    Cell cell = row.getCell(cellnum);
    if (cell != null) {
      return cell.getStringCellValue(); 
    } else {
      return null;
    }
  }
  
  public static Object get(Row row, int cellnum) {
    Cell cell = row.getCell(cellnum);
    if (cell != null) {
      switch (cell.getCellTypeEnum()) {
        case BOOLEAN: return cell.getBooleanCellValue();
        case STRING: return cell.getStringCellValue();
        case NUMERIC: return cell.getNumericCellValue();
        default: 
          System.out.println(cell.getCellTypeEnum());
          return getString(row, cellnum);
      } 
    } else {
      return null;
    }
  }
  
  public static Object get(Cell cell) {
    if (cell != null) {
      switch (cell.getCellTypeEnum()) {
        case BOOLEAN: return cell.getBooleanCellValue();
        case STRING: return cell.getStringCellValue();
        case NUMERIC: return cell.getNumericCellValue();
        case BLANK: return null;
        default:
          System.out.println(cell.getCellTypeEnum());
          return null;
      } 
    } else {
      return null;
    }
  }
  
  public static void writeToWorkbook(String path, Dataset<String, String, Object> dataset, String yAxis, String sheetName) {
    final XSSFWorkbook workbook;
    File f = new File(path);
    
    OutputStream os;
    InputStream is;

    
    try {
      
      if (f.exists()) {
        is = new FileInputStream(f);
        workbook = new XSSFWorkbook(is);
        is.close();
      } else {
        workbook = new XSSFWorkbook(XSSFWorkbookType.XLSX);
      }
      
      
      writeDataset(workbook, dataset, yAxis, sheetName);
      
      os = new FileOutputStream(f);
      workbook.write(os);
      workbook.close();
      os.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void writeDataset(Workbook workbook, List<List<Object>> dataset, String yAxis, String sheetName) {
    Sheet sheet = workbook.createSheet(sheetName);
    for (int i = 0; i < dataset.size(); i++) {
      Row row = sheet.createRow(i);
      List<Object> rowData = dataset.get(i);
      for (int j = 0; j < rowData.size(); j++) {
        Cell cell = row.createCell(j);
        String value = rowData.get(j).toString();
        if ("true".equals(value) || "false".equals(value)) {
          cell.setCellValue(Boolean.parseBoolean(value));
        } else if (NumberUtils.isNumber(value)) {
          cell.setCellValue(Double.parseDouble(value));
        } else {
          cell.setCellValue(value);
        }
      }
    }
  }
  
  public static void writeDataset(Workbook workbook, Dataset<String, String, Object> dataset, String yAxis, String sheetName) {
    Sheet sheet = workbook.createSheet(sheetName);
    MatrixSFactory<String, String, Object> f = new MatrixSFactory<>();
    MatrixS m = f.withData(dataset.dataset)
                 .withYAxisLabel(yAxis).build();
    
    {
      Row row = sheet.createRow(0);
      Cell cell = row.createCell(0);
      cell.setCellValue(yAxis);
      for (int i = 0; i < m.y; i++) {
        cell = row.createCell(i + 1);
        cell.setCellValue(m.yLabel[i]);
      }
    }
    
    for (int i = 0; i < m.x; i++) {
      Row row = sheet.createRow(i + 1);
      Cell cell = row.createCell(0);
      cell.setCellValue(m.xLabel[i]);
      for (int j = 0; j < m.y; j++) {
        cell = row.createCell(j + 1);
        String value = m.matrix[i][j];
        if ("true".equals(value) || "false".equals(value)) {
          cell.setCellValue(Boolean.parseBoolean(value));
        } else if (NumberUtils.isNumber(value)) {
          cell.setCellValue(Double.parseDouble(m.matrix[i][j]));
        } else {
          cell.setCellValue(m.matrix[i][j]);
        }
      }
    }
//    dataset.dataset.keySet();
  }
}
