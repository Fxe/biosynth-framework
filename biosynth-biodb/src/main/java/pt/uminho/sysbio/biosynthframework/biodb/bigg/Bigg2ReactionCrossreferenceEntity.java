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
@Table(name="bigg2_reaction_crossref")
public class Bigg2ReactionCrossreferenceEntity extends GenericCrossreference {
  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="reaction_id")
  private Bigg2ReactionEntity bigg2ReactionEntity;
  public Bigg2ReactionEntity getBigg2ReactionEntity() { return bigg2ReactionEntity;}
  public void setBigg2ReactionEntity(Bigg2ReactionEntity bigg2ReactionEntity) {
    this.bigg2ReactionEntity = bigg2ReactionEntity;
  }
  @MetaProperty
  @Column(name="link") private String link;
  public String getLink() { return link;}
  public void setLink(String link) { this.link = link;}
  
  public Bigg2ReactionCrossreferenceEntity() { super(null, null, null); }
  public Bigg2ReactionCrossreferenceEntity(ReferenceType type, String reference, String value) {
      super(type, reference, value);
  }
}
