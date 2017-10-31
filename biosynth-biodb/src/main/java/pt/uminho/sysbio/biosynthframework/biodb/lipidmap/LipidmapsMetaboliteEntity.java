package pt.uminho.sysbio.biosynthframework.biodb.lipidmap;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="lipidmaps_metabolite")
public class LipidmapsMetaboliteEntity extends GenericMetabolite {

  private static final long serialVersionUID = 1L;

  @MetaProperty
  @Column(name="systematic_name")
  private String systematicName;
  public String getSystematicName() { return systematicName;}
  public void setSystematicName(String systematicName) {
    this.systematicName = systematicName;
  }
  
  @MetaProperty
  @Column(name="synonyms")
  private String synonyms;
  public String getSynonyms() { return synonyms;}
  public void setSynonyms(String synonyms) { this.synonyms = synonyms;}

  @MetaProperty
  @Column(name="exact_mass")
  private Double exactMass;
  public Double getExactMass() { return exactMass;}
  public void setExactMass(double exactMass) { this.exactMass = exactMass;}
  
  @MetaProperty
  @Column(name="inchi")
  private String inchi;
  public String getInchi() { return inchi;}
  public void setInchi(String inchi) { this.inchi = inchi;}

  @MetaProperty
  @Column(name="inchi_key")
  private String inchiKey;
  public String getInchiKey() { return inchiKey;}
  public void setInchiKey(String inchiKey) { this.inchiKey = inchiKey;}
  
  @MetaProperty
  @Column(name="pubchem_substance_url")
  private String pubchemSubstanceUrl;
  public String getPubchemSubstanceUrl() { return pubchemSubstanceUrl;}
  public void setPubchemSubstanceUrl(String pubchemSubstanceUrl) { this.pubchemSubstanceUrl = pubchemSubstanceUrl;}
  
  @MetaProperty
  @Column(name="lipid_maps_cmpd_url")
  private String lipidMapsCmpdUrl;
  public String getLipidMapsCmpdUrl() { return lipidMapsCmpdUrl;}
  public void setLipidMapsCmpdUrl(String lipidMapsCmpdUrl) { this.lipidMapsCmpdUrl = lipidMapsCmpdUrl;}

  @MetaProperty
  @Column(name="status")
  public String status;
  public String getStatus() { return status;}
  public void setStatus(String status) { this.status = status;}

  @MetaProperty
  @Column(name="active")
  public Boolean active;
  public Boolean isActive() { return active;}
  public void setActive(Boolean active) { this.active = active;}
  
  @MetaProperty
  @Column(name="generated")
  public Boolean generated;
  public Boolean isGenerated() { return generated;}
  public void setGenerated(Boolean generated) { this.generated = generated;}

  @MetaProperty
  @Column(name="category")
  public String category;
  public String getCategory() { return category;}
  public void setCategory(String category) { this.category = category;}
  
  @MetaProperty
  @Column(name="main_class")
  public String mainClass;
  public String getMainClass() { return mainClass;}
  public void setMainClass(String mainClass) { this.mainClass = mainClass;}
  
  @MetaProperty
  @Column(name="sub_class")
  public String subSlass;
  public String getSubSlass() { return subSlass;}
  public void setSubSlass(String subSlass) { this.subSlass = subSlass;}
  
  @MetaProperty
  @Column(name="class_level4")
  public String classLevel4;
  public String getClassLevel4() { return classLevel4;}
  public void setClassLevel4(String classLevel4) { this.classLevel4 = classLevel4;}

  @OneToMany(mappedBy = "metaboliteEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<LipidmapsMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<> ();
  public List<LipidmapsMetaboliteCrossreferenceEntity> getCrossreferences() { return crossreferences; }
  public void setCrossReferences(List<LipidmapsMetaboliteCrossreferenceEntity> crossreferences) {
      this.crossreferences = new ArrayList<>(crossreferences);
      for (LipidmapsMetaboliteCrossreferenceEntity crossReference : this.crossreferences) {
          crossReference.setMetaboliteEntity(this);
      }
  }
  public void addCrossReference(LipidmapsMetaboliteCrossreferenceEntity crossreference) {
      this.crossreferences.add(crossreference);
      crossreference.setMetaboliteEntity(this);
  }
}
