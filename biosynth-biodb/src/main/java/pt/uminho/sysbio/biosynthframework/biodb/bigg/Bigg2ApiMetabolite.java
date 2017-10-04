package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bigg2ApiMetabolite {
  public List<String> formulae;
  public String bigg_id;
  public String name;
  public List<Long> charges;
  public List<String> old_identifiers = new ArrayList<> ();
  public List<Map<String, Object>> compartments_in_models = new ArrayList<> ();
  public List<Map<String, List<Map<String, Object>>>> database_links = new ArrayList<> ();
  
//  public List<Map<String, Object>> metabolites = new ArrayList<> ();
  
//  public List<Map<String, String>> models_containing_reaction = new ArrayList<> ();
}
