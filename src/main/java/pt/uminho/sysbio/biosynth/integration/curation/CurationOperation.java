package pt.uminho.sysbio.biosynth.integration.curation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name="aaa")
public class CurationOperation {

	@Id
	private long id;
	public long getId() { return id;}
	public void setId(long id) { this.id = id;}
	
	@Column
	private String entry;
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry; }
	
	@Column
	private String operationType;
	public String getOperationType() { return operationType;}
	public void setOperationType(String operationType) { this.operationType = operationType;}
	public void setOperationType(Object operationType) { this.operationType = operationType.toString();}

	@Column
	private String clusterType;
	public String getClusterType() { return clusterType;}
	public void setClusterType(String clusterType) { this.clusterType = clusterType;}
	public void setClusterType(Object clusterType) { this.clusterType = clusterType.toString();}

	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="pk.cluster")
	@JoinTable(name="abc", joinColumns=@JoinColumn(), inverseJoinColumns=@JoinColumn)
	@Fetch(FetchMode.SELECT)
	private List<IntegratedClusterMember> members = new ArrayList<> ();
	public List<IntegratedClusterMember> getMembers() { return members;}
	public void setMembers(List<IntegratedClusterMember> members) { this.members = members;}
	
	private CurationSet curationSet;
	public CurationSet getCurationSet() { return curationSet;}
	public void setCurationSet(CurationSet curationSet) {
		this.curationSet = curationSet;
	}
	
	private long createdAt;
	public long getCreatedAt() { return createdAt;}
	public void setCreatedAt(long createdAt) { this.createdAt = createdAt;}

	private CurationUser curationUser;
	public CurationUser getCurationUser() { return curationUser; }
	public void setCurationUser(CurationUser curationUser) { this.curationUser = curationUser;}

	private String clusterRelationship;
	public String getClusterRelationship() { return clusterRelationship;}
	public void setClusterRelationship(String clusterRelationship) {
		this.clusterRelationship = clusterRelationship;
	}
	public void setClusterRelationship(Object clusterRelationship) {
		this.clusterRelationship = clusterRelationship.toString();
	}

	private List<IntegratedCluster> integratedClusters = new ArrayList<> ();
	public List<IntegratedCluster> getIntegratedClusters() { return integratedClusters;}
	public void setIntegratedClusters(List<IntegratedCluster> integratedClusters) { 
		this.integratedClusters = integratedClusters;
	}
	
}
