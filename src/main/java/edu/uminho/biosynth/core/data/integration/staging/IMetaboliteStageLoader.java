package edu.uminho.biosynth.core.data.integration.staging;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;

public interface IMetaboliteStageLoader<T extends GenericMetabolite> {
	public MetaboliteStga stageMetabolite(T cpd);
}
