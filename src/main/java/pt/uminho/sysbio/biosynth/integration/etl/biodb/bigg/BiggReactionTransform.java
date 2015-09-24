package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;

public class BiggReactionTransform
extends AbstractReactionTransform<BiggReactionEntity> {

	private static final String BIGG_REACTION_LABEL = ReactionMajorLabel.BiGG.toString(); 
	
	public BiggReactionTransform() {
		super(BIGG_REACTION_LABEL, new BiobaseMetaboliteEtlDictionary<>(BiggMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphReactionEntity entity,
			BiggReactionEntity reaction) {
		
		entity.addConnectedEntity(buildPair(
				new SomeNodeFactory()
					.withEntry(reaction.getEnzyme())
					.withMajorLabel(GlobalLabel.EnzymeCommission)
					.buildGenericNodeEntity(), 
				new SomeNodeFactory()
					.buildReactionEdge(ReactionRelationshipType.has_ec_number)));
	}

	@Override
	protected void configureNameLink(GraphReactionEntity centralReactionEntity,
			BiggReactionEntity entity) {
		for (String name : entity.getSynonyms()) {
			this.configureNameLink(centralReactionEntity, name);
		}
		super.configureNameLink(centralReactionEntity, entity);
	}

	@Override
	protected String resolveComponentLabel(String entry) {
		return BIGG_REACTION_LABEL;
	}
}
