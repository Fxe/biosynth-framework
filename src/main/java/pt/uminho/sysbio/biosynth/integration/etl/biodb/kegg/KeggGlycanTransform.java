package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteEntity;

public class KeggGlycanTransform 
extends AbstractMetaboliteTransform<KeggGlycanMetaboliteEntity> {

	private static final String KEGG_GLYCAN_METABOLITE_LABEL = MetaboliteMajorLabel.LigandGlycan.toString();
	
	public KeggGlycanTransform() {
		super(KEGG_GLYCAN_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(KeggGlycanMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {

	}

	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {
		
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
