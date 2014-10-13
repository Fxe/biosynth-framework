package pt.uminho.sysbio.biosynthframework.core.data.io.migration;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public interface MetaboliteDaoMigration<M extends GenericMetabolite, SRC extends MetaboliteDao<M>, DST extends MetaboliteDao<M>> {
	public void migrate(SRC source, DST destination);
}
