package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import java.util.ArrayList;
import java.util.Arrays;
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

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="bigg_metabolite")
public class BiggMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;

	@Column(name="charge")
	private Integer charge;
	public Integer getCharge() { return charge; }
	public void setCharge(Integer charge) { this.charge = charge; }
	
	@JsonIgnore
	@ElementCollection()
	@CollectionTable(name="bigg_metabolite_compartment", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="compartment", length=127)
	private List<String> compartments = new ArrayList<> ();
	public List<String> getCompartments() { return compartments; }
	public void setCompartments(List<String> compartments) { this.compartments = compartments; }
	public void setCompartments(String[] compartments) { 
		this.compartments.clear();
		this.compartments.addAll(Arrays.asList(compartments)); }

	@OneToMany(mappedBy = "biggMetaboliteEntity", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<BiggMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
	public List<BiggMetaboliteCrossreferenceEntity> getCrossreferences() { return crossReferences; }
	public void setCrossReferences(List<BiggMetaboliteCrossreferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (BiggMetaboliteCrossreferenceEntity crossReference : this.crossReferences) {
			crossReference.setBiggMetaboliteEntity(this);
		}
	}
	public void addCrossReference(BiggMetaboliteCrossreferenceEntity crossReference) {
		this.crossReferences.add(crossReference);
		crossReference.setBiggMetaboliteEntity(this);
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append('\n');
		sb.append("CHARGE:").append(this.charge).append(sep);
		sb.append("COMPARTMENTS:").append(this.compartments).append(sep);
		sb.append("CROSSREFERENCE:").append(this.crossReferences);
		return sb.toString();
	}
}
