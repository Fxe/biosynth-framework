package pt.uminho.sysbio.biosynthframework;

public enum EntityType {
  
  DRAIN, //reactions of type supply or sink 
  SINK,
  DEMAND,
  
  /**
   * SBO:0000395 (encapsulating process)
   * parent of ATPM and biomass
   */
  ENCAPSULATIING_PROCESS,
  
  /**
   * SBO:0000629 (biomass production)
   * virtual reaction to define growth or certain objetives
   */
  BIOMASS,
  
  /**
   * SBO:0000630 (ATP maintenance)
   * NGAM
   */
  ATPM,

  DRAIN_COUPLED, //A+B => 0
  TRANSLOCATION, //reactions that involves two or more compartments
  TRASNPORT_UNIPORTER,
  TRASNPORT_SYMPORTER,
  TRASNPORT_ANTIPORTER,
  AUXILIAR, //virtual reactions to group virtual components
  METABOLITE,
  REACTION, //standard reaction operators
  MODEL_REACTION,
  
  CURRENCY, //componenents that to not play main route role
  SPECIE, 
  SPECIE_BOUNDARY, //boundary
  GENE,
  VIRTUAL,
  ERROR, //invalid objects
  MODEL_COMPARTMENT,
  COMPARTMENT,
}
