package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="bigg2_metabolite")
public class Bigg2MetaboliteEntity extends GenericMetabolite {

  private static final long serialVersionUID = 1L;
  
  @MetaProperty
  @Column(name="universal_entry")
  private String universalEntry;
  
  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="bigg2_metabolite_model", joinColumns=@JoinColumn(name="metabolite_id"))
  @Column(name="model", length=127)
  private List<String> modelList = new ArrayList<> ();

  public String getUniversalEntry() { return universalEntry;}

  public void setUniversalEntry(String universalEntry) { this.universalEntry = universalEntry;}

  public List<String> getModelList() { return modelList;}

  public void setModelList(List<String> modelList) { this.modelList = modelList;}
  
  @OneToMany(mappedBy = "bigg2MetaboliteEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<Bigg2MetaboliteCrossreferenceEntity> crossreferences = new ArrayList<> ();
  public List<Bigg2MetaboliteCrossreferenceEntity> getCrossreferences() { return crossreferences; }
  public void setCrossReferences(List<Bigg2MetaboliteCrossreferenceEntity> crossReferences) {
    this.crossreferences = new ArrayList<>(crossReferences);
    for (Bigg2MetaboliteCrossreferenceEntity crossReference : this.crossreferences) {
      crossReference.setBigg2MetaboliteEntity(this);
    }
  }
  
  public void addCrossReference(Bigg2MetaboliteCrossreferenceEntity crossReference) {
    this.crossreferences.add(crossReference);
    crossReference.setBigg2MetaboliteEntity(this);
  }
}
