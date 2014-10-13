package pt.uminho.sysbio.biosynthframework.core.components.model.sbml;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class SbmlReactionStoichiometry {

	@Embeddable
	public class SbmlReactionStoichiometryPk implements Serializable{
		
		private static final long serialVersionUID = 7526472295622776147L;
		
		@ManyToOne
		protected SbmlReactionEntity sbmlReaction;
		public SbmlReactionEntity getSbmlReaction() { return sbmlReaction;}
		public void setSbmlReaction(SbmlReactionEntity sbmlReaction) { this.sbmlReaction = sbmlReaction;}
		
		@ManyToOne
		protected SbmlMetaboliteSpecieEntity sbmlSpecie;
		public SbmlMetaboliteSpecieEntity getSbmlSpecie() { return sbmlSpecie;}
		public void setSbmlSpecie(SbmlMetaboliteSpecieEntity sbmlSpecie) { this.sbmlSpecie = sbmlSpecie;}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || this.getClass() != obj.getClass()) return false;
			
			SbmlReactionStoichiometryPk obj_pk = (SbmlReactionStoichiometryPk) obj;
			
			if (sbmlReaction != null ? !sbmlReaction.equals(obj_pk.sbmlReaction) : obj_pk.sbmlReaction != null) return false;
	        if (sbmlSpecie != null ? !sbmlSpecie.equals(obj_pk.sbmlSpecie) : obj_pk.sbmlSpecie != null) return false;
	 
	        return true;
		}
		
		@Override
		public int hashCode() {
			int hash;
			hash = (sbmlReaction != null ? sbmlReaction.hashCode() : 0);
			hash = 31 * hash + (sbmlSpecie != null ? sbmlSpecie.hashCode() : 0);
			return hash;
		}
	}
	
	@EmbeddedId
	protected SbmlReactionStoichiometryPk pk = new SbmlReactionStoichiometryPk();
	public SbmlReactionStoichiometryPk getPk() { return pk;}
	public void setPk(SbmlReactionStoichiometryPk pk) { this.pk = pk;}

	@Column(name="stoichiometry")
	protected String stoichiometry;
	public String getStoichiometry() { return stoichiometry;}
	public void setStoichiometry(String stoichiometry) { this.stoichiometry = stoichiometry;}
	
	public void setSbmlReaction(SbmlReactionEntity sbmlReaction) { this.pk.sbmlReaction = sbmlReaction;}
	@Transient
	public SbmlReactionEntity getSbmlReaction() { return this.pk.sbmlReaction;}
	
	public void setSbmlSpecie(SbmlMetaboliteSpecieEntity sbmlSpecie) { this.pk.sbmlSpecie = sbmlSpecie;}
	@Transient
	public SbmlMetaboliteSpecieEntity getSbmlSpecie() { return this.pk.sbmlSpecie;}
}
