package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="kegg_glycan_metabolite_crossreference")
public class KeggGlycanMetaboliteCrossreferenceEntity extends GenericCrossreference {

	private static final long serialVersionUID = -5573712793876140763L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private KeggGlycanMetaboliteEntity keggGlycanMetaboliteEntity;
	public KeggGlycanMetaboliteEntity getKeggGlycanMetaboliteEntity() { return keggGlycanMetaboliteEntity; }
	public void setKeggGlycanMetaboliteEntity(KeggGlycanMetaboliteEntity keggGlycanMetaboliteEntity) {
		this.keggGlycanMetaboliteEntity = keggGlycanMetaboliteEntity;
	}
	
	public KeggGlycanMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public KeggGlycanMetaboliteCrossreferenceEntity(ReferenceType type, String reference, String value) {
		super(type, reference, value);
	}
}
