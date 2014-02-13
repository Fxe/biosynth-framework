package edu.uminho.biosynth.core.data.integration.staging;

import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;

public class MnxMetaboliteStageLoader extends AbstractMetaboliteStageLoader<MnxMetaboliteEntity, MnxMetaboliteCrossReferenceEntity> {

	@Override
	public MetaboliteStga stageMetabolite(MnxMetaboliteEntity cpd) {
		MetaboliteStga cpd_stga = new MetaboliteStga();
		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setDescription( cpd.getDescription());
		cpd_stga.setClass_( cpd.getMetaboliteClass());
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteXrefGroup( this.generateXrefGroup(cpd.getCrossReferences()));
		cpd_stga.setMetaboliteInchiDim( this.generateInChI(cpd.getInChI()));
		cpd_stga.setMetaboliteSmilesDim( this.generateSmiles(cpd.getSmiles()));
		
		return cpd_stga;
	}

}
