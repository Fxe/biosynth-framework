package pt.uminho.sysbio.biosynth.integration.lostandfound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public class IntegratedMetaboliteAssembler {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IntegratedMetaboliteAssembler.class);
//	MetaboliteHeterogeneousDao<GraphMetaboliteEntity> heterogeneousDao;
//	
//	public IntegratedMetaboliteAssembler(MetaboliteHeterogeneousDao<GraphMetaboliteEntity> heterogeneousDao) {
//		this.heterogeneousDao = heterogeneousDao;
//	}
	
	public IntegratedMetaboliteEntity assemble(String entry, Set<GraphMetaboliteEntity> graphMetaboliteEntities) {
		LOGGER.debug(String.format("Assembling %s with %d metabolites", entry, graphMetaboliteEntities.size()));
		IntegratedMetaboliteEntity integratedMetaboliteEntity = new IntegratedMetaboliteEntity();
		integratedMetaboliteEntity.setEntry(entry);
		
//		Map<Long, String> formulaMap = new HashMap<> ();
////		Map<Long, String> formulaMap = new HashMap<> ();
////		Map<Long, String> formulaMap = new HashMap<> ();
////		Map<Long, String> formulaMap = new HashMap<> ();
		
		
		Map<String, Map<Object, List<Long>>> propertyMap = new HashMap<> ();
		
		for (GraphMetaboliteEntity cpd : graphMetaboliteEntities) {
			LOGGER.debug("Collecting properties of {}:{}", cpd.getLabels(), cpd.getEntry());
			long eid_ = cpd.getId();
//			formulaMap.put(eid_, graphMetaboliteEntity.getFormula());
			
			for (String relationshipType : cpd.getConnectedEntities().keySet()) {
				List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> pairs = cpd.getConnectedEntities().get(relationshipType);
				for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : pairs) {
					AbstractGraphNodeEntity node = p.getRight();
					if (node.getLabels().contains(GlobalLabel.MetaboliteProperty.toString())) {
						LOGGER.trace("Found connected link {}", node.getLabels());
						String property = node.getMajorLabel();
						if (!propertyMap.containsKey(property)) {
							propertyMap.put(property, new HashMap<Object, List<Long>> ());
						}
						Object value = node.getProperties().get(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
						if (!propertyMap.get(property).containsKey(value)) {
							propertyMap.get(property).put(value, new ArrayList<Long> ());
						}
						propertyMap.get(property).get(value).add(eid_);
						LOGGER.trace("Added property {} - {} to {}", property, value.toString().replaceAll("\n", ""), eid_);
					} else {
						LOGGER.trace("Ignored connected link {}", node.getLabels());
					}
				}
			}
			
//			for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair 
//					: graphMetaboliteEntity.getPropertyEntities()) {
//				GraphPropertyEntity propertyEntity = pair.getLeft();
//				String property = propertyEntity.getMajorLabel();
//				
//				if (!propertyMap.containsKey(property)) {
//					propertyMap.put(property, new HashMap<Object, List<Long>> ());
//				}
//				
//				for (String key : propertyEntity.getProperties().keySet()) {
//					Object value = propertyEntity.getProperties().get(key);
//					if (!propertyMap.get(property).containsKey(value)) {
//						propertyMap.get(property).put(value, new ArrayList<Long> ());
//					}
//					propertyMap.get(property).get(value).add(eid_);
//				}
//				
//			}
//			formulaMap.put(eid_, graphMetaboliteEntity.getPropertyEntities());
//			formulaMap.put(eid_, graphMetaboliteEntity.getFormula());
//			integratedMetaboliteEntity.set
//			graphMetaboliteEntity.get
		}
		
		integratedMetaboliteEntity.setProperties(propertyMap);
		
		return integratedMetaboliteEntity;
	}
}
