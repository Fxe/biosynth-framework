package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="integrated_cluster")
public class IntegratedCluster {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	@Column(name="entry", length=255)
	private String entry;
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry;}
	
	@Column(name="description", nullable=true, length=255)
	private String description = "";
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}
	
	@Column(name="cluster_type", nullable=false, length=255)
	private String clusterType = null;
	public String getClusterType() { return clusterType;}
	public void setClusterType(String clusterType) { this.clusterType = clusterType;}

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="integration_id")
	private IntegrationSet integrationSet;
	public IntegrationSet getIntegrationSet() { return integrationSet;}
	public void setIntegrationSet(IntegrationSet integrationSet) { this.integrationSet = integrationSet;}
	
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="pk.cluster")
	@Fetch(FetchMode.SELECT)
	private List<IntegratedClusterMember> members = new ArrayList<> ();
	public List<IntegratedClusterMember> getMembers() { return members;}
	public void setMembers(List<IntegratedClusterMember> members) { this.members = members;}
	
	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="integratedCluster")
	@MapKey(name="metaType")
	private Map<String, IntegratedClusterMeta> meta = new HashMap<> ();
	public Map<String, IntegratedClusterMeta> getMeta() { return meta;}
	public void setMeta(Map<String, IntegratedClusterMeta> meta) {
		for (IntegratedClusterMeta integratedClusterMeta : meta.values()) {
			integratedClusterMeta.setIntegratedCluster(this);
		}
		this.meta = meta;
	}
	
	
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
	

	public int size() {
		return this.members.size();
	}


	public IntegratedClusterMember removeMember(Long eid) {
		IntegratedClusterMember toRemove = null;
		for (IntegratedClusterMember clusterMember : this.members) {
			if (clusterMember.getMember().getId().equals(eid)) {
				toRemove = clusterMember;
				break;
			}
		}
		
		if (toRemove != null) {
			this.members.remove(toRemove);
		}
		
		return toRemove;
	}
	
	public void addMember(IntegratedMember integratedMember) {
		if (!this.containsMember(integratedMember)) {
			IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
			integratedClusterMember.setCluster(this);
			integratedClusterMember.setMember(integratedMember);
			
			this.getMembers().add(integratedClusterMember);
		}
	}
	
	public boolean containsMember(IntegratedMember integratedMember) {
		if (integratedMember == null) return false;
		if (integratedMember.getId() == null) return false;
		
		Long id = integratedMember.getId();
		
		for (IntegratedClusterMember clusterMember : this.members) {
			if (clusterMember.getMember().getId() == id) return true;
		}
		
		return false;
	}
	
	@Transient
	public List<Long> listAllIntegratedMemberIds() {
		List<Long> res = new ArrayList<> ();
		for (IntegratedClusterMember integratedClusterMember : this.getMembers()) {
			res.add(integratedClusterMember.getMember().getId());
		}
		
		return res;
	}
	
	@Override
	public String toString() {
		return String.format("IntegratedCluster[%d:%s]", id, entry);
	}

}
