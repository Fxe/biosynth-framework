package edu.uminho.biosynth.core.data.integration.etl;

import java.io.Serializable;
import java.util.List;

public interface IEtlExtract<SRC> {
	public SRC extract(Serializable id);
	public List<SRC> extractAll();
}
