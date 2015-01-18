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
public class CurationCluster {

	@Id
	private long id;
	public long getId() { return id;}
	public void setId(long id) { this.id = id;}
	
	@Column
	private String entry;
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry; }
	
	private String type;
	public String getType() { return type;}
	public void setType(String type) { this.type = type;}

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
	
	private List<IntegratedCluster> integratedClusters = new ArrayList<> ();
	public List<IntegratedCluster> getIntegratedClusters() { return integratedClusters;}
	public void setIntegratedClusters(List<IntegratedCluster> integratedClusters) { 
		this.integratedClusters = integratedClusters;
	}
	
}
