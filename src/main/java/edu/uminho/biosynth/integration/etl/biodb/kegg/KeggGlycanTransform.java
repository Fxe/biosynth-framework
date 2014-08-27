package edu.uminho.biosynth.integration.etl.biodb.kegg;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;
import edu.uminho.biosynth.integration.CentralMetaboliteEntity;
import edu.uminho.biosynth.integration.etl.EtlTransform;

public class KeggGlycanTransform 
implements EtlTransform<KeggGlycanMetaboliteEntity, CentralMetaboliteEntity>{

	@Override
	public CentralMetaboliteEntity etlTransform(
			KeggGlycanMetaboliteEntity entity) {

		CentralMetaboliteEntity centralMetaboliteEntity = new CentralMetaboliteEntity();
		centralMetaboliteEntity.setMajorLabel("LigandGlycan");
		centralMetaboliteEntity.addLabel("Metabolite");
		centralMetaboliteEntity.putProperty("entry", entity.getEntry());
		centralMetaboliteEntity.putProperty("formula", entity.getFormula());
		
		return centralMetaboliteEntity;
	}

}
