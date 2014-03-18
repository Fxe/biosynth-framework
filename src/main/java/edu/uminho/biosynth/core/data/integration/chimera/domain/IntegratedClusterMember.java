package edu.uminho.biosynth.core.data.integration.chimera.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="integrated_cluster_member")
public class IntegratedClusterMember {
	
	@Id
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="description", nullable=true, length=255)
	private String description;
	
	@ManyToOne
	@JoinColumn(name="integrated_cluster_id")
	private IntegratedCluster integratedCluster;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public IntegratedCluster getIntegratedCluster() {
		return integratedCluster;
	}

	public void setIntegratedCluster(IntegratedCluster integratedCluster) {
		this.integratedCluster = integratedCluster;
	}
	
	
}
