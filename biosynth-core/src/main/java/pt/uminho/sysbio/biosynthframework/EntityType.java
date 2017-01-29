package pt.uminho.sysbio.biosynthframework;

public enum EntityType {
  BIOMASS, //virtual reaction to define growth or certain objetives
  DRAIN, //reactions of type supply or sink 
  TRANSLOCATION, //reactions that involves two or more compartments 
  AUXILIAR, //virtual reactions to group virtual components
  REACTION, //standard reaction operators
  ATPM, //deprecated ... :( (fixed constant)
  CURRENCY, //componenents that to not play main route role
  SPECIE, 
  GENE,
  VIRTUAL,
  ERROR, //invalid objects
}
