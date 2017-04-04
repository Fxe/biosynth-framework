package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;

import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

public class ModelSeedReactionEntity extends GenericReaction {

  private static final long serialVersionUID = 1L;
  
  @MetaProperty
  @Column(name="abbreviation") 
  private String abbreviation;
  
  @MetaProperty
  @Column(name="status") 
  private String status;
  
  @MetaProperty
  @Column(name="direction") 
  private String direction;
  
  @MetaProperty
  @Column(name="reversibility") 
  private String reversibility;
  
  @MetaProperty
  @Column(name="deltagerr") 
  private Double deltagerr;
  
  @MetaProperty
  @Column(name="deltag") 
  private Double deltag;
  
  @MetaProperty
  @Column(name="obsolete") 
  private Boolean obsolete;
  
  @MetaProperty
  @Column(name="definition") 
  private String definition;
  
  @MetaProperty
  @Column(name="equation") 
  private String equation;
  
  @MetaProperty
  @Column(name="code")
  private String code;
  
  @OneToMany (mappedBy = "modelSeedReactionEntity", cascade = CascadeType.ALL)
  private List<ModelSeedReactionReagentEntity> reagents = new ArrayList<> ();

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getReversibility() {
    return reversibility;
  }

  public void setReversibility(String reversibility) {
    this.reversibility = reversibility;
  }

  public Double getDeltagerr() {
    return deltagerr;
  }

  public void setDeltagerr(Double deltagerr) {
    this.deltagerr = deltagerr;
  }

  public Double getDeltag() {
    return deltag;
  }

  public void setDeltag(Double deltag) {
    this.deltag = deltag;
  }

  public Boolean getObsolete() {
    return obsolete;
  }

  public void setObsolete(Boolean obsolete) {
    this.obsolete = obsolete;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public String getEquation() {
    return equation;
  }

  public void setEquation(String equation) {
    this.equation = equation;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public List<ModelSeedReactionReagentEntity> getReagents() {
    return reagents;
  }

  public void setReagents(List<ModelSeedReactionReagentEntity> reagents) {
    this.reagents = reagents;
  }
  
  
}
