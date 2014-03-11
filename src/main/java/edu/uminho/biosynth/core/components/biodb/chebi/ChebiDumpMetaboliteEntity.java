package edu.uminho.biosynth.core.components.biodb.chebi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteChemicalDataEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteCommentEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteNameEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiDumpMetaboliteStructuresEntity;

@Entity
@Table(name="compounds")
public class ChebiDumpMetaboliteEntity {
	
//	CREATE TABLE `compounds` (
//			  `id` INT NOT NULL,
//			  `name` TEXT,
//			  `source` VARCHAR(32) NOT NULL,
//			  `parent_id` INT,
//			  `chebi_accession` VARCHAR(30) NOT NULL,
//			  `status` VARCHAR(1) NOT NULL,
//			  `definition` TEXT,
//			  `star` INT NOT NULL,
//			  `modified_on` TEXT,
//			  `created_by` TEXT,
//			  PRIMARY KEY (`id`)
//			) ENGINE=InnoDB;

	@Id @Column(name="id", nullable=false) private Integer id;
	
	@Column(name="name", nullable=false, length=255) private String name;
	
	@Column(name="source", nullable=false, length=32) private String source;
	
	@Column(name="parent_id") private Integer parentId;
	
	@Column(name="chebi_accession", nullable=false, length=30) private String chebiAccession;
	
	@Column(name="status", nullable=false, length=1) private String status;
	
	@Column(name="star", nullable=false) private Integer star;
	
    @Column(name="created_by") private String createdBy;
    
//    private DateTime created_at;
	
	@OneToMany(mappedBy = "chebiDumpMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiDumpMetaboliteChemicalDataEntity> chemicalData = new ArrayList<> ();
	
	@OneToMany(mappedBy = "chebiDumpMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiDumpMetaboliteCommentEntity> comments = new ArrayList<> ();
	
	@OneToMany(mappedBy = "chebiDumpMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiDumpMetaboliteNameEntity> names = new ArrayList<> ();
	
	@OneToMany(mappedBy = "chebiDumpMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiDumpMetaboliteStructuresEntity> structures = new ArrayList<> ();
	
	@OneToMany(mappedBy = "chebiDumpMetaboliteEntity", cascade = CascadeType.ALL)
	private List<ChebiDumpMetaboliteReferenceEntity> references = new ArrayList<> ();
	
	public Integer getId() { return id;}
	public void setId(Integer id) { this.id = id;}

	public String getName() { return name;}
	public void setName(String name) { this.name = name;}

	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}

	public Integer getParentId() { return parentId;}
	public void setParentId(Integer parentId) { this.parentId = parentId;}

	public String getChebiAccession() { return chebiAccession;}
	public void setChebiAccession(String chebiAccession) { this.chebiAccession = chebiAccession;}

	public String getStatus() { return status;}
	public void setStatus(String status) { this.status = status;}

	public Integer getStar() { return star;}
	public void setStar(Integer star) { this.star = star;}
	
	public String getCreatedBy() { return createdBy;}
	public void setCreatedBy(String createdBy) { this.createdBy = createdBy;}


//	public DateTime getCreated_at() { return created_at;}
//	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
//    @Column(name="modified_on")
//	public void setCreated_at(String created_at) { 
//		System.out.println(created_at);
////		this.created_at = created_at;
//	}

	public List<ChebiDumpMetaboliteChemicalDataEntity> getChemicalData() { return chemicalData;}
	public void setChemicalData(
			List<ChebiDumpMetaboliteChemicalDataEntity> chemicalData) { this.chemicalData = chemicalData;}

	public List<ChebiDumpMetaboliteCommentEntity> getComments() { return comments;}
	public void setComments(List<ChebiDumpMetaboliteCommentEntity> comments) { this.comments = comments;}

	public List<ChebiDumpMetaboliteNameEntity> getNames() { return names;}
	public void setNames(List<ChebiDumpMetaboliteNameEntity> names) { this.names = names;}

	public List<ChebiDumpMetaboliteStructuresEntity> getStructures() { return structures;}
	public void setStructures(List<ChebiDumpMetaboliteStructuresEntity> structures) { this.structures = structures;}
	
	public List<ChebiDumpMetaboliteReferenceEntity> getReferences() { return references;}
	public void setReferences(List<ChebiDumpMetaboliteReferenceEntity> references) { this.references = references;}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("id:").append(this.id).append(sep);
		sb.append("name:").append(this.name).append(sep);
		sb.append("source:").append(this.source).append(sep);
		sb.append("parentId:").append(this.parentId).append(sep);
		sb.append("chebiAccession:").append(this.chebiAccession).append(sep);
		sb.append("status:").append(this.status).append(sep);
		sb.append("star:").append(this.star).append(sep);
		sb.append("createdBy:").append(this.createdBy).append(sep);
//		sb.append("created_at:").append(this.created_at).append(sep);
		sb.append("chemicalData:").append(this.chemicalData).append(sep);
		sb.append("comments:").append(this.comments).append(sep);
		sb.append("names:").append(this.names).append(sep);
		sb.append("structures:").append(this.structures).append(sep);
		if (references.size() > 10) {
			sb.append("references:")
			  .append(this.references.get(0))
			  .append(this.references.get(1))
			  .append(this.references.get(2))
			  .append(this.references.size() - 3).append(sep);
		} else {
			sb.append("references:").append(this.references).append(sep);
		}
		return sb.toString();
	}
}
