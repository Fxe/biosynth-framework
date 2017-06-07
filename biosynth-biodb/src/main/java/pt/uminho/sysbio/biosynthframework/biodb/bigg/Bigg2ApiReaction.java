package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bigg2ApiReaction {
  public String reaction_string;
  public String bigg_id;
  public String name;
  public Boolean pseudoreaction;
  public List<String> old_identifiers = new ArrayList<> ();
  public List<Map<String, Object>> metabolites = new ArrayList<> ();
  public List<Map<String, Object>> database_links = new ArrayList<> ();
  public List<Map<String, String>> models_containing_reaction = new ArrayList<> ();
}
