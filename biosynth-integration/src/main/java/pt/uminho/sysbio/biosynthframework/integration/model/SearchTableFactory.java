package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashSet;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public class SearchTableFactory {
  
  protected Set<MetaboliteMajorLabel> dbs = new HashSet<> ();
  
  protected final BiodbService biodbService;
  
  public SearchTableFactory(BiodbService biodbService) {
    this.biodbService = biodbService;
  }
  
  public SearchTableFactory withDatabase(MetaboliteMajorLabel db) {
    this.dbs.add(db);
    return this;
  }
  
  
  public SearchTable<MetaboliteMajorLabel, String> build() {
    SearchTable<MetaboliteMajorLabel, String> searchTable = new SearchTable<>();
    
    for (MetaboliteMajorLabel database : dbs) {
      for (long id : biodbService.getIdsByDatabaseAndType(
          database.toString(), GlobalLabel.Metabolite.toString())) {
        searchTable.add(biodbService.getEntryById(id), biodbService.getEntryById(id), 
            database, "entry");
      }
    }
    
    return searchTable;
  }
}
