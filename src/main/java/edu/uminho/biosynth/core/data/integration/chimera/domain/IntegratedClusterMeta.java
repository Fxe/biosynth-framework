package edu.uminho.biosynth.core.data.integration.chimera.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uminho.biosynth.core.data.integration.IntegrationMessageLevel;

@Entity
@Table(name="integrated_cluster_meta")
public class IntegratedClusterMeta {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
//	message varchar(255) NOT NULL,
//	meta_type varchar(255) NOT NULL
	
	@Column(name="message", length=255, nullable=false)
	private String message;
	
	@Enumerated(EnumType.STRING)
	@Column(name="level", length=15, nullable=false)
	private IntegrationMessageLevel level;
	
	@Column(name="meta_type", length=255, nullable=false)
	private String metaType;
	
	@JsonIgnore
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
	
	public IntegrationMessageLevel getLevel() {
		return level;
	}
	public void setLevel(IntegrationMessageLevel level) {
		this.level = level;
	}
	
	public String getMetaType() {
		return metaType;
	}
	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s - %s", metaType, level, message);
	}
}
