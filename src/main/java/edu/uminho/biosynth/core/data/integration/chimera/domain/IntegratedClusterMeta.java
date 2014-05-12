package edu.uminho.biosynth.core.data.integration.chimera.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class IntegratedClusterMeta {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
//	message varchar(255) NOT NULL,
//	meta_type varchar(255) NOT NULL
	
	@Column(name="message", length=255, nullable=false)
	private String message;
	
	@Column(name="meta_type", length=255, nullable=false)
	private String type;
	
	@ManyToOne
	@JoinColumn(name="integrated_cluster_id")
	private IntegratedCluster integratedCluster;
	public IntegratedCluster getIntegratedCluster() { return integratedCluster;}
	public void setIntegratedCluster(IntegratedCluster integratedCluster) { this.integratedCluster = integratedCluster;}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
