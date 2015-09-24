package pt.uminho.sysbio.biosynth.integration.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class GeneralIntegrationSetReporter implements IntegrationSetReporter {

	private GraphDatabaseService graphDatabaseService;
	
	public Map<String, Object> output;
	
	public GeneralIntegrationSetReporter(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}
	
	@Override
	public void generateReport(IntegrationSet integrationSet) {
		Map<String, Object> output = new HashMap<> ();
		Node iidNode = graphDatabaseService.getNodeById(integrationSet.getId());
		Set<Long> mCidIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(iidNode, IntegrationRelationshipType.IntegratedMetaboliteCluster);
		Set<Long> rCidIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(iidNode, IntegrationRelationshipType.IntegratedReactionCluster);
		Set<Long> oCidIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(iidNode);
		oCidIdSet.removeAll(mCidIdSet);
		oCidIdSet.removeAll(rCidIdSet);

		Set<Long> metaboliteMember = new HashSet<> ();
		Set<Long> collisionMember = new HashSet<> ();
		for (long mCid : mCidIdSet) {
			Node cidNode = graphDatabaseService.getNodeById(mCid);
			if (!cidNode.hasLabel(IntegrationNodeLabel.MetaboliteCluster)) {
				//whine about label
			}
			Set<Long> eidIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(cidNode, IntegrationRelationshipType.Integrates);
			for (long eid : eidIdSet) {
				if (!metaboliteMember.add(eid)) {
					collisionMember.add(eid);
				}
			}
		}

		output.put("ERROR IID LINKED NODES", oCidIdSet.size());
		output.put("TOTAL MCID", mCidIdSet.size());
		output.put("TOTAL RCID", rCidIdSet.size());
		output.put("TOTAL MEID", metaboliteMember.size());
		output.put("ERROR MEID", collisionMember.size());
		output.put("TOTAL REID", "not implemented");
		output.put("ERROR REID", "not implemented");
		this.output = output;
	}

}