package pt.uminho.sysbio.biosynthframework.integration.model;

import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.ExternalReference;

public class Dictionary {
  
  public BMap<String, ExternalReference> dictionary = new BHashMap<>();
  
  public void add(String a, ExternalReference b) {
    dictionary.put(a.trim(), b);
  }
}
