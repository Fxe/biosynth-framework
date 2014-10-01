package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.ReactionNodeLabel;
import pt.uminho.sysbio.biosynth.integration.CentralReactionEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;

public class BiggReactionTransform
extends AbstractReactionTransform<BiggReactionEntity> {

	private static final String BIGG_REACTION_LABEL = ReactionNodeLabel.BiGG.toString(); 
	
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
