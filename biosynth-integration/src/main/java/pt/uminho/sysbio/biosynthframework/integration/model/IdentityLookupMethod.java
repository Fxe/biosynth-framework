package pt.uminho.sysbio.biosynthframework.integration.model;



public class IdentityLookupMethod implements LookupMethod {

  @Override
  public String lookup(EntryPattern pattern) {
    return pattern.trim;
  }
}
