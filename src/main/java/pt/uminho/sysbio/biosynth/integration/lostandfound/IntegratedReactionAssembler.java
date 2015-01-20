package pt.uminho.sysbio.biosynth.integration.lostandfound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.CompositeProxyId;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedReactionEntity;
import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class IntegratedReactionAssembler {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IntegratedReactionAssembler.class);
	
	public Map<Long, Long> metaboliteUnificationMap;
	
	/**
	 * Merge m2 to m1
	 * @param m1
	 * @param m2
	 */
	private void merge(Map<Long, Set<Long>> m1, Map<Long, Set<Long>> m2) {
		for (Long key : m2.keySet()) {
			if (!m1.containsKey(key)) m1.put(key, new HashSet<Long> ());
			m1.get(key).addAll(m2.get(key));
		}
	}
	
	private Map<Long, Double> toLong(Map<String, Double> map) {
		Map<Long, Double> res = new HashMap<> ();
		
		for (String key : map.keySet())
			res.put(Long.parseLong(key), map.get(key));
		
		return res;
	}
	
	public IntegratedReactionEntity assemble(String entry, Set<GraphReactionEntity> graphReactionEntities) {
		LOGGER.debug(String.format("Assemble IntegratedReaction[%s] from %d reactions", entry, graphReactionEntities.size()));
		IntegratedReactionEntity entity = new IntegratedReactionEntity();
		entity.setEntry(entry);
		
		Map<String, Map<Object, List<Long>>> propertyMap = new HashMap<> ();
		Map<Long, Set<Long>> metaboliteMapping = new HashMap<> ();
		for (GraphReactionEntity reactionEntity : graphReactionEntities) {
			
			LOGGER.debug(reactionEntity.getLeftStoichiometry() + " <?> " + reactionEntity.getRightStoichiometry());
//			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, unificationMap);
		}
		LOGGER.debug("after unification");
		for (GraphReactionEntity reactionEntity : graphReactionEntities) {
			Map<Long, Set<Long>> out = 
					IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, metaboliteUnificationMap);
			LOGGER.debug(reactionEntity.getLeftStoichiometry() + " <?> " + reactionEntity.getRightStoichiometry());
			
			merge(metaboliteMapping, out);
//			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, unificationMap);
		}
		List<GraphReactionEntity> a = new ArrayList<> (graphReactionEntities);
		IntegrationUtils.alignReactions(a);
		LOGGER.debug("after align");
		for (GraphReactionEntity reactionEntity : graphReactionEntities) {
			Long id = reactionEntity.getId();
			String entry_ = reactionEntity.getEntry();
			String source = reactionEntity.getMajorLabel();
//			System.out.println(reactionEntity.getLeftStoichiometry() + " <?> " + reactionEntity.getRightStoichiometry());
			entity.getLeftUnifiedStoichiometry().put(id, toLong(reactionEntity.getLeftStoichiometry()));
			entity.getRightUnifiedStoichiometry().put(id, toLong(reactionEntity.getRightStoichiometry()));
//			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, unificationMap);
			
			CompositeProxyId proxyId = new CompositeProxyId();
			proxyId.setId(id);
			proxyId.setEntry(entry_);
			proxyId.setSource(source);
			
			entity.getSourcesMap().put(id, proxyId);
		}
		
		entity.setMetaboliteMapping(metaboliteMapping);
		
		return entity;
	}
}
