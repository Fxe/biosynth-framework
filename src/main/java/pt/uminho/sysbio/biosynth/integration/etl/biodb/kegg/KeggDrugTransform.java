package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;

public class KeggDrugTransform 
extends AbstractMetaboliteTransform<KeggDrugMetaboliteEntity> {

	private static final String KEGG_DRUG_METABOLITE_LABEL = MetaboliteMajorLabel.LigandDrug.toString();
	
	public KeggDrugTransform() {
		super(KEGG_DRUG_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(KeggDrugMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggDrugMetaboliteEntity entity) {
		
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
			KeggDrugMetaboliteEntity entity) {
		
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
