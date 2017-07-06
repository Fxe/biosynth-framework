package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class BasicTokenizer {
  
  public String singleStaticPrefix = "M_";
  public Set<String> compartments = new HashSet<> ();
  
  public Map<String, EntryPattern> patterns = new HashMap<> ();
  
  public IdPattern generatePattern(Set<String> strings) {
    
    for (String str : strings) {
      patterns.put(str, new EntryPattern(str));
    }
    
    IdPattern p = new IdPattern();
    
    boolean prefixGlobal = true;
    Map<String, Integer> compartmentFreq = new HashMap<> ();
    for (EntryPattern ep : patterns.values()) {
      prefixGlobal = prefixGlobal && ep.splicePrefix(singleStaticPrefix);
      for (String cmp : compartments) {
        if (ep.spliceSuffix("_" + cmp)) {
          CollectionUtils.increaseCount(compartmentFreq, "_" + cmp, 1);
          break;
        }
      }
      System.out.println(ep);
    }
    System.out.println(compartmentFreq);
    
    if (prefixGlobal) {
      p.addPrefix(singleStaticPrefix, null);
    } else {
      List<String> single = new ArrayList<> ();
      p.addPrefix(null, single);
    }
    
    if (compartmentFreq.size() == 1 && 
        compartmentFreq.values().iterator().next() == strings.size()) {
      p.addSuffix(compartmentFreq.keySet().iterator().next(), null);
    } else {
      p.addSuffix(null, compartmentFreq.keySet());
    }
    
    return p;
  }
}
