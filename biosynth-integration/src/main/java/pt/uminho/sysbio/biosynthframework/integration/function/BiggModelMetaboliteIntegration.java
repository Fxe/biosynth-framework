package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiggModelMetaboliteIntegration implements Function<Set<String>, Set<Set<String>>> {
  
  private static final Logger logger = LoggerFactory.getLogger(BiggModelMetaboliteIntegration.class);
  
  private boolean strict = false;
  
  public BiggModelMetaboliteIntegration(boolean strict) {
    this.strict = strict;
  }
  
  @Override
  public Set<Set<String>> apply(Set<String> t) {
    Map<String, Set<String>> mapping = new HashMap<>();
    for (String s : t) {
      String cpdEntry = s.replace("M_", "");
      int li = cpdEntry.lastIndexOf('_');
      cpdEntry = cpdEntry.substring(0, li);
      logger.trace("transform: [{}] -> [{}]", s, cpdEntry);
      if (!mapping.containsKey(cpdEntry)) {
        mapping.put(cpdEntry, new HashSet<String>());
      }
      mapping.get(cpdEntry).add(s);
    }
    
    return new HashSet<>(mapping.values());
  }
}
