package pt.uminho.sysbio.biosynth.integration.etl.biodb.seed;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;

public class SeedMetaboliteTransform
extends AbstractMetaboliteTransform<SeedMetaboliteEntity>{

	private static final String SEED_METABOLITE_LABEL = MetaboliteMajorLabel.Seed.toString();
	
	public SeedMetaboliteTransform() {
		super(SEED_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(SeedMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			SeedMetaboliteEntity entity) {
		
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getDefaultCharge(), MetabolitePropertyLabel.Charge, MetaboliteRelationshipType.has_charge);
		
//		centralMetaboliteEntity.getConnectedEntities().add(
//				this.buildPair(
//				new SomeNodeFactory().buildGraphMetabolitePropertyEntity(
//						MetabolitePropertyLabel.Charge, entity.getDefaultCharge()), 
//				new SomeNodeFactory().buildMetaboliteEdge(
//						MetaboliteRelationshipType.has_charge)));
//		for (SeedMetaboliteStructureEntity structure : entity.getStructures()) {
//			centralMetaboliteEntity.addPropertyEntity(
//					this.buildPropertyLinkPair(
//							PROPERTY_UNIQUE_KEY, 
//							structure.getStructure(), 
//							METABOLITE_SMILE_LABEL, 
//							METABOLITE_SMILE_RELATIONSHIP_TYPE));
//		}
//		centralMetaboliteEntity.addPropertyEntity(
//				this.buildPropertyLinkPair(
//						PROPERTY_UNIQUE_KEY, 
//						entity.getDefaultCharge(), 
//						METABOLITE_CHARGE_LABEL, 
//						METABOLITE_CHARGE_RELATIONSHIP_TYPE));
	}

	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			SeedMetaboliteEntity entity) {
		
		for (String name : entity.getSynonyms()) {
			this.configureNameLink(centralMetaboliteEntity, name);
		}
	}
}
