package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class RoleStandardizeFunction implements Function<String, Set<String>> {

  private final String SPLIT_REGEX = " @ | / |; ";
  //private final String SPLIT_REGEX = null;

  @Override
  public Set<String> apply(String i) {
    Set<String> functions = new HashSet<>();
    String n = i.trim().toLowerCase();
    if (SPLIT_REGEX != null) {
      for (String f : n.split(SPLIT_REGEX)) {
        if (!DataUtils.empty(f)) {
          functions.add(f.trim());          
        }
      }  
    } else {
      functions.add(n);
    }


    return functions;
  }

}
