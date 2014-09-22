package pt.uminho.sysbio.biosynth.integration.etl.biodb.mnx;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;

public class MnxMetaboliteTransform
extends AbstractMetaboliteTransform<MnxMetaboliteEntity>{

	private static final String MNX_METABOLITE_LABEL = MetaboliteMajorLabel.MetaNetX.toString();
	
	public MnxMetaboliteTransform() {
		super(MNX_METABOLITE_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralMetaboliteEntity centralMetaboliteEntity,
			MnxMetaboliteEntity entity) {
		
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
			MnxMetaboliteEntity entity) {
		
		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (MnxMetaboliteCrossReferenceEntity xref : entity.getCrossreferences()) {
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
