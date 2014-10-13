package pt.uminho.sysbio.biosynthframework.core.components.model.sbml;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="SBML_REACTION_REACTANT")
@AssociationOverrides( {
		@AssociationOverride(name = "pk.sbmlSpecie",
				joinColumns = @JoinColumn(name = "SPECIE_ID")),
		@AssociationOverride(name = "pk.sbmlReaction",
				joinColumns = @JoinColumn(name = "REACTION_ID")) }
		)
public class SbmlReactionReactant extends SbmlReactionStoichiometry {

}
