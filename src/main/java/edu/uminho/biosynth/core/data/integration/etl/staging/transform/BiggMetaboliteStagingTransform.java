package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public class BiggMetaboliteStagingTransform extends AbstractMetaboliteStagingTransform<BiggMetaboliteEntity, BiggMetaboliteCrossReferenceEntity> {

	@Override
	public MetaboliteStga etlTransform(BiggMetaboliteEntity cpd) {
		MetaboliteStga cpd_stga = new MetaboliteStga();
//		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		
		String mass = null;
		String charge = null;
		if (cpd.getCharge() != null) {
			charge = cpd.getCharge()<-10000 ? null : Integer.toString(cpd.getCharge());
		}
		
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteXrefGroupDim( this.generateXrefGroup(cpd.getCrossReferences()));
		cpd_stga.setMetaboliteInchiDim( this.generateInChI(null));
		cpd_stga.setMetaboliteSmilesDim( this.generateSmiles(null));
		List<String> names = new ArrayList<> ();
		//add name if not null or contains at least one character
		if (cpd.getName() != null && cpd.getName().trim().length() > 0) names.add(cpd.getName());
		
		cpd_stga.setMetaboliteNameGroupDim( this.generateNames(names));
		
		cpd_stga.setCharge(charge);
		cpd_stga.setComment( null);
		cpd_stga.setRemark(null);
		cpd_stga.setDescription( cpd.getDescription().trim().isEmpty()?null:cpd.getDescription());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setGibbs(null);
		cpd_stga.setMass(mass);
		cpd_stga.setClass_( cpd.getMetaboliteClass());

		return cpd_stga;
	}
}
