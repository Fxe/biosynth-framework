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
	
	private enum ComponentType {
		SINGLETON, OPTINAL, REQUIRED, NULL
	}
	
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
	
	public Map<Long, Double> resolveStoichiometry(Map<Long, Map<Long, Double>> unifiedStoichiometryMap) {
		Map<Long, Double> stoichiometryMap = new HashMap<> ();
		int totalReactions = unifiedStoichiometryMap.size();
		Long[] rxn_array = new Long[totalReactions];
		int i = 0;
		for (Long rxnId : unifiedStoichiometryMap.keySet()) {
			rxn_array[i++] = rxnId;
		}
		
		LOGGER.trace("Reactions: " + java.util.Arrays.toString(rxn_array));
		
		int totalLeftMetabolites = 0;
		for (int j = 0; j < rxn_array.length; j++) {
			totalLeftMetabolites += unifiedStoichiometryMap.get(rxn_array[j]).size();
		}

		Long[][] left = new Long[totalReactions][totalLeftMetabolites];
		
		int metaboliteMaxIndex = 0;
		Map<Long, Integer> metaboliteIndex = new HashMap <> ();
		for (int j = 0; j < rxn_array.length; j++) {
			for (Long cpdId : unifiedStoichiometryMap.get(rxn_array[j]).keySet()) {
				if (!metaboliteIndex.containsKey(cpdId)) {
					metaboliteIndex.put(cpdId, metaboliteMaxIndex++);
				}
				int index = metaboliteIndex.get(cpdId);
				left[j][index] = cpdId;
			}
		}

		ComponentType[] componentType = new ComponentType[metaboliteMaxIndex];
		Double[][] value = new Double[totalReactions][metaboliteMaxIndex];
		
		for (i = 0; i < metaboliteMaxIndex; i++) {
			ComponentType type = ComponentType.OPTINAL;
			int nonNull = 0;
			for (int j = 0; j < totalReactions; j++) {
				nonNull += left[j][i] == null ? 0 : 1;
				Double value_ = unifiedStoichiometryMap.get(rxn_array[j]).get(left[j][i]);
				value[j][i] = value_;
			}
			
			if (nonNull == totalReactions) {
				type = ComponentType.REQUIRED;
				double value_ = value[0][i];
				for (int k = 1; k < rxn_array.length; k++) {
					if (value_ != value[k][i]) {
						LOGGER.warn("Multiple stoichiometry values");
					}
				}
				stoichiometryMap.put(left[0][i], value_);
			}
			if (nonNull <= 0) {
				type = ComponentType.NULL;
				LOGGER.warn("Null component");
			}
			if (nonNull == 1) {
				type = ComponentType.SINGLETON;
				LOGGER.warn("Singleton component");
			}
			
//			if (type.equals(other))
			componentType[i] = type;
		}
		
//		for (int j = 0; j < left.length; j++) {
//			System.out.println(java.util.Arrays.toString(left[j]));
//		}
//		for (int j = 0; j < left.length; j++) {
//			System.out.println(java.util.Arrays.toString(value[j]));
//		}
//		
//		System.out.println(java.util.Arrays.toString(componentType));
		
		return stoichiometryMap;
	}
	
	public Map<String, Double> translate(Map<Long, Double> map) {
		System.out.println(map);
		Map<String, Double> stoichiometryMap = new HashMap<> ();
		for (Long id : map.keySet()) {
			stoichiometryMap.put(Long.toString(id), map.get(id));
		}
		return stoichiometryMap;
	}
	
	public IntegratedReactionEntity assemble(IntegratedReactionEntity entity) {
//		IntegratedReactionEntity rxn = new GenericReaction();
//		rxn.setEntry(entity.getEntry());
//		System.out.println("left" + entity.getLeftUnifiedStoichiometry());
//		System.out.println(entity.getRightUnifiedStoichiometry());
//		System.out.println(entity.getMetaboliteMapping());
		Map<Long, Double> left = resolveStoichiometry(entity.getLeftUnifiedStoichiometry());
		Map<Long, Double> right = resolveStoichiometry(entity.getRightUnifiedStoichiometry());
		entity.setLeftStoichiometry(translate(left));
		entity.setRightStoichiometry(translate(right));
		return entity;
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
		
		for (long cpdId : entity.getMetaboliteMapping().keySet()) {
			IntegratedReactionEntity.MappingType type = 
					this.metaboliteUnificationMap.containsKey(cpdId) ? 
							IntegratedReactionEntity.MappingType.CLUSTER : 
							IntegratedReactionEntity.MappingType.MEMBER;
			entity.getMetaboliteMappingType().put(cpdId, type);
		}
		
		entity.setMetaboliteMapping(metaboliteMapping);
		
		//decide translocation consensus
		Boolean translocation = true;
		for (GraphReactionEntity grxn : graphReactionEntities) {
			Boolean trans = (Boolean) grxn.getProperty("translocation", null);
			if (trans == null) {
				translocation = null;
				break;
			}
			translocation &= trans;
		}
		
		entity.setTranslocation(translocation);
		
		return entity;
	}
}
