package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="modelseed_reaction")
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
  
  @ElementCollection
  @CollectionTable(name="modelseed_reaction_name", joinColumns=@JoinColumn(name="reaction_id"))
  @Column(name="name", length=255)
  private List<String> names = new ArrayList<> ();
  
  @ElementCollection
  @CollectionTable(name="modelseed_reaction_ec", joinColumns=@JoinColumn(name="reaction_id"))
  @Column(name="ec", length=40)
  private List<String> ec = new ArrayList<> ();
  
  @ElementCollection
  @CollectionTable(name="modelseed_reaction_role", joinColumns=@JoinColumn(name="reaction_id"))
  @Column(name="role", length=40)
  private List<String> roles = new ArrayList<> ();
  
  @ElementCollection
  @CollectionTable(name="modelseed_reaction_subsystem", joinColumns=@JoinColumn(name="reaction_id"))
  @Column(name="subsystem", length=40)
  private List<String> subsystems = new ArrayList<> ();
  
  @OneToMany (mappedBy = "modelSeedReactionEntity", cascade = CascadeType.ALL)
  private List<ModelSeedReactionReagentEntity> reagents = new ArrayList<> ();
  
  @OneToMany (mappedBy = "modelSeedReactionEntity", cascade = CascadeType.ALL)
  private List<ModelSeedReactionCrossreferenceEntity> crossreferences = new ArrayList<> ();

  public String getAbbreviation() { return abbreviation;}
  public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation;}

  public String getStatus() { return status;}
  public void setStatus(String status) { this.status = status;}

  public String getDirection() { return direction;}
  public void setDirection(String direction) { this.direction = direction;}

  public String getReversibility() { return reversibility;}
  public void setReversibility(String reversibility) { this.reversibility = reversibility;}

  public Double getDeltagerr() { return deltagerr;}
  public void setDeltagerr(Double deltagerr) { this.deltagerr = deltagerr;}

  public Double getDeltag() { return deltag;}
  public void setDeltag(Double deltag) { this.deltag = deltag;}

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

  public List<ModelSeedReactionReagentEntity> getReagents() { return reagents;}

  public void setReagents(List<ModelSeedReactionReagentEntity> reagents) {
    for (ModelSeedReactionReagentEntity r : reagents) {
      r.setModelSeedReactionEntity(this);
    }
    this.reagents = reagents;
  }

  public List<String> getNames() { return names;}
  public void setNames(List<String> names) { this.names = names;}
  
  public List<String> getEc() { return ec;}
  public void setEc(List<String> ec) { this.ec = ec;}

  public List<String> getRoles() { return roles;}
  public void setRoles(List<String> roles) { this.roles = roles;}
  
  public List<String> getSubsystems() { return subsystems;}
  public void setSubsystems(List<String> subsystems) { this.subsystems = subsystems;}
  
  public List<ModelSeedReactionCrossreferenceEntity> getCrossreferences() { return crossreferences;}

  public void setCrossreferences(List<ModelSeedReactionCrossreferenceEntity> crossreferences) {
    for (ModelSeedReactionCrossreferenceEntity r : crossreferences) {
      r.setModelSeedReactionEntity(this);
    }
    this.crossreferences = crossreferences;
  }
  
  @Override
  public Map<String, Double> getLeftStoichiometry() {
    Map<String, Double> lhs = new HashMap<> ();
    for (ModelSeedReactionReagentEntity r : this.reagents) {
      if (r.getStoichiometry() < 0.0) {
        lhs.put(r.getCpdEntry(), r.getStoichiometry());
      }
    }
    return lhs;
  }
  
  @Override
  public Map<String, Double> getRightStoichiometry() {
    Map<String, Double> rhs = new HashMap<> ();
    for (ModelSeedReactionReagentEntity r : this.reagents) {
      if (r.getStoichiometry() > 0.0) {
        rhs.put(r.getCpdEntry(), r.getStoichiometry());
      }
    }
    return rhs;
  }
}
