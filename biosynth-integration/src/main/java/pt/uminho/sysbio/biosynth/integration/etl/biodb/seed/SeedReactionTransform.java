package pt.uminho.sysbio.biosynth.integration.etl.biodb.seed;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedReactionReagentEntity;

public class SeedReactionTransform extends AbstractReactionTransform<SeedReactionEntity> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SeedReactionTransform.class);
	
	public SeedReactionTransform() {
		super(ReactionMajorLabel.Seed.toString(), new BiobaseMetaboliteEtlDictionary<>(SeedMetaboliteEntity.class));
	}

	@Override
	protected String resolveComponentLabel(String entry) {
		return majorLabel;
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphReactionEntity centralReactionEntity,
			SeedReactionEntity reaction) {
		
		for (String ecn : reaction.getEnzymeClass()) {
			LOGGER.debug("Add EC number: " + ecn);
			centralReactionEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(ecn)
							.withMajorLabel(GlobalLabel.EnzymeCommission)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildReactionEdge(
							ReactionRelationshipType.has_ec_number)));
		}
	}
	
	private void processReagent(SeedReactionReagentEntity reagent, Map<GraphMetaboliteProxyEntity, Map<String, Object>> result) {
		try {
			Map<String, Object> stoichiometryProperties = 
					this.propertyContainerBuilder.extractProperties(
							reagent, 
							reagent.getClass());
			GraphMetaboliteProxyEntity entity = new GraphMetaboliteProxyEntity();
			entity.setEntry(reagent.getCpdEntry());
			entity.setMajorLabel(resolveComponentLabel(reagent.getCpdEntry()));
			entity.addLabel(METABOLITE_LABEL);
			result.put(entity, stoichiometryProperties);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void setupLeftMetabolites(GraphReactionEntity centralReactionEntity, SeedReactionEntity reaction) {
		Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
		for (SeedReactionReagentEntity reagent : reaction.getReagents()) {
			if (reagent.getCoefficient() < 0) processReagent(reagent, result);
		}
		centralReactionEntity.setLeft(result);
	};
	
	@Override
	protected void setupRightMetabolites(GraphReactionEntity centralReactionEntity, SeedReactionEntity reaction) {
		Map<GraphMetaboliteProxyEntity, Map<String, Object>> result = new HashMap<> ();
		for (SeedReactionReagentEntity reagent : reaction.getReagents()) {
			if (reagent.getCoefficient() > 0) processReagent(reagent, result);
		}
		centralReactionEntity.setRight(result);
	};
	
	@Override
	protected void configureNameLink(GraphReactionEntity centralReactionEntity,
			SeedReactionEntity entity) {
		
		for (String name : entity.getSynonyms()) {
			this.configureNameLink(centralReactionEntity, name);
		}
		
		super.configureNameLink(centralReactionEntity, entity);
	}

}
