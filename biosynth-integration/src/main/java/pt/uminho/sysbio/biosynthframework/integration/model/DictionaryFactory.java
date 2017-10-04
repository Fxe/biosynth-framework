package pt.uminho.sysbio.biosynthframework.integration.model;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public class DictionaryFactory {
  
  private final BiodbService biodbService;
  
  public DictionaryFactory(BiodbService biodbService) {
    this.biodbService = biodbService;
  }
  
  
  public Dictionary buildBiggDictionary() {
    Dictionary dict = new Dictionary();
    
    for (long id : biodbService.getIdsByDatabaseAndType(
        MetaboliteMajorLabel.BiGG.toString(), 
        GlobalLabel.Metabolite.toString())) {
      System.out.println(id);
    }
    
    return dict;
  }
}
