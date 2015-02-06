package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;

public class BiggReactionTransform
extends AbstractReactionTransform<BiggReactionEntity> {

	private static final String BIGG_REACTION_LABEL = ReactionMajorLabel.BiGG.toString(); 
	
	public BiggReactionTransform() {
		super(BIGG_REACTION_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphReactionEntity centralReactionEntity,
			BiggReactionEntity reaction) {
//		reaction.get
		// TODO Auto-generated method stub
//		Annotation
	}

}
