package pt.uminho.sysbio.biosynthframework.io;

import pt.uminho.sysbio.biosynthframework.DefaultMetaboliteSpecie;
import pt.uminho.sysbio.biosynthframework.DefaultModelMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.DefaultSubcellularCompartmentEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.MetabolicModelDao;

public interface OptfluxMetabolicModelDao extends MetabolicModelDao<
OptfluxContainerMetabolicModelEntity,
DefaultMetaboliteSpecie, 
DefaultModelMetaboliteEntity,
OptfluxContainerReactionEntity,
DefaultSubcellularCompartmentEntity> {

	

}
