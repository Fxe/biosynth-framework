package pt.uminho.sysbio.biosynth.integration.etl.biodb.mnx;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;

public class MnxMetaboliteTransform
extends AbstractMetaboliteTransform<MnxMetaboliteEntity>{

	private static final String MNX_METABOLITE_LABEL = MetaboliteMajorLabel.MetaNetX.toString();
	
	public MnxMetaboliteTransform() {
		super(MNX_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(MnxMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
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
			MnxMetaboliteEntity entity) {
		
		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (MnxMetaboliteCrossreferenceEntity xref : entity.getCrossreferences()) {
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
