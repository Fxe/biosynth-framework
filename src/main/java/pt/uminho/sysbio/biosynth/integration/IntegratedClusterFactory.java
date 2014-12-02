package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public class IntegratedClusterFactory {

	private IntegrationSet integrationSet;
	private Long id;
	private String entry;
	private String description;
	private String clusterType;
	private String memberType;
	private Set<Long> eids = new HashSet<> ();
	private Set<GraphMetaboliteEntity> cpdEntities = new HashSet<> ();
	
	public IntegratedClusterFactory withIntegrationSet(IntegrationSet integrationSet) {
		this.integrationSet = integrationSet;
		return this;
	}
	
	public IntegratedClusterFactory withId(Long id) {
		this.id = id;
		return this;
	}
	
	public IntegratedClusterFactory withEntry(String entry) {
		this.entry = entry;
		return this;
	}
	
	public IntegratedClusterFactory withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public IntegratedClusterFactory withClusterType(String clusterType) {
		this.clusterType = clusterType;
		return this;
	}
	
	public IntegratedClusterFactory withMemberType(String memberType) {
		this.memberType = memberType;
		return this;
	}
	
	public IntegratedClusterFactory withMemberIdCollection(Collection<Long> eids) {
		this.eids.addAll(eids);
		return this;
	}
	
	public IntegratedClusterFactory withMemberEntityCollection(Collection<GraphMetaboliteEntity> eids) {
		this.cpdEntities.addAll(eids);
		return this;
	}
	
	public IntegratedCluster build() {
		IntegratedCluster integratedCluster = new IntegratedCluster();
		
		
		List<IntegratedClusterMember> integratedClusterMembers = new ArrayList<> ();
		for (Long eid : eids) {
			IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
			integratedClusterMember.setCluster(integratedCluster);
			IntegratedMember integratedMember = new IntegratedMember();
			integratedMember.setReferenceId(eid);
			integratedMember.setMemberType(memberType);
			integratedClusterMember.setMember(integratedMember);
			integratedClusterMembers.add(integratedClusterMember);
		}
		
		for (Metabolite cpd : cpdEntities) {
			IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
			integratedClusterMember.setCluster(integratedCluster);
			IntegratedMember integratedMember = new IntegratedMember();
			integratedMember.setReferenceId(cpd.getId());
			integratedMember.setEntry(cpd.getEntry());
			integratedMember.setMemberType(memberType);
			integratedClusterMember.setMember(integratedMember);
			integratedClusterMembers.add(integratedClusterMember);
		}
		
		integratedCluster.setId(id);
		integratedCluster.setEntry(entry);
		integratedCluster.setDescription(description);
		integratedCluster.setClusterType(clusterType);
		
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setMembers(integratedClusterMembers);
		
		return integratedCluster;
	}
}
