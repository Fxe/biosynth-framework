package pt.uminho.sysbio.biosynthframework.biodb.chebi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="chebi_metabolite_comment")
public class ChebiMetaboliteComment {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private ChebiMetaboliteEntity chebiMetaboliteEntity;
	
	@Column(name="comment", nullable=false)
	private String comment;
	
	@Column(name="datatype", nullable=false)
	private String dataType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChebiMetaboliteEntity getChebiMetaboliteEntity() {
		return chebiMetaboliteEntity;
	}

	public void setChebiMetaboliteEntity(ChebiMetaboliteEntity chebiMetaboliteEntity) {
		this.chebiMetaboliteEntity = chebiMetaboliteEntity;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	
}
