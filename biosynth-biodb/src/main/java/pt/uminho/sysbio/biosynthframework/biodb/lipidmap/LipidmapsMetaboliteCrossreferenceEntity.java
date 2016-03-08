package pt.uminho.sysbio.biosynthframework.biodb.lipidmap;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

@Entity
@Table(name="lipidmaps_metabolite_crossref")
public class LipidmapsMetaboliteCrossreferenceEntity extends GenericCrossreference {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="metabolite_id")
  private LipidmapsMetaboliteEntity metaboliteEntity;
  public LipidmapsMetaboliteEntity getMetaboliteEntity() { return metaboliteEntity;}
  public void setMetaboliteEntity(LipidmapsMetaboliteEntity metaboliteEntity) { this.metaboliteEntity = metaboliteEntity;}
  
  public LipidmapsMetaboliteCrossreferenceEntity() { super(null, null, null); }
  public LipidmapsMetaboliteCrossreferenceEntity(ReferenceType type, String reference, String value) {
      super(type, reference, value);
  }
  
}
