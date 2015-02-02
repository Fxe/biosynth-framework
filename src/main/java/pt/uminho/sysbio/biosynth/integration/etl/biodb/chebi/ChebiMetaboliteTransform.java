package pt.uminho.sysbio.biosynth.integration.etl.biodb.chebi;

import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteNameEntity;

public class ChebiMetaboliteTransform
extends AbstractMetaboliteTransform<ChebiMetaboliteEntity>{

	private static final String CHEBI_METABOLITE_LABEL = MetaboliteMajorLabel.ChEBI.toString();
	
	private static final String CHEBI_METABOLITE_RELATIONSHIP_PARENT_LABEL = MetaboliteRelationshipType.chebi_parent.toString();
	
	public ChebiMetaboliteTransform() {
		super(CHEBI_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(ChebiMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			ChebiMetaboliteEntity entity) {
		
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
						entity.getCharge(), 
						METABOLITE_CHARGE_LABEL, 
						METABOLITE_CHARGE_RELATIONSHIP_TYPE));
		
		for (ChebiMetaboliteNameEntity name : entity.getNames()) {
			try {
				Map<String, Object> properties = this.propertyContainerBuilder
						.extractProperties(name, ChebiMetaboliteNameEntity.class);
				centralMetaboliteEntity.addPropertyEntity(
						this.buildPropertyLinkPair(
								PROPERTY_UNIQUE_KEY, 
								name.getName(), 
								METABOLITE_NAME_LABEL, 
								METABOLITE_NAME_RELATIONSHIP_TYPE,
								properties));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	protected void configureCrossreferences(
			GraphMetaboliteEntity centralMetaboliteEntity,
			ChebiMetaboliteEntity entity) {
		
		super.configureCrossreferences(centralMetaboliteEntity, entity);
		
		Long parentId = entity.getParentId();
		if (parentId != null) {
			GraphMetaboliteProxyEntity proxyEntity = new GraphMetaboliteProxyEntity();
			proxyEntity.setEntry(Long.toString(parentId));
			proxyEntity.setMajorLabel(CHEBI_METABOLITE_LABEL);
			proxyEntity.addLabel(METABOLITE_LABEL);
			GraphRelationshipEntity relationshipEntity = new GraphRelationshipEntity();
			relationshipEntity.setMajorLabel(CHEBI_METABOLITE_RELATIONSHIP_PARENT_LABEL);
			centralMetaboliteEntity.addCrossreference(proxyEntity, relationshipEntity);
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
