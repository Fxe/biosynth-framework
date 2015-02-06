package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
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
		
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getInchi(), MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getSmiles(), MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getMol2d(), MetabolitePropertyLabel.MDLMolFile, MetaboliteRelationshipType.has_mdl_mol_file);
	}

	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggDrugMetaboliteEntity entity) {
		
		for (String name : entity.getNames()) {
			configureNameLink(centralMetaboliteEntity, name);
		}
	}
	
}
