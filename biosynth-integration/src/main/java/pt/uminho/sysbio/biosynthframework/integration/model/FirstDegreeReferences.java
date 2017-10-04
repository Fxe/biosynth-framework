package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public class FirstDegreeReferences implements IntegrationEngine {

  private static final Logger logger = LoggerFactory.getLogger(FirstDegreeReferences.class);
  
//  IntegrationMap<String, MetaboliteMajorLabel> integration;
  public boolean acceptProxy = false;
  private final BiodbService biodbService;
  
//  public FirstDegreeReferences(IntegrationMap<String, MetaboliteMajorLabel> integration, BiodbService biodbService) {
//    this.integration = integration;
//    this.biodbService = biodbService;
//  }
  
  public FirstDegreeReferences(BiodbService biodbService) {
    this.biodbService = biodbService;
  }
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate(IntegrationMap<String, MetaboliteMajorLabel> imap) {
    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<> ();
    for (String id : imap.keySet()) {
      Map<MetaboliteMajorLabel, Set<String>> i = imap.get(id);
      for (MetaboliteMajorLabel db : i.keySet()) {
        for (String cpdEntry : i.get(db)) {
          Long cpdId = biodbService.getIdByEntryAndDatabase(cpdEntry, db.toString());
//          System.out.println(cpdId + " " + biodbService.getEntryById(cpdId));
          if (cpdId == null && MetaboliteMajorLabel.BiGG2.equals(db)) {
            Set<Long> allIds = biodbService.getIdByProperty(cpdEntry, "alias");
            Set<Long> bigg2Ids = new HashSet<> ();
            for (long id_ : allIds) {
              String db_ = biodbService.getDatabaseById(id_);
              if (MetaboliteMajorLabel.BiGG2.toString().equals(db_)) {
                bigg2Ids.add(id_);
              }
              if (!bigg2Ids.isEmpty()) {
                cpdId = bigg2Ids.iterator().next();
              }
            }
          }
          if (cpdId != null) {
            Set<Long> refIds = biodbService.getReferencesBy(cpdId);
            
            if (refIds == null) {
              refIds = new HashSet<> ();
            }
            for (long refId : refIds) {
              boolean proxy = biodbService.isProxy(refId);
              if (!proxy || acceptProxy) {
                MetaboliteMajorLabel database = MetaboliteMajorLabel.valueOf(biodbService.getDatabaseById(refId));
                String refEntry = biodbService.getEntryById(refId);
                result.addIntegration(id, database, refEntry);
//                for (long idss : refIds) {
//                  System.out.println("\t" + biodbService.getEntryById(idss));
//                }
              }
            }
          } else {
            logger.warn("not found {} @ {}", cpdEntry, db);
          }
        }
      }
    }
    
    return result;
  }
}
