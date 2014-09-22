package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;

public class KeggGlycanTransform 
extends AbstractMetaboliteTransform<KeggGlycanMetaboliteEntity> {

	private static final String KEGG_DRUG_METABOLITE_LABEL = MetaboliteMajorLabel.LigandGlycan.toString();
	
	public KeggGlycanTransform() {
		super(KEGG_DRUG_METABOLITE_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {

	}

	@Override
	protected void configureCrossreferences(
			CentralMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {
		
		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (KeggGlycanMetaboliteCrossreferenceEntity xref : entity.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
			proxy.setEntry(dbEntry);
			proxy.setMajorLabel(dbLabel);
			proxy.addLabel(METABOLITE_LABEL);
			crossreferences.add(proxy);
		}
	}
	

}
