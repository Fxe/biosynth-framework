package pt.uminho.sysbio.biosynth.integration.curation;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;

public class CurationOperationFactory {
	
	public static CurationOperation buildSplitOperation(
			String oid,
			CurationSet xid,
			CurationUser usr,
			IntegratedCluster cid1,
			IntegratedCluster cid2) {
		CurationOperation curationOperation = new CurationOperation();
		curationOperation.setEntry(oid);
		curationOperation.setCreatedAt(System.currentTimeMillis());
		curationOperation.setClusterType(CurationLabel.CurationMetabolite);
		curationOperation.setOperationType(CurationOperationType.SPLIT);
		
		for (IntegratedClusterMember integratedClusterMember : cid1.getMembers()) {
			curationOperation.getMembers().add(integratedClusterMember);
		}
		for (IntegratedClusterMember integratedClusterMember : cid2.getMembers()) {
			curationOperation.getMembers().add(integratedClusterMember);
		}
		
		curationOperation.getIntegratedClusters().add(cid1);
		curationOperation.getIntegratedClusters().add(cid2);
		curationOperation.setClusterRelationship(CurationRelationship.NOT_EQUAL);
		
		curationOperation.setCurationSet(xid);
		curationOperation.setCurationUser(usr);
		return curationOperation;
	}
	
	public static CurationOperation buildAcceptOperation(
			String oid,
			CurationSet xid,
			CurationUser usr,
			IntegratedCluster cid) {
		CurationOperation curationOperation = new CurationOperation();
		curationOperation.setEntry(oid);
		curationOperation.setCreatedAt(System.currentTimeMillis());
		curationOperation.setClusterType(CurationLabel.CurationMetabolite);
		curationOperation.setOperationType(CurationOperationType.ACCEPT);
		
		for (IntegratedClusterMember integratedClusterMember : cid.getMembers()) {
			curationOperation.getMembers().add(integratedClusterMember);
		}
		
		curationOperation.getIntegratedClusters().add(cid);
		curationOperation.setCurationSet(xid);
		curationOperation.setCurationUser(usr);
		return curationOperation;
	}
	
	public static CurationOperation buildExcludeOperation(
			String oid,
			CurationSet xid,
			CurationUser usr,
			IntegratedCluster cid,
			IntegratedMember...eids) {
		CurationOperation curationOperation = new CurationOperation();
		curationOperation.setEntry(oid);
		curationOperation.setCreatedAt(System.currentTimeMillis());
		curationOperation.setClusterType(CurationLabel.CurationMetabolite);
		curationOperation.setOperationType(CurationOperationType.EXCLUDE);
		curationOperation.getIntegratedClusters().add(cid);
		curationOperation.setCurationSet(xid);
		curationOperation.setCurationUser(usr);
		
		for (IntegratedClusterMember integratedClusterMember : cid.getMembers()) {
			curationOperation.getMembers().add(integratedClusterMember);
		}
		
		for (IntegratedMember eid : eids) {
			curationOperation.getExclude().add(eid);
			IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
			integratedClusterMember.setMember(eid);
			curationOperation.getMembers().add(integratedClusterMember);
		}
		return curationOperation;
	}
}
