package edu.uminho.biosynth.core.data.integration.chimera.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="integrated_cluster_member")
public class IntegratedClusterMember {
	
	@Embeddable
	public static class IntegratedClusterMemberPk implements Serializable{
		
		private static final long serialVersionUID = -63654766199161L;
		
		@ManyToOne(cascade=CascadeType.ALL)
		@JoinColumn(name="integrated_cluster_id")
		private IntegratedCluster cluster;
		public IntegratedCluster getCluster() { return cluster;}
		public void setCluster(IntegratedCluster cluster) { this.cluster = cluster;}
		
		@ManyToOne(cascade=CascadeType.ALL)
		@JoinColumn(name="integrated_member_id")
		private IntegratedMember member;
		public IntegratedMember getMember() { return member;}
		public void setMember(IntegratedMember member) { this.member = member;}
	}
	
	@EmbeddedId
	private IntegratedClusterMemberPk pk = new IntegratedClusterMemberPk();

	public IntegratedCluster getCluster() { return this.pk.getCluster();}
	public void setCluster(IntegratedCluster cluster) { this.pk.setCluster(cluster);}
	
	public IntegratedMember getMember() { return this.pk.getMember();}
	public void setMember(IntegratedMember member) { this.pk.setMember(member);}
	
	@Column(name="description", nullable=true, length=255)
	private String description;
	public String getDescription() { return description;}
	public void setDescription(String description) { this.description = description;}
}
