package pt.uminho.sysbio.biosynthframework.core.components.model.sbml;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;

@Entity
@Table(name="SBML_METABOLITE")
public class SbmlMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_MODEL")
	private SbmlMetabolicModel sbmlMetabolicModel;
	public SbmlMetabolicModel getSbmlMetabolicModel() { return sbmlMetabolicModel;}
	public void setSbmlMetabolicModel(SbmlMetabolicModel sbmlMetabolicModel) { this.sbmlMetabolicModel = sbmlMetabolicModel;}

}
