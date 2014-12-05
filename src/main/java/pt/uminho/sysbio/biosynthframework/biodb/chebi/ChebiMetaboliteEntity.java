package pt.uminho.sysbio.biosynthframework.biodb.chebi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="chebi_metabolite")
public class ChebiMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;
	
	@MetaProperty
	@Column(name="inchi", length=16383) private String inchi;
	public String getInchi() { return inchi;}
	public void setInchi(String inchi) { this.inchi = inchi;}
	
	@MetaProperty
	@Column(name="inchiKey", length=255) private String inchiKey;
	public String getInchiKey() { return inchiKey;}
	public void setInchiKey(String inchiKey) { this.inchiKey = inchiKey;}

	@MetaProperty
	@Column(name="smiles", length=16383) private String smiles;
	public String getSmiles() { return smiles;}
	public void setSmiles(String smiles) { this.smiles = smiles;}
	
	@MetaProperty
	@Column(name="charge") private Integer charge;
	public Integer getCharge() { return charge; }
	public void setCharge(Integer charge) { this.charge = charge; }
	
	@MetaProperty
	@Column(name="mass") private Double mass;
	public Double getMass() { return mass; }
	public void setMass(Double mass) { this.mass = mass; }
	
	@MetaProperty
	@Column(name="star") private Integer stars;
	public Integer getStars() { return stars; }
	public void setStars(Integer stars) { this.stars = stars; }
	
	@MetaProperty
	@Column(name="created_by") private String createdBy;
	public String getCreatedBy() { return createdBy; }
	public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
	
	@MetaProperty
	@Column(name="parent_id") public Long parentId;
	public Long getParentId() { return parentId;}
	public void setParentId(Long parentId) { this.parentId = parentId;}
	
	@Column(name="mol3d") private String mol3d;
	public String getMol3d() { return mol3d; }
	public void setMol3d(String mol3d) { this.mol3d = mol3d; }
	
	@Column(name="mol2d") private String mol2d;
	public String getMol2d() { return mol2d; }
	public void setMol2d(String mol2d) { this.mol2d = mol2d; }
	
	@OneToMany(mappedBy = "chebiMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiMetaboliteNameEntity> names = new ArrayList<> ();
	public List<ChebiMetaboliteCrossreferenceEntity> getCrossreferences() { return crossreferences;}
	public void setCrossreferences(
			List<ChebiMetaboliteCrossreferenceEntity> crossreferences) { this.crossreferences = crossreferences;}
	
	@OneToMany(mappedBy = "chebiMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<> ();
	

	
	public List<ChebiMetaboliteNameEntity> getNames() {
		return names;
	}
	public void setNames(List<ChebiMetaboliteNameEntity> names) {
		this.names = names;
	}
	
	

	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("charge:").append(this.charge).append(sep);
		sb.append("mass:").append(this.mass).append(sep);
		sb.append("stars:").append(this.stars).append(sep);
		sb.append("parentId:").append(this.parentId).append(sep);
		sb.append("Smiles:").append(this.smiles).append(sep);
		sb.append("InChI:").append(this.inchi).append(sep);
		sb.append("InChI Key:").append(this.inchiKey).append(sep);
		sb.append("Names:").append(this.names).append(sep);
		sb.append("Xrefs:").append(this.crossreferences.size()).append(sep);
		return sb.toString();
	}
}
