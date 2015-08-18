package pt.uminho.sysbio.biosynthframework.core.data.io.migration;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class DefaultMetaboliteDaoMigrationImpl<M extends GenericMetabolite>
implements MetaboliteDaoMigration<M, MetaboliteDao<M>, MetaboliteDao<M>> {

	@Override
	public void migrate(MetaboliteDao<M> source, MetaboliteDao<M> destination) {
		
	}

}
