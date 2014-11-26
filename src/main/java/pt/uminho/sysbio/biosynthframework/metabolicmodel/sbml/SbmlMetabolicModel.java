package pt.uminho.sysbio.biosynthframework.metabolicmodel.sbml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.metabolicmodel.AbstractMetabolicModel;

@Entity
@Table(name="SBML_MODEL")
public class SbmlMetabolicModel extends AbstractMetabolicModel{

	@OneToMany(mappedBy = "sbmlMetabolicModel", cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected List<SbmlMetaboliteEntity> sbmlMetabolites = new ArrayList<> ();
	@OneToMany(mappedBy = "sbmlMetabolicModel", cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected List<SbmlReactionEntity> sbmlReactions = new ArrayList<> ();
	@OneToMany(mappedBy = "sbmlMetabolicModel", cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected List<SbmlMetaboliteSpecieEntity> sbmlSpecies = new ArrayList<> ();
	@OneToMany(mappedBy = "sbmlMetabolicModel", cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected List<SbmlCompartment> sbmlCompartments = new ArrayList<> ();

	
	public SbmlMetabolicModel() { super("noIdAssigned");}
	public SbmlMetabolicModel(String id) { super(id);}

	public List<SbmlMetaboliteEntity> getSbmlMetabolites() {
		return sbmlMetabolites;
	}

	public void setSbmlMetabolites(List<SbmlMetaboliteEntity> sbmlMetabolites) {
		this.sbmlMetabolites = sbmlMetabolites;
	}

	public List<SbmlReactionEntity> getSbmlReactions() { return sbmlReactions;}
	public void addSbmlReaction(SbmlReactionEntity sbmlReaction) {
		sbmlReaction.setSbmlMetabolicModel(this);
		this.sbmlReactions.add(sbmlReaction);
	}
	public void setSbmlReactions(List<SbmlReactionEntity> sbmlReactions) {
		for (SbmlReactionEntity sbmlReaction : sbmlReactions) {
			sbmlReaction.setSbmlMetabolicModel(this);
		}
		this.sbmlReactions = sbmlReactions;
	}

	public List<SbmlCompartment> getSbmlCompartments() { return sbmlCompartments; }
	public void addSbmlCompartment(SbmlCompartment sbmlCompartment) {
		sbmlCompartment.setSbmlMetabolicModel(this);
		this.sbmlCompartments.add(sbmlCompartment);
	}
	public void setSbmlCompartments(List<SbmlCompartment> sbmlCompartments) {
		for (SbmlCompartment sbmlCompartment : sbmlCompartments) {
			sbmlCompartment.setSbmlMetabolicModel(this);
		}
		this.sbmlCompartments = sbmlCompartments;
	}

	public List<SbmlMetaboliteSpecieEntity> getSbmlSpecies() { return sbmlSpecies; }
	public void addSbmlSpecie(SbmlMetaboliteSpecieEntity sbmlSpecie) {
		sbmlSpecie.setSbmlMetabolicModel(this);
		this.sbmlSpecies.add(sbmlSpecie);
	}
	public void setSbmlSpecies(List<SbmlMetaboliteSpecieEntity> sbmlSpecies) {
		for (SbmlMetaboliteSpecieEntity sbmlSpecie : sbmlSpecies) {
			sbmlSpecie.setSbmlMetabolicModel(this);
		}
		this.sbmlSpecies = sbmlSpecies;
	}
	
	
}
