package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="kegg_drug_metabolite_crossreference")
public class KeggDrugMetaboliteCrossreferenceEntity extends GenericCrossReference{

	private static final long serialVersionUID = 4269987711164363768L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private KeggDrugMetaboliteEntity keggDrugMetaboliteEntity;
	public KeggDrugMetaboliteEntity getKeggDrugMetaboliteEntity() { return keggDrugMetaboliteEntity; }
	public void setKeggDrugMetaboliteEntity(KeggDrugMetaboliteEntity keggDrugMetaboliteEntity) {
		this.keggDrugMetaboliteEntity = keggDrugMetaboliteEntity;
	}
	
	public KeggDrugMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public KeggDrugMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
}
