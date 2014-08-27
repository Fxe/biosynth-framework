package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggGlycanMetaboliteEntity;

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
