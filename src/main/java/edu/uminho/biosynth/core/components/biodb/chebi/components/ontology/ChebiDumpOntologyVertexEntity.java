package edu.uminho.biosynth.core.components.biodb.chebi.components.ontology;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="vertice")
public class ChebiDumpOntologyVertexEntity {
	
	@Id
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="vertice_ref", nullable=false, length=60)
	private String verticeRef;
	
	@Column(name="compound_id", nullable=true)
	private Long compoundId;
	
	@ManyToOne
	@JoinColumn(name="ontology_id", nullable=false)
	private ChebiDumpOntologyEntity chebiDumpOntologyEntity;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="from")
	private List<ChebiDumpOntologyRelationEntity> chebiDumpOntologyOutgoingRelationEntities = new ArrayList<> ();

	@OneToMany(fetch=FetchType.LAZY, mappedBy="to")
	private List<ChebiDumpOntologyRelationEntity> chebiDumpOntologyIncommingRelationEntities = new ArrayList<> ();
	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}

	public String getVerticeRef() { return verticeRef;}
	public void setVerticeRef(String verticeRef) { this.verticeRef = verticeRef;}

	public Long getCompoundId() { return compoundId;}

	public void setCompoundId(Long compoundId) { this.compoundId = compoundId;}

	public ChebiDumpOntologyEntity getChebiDumpOntologyEntity() { return chebiDumpOntologyEntity;}

	public void setChebiDumpOntologyEntity(
			ChebiDumpOntologyEntity chebiDumpOntologyEntity) {
		this.chebiDumpOntologyEntity = chebiDumpOntologyEntity;
	}

	public List<ChebiDumpOntologyRelationEntity> getChebiDumpOntologyOutgoingRelationEntities() {
		return chebiDumpOntologyOutgoingRelationEntities;
	}
	public void setChebiDumpOntologyOutgoingRelationEntities(
			List<ChebiDumpOntologyRelationEntity> chebiDumpOntologyOutgoingRelationEntities) {
		this.chebiDumpOntologyOutgoingRelationEntities = chebiDumpOntologyOutgoingRelationEntities;
	}
	
	public List<ChebiDumpOntologyRelationEntity> getChebiDumpOntologyIncommingRelationEntities() {
		return chebiDumpOntologyIncommingRelationEntities;
	}
	public void setChebiDumpOntologyIncommingRelationEntities(
			List<ChebiDumpOntologyRelationEntity> chebiDumpOntologyIncommingRelationEntities) {
		this.chebiDumpOntologyIncommingRelationEntities = chebiDumpOntologyIncommingRelationEntities;
	}
	
	@Override
	public String toString() { 
		return String.format("V[%d, %d] %s", id, compoundId, verticeRef);
	}
}
