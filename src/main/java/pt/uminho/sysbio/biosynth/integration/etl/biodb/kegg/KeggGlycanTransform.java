package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
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
		
		for (String pwy : entity.getPathways()) {
			centralMetaboliteEntity.addPropertyEntity(
					this.buildPropertyLinkPair2(
							"entry", 
							pwy, 
							GlobalLabel.KeggPathway.toString(), 
							MetaboliteRelationshipType.in_pathway.toString(),
							GlobalLabel.MetabolicPathway.toString(), GlobalLabel.KEGG.toString()));
		}
		
		for (String ecn : entity.getEnzymes()) {
			centralMetaboliteEntity.addPropertyEntity(
					this.buildPropertyLinkPair2(
							"entry", 
							ecn, 
							GlobalLabel.EnzymeCommission.toString(), 
							MetaboliteRelationshipType.related_to.toString()
							));
		}
		
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
