package pt.uminho.sysbio.biosynth.integration.strategy.reaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.UnificationTable;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class NaiveReactionStrategy extends AbstractNeo4jClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(NaiveReactionStrategy.class);
	
	/**
	 * if strict then add self reaction to the metabolite to reaction mapping
	 * this implies that the intersection may be a singleton at the end
	 */
	protected boolean strictIntegration = true;
	
	protected Long protonId;
	protected UnificationTable metaboliteUnificationTable;
//	protected ClusterLookupTable<String, Long> metaboliteClusterLookup;
	
	public NaiveReactionStrategy(
			GraphDatabaseService graphDatabaseService, 
			Map<Long, Long> metaboliteUnificationMap) {
		super(graphDatabaseService);
		
		this.initialNodeLabel = GlobalLabel.Reaction;
		this.metaboliteUnificationTable = new UnificationTable(metaboliteUnificationMap);
	}
	
	public boolean isStrictIntegration() { return strictIntegration;}
	public void setStrictIntegration(boolean strictIntegration) { this.strictIntegration = strictIntegration;}

	public Long getProtonId() { return protonId;}
	public void setProtonId(Long protonId) { this.protonId = protonId;}
	
	public UnificationTable getMetaboliteUnificationTable() { return metaboliteUnificationTable;}
	public void setMetaboliteUnificationTable(
			UnificationTable metaboliteUnificationTable) {
		this.metaboliteUnificationTable = metaboliteUnificationTable;
	}

//	public ClusterLookupTable<String, Long> getMetaboliteClusterLookup() { return metaboliteClusterLookup;}
//	public void setMetaboliteClusterLookup(
//			ClusterLookupTable<String, Long> metaboliteClusterLookup) { this.metaboliteClusterLookup = metaboliteClusterLookup;}

	private Map<Long, Set<Long>> collectCompoundReactions(Set<Long> cpdIdSet) {
		Map<Long, Set<Long>> compoundToReactionMap = new HashMap<> ();
		
		for (Long cpdId : cpdIdSet) {
			Node cpdNode = db.getNodeById(cpdId);
			//ignore H on left -> this is cheating !
			long unif_cpdId = metaboliteUnificationTable.reconciliateId(cpdId);
			if (unif_cpdId != protonId) {
				Set<Long> meids = metaboliteUnificationTable.getIdMappingsTo(unif_cpdId);
				Set<Long> reids = collectNodes(meids, ReactionRelationshipType.left_component, ReactionRelationshipType.right_component);
				
				//non-integrated compounds matches the pivot reaction
				if (strictIntegration && reids.isEmpty()) reids.add(this.initialNode.getId());
				
				LOGGER.debug(String.format("MetaboliteNode %s Mapped to CID[%d] matched %d reactions",
						cpdNode, unif_cpdId, reids.size()));
				
				if (!reids.isEmpty()) compoundToReactionMap.put(cpdId, reids);
			} else {
				LOGGER.debug(String.format("MetaboliteNode %s Mapped to CID[%d] skipped (ignore protons)",
						cpdNode, unif_cpdId));
			}
		}
		
		return compoundToReactionMap;
	}
	
	@Override
	public Set<Long> execute() {
		LOGGER.debug(String.format("Generating cluster for %d", initialNode.getId()));
		
		Node rxn = initialNode;
		if (rxn != null && rxn.hasLabel(GlobalLabel.Reaction)) {
			LOGGER.debug(String.format("Found reaction %d:%s", initialNode.getId(), rxn.getProperty("entry")));
		}
		
		Set<Long> left = Neo4jUtils.collectNodeRelationshipNodeIds(rxn, ReactionRelationshipType.left_component);
		Set<Long> right = Neo4jUtils.collectNodeRelationshipNodeIds(rxn, ReactionRelationshipType.right_component);
		
		Map<Long, Set<Long>> compoundToReactionMap = new HashMap<> ();
		LOGGER.debug("Gathering Left  Metabolite Reactions ...");
		compoundToReactionMap.putAll(this.collectCompoundReactions(left));
		LOGGER.debug("Gathering Right Metabolite Reactions ...");
		compoundToReactionMap.putAll(this.collectCompoundReactions(right));
//		for (Long l : left) {
//			Node lnode = db.getNodeById(l);
//			//ignore H on left -> this is cheating !
//			long unif_l = metaboliteUnificationTable.reconciliateId(l);
//			if (unif_l != protonId) {
////				Set<String> clusters = metaboliteClusterLookup.findClusterForValue(l);
//				
//				Set<Long> meids = metaboliteUnificationTable.getIdMappingsTo(unif_l);
////				if (clusters == null || clusters.isEmpty() || clusters.size() > 1) {
////					System.err.println("LNode:" + lnode);
////					System.err.println(lnode.getProperty("entry", null));
////					System.err.println(lnode.getProperty("name", null));
////					System.err.println(lnode.getProperty("formula", null));
////					System.err.println("Cluster:" + clusters + " l:" + l);
////					meids = new HashSet<Long> ();
////					meids.add(l);
////				} else {
////					meids = 
////				}
//				
//				
//				Set<Long> reids = collectNodes(meids, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
//				
//				System.out.println(reids);
//				
//				LOGGER.debug(String.format("Compound %s cluster %d -> %d",
//						lnode, unif_l, reids.size()));
//				compoundToReactionMap.put(l, reids);
//			}
//		}
//		
//		for (Long r : right) {
//			Node rnode = db.getNodeById(r);
//			//ignore H on right-> this is cheating !
//			long unif_r = metaboliteUnificationTable.reconciliateId(r);
//			
//			if (unif_r != protonId) {
////				Set<String> clusters = metaboliteClusterLookup.findClusterForValue(r);
//				
//				Set<Long> meids = metaboliteUnificationTable.getIdMappingsTo(unif_r);
////				if (clusters == null || clusters.isEmpty() || clusters.size() > 1) {
////					System.err.println("RNode:" + rnode);
////					System.err.println(rnode.getProperty("entry", null));
////					System.err.println(rnode.getProperty("name", null));
////					System.err.println(rnode.getProperty("formula", null));
////					System.err.println("Cluster:" + clusters + " r:" + r);
////					meids = new HashSet<Long> ();
////					meids.add(r);
////				} else {
////					meids = metaboliteClusterLookup.getCluster(clusters.iterator().next());
////					System.out.println(meids);
////					meids = 
////					System.out.println(meids);
////				}
//				
//				Set<Long> reids = collectNodes(meids, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
//				
//				System.out.println(reids);
//				
//				LOGGER.debug(String.format("Compound %s cluster %d -> %d", 
//						rnode, unif_r, reids.size()));
//				compoundToReactionMap.put(r, reids);
//			}
//		}
		
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
			Set<Long> compounds = Neo4jUtils.collectNodeRelationshipNodeIds(
					rxnNode, ReactionRelationshipType.left_component, ReactionRelationshipType.right_component);
			int diff = Math.abs(compounds.size() - compoundToReactionMap.size());
			if (diff > 2) {
				remove.add(reid);
			}
		}
		strongIntersection.removeAll(remove);
		strongIntersection.add(initialNode.getId());
//		System.out.println(strongIntersection);
		
		return strongIntersection;
	}
	
	private Set<Long> collectNodes(Set<Long> eids, ReactionRelationshipType...relationshipTypes) {
		Set<Long> nodes = new HashSet<> ();
		
		for (Long eid : eids) {
			Node node = db.getNodeById(eid);
			nodes.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(node, relationshipTypes));
		}
		
		return nodes;
	}
}
