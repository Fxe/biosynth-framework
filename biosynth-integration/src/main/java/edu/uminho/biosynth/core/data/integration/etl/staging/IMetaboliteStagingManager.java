package edu.uminho.biosynth.core.data.integration.etl.staging;

import java.util.Set;

import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameGroupDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteXrefGroupDim;

public interface IMetaboliteStagingManager {
	
	public final static String NULL_INCHI = "INCHI_NOT_FOUND";
	public final static String NULL_FORMULA = "FORMULA_NOT_FOUND";
	public final static String NULL_SMILES = "SMILES_NOT_FOUND";
	
	public MetaboliteServiceDim createOrGetService(MetaboliteServiceDim service);
	public MetaboliteSmilesDim getNullSmilesDim();
	public MetaboliteInchiDim getNullInchiDim();
	public MetaboliteInchiDim getInvalidInchiDim(String errorType, String longMsg);
	public MetaboliteFormulaDim getNullFormulaDim();
	public MetaboliteXrefGroupDim getNullXrefGroupDim();
	public MetaboliteNameGroupDim getNullNameGroupDim();
	public MetaboliteXrefGroupDim createOrGetXrefGroupDim(Set<Integer> xrefIds);
	public MetaboliteNameGroupDim createOrGetNameGroupDim(Set<Integer> namesIds);
}
