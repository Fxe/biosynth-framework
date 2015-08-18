package pt.uminho.sysbio.biosynthframework.io;

import pt.uminho.sysbio.biosynthframework.DefaultSubcellularCompartmentEntity;
import pt.uminho.sysbio.biosynthframework.ExtendedMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.ExtendedMetaboliteSpecie;
import pt.uminho.sysbio.biosynthframework.ExtendedModelMetabolite;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;

public interface ExtendedMetabolicModelDao extends MetabolicModelDao <
ExtendedMetabolicModelEntity,
ExtendedMetaboliteSpecie, 
ExtendedModelMetabolite,
OptfluxContainerReactionEntity,
DefaultSubcellularCompartmentEntity> {

}
