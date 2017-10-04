package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.Set;

public interface ConflictResolver {
  public Set<String> resolve(String spi, Set<String> spiSet);
}
