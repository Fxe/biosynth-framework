package edu.uminho.biosynth.integration.etl.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggCompoundMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataMetaboliteProxyEntity;
import edu.uminho.biosynth.integration.CentralMetaboliteEntity;
import edu.uminho.biosynth.integration.etl.EtlTransform;

public class KeggCompoundTransform 
implements EtlTransform<KeggCompoundMetaboliteEntity, CentralMetaboliteEntity> {

	@Override
	public CentralMetaboliteEntity etlTransform(
			KeggCompoundMetaboliteEntity entity) {
		
		CentralMetaboliteEntity centralMetaboliteEntity = new CentralMetaboliteEntity();
		centralMetaboliteEntity.setMajorLabel("LigandCompound");
		centralMetaboliteEntity.addLabel("Metabolite");
		centralMetaboliteEntity.putProperty("entry", entity.getEntry());
		centralMetaboliteEntity.putProperty("formula", entity.getFormula());
		
		List<CentralDataMetaboliteProxyEntity> crossreferences = new ArrayList<> ();
		for (KeggCompoundMetaboliteCrossreferenceEntity xref : entity.getCrossReferences()) {
			String dbLabel = BioDbDictionary.translateDatabase(xref.getRef());
			String dbEntry = xref.getValue(); //Also need to translate if necessary
			CentralDataMetaboliteProxyEntity proxy = new CentralDataMetaboliteProxyEntity();
			proxy.setEntry(dbEntry);
			proxy.setMajorLabel(dbLabel);
			proxy.addLabel("Metabolite");
			crossreferences.add(proxy);
		}
		centralMetaboliteEntity.setCrossreferences(crossreferences);
		
		return centralMetaboliteEntity;
	}

}
