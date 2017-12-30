package pt.uminho.sysbio.biosynthframework.biodb.biocyc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BiocycMetaboliteRegulationEntity {
  
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="metabolite_id")
  private BioCycMetaboliteEntity biocycMetaboliteEntity;
  public BioCycMetaboliteEntity getBiocycMetaboliteEntity() { return biocycMetaboliteEntity; }
  public void setBiocycMetaboliteEntity(BioCycMetaboliteEntity biocycMetaboliteEntity) {
      this.biocycMetaboliteEntity = biocycMetaboliteEntity;
  }
  
  private String entry;
  private String orgid;
  private String mode;
  private String regulator;
  private String parent;
  private List<String> reaction = new ArrayList<> ();
  private String protein;
  private String commonName;
  
  private String enzymeEntry;
  private String enzymeOrgid;
  
  /**
   * temporary
   */
  private List<String> proteinSynonym = new ArrayList<>();
  
  
  
  
  
  public List<String> getProteinSynonym() {
    return proteinSynonym;
  }
  public void setProteinSynonym(List<String> proteinSynonym) {
    this.proteinSynonym = proteinSynonym;
  }
  public List<String> getReaction() { return reaction;}
  public void setReaction(List<String> reaction) { this.reaction = reaction;}
  
  public String getProtein() { return protein;}
  public void setProtein(String protein) { this.protein = protein;}
  
  public String getCommonName() { return commonName;}
  public void setCommonName(String commonName) { this.commonName = commonName;}
  
  
  
  public String getEntry() { return entry;}
  public void setEntry(String entry) { this.entry = entry;}
  
  public String getOrgid() { return orgid;}
  public void setOrgid(String orgid) { this.orgid = orgid;}
  
  public String getMode() { return mode;}
  public void setMode(String mode) { this.mode = mode;}
  
  public String getRegulator() { return regulator;}
  public void setRegulator(String regulator) { this.regulator = regulator;}
  
  public String getParent() { return parent;}
  public void setParent(String parent) { this.parent = parent;}
  
  
  
  public String getEnzymeEntry() {
    return enzymeEntry;
  }
  public void setEnzymeEntry(String enzymeEntry) {
    this.enzymeEntry = enzymeEntry;
  }
  public String getEnzymeOrgid() {
    return enzymeOrgid;
  }
  public void setEnzymeOrgid(String enzymeOrgid) {
    this.enzymeOrgid = enzymeOrgid;
  }
  
  
  
}
