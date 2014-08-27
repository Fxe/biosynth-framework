package edu.uminho.biosynth.integration.etl.biodb.kegg;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;
import edu.uminho.biosynth.integration.CentralMetaboliteEntity;
import edu.uminho.biosynth.integration.etl.EtlTransform;

public class KeggDrugTransform 
implements EtlTransform<KeggDrugMetaboliteEntity, CentralMetaboliteEntity>{

	@Override
	public CentralMetaboliteEntity etlTransform(KeggDrugMetaboliteEntity entity) {

		CentralMetaboliteEntity centralMetaboliteEntity = new CentralMetaboliteEntity();
		centralMetaboliteEntity.setMajorLabel("LigandDrug");
		centralMetaboliteEntity.addLabel("Metabolite");
		centralMetaboliteEntity.putProperty("entry", entity.getEntry());
		centralMetaboliteEntity.putProperty("formula", entity.getFormula());
		
		return centralMetaboliteEntity;
	}

}
