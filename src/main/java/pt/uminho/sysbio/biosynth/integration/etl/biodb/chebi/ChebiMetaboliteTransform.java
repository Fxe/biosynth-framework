package pt.uminho.sysbio.biosynth.integration.etl.biodb.chebi;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.components.ChebiMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;

public class ChebiMetaboliteTransform
extends AbstractMetaboliteTransform<ChebiMetaboliteEntity>{

	private static final String CHEBI_METABOLITE_LABEL = MetaboliteMajorLabel.ChEBI.toString();
	
	public ChebiMetaboliteTransform() {
		super(CHEBI_METABOLITE_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralMetaboliteEntity centralMetaboliteEntity,
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
	}

	@Override
	protected void configureCrossreferences(
			CentralMetaboliteEntity centralMetaboliteEntity,
			ChebiMetaboliteEntity entity) {
		
		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (ChebiMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
			proxy.setEntry(dbEntry);
			proxy.setMajorLabel(dbLabel);
			proxy.addLabel(METABOLITE_LABEL);
			crossreferences.add(proxy);
		}
		
		centralMetaboliteEntity.setCrossreferences(crossreferences);
	}

}
