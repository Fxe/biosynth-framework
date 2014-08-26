package edu.uminho.biosynth.core.data.io.migration;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public interface MetaboliteDaoMigration<M extends GenericMetabolite, SRC extends MetaboliteDao<M>, DST extends MetaboliteDao<M>> {
	public void migrate(SRC source, DST destination);
}
