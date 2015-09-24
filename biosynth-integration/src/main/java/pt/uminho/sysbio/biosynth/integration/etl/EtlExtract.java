package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;
import java.util.List;

public interface EtlExtract<SRC> {
	public SRC extract(Serializable id);
	public List<Serializable> getAllKeys();
	public List<SRC> extractAll();
}
