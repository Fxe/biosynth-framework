package pt.uminho.sysbio.biosynthframework.integration.model;

import org.apache.commons.lang3.math.NumberUtils;

public class PrefixNumberSequenceLookupMethod implements LookupMethod {
  
  public String prefix = "";
  
  public PrefixNumberSequenceLookupMethod(String prefix) {
    this.prefix = prefix;
  }
  
  @Override
  public String lookup(EntryPattern pattern) {
    String base = pattern.trim;
    
    if (base != null && NumberUtils.isDigits(base)) {
      String prevPrefix = "";
      if (pattern.prefixes.size() > 0) {
        prevPrefix = pattern.prefixes.get(pattern.prefixes.size() - 1);
      }
      if (prevPrefix.toLowerCase().endsWith("cpd")) {
        base = "cpd" + base;
      }
    }
    
    return base;
  }
}
