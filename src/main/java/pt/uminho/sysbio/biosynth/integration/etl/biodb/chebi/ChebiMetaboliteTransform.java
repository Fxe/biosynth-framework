package pt.uminho.sysbio.biosynth.integration.etl.biodb.chebi;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;

public class ChebiMetaboliteTransform
extends AbstractMetaboliteTransform<ChebiMetaboliteEntity>{

	private static final String CHEBI_METABOLITE_LABEL = MetaboliteMajorLabel.ChEBI.toString();
	
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
	}

	@Override
	protected void configureCrossreferences(
			GraphMetaboliteEntity centralMetaboliteEntity,
			ChebiMetaboliteEntity entity) {
		
		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (ChebiMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			GraphMetaboliteProxyEntity proxy = new GraphMetaboliteProxyEntity();
			proxy.setEntry(dbEntry);
			proxy.setMajorLabel(dbLabel);
			proxy.addLabel(METABOLITE_LABEL);
			crossreferences.add(proxy);
		}
		
		centralMetaboliteEntity.setCrossreferences(crossreferences);
	}

}
