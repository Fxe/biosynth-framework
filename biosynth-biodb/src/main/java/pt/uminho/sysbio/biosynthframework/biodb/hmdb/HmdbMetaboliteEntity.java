package pt.uminho.sysbio.biosynthframework.biodb.hmdb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="hmdb_metabolite")
public class HmdbMetaboliteEntity extends GenericMetabolite {

  private static final long serialVersionUID = 1L;

  @MetaProperty
  @Column(name="iupac_name")
  private String iupacName;
  public String getIupacName() { return iupacName;}
  public void setIupacName(String iupacName) { this.iupacName = iupacName;}

  @MetaProperty
  @Column(name="smiles")
  private String smiles;
  public String getSmiles() { return smiles;}
  public void setSmiles(String smiles) { this.smiles = smiles;}
  
  @MetaProperty
  @Column(name="inchi")
  private String inchi;
  public String getInchi() { return inchi;}
  public void setInchi(String inchi) { this.inchi = inchi;}
  
  @MetaProperty
  @Column(name="inchikey")
  private String inchikey;
  public String getInchikey() { return inchikey;}
  public void setInchikey(String inchikey) { this.inchikey = inchikey;}
  
  @MetaProperty
  @Column(name="average_molecular_weight")
  private Double averageMolecularWeight;
  public Double getAverageMolecularWeight() { return averageMolecularWeight;}
  public void setAverageMolecularWeight(Double averageMolecularWeight) {
    this.averageMolecularWeight = averageMolecularWeight;
  }
  
  @MetaProperty
  @Column(name="monisotopic_moleculate_weight")
  private Double monisotopicMoleculateWeight;
  public Double getMonisotopicMoleculateWeight() { return monisotopicMoleculateWeight;}
  public void setMonisotopicMoleculateWeight(Double monisotopicMoleculateWeight) {
    this.monisotopicMoleculateWeight = monisotopicMoleculateWeight;
  }
  
  @OneToOne(mappedBy = "metaboliteEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private HmdbMetaboliteOntology ontology;
  public HmdbMetaboliteOntology getOntology() { return ontology;}
  public void setOntology(HmdbMetaboliteOntology ontology) {
    ontology.setMetaboliteEntity(this);
    this.ontology = ontology;
  }

  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_synonym", joinColumns=@JoinColumn(name="metabolite_id"))
  @Column(name="synonym", length=2048)
  private Set<String> synonyms = new HashSet<> ();
  public Set<String> getSynonyms() { return synonyms;}
  public void setSynonyms(Set<String> synonyms) { this.synonyms = synonyms;}

  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_biofluid", joinColumns=@JoinColumn(name="metabolite_id"))
  @Column(name="biofluid", length=255)
  private Set<String> biofluids = new HashSet<> ();
  public Set<String> getBiofluids() { return biofluids;}
  public void setBiofluids(Set<String> biofluids) { this.biofluids = biofluids;}
  
  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_tissue", joinColumns=@JoinColumn(name="metabolite_id"))
  @Column(name="tissue", length=255)
  private Set<String> tissues = new HashSet<> ();
  public Set<String> getTissues() { return tissues;}
  public void setTissues(Set<String> tissues) { this.tissues = tissues;}
  
  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_secondary_accession", joinColumns=@JoinColumn(name="metabolite_id"))
  @Column(name="accession", length=64)
  private Set<String> accessions = new HashSet<> ();
  public Set<String> getAccessions() { return accessions;}
  public void setAccessions(Set<String> accessions) { this.accessions = accessions;}

  @OneToMany(mappedBy = "metaboliteEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<HmdbMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<> ();
  public List<HmdbMetaboliteCrossreferenceEntity> getCrossreferences() { return crossreferences; }
  public void setCrossReferences(List<HmdbMetaboliteCrossreferenceEntity> crossreferences) {
    this.crossreferences = new ArrayList<>(crossreferences);
    for (HmdbMetaboliteCrossreferenceEntity crossReference : this.crossreferences) {
      crossReference.setMetaboliteEntity(this);
    }
  }
  public void addCrossReference(HmdbMetaboliteCrossreferenceEntity crossreference) {
    if (crossreference != null) {
      this.crossreferences.add(crossreference);
      crossreference.setMetaboliteEntity(this);
    }
  }
}
