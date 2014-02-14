package edu.uminho.biosynth.core.data.integration.staging;

import java.util.ArrayList;
import java.util.List;

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
		cpd_stga.setMetaboliteXrefGroupDim( this.generateXrefGroup(cpd.getCrossReferences()));
		List<String> names = new ArrayList<> ();
		names.add(cpd.getName());
		cpd_stga.setMetaboliteNameGroupDim( this.generateNames(names));
		return cpd_stga;
	}
}
