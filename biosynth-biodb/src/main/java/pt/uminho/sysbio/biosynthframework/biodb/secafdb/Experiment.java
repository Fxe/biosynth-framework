package pt.uminho.sysbio.biosynthframework.biodb.secafdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Organism;

public class Experiment {
  public String name;
  public String description;
  public String growthRate;
  public String genotype;
  public String source;
  public String medium;
  
  public Map<String, Object> extra = new HashMap<>();
  
  public Organism organism;
  
  public List<Map<String, String>> reactionFlux = new ArrayList<>();
  public Map<String, Double[]> out = new HashMap<>();
  public Map<String, Double[]> in = new HashMap<>();
}
