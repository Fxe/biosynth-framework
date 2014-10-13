package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossreferenceEntity;

public class BiggMetaboliteTransform
extends AbstractMetaboliteTransform<BiggMetaboliteEntity> {

	private static final String BIGG_METABOLITE_LABEL = MetaboliteMajorLabel.BiGG.toString();
	
	public BiggMetaboliteTransform() {
		super(BIGG_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(BiggMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			BiggMetaboliteEntity entity) {

	}
	
	@Override
	protected void configureProperties(GraphMetaboliteEntity centralMetaboliteEntity, BiggMetaboliteEntity metabolite) {
		super.configureProperties(centralMetaboliteEntity, metabolite);
		centralMetaboliteEntity.getProperties().put("id", metabolite.getId());
	};

	@Override
	protected void configureCrossreferences(
			GraphMetaboliteEntity centralMetaboliteEntity,
			BiggMetaboliteEntity entity) {
		
		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (BiggMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
			switch (xref.getType()) {
				case DATABASE:
					String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
					String dbEntry = xref.getValue(); //Also need to translate if necessary
					GraphMetaboliteProxyEntity proxy = new GraphMetaboliteProxyEntity();
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
