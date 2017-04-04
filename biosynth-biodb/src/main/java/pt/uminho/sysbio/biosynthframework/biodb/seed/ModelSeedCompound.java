package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

public class ModelSeedCompound {
  
  public String source;
  public String id;
  public String name;

  public String formula;
  
  public Integer charge;
  public Double deltag;
  public Double deltagerr;
  
  
  public String search_inchi;
  public String structure;
  public Double mass;
  
  
  public String abbreviation;
  
  public Integer is_cofactor;
  public Integer is_core;
  public Integer is_obsolete;
  
  public List<String> names = new ArrayList<> ();
  public List<String> metacyc_aliases = new ArrayList<> ();
  public List<String> searchnames = new ArrayList<> ();
  public List<String> bigg_aliases = new ArrayList<> ();
  public List<String> kegg_aliases = new ArrayList<> ();
  
  public List<String> pka = new ArrayList<> ();
  public List<String> pkb = new ArrayList<> ();
  public List<String> groups = new ArrayList<> ();
  
  @Override
  public String toString() {
    return String.format("[%s]%s %s %s:%s", source, id, formula, abbreviation, name);
  }
}
