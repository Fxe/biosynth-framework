package pt.uminho.sysbio.biosynth.integration.strategy.reaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class NaiveReactionStrategy extends AbstractNeo4jClusteringStrategy {

	public NaiveReactionStrategy() {
		this.initialNodeLabel = GlobalLabel.Reaction;
	}
	
	@Override
	public Set<Long> execute() {
		Node rxn = initialNode;
		Set<Long> left = this.collectNodes(rxn, ReactionRelationshipType.Left);
		Set<Long> right = this.collectNodes(rxn, ReactionRelationshipType.Right);
		Map<Long, Set<Long>> compoundToReactionMap = new HashMap<> ();
		
		for (Long l : left) {
			Node lnode = db.getNodeById(l);
			//ignore H on left -> this is cheating !
			if (!lnode.getProperty("entry").equals("h")) {
				Set<String> clusters = null; //metaboliteClusterLookup.findClusterForValue(l);
				
				Set<Long> meids;
				if (clusters == null || clusters.isEmpty() || clusters.size() > 1) {
					System.err.println("LNode:" + lnode);
					System.err.println(lnode.getProperty("entry", null));
					System.err.println(lnode.getProperty("name", null));
					System.err.println(lnode.getProperty("formula", null));
					System.err.println("Cluster:" + clusters + " l:" + l);
					meids = new HashSet<Long> ();
					meids.add(l);
				} else {
					meids = null; // metaboliteClusterLookup.getCluster(clusters.iterator().next());
				}
				
				Set<Long> reids = collectNodes(meids, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
				System.out.println(String.format("Compound %s cluster %s -> %d", lnode.getProperty("entry"), null /*metaboliteClusterLookup.findClusterForValue(l) */, reids.size()));
				compoundToReactionMap.put(l, reids);
			}
		}
		
		for (Long r : right) {
			
			Node rnode = db.getNodeById(r);
			//ignore H on right-> this is cheating !
			if (!rnode.getProperty("entry").equals("h")) {
				Set<String> clusters = null; // metaboliteClusterLookup.findClusterForValue(r);
				
				Set<Long> meids;
				if (clusters == null || clusters.isEmpty() || clusters.size() > 1) {
					System.err.println("RNode:" + rnode);
					System.err.println(rnode.getProperty("entry", null));
					System.err.println(rnode.getProperty("name", null));
					System.err.println(rnode.getProperty("formula", null));
					System.err.println("Cluster:" + clusters + " r:" + r);
					meids = new HashSet<Long> ();
					meids.add(r);
				} else {
					meids = null; // metaboliteClusterLookup.getCluster(clusters.iterator().next());
				}
				
				Set<Long> reids = collectNodes(meids, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
				System.out.println(String.format("Compound %s cluster %s -> %d", rnode.getProperty("entry"), null /*metaboliteClusterLookup.findClusterForValue(r)*/, reids.size()));
				compoundToReactionMap.put(r, reids);
			}
		}
		
		Set<Long> strongIntersection = new HashSet<> ();
		
		//if there is no elements
		if (compoundToReactionMap.isEmpty()) return strongIntersection;
			
		strongIntersection.addAll(compoundToReactionMap.values().iterator().next());
		for (Long ceid : compoundToReactionMap.keySet()) {
			strongIntersection.retainAll(compoundToReactionMap.get(ceid));
		}
//		strongIntersection.remove(eid);
		
		//filter remaining
		Set<Long> remove = new HashSet<> ();
		for (Long reid : strongIntersection) {
			Node rxnNode = db.getNodeById(reid);
			Set<Long> compounds = collectNodes(rxnNode, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
			int diff = Math.abs(compounds.size() - compoundToReactionMap.size());
			if (diff > 2) {
				remove.add(reid);
			}
		}
		strongIntersection.removeAll(remove);
		
//		System.out.println(strongIntersection);
		
		return strongIntersection;
	}
	
	private Set<Long> collectNodes(Node node, ReactionRelationshipType...relationshipTypes) {
		Set<Long> nodes = new HashSet<> ();
		
		for (Relationship relationship : node.getRelationships(relationshipTypes)) {
			Node other = relationship.getOtherNode(node);
			nodes.add(other.getId());
		}
		
		return nodes;
	}
	
	private Set<Long> collectNodes(Set<Long> eids, ReactionRelationshipType...relationshipTypes) {
		Set<Long> nodes = new HashSet<> ();
		
		for (Long eid : eids) nodes.addAll(collectNodes(db.getNodeById(eid), relationshipTypes));
		
		return nodes;
	}
}
