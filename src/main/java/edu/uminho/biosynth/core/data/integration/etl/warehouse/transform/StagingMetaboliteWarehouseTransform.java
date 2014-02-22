package edu.uminho.biosynth.core.data.integration.etl.warehouse.transform;

import edu.uminho.biosynth.core.data.integration.etl.IEtlTransform;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameBridge;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.etl.warehouse.components.MetaboliteFact;

public class StagingMetaboliteWarehouseTransform implements IEtlTransform<MetaboliteStga, MetaboliteFact>{

	@Override
	public MetaboliteFact etlTransform(MetaboliteStga cpd) {
		MetaboliteFact res = new MetaboliteFact();
		res.setNumeryKey(cpd.getNumeryKey());
		res.setTextKey(cpd.getTextKey());
		for (MetaboliteNameBridge nameBridge : cpd.getMetaboliteNameGroupDim().getMetaboliteNameBridges()) {
//			res.getStagedMetaboliteNameDim().add(nameBridge.getMetaboliteNameDim().getId());
		}
//		res.setMetaboliteNameGroupDim();
//		res.setMetaboliteChargeDim(metaboliteChargeDim);
		return res;
	}

}
