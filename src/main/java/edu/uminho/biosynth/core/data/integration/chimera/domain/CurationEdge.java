package edu.uminho.biosynth.core.data.integration.chimera.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="curation_member_to_member")
public class CurationEdge {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="integrated_member_src_id", nullable=false)
	private Long src;
	
	@Column(name="integrated_member_dst_id", nullable=false)
	private Long dst;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type", nullable=false, length=255)
	private CurationEdgeType type;
	
	@Column(name="description", nullable=false)
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSrc() {
		return src;
	}

	public void setSrc(Long src) {
		this.src = src;
	}

	public Long getDst() {
		return dst;
	}

	public void setDst(Long dst) {
		this.dst = dst;
	}

	public CurationEdgeType getType() {
		return type;
	}

	public void setType(CurationEdgeType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
