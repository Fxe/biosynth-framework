package edu.uminho.biosynth.core.data.integration.chimera.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="integrated_member")
public class IntegratedMember {
	
	@Id
	@Column(name="id", nullable=false)
	private Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	@Column(name="description", nullable=true, length=255)
	private String description;
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="pk.member")
	private List<IntegratedClusterMember> clusters = new ArrayList<> ();
	public List<IntegratedClusterMember> getClusters() { return clusters;}
	public void setClusters(List<IntegratedClusterMember> clusters) { this.clusters = clusters;}
}
