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

import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="bigg2_reaction")
public class Bigg2ReactionEntity extends GenericReaction {

  private static final long serialVersionUID = 1L;
  
  @MetaProperty
  @Column(name="reaction_string")
  private String reactionString;
  
  @MetaProperty
  @Column(name="pseudoreaction")
  private Boolean pseudoreaction;
  
  @MetaProperty
  @Column(name="ec_number")
  private String ecNumber;
  
  public String getEcNumber() { return ecNumber;}
  public void setEcNumber(String ecNumber) { this.ecNumber = ecNumber;}

  @OneToMany(mappedBy = "bigg2ReactionEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<Bigg2ReactionMetaboliteEntity> metabolites = new ArrayList<> ();
  
  public List<Bigg2ReactionMetaboliteEntity> getMetabolites() { return metabolites;}
  public void setMetabolites(List<Bigg2ReactionMetaboliteEntity> metabolites) {
    this.metabolites = metabolites;
    for (Bigg2ReactionMetaboliteEntity e : metabolites) {
      e.setBigg2ReactionEntity(this);
    }
  }
  public void addMetabolite(Bigg2ReactionMetaboliteEntity e) {
    this.metabolites.add(e);
    e.setBigg2ReactionEntity(this);
  }
  
  public String getReactionString() { return reactionString;}
  public void setReactionString(String reactionString) { this.reactionString = reactionString;}

  public Boolean getPseudoreaction() { return pseudoreaction;}
  public void setPseudoreaction(Boolean pseudoreaction) { this.pseudoreaction = pseudoreaction;}

  @OneToMany(mappedBy = "bigg2ReactionEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private List<Bigg2ReactionCrossreferenceEntity> crossreferences = new ArrayList<> ();

  @JsonIgnore
  @ElementCollection()
  @CollectionTable(name="bigg2_reaction_model", joinColumns=@JoinColumn(name="reaction_id"))
  @Column(name="model", length=127)
  private List<String> models = new ArrayList<> ();
  public List<String> getModels() { return models;}
  public void setModels(List<String> models) { this.models = models;}
  
  public List<Bigg2ReactionCrossreferenceEntity> getCrossreferences() {
    return crossreferences;
  }

  public void setCrossreferences(List<Bigg2ReactionCrossreferenceEntity> crossreferences) {
    this.crossreferences = new ArrayList<>(crossreferences);
    for (Bigg2ReactionCrossreferenceEntity crossReference : this.crossreferences) {
      crossReference.setBigg2ReactionEntity(this);
    }
  }
  
  public void addCrossReference(Bigg2ReactionCrossreferenceEntity crossReference) {
    this.crossreferences.add(crossReference);
    crossReference.setBigg2ReactionEntity(this);
  }
  
}
