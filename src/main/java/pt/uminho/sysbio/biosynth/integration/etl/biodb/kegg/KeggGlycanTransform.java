package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
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
			centralMetaboliteEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(pwy)
							.withLabel(GlobalLabel.KEGG)
							.withLabel(GlobalLabel.MetabolicPathway)
							.withMajorLabel(GlobalLabel.KeggPathway)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildMetaboliteEdge(
							MetaboliteRelationshipType.in_pathway)));
		}
		
		for (String ecn : entity.getEnzymes()) {
			centralMetaboliteEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(ecn)
							.withMajorLabel(GlobalLabel.EnzymeCommission)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildMetaboliteEdge(
							MetaboliteRelationshipType.related_to)));
		}
		
	}

	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {
		
		for (String name : entity.getNames()) {
			configureNameLink(centralMetaboliteEntity, name);
		}
	}
	

}
