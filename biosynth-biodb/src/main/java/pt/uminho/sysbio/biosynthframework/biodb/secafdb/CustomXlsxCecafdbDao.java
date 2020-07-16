package pt.uminho.sysbio.biosynthframework.biodb.secafdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Organism;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class CustomXlsxCecafdbDao {
  private static final Logger logger = LoggerFactory.getLogger(CustomXlsxCecafdbDao.class);
  
  public static enum CeCaFBDFields {
    rxnEntry,
    equation,
    flux,
    sourceStr, //unique per experiment
    growth,
    medium, //unique per experiment
    genotype, //unique per experiment
    strain, //unique per experiment
    experimentCase,
    description, //unique per experiment
    dataset,
    in,
    out,
  }
  
  public static String getCellAsString(Row row, int index) {
    Cell cell = row.getCell(index);
    String result = null;
    switch (cell.getCellTypeEnum()) {
      case BOOLEAN:
        boolean b = cell.getBooleanCellValue();
        result = Boolean.toString(b);
        break;
      case NUMERIC:
        double d = cell.getNumericCellValue();
        result = Double.toString(d);
        break;
      case STRING:
        result = cell.getStringCellValue();
        break;
      default:
        break;
    }
    
    return result;
  }
  
  public Map<String, String> mapping = new HashMap<>();
  public Map<String, Experiment> experiments = new HashMap<>();
  public Map<String, Map<CeCaFBDFields, Set<String>>> data = new HashMap<>();
  public Map<String, Organism> orgMap = new HashMap<>();
  public Map<String, String> swap = new HashMap<>();
  
  public static Map<String, Double[]> parseMetaboliteFluxes(String sourceStr, Map<String, String> swap) {
    Map<String, Double[]> result = new HashMap<>();
    
    String[] p = sourceStr.split("\\|");
    for (String str : p) {
      String[] b = str.split("_");
      if (b.length == 1) {
        String single = b[0].trim();
        if (swap.containsKey(single)) {
          b = swap.get(single).split("_");
        }
      }
      if (b.length == 3) {
        String keggEntry = b[0];
        String name = b[1];
        String rangeStr = b[2];
        Double[] range = parse(rangeStr);
        logger.debug("KEGG/Name/Range: {}/{}/{}", keggEntry, name, range);
        result.put(keggEntry, range);
      } else {
        logger.warn("not sure what is this: {}", str);
      }
    }
    return result;
  }
  
  public static Double[] parse(String str) {
    Double lb = null, ub = null;
    if (str.startsWith("[") && str.endsWith("]")) {
      str = str.substring(1, str.length() - 1);
      str = str.replace(',', '.');
      String[] p = str.split(";");
      if (NumberUtils.isParsable(p[0].trim())) {
        lb = Double.parseDouble(p[0].trim());
      } else {
        logger.warn("unknown value for: {}", p[0]);
      }
      if (NumberUtils.isParsable(p[1].trim())) {
        ub = Double.parseDouble(p[1].trim());
      } else {
        logger.warn("unknown value for: {}", p[1]);
      }
    }
    return new Double[]{lb, ub};
  }
  
  public CustomXlsxCecafdbDao(File file) {
    mapping.put("Reaction Name", CeCaFBDFields.rxnEntry.toString());
    mapping.put("Reaction", "equation");
    mapping.put("Relative Flux", "flux");
    mapping.put("Carbon Source", "sourceStr");
    mapping.put("Growth Rate", "growth");
    mapping.put("Culture Medium", "medium");
    mapping.put("Genotype", "genotype");
    mapping.put("Strain", "strain");
    mapping.put("Case", "case");
    mapping.put("Case-specific description", "description");
    mapping.put("Dataset Info", "dataset");
    mapping.put("Mets Consumptiom", "in");
    mapping.put("Mets Production", "out");
    
    InputStream is = null;
    try {
      final XSSFWorkbook workbook;
      File f = new File("/var/biodb/cecafdb/aalao_data.xlsx");
      is = new FileInputStream(f);
      workbook = new XSSFWorkbook(is);
      int sheets = workbook.getNumberOfSheets();
      for (int i = 0; i < sheets; i++) {
        XSSFSheet sheet = workbook.getSheetAt(i);
        String sheetname = workbook.getSheetName(i);
        int rowStart = sheet.getFirstRowNum();
        int rowEnd = sheet.getLastRowNum();
        Row headerRow = sheet.getRow(rowStart);
        Map<String, Integer> aa = new HashMap<>();
        for (int k = 0; k < headerRow.getLastCellNum(); k++) {
          String headerValue = headerRow.getCell(k).getStringCellValue();
          if (mapping.containsKey(headerValue)) {
            headerValue = mapping.get(headerValue);
          } else {
            logger.warn("Not found: {} [{}]", sheetname, headerValue);;
          }
//          System.out.println(headerValue);
          aa.put(headerValue, k);
        }
        for (int rowIndex = rowStart + 1; rowIndex <= rowEnd; rowIndex++) {
          
          Row row = sheet.getRow(rowIndex);
          String caseStr = row.getCell(aa.get("case")).getStringCellValue();
          String rxnEntry = row.getCell(aa.get("rxnEntry")).getStringCellValue();
          String equation = row.getCell(aa.get("equation")).getStringCellValue();
          String growth = getCellAsString(row, 
              aa.get(CeCaFBDFields.growth.toString()));
          String flux = getCellAsString(row, 
              aa.get(CeCaFBDFields.flux.toString()));
//          String flux = row.getCell(aa.get("flux")).getStringCellValue();
          String sourceStr = row.getCell(aa.get("sourceStr")).getStringCellValue();
          
          String medium = row.getCell(aa.get(
              CeCaFBDFields.medium.toString())).getStringCellValue();
          String description = row.getCell(aa.get(
              CeCaFBDFields.description.toString())).getStringCellValue();
          String strain = row.getCell(aa.get(
              CeCaFBDFields.strain.toString())).getStringCellValue();
          String genotype = row.getCell(aa.get(
              CeCaFBDFields.genotype.toString())).getStringCellValue();
          String datasetInfo = row.getCell(aa.get(
              CeCaFBDFields.dataset.toString())).getStringCellValue();
//          System.out.println(row.getCell(0).getStringCellValue());
          String experimentName = String.format("%s %s", sheetname, caseStr);
          
          String inStr = row.getCell(aa.get(
              CeCaFBDFields.in.toString())).getStringCellValue();
          String outStr = row.getCell(aa.get(
              CeCaFBDFields.out.toString())).getStringCellValue();
          if (inStr != null && inStr.trim().equals("-")) {
            inStr = "";
          }
          if (outStr != null && outStr.trim().equals("-")) {
            outStr = "";
          }
          
          if (!orgMap.containsKey(strain)) {
            Organism organism = new Organism();
            organism.scientificName = strain;
            orgMap.put(strain, organism);
          }
          
          if (!experiments.containsKey(experimentName)) {
            Experiment experiment = new Experiment();
            experiment.name = experimentName;
            experiment.growthRate = growth;
            experiment.description = description;
            experiment.medium = medium;
            experiment.genotype = genotype;
            experiment.source = sourceStr;
            experiment.organism = orgMap.get(strain);
            if (!DataUtils.empty(inStr)) {
              Map<String, Double[]> data = parseMetaboliteFluxes(inStr, this.swap);
              experiment.in.putAll(data);
            }
            if (!DataUtils.empty(outStr)) {
              Map<String, Double[]> data = parseMetaboliteFluxes(outStr, this.swap);
              experiment.out.putAll(data);
            }
            experiment.extra.put("dataset_info", datasetInfo);
            experiments.put(experimentName, experiment);
          }
          
          Experiment experiment = experiments.get(experimentName);
          Map<String, String> rxnData = new HashMap<>();
          rxnData.put("rxnEntry", rxnEntry.trim());
          rxnData.put("equation", equation.trim());
          rxnData.put("flux", flux);
          experiment.reactionFlux.add(rxnData);
          
          if (!data.containsKey(experimentName)) {
            data.put(experimentName, new HashMap<CeCaFBDFields, Set<String>>());
            data.get(experimentName).put(CeCaFBDFields.sourceStr, new HashSet<String>());
            data.get(experimentName).put(CeCaFBDFields.description, new HashSet<String>());
            data.get(experimentName).put(CeCaFBDFields.genotype, new HashSet<String>());
            data.get(experimentName).put(CeCaFBDFields.strain, new HashSet<String>());
            data.get(experimentName).put(CeCaFBDFields.medium, new HashSet<String>());
            data.get(experimentName).put(CeCaFBDFields.growth, new HashSet<String>());
          }
          
          data.get(experimentName).get(CeCaFBDFields.sourceStr).add(sourceStr);
          data.get(experimentName).get(CeCaFBDFields.description).add(description);
          data.get(experimentName).get(CeCaFBDFields.genotype).add(genotype);
          data.get(experimentName).get(CeCaFBDFields.strain).add(strain);
          data.get(experimentName).get(CeCaFBDFields.medium).add(medium);
          data.get(experimentName).get(CeCaFBDFields.growth).add(growth);
        }
//        break;
      }
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    

//    for (String k : data.keySet()) {
//      System.out.println(k);
//      Map<String, Set<String>> a = data.get(k);
//      for (String kk : a.keySet()) {
//        System.out.println(a.get(kk).size() + " ");
//      }
//    }
  }
  
  public Set<String> listExperimetns() {
    return new HashSet<>(this.experiments.keySet());
  }
  
  public Experiment getExperimentByEntry(String entry) {
    return this.experiments.get(entry);
  }
}
