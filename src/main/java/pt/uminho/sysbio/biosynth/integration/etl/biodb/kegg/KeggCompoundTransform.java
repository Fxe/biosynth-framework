package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.AbstractMetaboliteTransform;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggCompoundMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;

public class KeggCompoundTransform extends AbstractMetaboliteTransform<KeggCompoundMetaboliteEntity> {

	private static final String KEGG_COMPOUND_METABOLITE_LABEL = "LigandCompound";
	
	public KeggCompoundTransform() {
		super(KEGG_COMPOUND_METABOLITE_LABEL);
	}

	@Override
	protected void configureAdditionalPropertyLinks(
			CentralMetaboliteEntity centralMetaboliteEntity,
			KeggCompoundMetaboliteEntity entity) {
		
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyEntity(entity.getInchi(), METABOLITE_INCHI_LABEL));
		centralMetaboliteEntity.addPropertyEntity(
				this.buildPropertyEntity(entity.getSmiles(), METABOLITE_SMILE_LABEL));
	}

	@Override
	protected void configureCrossreferences(
			CentralMetaboliteEntity centralMetaboliteEntity,
			KeggCompoundMetaboliteEntity entity) {
		
		List<CentralMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		
		for (KeggCompoundMetaboliteCrossreferenceEntity xref : entity.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			CentralMetaboliteProxyEntity proxy = new CentralMetaboliteProxyEntity();
			proxy.setEntry(dbEntry);
			proxy.setMajorLabel(dbLabel);
			proxy.addLabel("Metabolite");
			crossreferences.add(proxy);
		}
		
		centralMetaboliteEntity.setCrossreferences(crossreferences);
	}

}