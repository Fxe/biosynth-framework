package pt.uminho.sysbio.biosynthframework.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonModel {
  
  public static class JsonReaction {
    public String id;
    public String name;
    public Map<String, Double> metabolites = new HashMap<>();
    
    public Double lower_bound;
    public Double upper_bound;
    public Double objective_coefficient;
    public String subsystem;
    public String gene_reaction_rule;
    public String protein;
    public String ecn;
    public String equation;
    public Map<String, String> notes = new HashMap<>();
  }
  
  public static class JsonMetabolite {
    public String id;
    public String name;
    public String compartment;
    public String formula;
    public Map<String, String> notes = new HashMap<>();
  }
  
  public static class JsonGene {
    public String id;
    public String name;
    public Map<String, String> notes = new HashMap<>();
  }
  
  public String id;
  public String version;
  public Map<String, String> compartments = new HashMap<>();
  public List<JsonReaction> reactions = new ArrayList<>();
  public List<JsonMetabolite> metabolites = new ArrayList<>();
  public List<JsonGene> genes = new ArrayList<>();
  public Map<String, String> notes = new HashMap<>();
}
