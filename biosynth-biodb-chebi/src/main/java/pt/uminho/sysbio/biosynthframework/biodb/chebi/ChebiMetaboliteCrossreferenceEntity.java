package pt.uminho.sysbio.biosynthframework.biodb.chebi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="chebi_metabolite_crossreference")
public class ChebiMetaboliteCrossreferenceEntity extends GenericCrossreference {

	private static final long serialVersionUID = 4485931882242085419L;
	
	@Column(name="reference_name", length=512)
	private String referenceName;
	public String getReferenceName() { return referenceName;}
	public void setReferenceName(String referenceName) { this.referenceName = referenceName;}
	
	@Column(name="location_in_reference", length=90)
	private String locationInReference;
	public String getLocationInReference() { return locationInReference;}
	public void setLocationInReference(String locationInReference) { this.locationInReference = locationInReference;}

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private ChebiMetaboliteEntity chebiMetaboliteEntity;
	public ChebiMetaboliteEntity getChebiMetaboliteEntity() { return chebiMetaboliteEntity;}
	public void setChebiMetaboliteEntity(ChebiMetaboliteEntity chebiMetaboliteEntity) { 
		this.chebiMetaboliteEntity = chebiMetaboliteEntity;
	}


	public ChebiMetaboliteCrossreferenceEntity() { super(null, null, null);}
}
