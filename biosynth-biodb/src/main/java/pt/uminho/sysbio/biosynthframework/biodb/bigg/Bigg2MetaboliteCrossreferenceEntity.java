package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="bigg2_metabolite_crossref")
public class Bigg2MetaboliteCrossreferenceEntity extends GenericCrossreference {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="metabolite_id")
  private Bigg2MetaboliteEntity bigg2MetaboliteEntity;
  public Bigg2MetaboliteEntity getBigg2MetaboliteEntity() { return this.bigg2MetaboliteEntity; }
  public void setBigg2MetaboliteEntity(Bigg2MetaboliteEntity bigg2MetaboliteEntity) {
      this.bigg2MetaboliteEntity = bigg2MetaboliteEntity;
  }
  
  @MetaProperty
  @Column(name="link") private String link;
  public String getLink() { return link;}
  public void setLink(String link) { this.link = link;}
  
  public Bigg2MetaboliteCrossreferenceEntity() { super(null, null, null); }
  public Bigg2MetaboliteCrossreferenceEntity(ReferenceType type, String reference, String value) {
      super(type, reference, value);
  }
}
