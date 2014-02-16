package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public class MnxMetaboliteStageLoader extends AbstractMetaboliteStageTransform<MnxMetaboliteEntity, MnxMetaboliteCrossReferenceEntity> {

	@Override
	public MetaboliteStga etlTransform(MnxMetaboliteEntity cpd) {
		MetaboliteStga cpd_stga = new MetaboliteStga();
		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setDescription( cpd.getDescription());
		cpd_stga.setClass_( cpd.getMetaboliteClass());
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteXrefGroupDim( this.generateXrefGroup(cpd.getCrossReferences()));
		cpd_stga.setMetaboliteInchiDim( this.generateInChI(cpd.getInChI()));
		cpd_stga.setMetaboliteSmilesDim( this.generateSmiles(cpd.getSmiles()));
		List<String> names = new ArrayList<> ();
		//add name if not null or contains at least one character
		if (cpd.getName() != null && cpd.getName().trim().length() > 0) names.add(cpd.getName());
		
		cpd_stga.setMetaboliteNameGroupDim( this.generateNames(names));
		return cpd_stga;
	}

}
