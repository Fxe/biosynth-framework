package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;

public class KeggGlycanTransform 
extends AbstractMetaboliteTransform<KeggGlycanMetaboliteEntity> {

	private static final String KEGG_DRUG_METABOLITE_LABEL = MetaboliteMajorLabel.LigandGlycan.toString();
	
	public KeggGlycanTransform() {
		super(KEGG_DRUG_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(KeggGlycanMetaboliteEntity.class));
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {

	}

	@Override
	protected void configureCrossreferences(
			GraphMetaboliteEntity centralMetaboliteEntity,
			KeggGlycanMetaboliteEntity entity) {
		
		List<GraphMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (KeggGlycanMetaboliteCrossreferenceEntity xref : entity.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			GraphMetaboliteProxyEntity proxy = new GraphMetaboliteProxyEntity();
			proxy.setEntry(dbEntry);
			proxy.setMajorLabel(dbLabel);
			proxy.addLabel(METABOLITE_LABEL);
			crossreferences.add(proxy);
		}
	}
	

}
