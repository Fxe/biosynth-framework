package pt.uminho.sysbio.biosynthframework;

public enum EntityType {
  BIOMASS, //virtual reaction to define growth or certain objetives
  DRAIN, //reactions of type supply or sink 
  DRAIN_COUPLED, //A+B => 0
  TRANSLOCATION, //reactions that involves two or more compartments
  TRASNPORT_UNIPORTER,
  TRASNPORT_SYMPORTER,
  TRASNPORT_ANTIPORTER,
  AUXILIAR, //virtual reactions to group virtual components
  REACTION, //standard reaction operators
  ATPM, //deprecated ... :( (fixed constant)
  CURRENCY, //componenents that to not play main route role
  SPECIE, 
  SPECIE_BOUNDARY, //boundary
  GENE,
  VIRTUAL,
  ERROR, //invalid objects
}
