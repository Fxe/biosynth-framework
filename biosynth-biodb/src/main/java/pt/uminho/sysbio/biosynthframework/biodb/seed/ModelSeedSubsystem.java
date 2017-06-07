package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelSeedSubsystem {
  public String id;
  public String type;
  
  @JsonProperty("class")
  public String sclass;
  
  public String subclass;
  public String name;
  public List<String> roles = new ArrayList<> ();
  public List<String> reactions = new ArrayList<> ();
}
