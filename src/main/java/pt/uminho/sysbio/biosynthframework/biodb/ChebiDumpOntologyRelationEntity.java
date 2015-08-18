package pt.uminho.sysbio.biosynthframework.biodb;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="relation")
public class ChebiDumpOntologyRelationEntity {
	
	@Id
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="type", nullable=false, length=255)
	private String type;
	
	@Column(name="status", nullable=false, length=1)
	private String status;
	
	@ManyToOne
	@JoinColumn(name="init_id", nullable=false)
	private ChebiDumpOntologyVertexEntity to;
	
	@ManyToOne
	@JoinColumn(name="final_id", nullable=false)
	private ChebiDumpOntologyVertexEntity from;

	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}

	public String getType() { return type;}
	public void setType(String type) { this.type = type;}

	public String getStatus() { return status;}
	public void setStatus(String status) { this.status = status;}

	public ChebiDumpOntologyVertexEntity getFrom() { return from;}
	public void setFrom(ChebiDumpOntologyVertexEntity from) { this.from = from;}

	public ChebiDumpOntologyVertexEntity getTo() { return to;}
	public void setTo(ChebiDumpOntologyVertexEntity to) { this.to = to;}
	
	@Override
	public String toString() {
		
		return String.format("R[%d:%s] {%s} %s {%s}", id, status, from, type, to);
	}
}
