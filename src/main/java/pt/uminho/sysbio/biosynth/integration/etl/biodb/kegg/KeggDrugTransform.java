package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteCrossreferenceEntity;
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
	}

//	@Override
//	protected void configureCrossreferences(
//			GraphMetaboliteEntity centralMetaboliteEntity,
//			KeggDrugMetaboliteEntity entity) {
//		
//		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
//		
//		for (KeggDrugMetaboliteCrossreferenceEntity xref : entity.getCrossReferences()) {
//			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
//			String dbEntry = xref.getValue(); //Also need to translate if necessary
//			GraphMetaboliteProxyEntity proxy = new GraphMetaboliteProxyEntity();
//			proxy.setEntry(dbEntry);
//			proxy.setMajorLabel(dbLabel);
//			proxy.addLabel(METABOLITE_LABEL);
//			crossreferences.add(proxy);
//		}
//	}
	
}
