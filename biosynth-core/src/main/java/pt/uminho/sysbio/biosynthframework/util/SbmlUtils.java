package pt.uminho.sysbio.biosynthframework.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbmlUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(SbmlUtils.class);
  
  public static String extractString(String string) {
    String[] split = string.replace("</p>", "").split(":");
    if (split.length > 1) {
      String formula = split[1].trim();
      if (!formula.isEmpty()) {
        return formula;
      }
    }
    
    return null;
  }
  
  public static Map<String, String> parseNotes(List<String> notes) {
    Map<String, String> data = new HashMap<> ();
    Map<String, String> fields = new HashMap<> ();
    fields.put("FORMULA:", "formula");
    fields.put("CHARGE:", "charge");
    fields.put("EQUATION:", "equation");
    fields.put("GPR_ASSOCIATION:", "gene_association");
    fields.put("GENE_ASSOCIATION:", "gene_association");
    fields.put("GENE ASSOCIATION:", "gene_association");
    fields.put("GENE_LIST:", "gene_list");
    fields.put("PROTEIN_ASSOCIATION:", "protein_association");
    fields.put("SUBSYSTEM:", "subsystem");
    fields.put("PROTEIN_CLASS:", "protein_class");
    fields.put("CONFIDENCE LEVEL:", "confidence_level");
    fields.put("EC NUMBER:", "ec_number");
    fields.put("EC_NUMBER:", "ec_number");
    fields.put("EC:", "ec_number");
    fields.put("AUTHORS:", "authors");
    fields.put("COMPARTMENT:", "compartment");
    fields.put("KEGG ID:", "kegg");
    fields.put("KEGG_RID:", "kegg");
    fields.put("PUBCHEM ID:", "pubchem");
    fields.put("CHEBI ID:", "chebi");
    
    for (String line : notes) {
      if (line.replaceAll(" ", "").equals("<p></p>")) {
        logger.debug("skipped empty paragraph");
      } else {
        boolean detected = false;
        for (String field : fields.keySet()) {
          if (line.toUpperCase().contains(field)) {
            detected = true;
            String str = extractString(line);
            if (str != null) {
              if (data.put(fields.get(field), str) != null) {
                logger.warn("duplicate field {} -> {}", fields.get(field), field);
              }
            }
          }
        }
        
        if (!detected) {
          logger.warn("Unknown field: {}", line);
        }
      }

    }
    
    return data;
  }
}
