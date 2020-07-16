package pt.uminho.sysbio.biosynthframework.integration.assembler;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.ExternalReference;

public interface AssemblePlugin {
  public Map<String, Object> assemble(Set<ExternalReference> refs);
}
