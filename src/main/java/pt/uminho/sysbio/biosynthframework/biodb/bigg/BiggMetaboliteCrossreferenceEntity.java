package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="bigg_metabolite_crossref")
public class BiggMetaboliteCrossreferenceEntity extends GenericCrossReference {
	
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private BiggMetaboliteEntity biggMetaboliteEntity;
	public BiggMetaboliteEntity getBiggMetaboliteEntity() { return this.biggMetaboliteEntity; }
	public void setBiggMetaboliteEntity(BiggMetaboliteEntity biggMetaboliteEntity) {
		this.biggMetaboliteEntity = biggMetaboliteEntity;
	}
	
	public BiggMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public BiggMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
}
