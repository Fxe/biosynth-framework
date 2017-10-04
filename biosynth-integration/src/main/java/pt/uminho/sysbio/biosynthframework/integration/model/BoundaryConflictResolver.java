package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashSet;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.integration.model.ConflictResolver;

public class BoundaryConflictResolver implements ConflictResolver {

  public Set<String> boundary = new HashSet<> ();

  @Override
  public Set<String> resolve(String spi, Set<String> spiSet) {
//    System.out.println(spi + " " + spiSet);
    if (spiSet != null && spiSet.size() > 1 && !boundary.contains(spi)) {
      Set<String> k = new HashSet<> (spiSet);
      k.removeAll(boundary);
      return k;
    }
    return spiSet;
  }
}
