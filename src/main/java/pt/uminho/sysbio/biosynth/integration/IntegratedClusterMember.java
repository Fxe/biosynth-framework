package pt.uminho.sysbio.biosynth.integration;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="integrated_cluster_member")
public class IntegratedClusterMember {
	
	@Embeddable
	public static class IntegratedClusterMemberPk implements Serializable{
		
		private static final long serialVersionUID = -63654766199161L;
		
		@JsonIgnore
		@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
		@JoinColumn(name="integrated_cluster_id")
		private IntegratedCluster cluster;
		public IntegratedCluster getCluster() { return cluster;}
		public void setCluster(IntegratedCluster cluster) { this.cluster = cluster;}
		
		@JsonIgnore
		@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
		@JoinColumn(name="integrated_member_id")
		private IntegratedMember member;
		public IntegratedMember getMember() { return member;}
		public void setMember(IntegratedMember member) { this.member = member;}
		
		@Override
		public boolean equals(Object other) {
			if (this == other) return true;
	        if ( !(other instanceof IntegratedClusterMemberPk) ) return false;

	        final IntegratedClusterMemberPk pk_ = (IntegratedClusterMemberPk) other;

	        if ( !pk_.getCluster().equals( getCluster() ) ) return false;
	        if ( !pk_.getMember().equals( getMember() ) ) return false;

	        return true;
		}
		
		@Override
		public int hashCode() {
			int result;
			result = this.cluster.hashCode();
			result = result + 31 * this.getMember().hashCode();
			return result;
		}
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
	
	@Override
	public String toString() {
		return String.format("[%d, %d]",  this.pk.cluster.getId(), this.pk.member.getId());
	}
}
