package pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractReactionTransform;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;

public class BiocycReactionTransform
extends AbstractReactionTransform<BioCycReactionEntity> {
	
	public BiocycReactionTransform(String majorLabel) {
		super(majorLabel);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphReactionEntity centralReactionEntity,
			BioCycReactionEntity reaction) {
		
//		centralReactionEntity.addP
	}

}
