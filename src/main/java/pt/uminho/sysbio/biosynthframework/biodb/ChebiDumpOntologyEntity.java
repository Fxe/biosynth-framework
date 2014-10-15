package pt.uminho.sysbio.biosynthframework.biodb;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ontology")
public class ChebiDumpOntologyEntity {

	@Id
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="title", length=255, nullable=false)
	private String title;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="chebiDumpOntologyEntity")
	public List<ChebiDumpOntologyVertexEntity> chebiDumpOntologyVertexEntities = new ArrayList<> ();

	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}

	public String getTitle() { return title;}
	public void setTitle(String title) { this.title = title;}

	public List<ChebiDumpOntologyVertexEntity> getChebiDumpOntologyVertexEntities() {
		return chebiDumpOntologyVertexEntities;
	}

	public void setChebiDumpOntologyVertexEntities(
			List<ChebiDumpOntologyVertexEntity> chebiDumpOntologyVertexEntities) {
		this.chebiDumpOntologyVertexEntities = chebiDumpOntologyVertexEntities;
	}
	
	
}
