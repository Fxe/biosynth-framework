package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.List;
import java.util.Map;

public class EntrezSearchResult {
  public long Count;
  public int RetMax;
  public int RetStart;
  public String TranslationSet;
  public String QueryTranslation;
  
  public Map<String, Object> TranslationStack;
  public List<Long> IdList;
}
