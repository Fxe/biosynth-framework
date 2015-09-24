package pt.uminho.sysbio.biosynth.integration.etl.biodb.chebi;

import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteNameEntity;

public class ChebiMetaboliteTransform
extends AbstractMetaboliteTransform<ChebiMetaboliteEntity>{

	protected static final String CHEBI_METABOLITE_LABEL = MetaboliteMajorLabel.ChEBI.toString();
	protected static final String CHEBI_METABOLITE_RELATIONSHIP_PARENT_LABEL = MetaboliteRelationshipType.chebi_parent.toString();
	
	public ChebiMetaboliteTransform() {
		super(CHEBI_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(ChebiMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			ChebiMetaboliteEntity entity) {
		
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
//						entity.getCharge(), 
//						METABOLITE_CHARGE_LABEL, 
//						METABOLITE_CHARGE_RELATIONSHIP_TYPE));

		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getCharge(), MetabolitePropertyLabel.Charge, MetaboliteRelationshipType.has_charge);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getInchi(), MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getSmiles(), MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getMol2d(), MetabolitePropertyLabel.MDLMolFile, MetaboliteRelationshipType.has_mdl_mol_file);
		this.configureGenericPropertyLink(centralMetaboliteEntity, entity.getMol3d(), MetabolitePropertyLabel.MDLMolFile, MetaboliteRelationshipType.has_mdl_mol_file);
	}
	
	@Override
	protected void configureNameLink(GraphMetaboliteEntity centralMetaboliteEntity, ChebiMetaboliteEntity entity) {
		super.configureNameLink(centralMetaboliteEntity, entity);
		for (ChebiMetaboliteNameEntity name : entity.getNames()) {
			try {
				Map<String, Object> edgeProperties = this.propertyContainerBuilder
						.extractProperties(name, ChebiMetaboliteNameEntity.class);
				centralMetaboliteEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory().buildGraphMetabolitePropertyEntity(
							MetabolitePropertyLabel.Name, name.getName()), 
					new SomeNodeFactory()
							.withProperties(edgeProperties)
							.buildMetaboliteEdge(MetaboliteRelationshipType.has_name)));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	@Override
	protected void configureCrossreferences(
			GraphMetaboliteEntity centralMetaboliteEntity,
			ChebiMetaboliteEntity entity) {
		
		super.configureCrossreferences(centralMetaboliteEntity, entity);
		
		Long parentId = entity.getParentId();
		if (parentId != null) {
			MetaboliteMajorLabel majorLabel = MetaboliteMajorLabel.valueOf(CHEBI_METABOLITE_LABEL);
			centralMetaboliteEntity.addConnectedEntity(
					this.buildPair(
					new SomeNodeFactory()
							.withEntry(Long.toString(parentId))
							.buildGraphMetaboliteProxyEntity(majorLabel), 
					new SomeNodeFactory()
							.buildMetaboliteEdge(MetaboliteRelationshipType.chebi_parent)));
		}
	}

//	@Override
//	protected void configureCrossreferences(
//			GraphMetaboliteEntity centralMetaboliteEntity,
//			ChebiMetaboliteEntity entity) {
//		
//		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
//		
//		for (ChebiMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
//			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
//			String dbEntry = xref.getValue(); //Also need to translate if necessary
//			GraphMetaboliteProxyEntity proxy = new GraphMetaboliteProxyEntity();
//			proxy.setEntry(dbEntry);
//			proxy.setMajorLabel(dbLabel);
//			proxy.addLabel(METABOLITE_LABEL);
//			crossreferences.add(proxy);
//		}
//		
//		centralMetaboliteEntity.setCrossreferences(crossreferences);
//	}

}
