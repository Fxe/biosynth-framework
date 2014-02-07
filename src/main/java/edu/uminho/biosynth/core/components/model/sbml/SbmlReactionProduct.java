package edu.uminho.biosynth.core.components.model.sbml;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name="SBML_REACTION_PRODUCT")
@AssociationOverrides( {
	@AssociationOverride(name = "pk.sbmlSpecie",
			joinColumns = @JoinColumn(name = "SPECIE_ID")),
	@AssociationOverride(name = "pk.sbmlReaction",
			joinColumns = @JoinColumn(name = "REACTION_ID")) }
	)
public class SbmlReactionProduct extends SbmlReactionStoichiometry {

}
