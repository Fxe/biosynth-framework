package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleStringMatchEngine {
  public Set<String> ids = new HashSet<> ();
  public List<String> prefixes = new ArrayList<> ();
  public Set<String> validIds = new HashSet<>();
  public String prefix;
  
  public SimpleStringMatchEngine(String prefix, String...strip) {
    this.prefix = prefix;
    this.prefixes.addAll(Arrays.asList(strip));
  }
  
  public String stripPrefix(String id) {
    for (String p : prefixes) {
      if (id.startsWith(p)) {
        return id.substring(p.length());
      }
    }
    return id;
  }
  
  public Map<String, String> match() {
    Map<String, String> result = new HashMap<>();
    
    for (String str : ids) {
      String id = str;
      if (!id.startsWith(prefix)) {
        id = stripPrefix(id);
      }
      
      if (id.startsWith(prefix) && id.length() >= 8) {
        id = id.substring(0, 8);
        if (validIds.contains(id)) {
          result.put(str, id);
        }
      }
    }
    
    return result;
  }
}
