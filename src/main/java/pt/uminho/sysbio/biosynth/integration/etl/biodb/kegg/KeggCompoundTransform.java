package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;

public class KeggCompoundTransform 
extends AbstractMetaboliteTransform<KeggCompoundMetaboliteEntity> {

	private static final String KEGG_COMPOUND_METABOLITE_LABEL = MetaboliteMajorLabel.LigandCompound.toString();
	
	public KeggCompoundTransform() {
		super(KEGG_COMPOUND_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(KeggCompoundMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggCompoundMetaboliteEntity entity) {
		
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						PROPERTY_UNIQUE_KEY, 
						entity.getInchi(), 
						METABOLITE_INCHI_LABEL, 
						METABOLITE_INCHI_RELATIONSHIP_TYPE));
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						PROPERTY_UNIQUE_KEY, 
						entity.getSmiles(), 
						METABOLITE_SMILE_LABEL, 
						METABOLITE_SMILE_RELATIONSHIP_TYPE));
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyLinkPair(
						PROPERTY_UNIQUE_KEY, 
						entity.getMol2d(), 
						METABOLITE_MOL_FILE_LABEL, 
						METABOLITE_MOL_FILE_RELATIONSHIP_TYPE));
	}
	
	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggCompoundMetaboliteEntity entity) {
		
		for (String name : entity.getNames()) {
			centralMetaboliteEntity.addPropertyEntity(
					this.buildPropertyLinkPair(
							"key", 
							name.trim(), 
							METABOLITE_NAME_LABEL, 
							METABOLITE_NAME_RELATIONSHIP_TYPE));
		}
	}

}