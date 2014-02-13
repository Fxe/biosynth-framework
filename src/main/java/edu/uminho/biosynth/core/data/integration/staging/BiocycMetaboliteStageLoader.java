package edu.uminho.biosynth.core.data.integration.staging;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;

public class BiocycMetaboliteStageLoader extends AbstractMetaboliteStageLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossReferenceEntity> {
	
	@Override
	public MetaboliteStga stageMetabolite(BioCycMetaboliteEntity cpd) {
		MetaboliteStga cpd_stga = new MetaboliteStga();
		cpd_stga.setNumeryKey(cpd.getId());
		cpd_stga.setTextKey(cpd.getEntry());
		cpd_stga.setFormula(cpd.getFormula());
		cpd_stga.setComment( cpd.getComment());
		cpd_stga.setDescription( cpd.getDescription());
		cpd_stga.setClass_( cpd.getMetaboliteClass());
		cpd_stga.setMetaboliteFormulaDim( this.generateFormula(cpd.getFormula()));
		cpd_stga.setMetaboliteXrefGroup( this.generateXrefGroup(cpd.getCrossReferences()));
		cpd_stga.setMetaboliteInchiDim( this.generateInChI(cpd.getInChI()));
		cpd_stga.setMetaboliteSmilesDim( this.generateSmiles(cpd.getSmiles()));
		
		return cpd_stga;
	}
}
