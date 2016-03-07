package pt.uminho.sysbio.biosynthframework.biodb.hmdb;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="hmdb_metabolite_ontology")
public class HmdbMetaboliteOntology implements Serializable {
  
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name="id")
  @GeneratedValue
  protected Long id;
  public Long getId() { return id;}
  public void setId(Long id) { this.id = id;}
  

  @Column(name="status", length=255)
  private String status;
  public String getStatus() { return status;}
  public void setStatus(String status) { this.status = status;}
  
  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_ontology_biofunction", joinColumns=@JoinColumn(name="metabolite_ontology_id"))
  @Column(name="biofunction", length=200)
  private Set<String> biofunctions = new HashSet<> ();
  public Set<String> getBiofunctions() { return biofunctions;}
  public void setBiofunctions(Set<String> biofunctions) { this.biofunctions = biofunctions;}

  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_ontology_application", joinColumns=@JoinColumn(name="metabolite_ontology_id"))
  @Column(name="application", length=200)
  private Set<String> applications = new HashSet<> ();
  public Set<String> getApplications() { return applications;}
  public void setApplications(Set<String> applications) { this.applications = applications;}
  
  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_ontology_origin", joinColumns=@JoinColumn(name="metabolite_ontology_id"))
  @Column(name="origin", length=200)
  private Set<String> origins = new HashSet<> ();
  public Set<String> getOrigins() { return origins;}
  public void setOrigins(Set<String> origins) { this.origins = origins;}

  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="hmdb_metabolite_ontology_cellular_location", joinColumns=@JoinColumn(name="metabolite_ontology_id"))
  @Column(name="cellular_location", length=200)
  private Set<String> cellularLocations = new HashSet<> ();
  public Set<String> getCellularLocations() { return cellularLocations;}
  public void setCellularLocations(Set<String> cellularLocations) { this.cellularLocations = cellularLocations;}
  
  @JsonIgnore
  @OneToOne
  @JoinColumn(name="metabolite_id")
  private HmdbMetaboliteEntity metaboliteEntity;
  public HmdbMetaboliteEntity getMetaboliteEntity() { return metaboliteEntity;}
  public void setMetaboliteEntity(HmdbMetaboliteEntity metaboliteEntity) { this.metaboliteEntity = metaboliteEntity;}
  
  @Override
  public String toString() {
    return String.format("HmdbMetaboliteOntology[%d:%s]", id, this.status);
  }
}
