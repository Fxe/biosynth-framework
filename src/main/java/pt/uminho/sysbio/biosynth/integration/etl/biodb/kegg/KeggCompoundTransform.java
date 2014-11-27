package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
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

//	@Override
//	protected void configureCrossreferences(
//			GraphMetaboliteEntity centralMetaboliteEntity,
//			KeggCompoundMetaboliteEntity entity) {
//		
//		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
//		
//		for (KeggCompoundMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
//			dictionary.translate(xref.getRef());
//			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
//	
//			String dbEntry = xref.getValue(); //Also need to translate if necessary
//			GraphMetaboliteProxyEntity proxy = new GraphMetaboliteProxyEntity();
//			if (xref.getRef().equals("PubChem")) {
//				dbLabel = MetaboliteMajorLabel.PubChemSubstance.toString();
//				proxy.addProperty("reference", xref.getRef());
//			}
//			proxy.setEntry(dbEntry);
//			proxy.setMajorLabel(dbLabel);
//			proxy.addLabel(METABOLITE_LABEL);
//			crossreferences.add(proxy);
//		}
//		
//		centralMetaboliteEntity.setCrossreferences(crossreferences);
//	}

}