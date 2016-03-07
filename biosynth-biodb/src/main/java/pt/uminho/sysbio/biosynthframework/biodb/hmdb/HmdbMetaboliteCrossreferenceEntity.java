package pt.uminho.sysbio.biosynthframework.biodb.hmdb;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

@Entity
@Table(name="hmdb_metabolite_crossref")
public class HmdbMetaboliteCrossreferenceEntity extends GenericCrossreference {
  
  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="metabolite_id")
  private HmdbMetaboliteEntity metaboliteEntity;
  public HmdbMetaboliteEntity getMetaboliteEntity() { return metaboliteEntity;}
  public void setMetaboliteEntity(HmdbMetaboliteEntity metaboliteEntity) { this.metaboliteEntity = metaboliteEntity;}
  
  public HmdbMetaboliteCrossreferenceEntity() { super(null, null, null); }
  public HmdbMetaboliteCrossreferenceEntity(ReferenceType type, String reference, String value) {
      super(type, reference, value);
  }
}
