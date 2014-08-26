package edu.uminho.biosynth.core.data.io.migration;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;

public class DefaultMetaboliteDaoMigrationImpl<M extends GenericMetabolite>
implements MetaboliteDaoMigration<M, MetaboliteDao<M>, MetaboliteDao<M>> {

	@Override
	public void migrate(MetaboliteDao<M> source, MetaboliteDao<M> destination) {
		
	}

}
