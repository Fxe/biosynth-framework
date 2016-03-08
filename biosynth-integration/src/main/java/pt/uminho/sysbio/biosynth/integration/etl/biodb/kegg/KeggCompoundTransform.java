package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
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
//		centralMetaboliteEntity.getConnectedEntities().add(
//				this.buildPair(
//				new SomeNodeFactory().buildGraphMetabolitePropertyEntity(
//						MetabolitePropertyLabel.InChI, entity.getInchi()), 
//				new SomeNodeFactory().buildMetaboliteEdge(
//						MetaboliteRelationshipType.has_inchi)));
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getInchi(), MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getSmiles(), MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getMol2d(), MetabolitePropertyLabel.MDLMolFile, MetaboliteRelationshipType.has_mdl_mol_file);

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
							.withMajorLabel(GlobalLabel.KEGG)
							.withLabel(GlobalLabel.EnzymeCommission)
							.buildGenericNodeEntity(), 
					new SomeNodeFactory().buildMetaboliteEdge(
							MetaboliteRelationshipType.related_to)));
		}
//		centralMetaboliteEntity.addPropertyEntity(
//				this.buildPropertyLinkPair(
//						PROPERTY_UNIQUE_KEY, 
//						entity.getInchi(), 
//						METABOLITE_INCHI_LABEL, 
//						METABOLITE_INCHI_RELATIONSHIP_TYPE));
//		centralMetaboliteEntity.addPropertyEntity(
//				this.buildPropertyLinkPair(
//						PROPERTY_UNIQUE_KEY, 
//						entity.getSmiles(), 
//						METABOLITE_SMILE_LABEL, 
//						METABOLITE_SMILE_RELATIONSHIP_TYPE));
//		centralMetaboliteEntity.addPropertyEntity(
//				this.buildPropertyLinkPair(
//						PROPERTY_UNIQUE_KEY, 
//						entity.getMol2d(), 
//						METABOLITE_MOL_FILE_LABEL, 
//						METABOLITE_MOL_FILE_RELATIONSHIP_TYPE));
//		for (String pwy : entity.getPathways()) {
//			centralMetaboliteEntity.addPropertyEntity(
//					this.buildPropertyLinkPair2(
//							"entry", 
//							pwy, 
//							GlobalLabel.KeggPathway.toString(), 
//							MetaboliteRelationshipType.in_pathway.toString(),
//							GlobalLabel.MetabolicPathway.toString(), GlobalLabel.KEGG.toString()));
//		}
		
//		for (String ecn : entity.getEnzymes()) {
//			centralMetaboliteEntity.addPropertyEntity(
//					this.buildPropertyLinkPair2(
//							"entry", 
//							ecn, 
//							GlobalLabel.EnzymeCommission.toString(), 
//							MetaboliteRelationshipType.related_to.toString()
//							));
//		}
		
	}
	
	@Override
	protected void configureNameLink(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggCompoundMetaboliteEntity entity) {
		
		for (String name : entity.getNames()) {
			configureNameLink(centralMetaboliteEntity, name);
		}
	}

}