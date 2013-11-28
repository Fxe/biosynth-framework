package edu.uminho.biosynth.core.components.bigg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.bigg.components.BiggMetaboliteCrossReferenceEntity;

@Entity
@Table(name="BIGG_METABOLITE")
public class BiggMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;

	@Column(name="CHARGE")
	private int charge;
	public int getCharge() { return charge; }
	public void setCharge(int charge) { this.charge = charge; }
	
	@ElementCollection
	@CollectionTable(name="BIGG_METABOLITE_COMPARTMENT", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="COMPARTMENT")
	private List<String> compartments = new ArrayList<> ();
	public List<String> getCompartments() { return compartments; }
	public void setCompartments(List<String> compartments) { this.compartments = compartments; }

	@OneToMany(mappedBy = "biggMetaboliteEntity")
	private List<BiggMetaboliteCrossReferenceEntity> crossReferences = new ArrayList<> ();
	public List<BiggMetaboliteCrossReferenceEntity> getCrossReferences() { return crossReferences; }
	public void setCrossReferences(List<BiggMetaboliteCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (BiggMetaboliteCrossReferenceEntity crossReference : this.crossReferences) {
			crossReference.setBiggMetaboliteEntity(this);
		}
	}
	public void addCrossReference(BiggMetaboliteCrossReferenceEntity crossReference) {
		this.crossReferences.add(crossReference);
		crossReference.setBiggMetaboliteEntity(this);
	}
}
