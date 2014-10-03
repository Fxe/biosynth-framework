package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynth.integration.CentralReactionEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;

public class BiggReactionTransform
extends AbstractReactionTransform<BiggReactionEntity> {

	private static final String BIGG_REACTION_LABEL = ReactionMajorLabel.BiGG.toString(); 
	
	public BiggReactionTransform() {
		super(BIGG_REACTION_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralReactionEntity centralReactionEntity,
			BiggReactionEntity reaction) {
		// TODO Auto-generated method stub
		
	}

}
