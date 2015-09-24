package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class StaticMapClusterStrategy implements ClusteringStrategy{

	private GraphDatabaseService graphDatabaseService;

	private Map<Long, Set<Long>> map = new HashMap<> ();
	
	private Long initialNode;
	
	
	
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}

	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
	}

	public boolean isValid(Collection<Long> collection) {
		for (Long id : collection) {
			Node node = graphDatabaseService.getNodeById(id);
			if ( !(node.hasLabel(CompoundNodeLabel.Compound) && (Boolean)node.getProperty("proxy"))) {
				return false;
			}
		}
		return true;
	}
	
	public void setClusters(Set<Set<Long>> clusters) {
		for (Set<Long> set : clusters) {
//			if (isValid(set)) {
				for (Long eid : set) {
					map.put(eid, set);
				}
//			}
		}
	}
	
	@Override
	public void setInitialNode(Long id) {
		initialNode = id;
	}

	@Override
	public Set<Long> execute() {
		if (!map.containsKey(initialNode)) {
			Set<Long> singleton = new HashSet<> ();
			singleton.add(initialNode);
			return singleton;
		}
		return map.get(initialNode);
	}

}
