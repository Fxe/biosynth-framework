package pt.uminho.sysbio.biosynth.integration.lostandfound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedReactionEntity;
import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class IntegratedReactionAssembler {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IntegratedReactionAssembler.class);
	
	public Map<Long, Long> metaboliteUnificationMap;
	
	public IntegratedReactionEntity assemble(String entry, Set<GraphReactionEntity> graphReactionEntities) {
		LOGGER.debug(String.format("Assemble IntegratedReaction[%s] from %d reactions", entry, graphReactionEntities.size()));
		IntegratedReactionEntity entity = new IntegratedReactionEntity();
		entity.setEntry(entry);
		
		Map<String, Map<Object, List<Long>>> propertyMap = new HashMap<> ();
		
		for (GraphReactionEntity reactionEntity : graphReactionEntities) {
			
			System.out.println(reactionEntity.getLeftStoichiometry() + " <?> " + reactionEntity.getRightStoichiometry());
//			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, unificationMap);
		}
		System.out.println("after unification");
		for (GraphReactionEntity reactionEntity : graphReactionEntities) {
			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, metaboliteUnificationMap);
			System.out.println(reactionEntity.getLeftStoichiometry() + " <?> " + reactionEntity.getRightStoichiometry());
//			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, unificationMap);
		}
		List<GraphReactionEntity> a = new ArrayList<> (graphReactionEntities);
		IntegrationUtils.alignReactions(a);
		System.out.println("after align");
		for (GraphReactionEntity reactionEntity : graphReactionEntities) {
			String entry_ = reactionEntity.getEntry();
//			System.out.println(reactionEntity.getLeftStoichiometry() + " <?> " + reactionEntity.getRightStoichiometry());
			entity.getLeftUnifiedStoichiometry().put(entry_, reactionEntity.getLeftStoichiometry());
			entity.getRightUnifiedStoichiometry().put(entry_, reactionEntity.getRightStoichiometry());
//			IntegrationUtils.reconciliateReactionMetabolites(reactionEntity, unificationMap);
		}
		
		
		
		return entity;
	}
}
