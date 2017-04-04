package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.Charge;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="modelseed_metabolite")
public class ModelSeedMetaboliteEntity extends GenericMetabolite {

  private static final long serialVersionUID = 1L;
  
  @MetaProperty
  @Column(name="abbreviation") 
  private String abbreviation;
  
  @MetaProperty
  @Column(name="seed_source") 
  private String seedSource;
  
  @MetaProperty
  @Column(name="search_inchi") 
  private String searchInchi;
  
  @MetaProperty
  @Column(name="structure") 
  private String structure;
  
  @MetaProperty
  @Column(name="deltag") 
  private Double deltaG;
  
  @MetaProperty
  @Column(name="deltagerr") 
  private Double deltaGErr;
  
  @Charge
  @MetaProperty
  @Column(name="default_charge") 
  private Integer defaultCharge;
  
  @MetaProperty
  @Column(name="mass") 
  private Double mass;
  
  @MetaProperty
  @Column(name="obsolete") 
  private Boolean obsolete;
  
  @MetaProperty
  @Column(name="core") 
  private Boolean core;
  
  @MetaProperty
  @Column(name="cofactor") 
  private Boolean cofactor;
  
  @ElementCollection
  @CollectionTable(name="modelseed_metabolite_name", joinColumns=@JoinColumn(name="metabolite_id"))
  @Column(name="name", length=255)
  private List<String> names = new ArrayList<> ();
  
  @OneToMany(mappedBy = "modelSeedMetaboliteEntity", cascade = CascadeType.ALL)
  private List<ModelSeedMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<>();

  public String getAbbreviation() { return abbreviation;}
  public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation;}

  public String getSeedSource() { return seedSource;}
  public void setSeedSource(String seedSource) { this.seedSource = seedSource;}
  
  public String getSearchInchi() { return searchInchi;}
  public void setSearchInchi(String searchInchi) { this.searchInchi = searchInchi;}
  
  public String getStructure() { return structure;}
  public void setStructure(String structure) { this.structure = structure;}

  public Double getDeltaG() { return deltaG;}
  public void setDeltaG(Double deltaG) { this.deltaG = deltaG;}

  public Double getDeltaGErr() { return deltaGErr;}
  public void setDeltaGErr(Double deltaGErr) { this.deltaGErr = deltaGErr;}

  public Integer getDefaultCharge() { return defaultCharge;}
  public void setDefaultCharge(Integer defaultCharge) { this.defaultCharge = defaultCharge;}

  public Double getMass() { return mass;}
  public void setMass(Double mass) { this.mass = mass;}

  public Boolean getObsolete() { return obsolete;}
  public void setObsolete(Boolean obsolete) { this.obsolete = obsolete;}

  public Boolean getCore() { return core;}
  public void setCore(Boolean core) { this.core = core;}

  public Boolean getCofactor() { return cofactor;}
  public void setCofactor(Boolean cofactor) { this.cofactor = cofactor;}
  
  public List<String> getNames() { return names;}
  public void setNames(List<String> names) { this.names = names;}
  
  public List<ModelSeedMetaboliteCrossreferenceEntity> getCrossreferences() { return crossreferences;}
  public void setCrossreferences(List<ModelSeedMetaboliteCrossreferenceEntity> crossreferences) {
    this.crossreferences = new ArrayList<> (crossreferences);
    for (ModelSeedMetaboliteCrossreferenceEntity crossReference : this.crossreferences) {
        crossReference.setModelSeedMetaboliteEntity(this);
    }
  }
  
}
