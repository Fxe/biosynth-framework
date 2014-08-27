package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;

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
