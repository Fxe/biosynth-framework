package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

public class ModelSeedReaction {
  public String id;
  public String name;
  
  public Double deltagerr;
  public Double deltag;
  
  public String abbreviation;
  public String status;
  public String direction;
  public Integer is_obsolete;
  
  

  
  public String definition;
  public String code;
  public String equation;
  
  public String reversibility;
  

  
  public List<String> compound_ids = new ArrayList<> ();
  public List<String> names = new ArrayList<> ();
  public List<String> searchnames = new ArrayList<> ();
  public List<String> ec_numbers = new ArrayList<> ();
  public List<String> stoichiometry = new ArrayList<> ();
  public List<String> complexes = new ArrayList<> ();
  public List<String> aracyc_pathways = new ArrayList<> ();
  public List<String> plantcyc_pathways = new ArrayList<> ();
  public List<String> metacyc_pathways = new ArrayList<> ();
  public List<String> kegg_pathways = new ArrayList<> ();
  public List<String> ecocyc_pathways = new ArrayList<> ();
  public List<String> hope_pathways = new ArrayList<> ();
  
  
  public List<String> linked_reaction = new ArrayList<> ();
  public List<String> metacyc_aliases = new ArrayList<> ();
  public List<String> kegg_aliases = new ArrayList<> ();
  public List<String> bigg_aliases = new ArrayList<> ();
  public List<String> roles = new ArrayList<> ();
  public List<String> templates = new ArrayList<> ();
  public List<String> subsystems = new ArrayList<> ();
  
  
}
