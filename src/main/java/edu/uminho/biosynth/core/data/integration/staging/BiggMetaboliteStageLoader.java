package edu.uminho.biosynth.core.data.integration.staging;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;

public class BiggMetaboliteStageLoader extends AbstractMetaboliteStageLoader<BiggMetaboliteEntity, BiggMetaboliteCrossReferenceEntity> {

	@Override
	public MetaboliteStga stageMetabolite(BiggMetaboliteEntity cpd) {

		MetaboliteStga cpd_stga = new MetaboliteStga();
		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setDescription( cpd.getDescription());
		cpd_stga.setClass_( cpd.getMetaboliteClass());
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteXrefGroup( this.generateXrefGroup(cpd.getCrossReferences()));
		
		return cpd_stga;
	}
}
