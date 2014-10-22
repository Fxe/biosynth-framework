package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="integrated_member")
public class IntegratedMember {
	
	@Id
	@Column(name="id", nullable=false)
	private Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	@Column(name="entry", nullable=true)
	private String entry;
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry;}

	@Column(name="source", nullable=true)
	private String source;
	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}

	@Column(name="description", nullable=true, length=255)
	private String description = "";
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}
	
	@Column(name="member_type", nullable=false, length=255)
	private String memberType = null;
	public String getMemberType() { return memberType;}
	public void setMemberType(String memberType) { this.memberType = memberType;}

	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="pk.member")
	private List<IntegratedClusterMember> clusters = new ArrayList<> ();
	public List<IntegratedClusterMember> getClusters() { return clusters;}
	public void setClusters(List<IntegratedClusterMember> clusters) { this.clusters = clusters;}
}
