package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class ModelSeedSubsystem extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;
  
  public String id;
  public String type;
  
  @JsonProperty("class")
  public String sclass;
  
  public String subclass;
  public String name;
  public List<String> roles = new ArrayList<> ();
  public List<String> reactions = new ArrayList<> ();
  
  @JsonProperty("id")
  public void setEntry(String entry) {
    this.entry = entry;
    this.id = entry;
  };
  
  @Override
  public void setName(String name) {
    this.name = name;
    super.setName(name);
  }
  
  @Override
  public void setSource(String source) {
    this.source = source;
    super.setSource(source);
  }
  
  @JsonIgnore
  @Override
  public void setId(Long id) {
    super.setId(id);
  } 
}
