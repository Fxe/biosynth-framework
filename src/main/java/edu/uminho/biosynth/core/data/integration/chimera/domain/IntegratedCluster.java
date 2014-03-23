package edu.uminho.biosynth.core.data.integration.chimera.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="integrated_cluster")
public class IntegratedCluster {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="name", length=255)
	private String name;
	
	@Column(name="description", nullable=true, length=255)
	private String description;
	
	@ManyToOne
	@JoinColumn(name="integration_id")
	private IntegrationSet integrationSet;
	public IntegrationSet getIntegrationSet() { return integrationSet;}
	public void setIntegrationSet(IntegrationSet integrationSet) { this.integrationSet = integrationSet;}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="pk.cluster")
	private List<IntegratedClusterMember> members = new ArrayList<> ();
	public List<IntegratedClusterMember> getMembers() { return members;}
	public void setMembers(List<IntegratedClusterMember> members) { this.members = members;}
	
	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="integratedCluster")
//	@MapKey(name="id")
//	private Map<Serializable, IntegratedClusterMember> memberMap = new HashMap<> ();
//	public Map<Serializable, IntegratedClusterMember> getMemberMap() {
//		return memberMap;
//	}
//
//	public void setMemberMap(Map<Serializable, IntegratedClusterMember> memberMap) {
//		this.memberMap = memberMap;
//	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}




	
	
}
