package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;

public class BiggMetaboliteTransform
extends AbstractMetaboliteTransform<BiggMetaboliteEntity> {

	private static final String BIGG_METABOLITE_LABEL = MetaboliteMajorLabel.BiGG.toString();
	
	public BiggMetaboliteTransform() {
		super(BIGG_METABOLITE_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralMetaboliteEntity centralMetaboliteEntity,
			BiggMetaboliteEntity entity) {

	}

	@Override
	protected void configureCrossreferences(
			CentralMetaboliteEntity centralMetaboliteEntity,
			BiggMetaboliteEntity entity) {
		
		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (BiggMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
			switch (xref.getType()) {
				case DATABASE:
					String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
					String dbEntry = xref.getValue(); //Also need to translate if necessary
					CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
					proxy.setEntry(dbEntry);
					proxy.setMajorLabel(dbLabel);
					proxy.addLabel(METABOLITE_LABEL);
					crossreferences.add(proxy);
					break;
				case MODEL:
					centralMetaboliteEntity.addPropertyEntity(
							this.buildPropertyLinkPair(
									"key", 
									xref.getValue(), 
									MODEL_LABEL, 
									"FoundIn"));
					break;
				default:
					break;
			}

		}
		
		centralMetaboliteEntity.setCrossreferences(crossreferences);
	}

}
