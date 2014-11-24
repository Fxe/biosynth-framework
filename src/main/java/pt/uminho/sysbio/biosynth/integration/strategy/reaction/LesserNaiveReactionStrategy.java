package pt.uminho.sysbio.biosynth.integration.strategy.reaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jReactionDao;
import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class LesserNaiveReactionStrategy extends NaiveReactionStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(LesserNaiveReactionStrategy.class);
	
	private Neo4jReactionDao reactionDao;
	protected List<GenericReaction> reactions = new ArrayList<> ();

	public LesserNaiveReactionStrategy(
			GraphDatabaseService graphDatabaseService,
			Map<Long, Long> metaboliteUnificationMap) {
		super(graphDatabaseService, metaboliteUnificationMap);
		reactionDao = new Neo4jReactionDao(db);
	}
	
	@Override
	public Set<Long> execute() {
		Set<Long> superResult = super.execute();
		
		if (superResult.isEmpty()) return superResult;
		
		GenericReaction rxnPivot = null;
		
		for (Long rxnId : superResult) {
			
			DefaultReaction defaultReaction = reactionDao.getReactionById(rxnId);
			
			if (defaultReaction != null) {
				defaultReaction.setReactantStoichiometry(
						this.reconciliateMetaboliteIds(defaultReaction.getLeftStoichiometry()));
				defaultReaction.setProductStoichiometry(
						this.reconciliateMetaboliteIds(defaultReaction.getRightStoichiometry()));
				
				if (rxnId == this.initialNode.getId()) {
					rxnPivot = defaultReaction;
				} else {
					reactions.add(defaultReaction);
				}
			} else {
				LOGGER.warn("Reaction not found: " + rxnId);
			}
		}
		//separate stoichiometry
		this.alignReactions(reactions);
		
		Set<Long> strictStoichMatchSet = new HashSet<> ();
		
//		GenericReaction rxnPivot = reactions.get(0);
		int l_pivot = rxnPivot.getLeftStoichiometry().size();
		int r_pivot = rxnPivot.getRightStoichiometry().size();
		
		strictStoichMatchSet.add(rxnPivot.getId());
		
//		LOGGER.debug(String.format("Pivot sizes %s / %s", rxnPivot.getLeftStoichiometry().keySet(), rxnPivot.getLeftStoichiometry().keySet()));
		LOGGER.debug(String.format("PIVOT::[%d]%s -> %s / %s", 
				rxnPivot.getId(), rxnPivot.getEntry(), 
				rxnPivot.getReactantStoichiometry().keySet(), rxnPivot.getProductStoichiometry().keySet()));
		for (int i = 0; i < reactions.size(); i++) {
			GenericReaction rxn = reactions.get(i);
			if (rxn.getReactantStoichiometry().size() == l_pivot
					&& rxn.getProductStoichiometry().size() == r_pivot) {
				LOGGER.debug(String.format("OK   ::[%d]%s -> %s / %s", 
						rxn.getId(), rxn.getEntry(), 
						rxn.getReactantStoichiometry().keySet(), rxn.getProductStoichiometry().keySet()));
				strictStoichMatchSet.add(rxn.getId());
			} else {
				LOGGER.debug(String.format("FAIL ::[%d]%s -> %s / %s", 
						rxn.getId(), rxn.getEntry(), 
						rxn.getReactantStoichiometry().keySet(), rxn.getProductStoichiometry().keySet()));
			}
		}
		
		return strictStoichMatchSet;
	}
	
	private Map<String, Double> reconciliateMetaboliteIds(Map<String, Double> map) {
		Map<String, Double> reconciliationMap = new HashMap<> ();
		
		for (String idStr : map.keySet()) {
			Long id = Long.parseLong(idStr);
			long uniId = this.metaboliteUnificationTable.reconciliateId(id);
			//ignore id if it is a proton
			if (uniId != protonId) {
				Double stoichiometry = map.get(idStr);
				reconciliationMap.put(Long.toString(uniId), stoichiometry);
			}
		}
		
		return reconciliationMap;
	}
	
	protected boolean alignReactions(List<GenericReaction> rxnList) {
		LOGGER.debug("Align reactions");
		
		if (rxnList.isEmpty()) return false;
		
		GenericReaction rxnPivot = rxnList.get(0);
		
		Set<String> leftPivot = new HashSet<> (rxnPivot.getReactantStoichiometry().keySet());
		Set<String> rightPivot = new HashSet<> (rxnPivot.getProductStoichiometry().keySet());
		LOGGER.debug(rxnPivot.getEntry() + ":" + leftPivot + " / " + rightPivot);

		for (int i = 1; i < rxnList.size(); i++) {
			GenericReaction rxn = rxnList.get(i);
			Set<String> left_ = new HashSet<> (rxn.getReactantStoichiometry().keySet());
			
			Double l_l = jaccard(left_, leftPivot);
			Double l_r = jaccard(left_, rightPivot);
			if (l_r > l_l) this.swapStoichiometry(rxn);
			
			LOGGER.debug(rxn.getEntry() + ":" + rxn.getReactantStoichiometry().keySet() + " / " + rxn.getProductStoichiometry().keySet());
		}
		return true;
	}
	
	protected<E> double jaccard(Collection<E> a, Collection<E> b) {
		if (a.isEmpty() && b.isEmpty()) return 1.0;
		
		Set<E> A_union_B = new HashSet<> (a);
		A_union_B.addAll(b);
		Set<E> A_intersect_B = new HashSet<> (a);
		A_intersect_B.retainAll(b);
		
		return A_intersect_B.size() / (double)A_union_B.size();
	}
	
	protected void swapStoichiometry(GenericReaction rxn) {
		LOGGER.debug("Swap eq rxn: " + rxn.getEntry());
		Map<String, Double> left = rxn.getLeftStoichiometry();
		Map<String, Double> right = rxn.getRightStoichiometry();
		rxn.setLeftStoichiometry(right);
		rxn.setRightStoichiometry(left);
	}
}
