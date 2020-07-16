package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.integration.ReactionTMatcher;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;

public abstract class AbstractMetabolicModelReactionIntegration<ID, RXN extends AbstractBiosynthEntity> implements MetabolicModelReactionIntegration {
  
  private static final Logger logger = LoggerFactory.getLogger(AbstractMetabolicModelReactionIntegration.class);
  
  public Dataset<String, String, Tuple2<Object>> report = new Dataset<>();
  public Dataset<String, String, Object> treport = new Dataset<>();
  public Dataset<String, String, Object> mreport = new Dataset<>();
  private final Function<Set<ID>, ID> selectFilter;
  public Set<ID> excludeIds = new HashSet<>();
  public Function<RXN, CompartmentalizedStoichiometry<ID, Integer>> convertToStoich;
  public Function<RXN, Boolean> isBasic;
  public BiosDao<RXN> dao;
  private final Function<ID, ID> spiToCpdTranslateFunction;
  
  public AbstractMetabolicModelReactionIntegration(Function<ID, ID> spiToCpdTranslateFunction,
                                                   Function<Set<ID>, ID> selectFilter) {
    
    this.spiToCpdTranslateFunction = spiToCpdTranslateFunction;
    this.selectFilter = selectFilter;
  }
  
  public<CMP> CompartmentalizedStoichiometry<ID, CMP> mapToUniversals(
      CompartmentalizedStoichiometry<ID, CMP> aa) {
    CompartmentalizedStoichiometry<ID, CMP> ustoich = new CompartmentalizedStoichiometry<>();
//    logger.debug("[{}] {}", mrxnNode.getSid(), aa);
    for (Pair<ID, CMP> p : aa.stoichiometry.keySet()) {
      ID spiId = p.getLeft();
      double value = aa.stoichiometry.get(p);
      ustoich.add(spiToCpdTranslateFunction.apply(spiId), p.getRight(), value);
//      if (map.containsKey(spiId)) {
//        CPDID cpdId = map.get(spiId);
//        ustoich.add(cpdId, p.getRight(), value);
//      } else {
//        ustoich.add(spiId, p.getRight(), value);
//      }
//      BiosModelSpeciesNode spiNode = new BiosModelSpeciesNode(service.getNodeById(spiId), null);
//      long id = spiNode.getId();
//      logger.debug("[{}] {} {} -> {}", mrxnNode.getSid(), p, value, id);
    }
    
    return ustoich;
  }
  
  public<T, CMP> void aaa(ID sid,
                     CompartmentalizedStoichiometry<ID, CMP> aa,
                     ReactionTMatcher<T, ID> matcher, 
                     Set<ID> excludeIds,
                     Dataset<ID, String, Object> report) {
//    String sid = mrxnNode.getSid();
//    CompartmentalizedStoichiometry<Long, Long> aa = mrxnNode.getCompartmentalizedStoichiometry(1.0);
    
    if (aa.getCompartments().size() > 0) {
      CompartmentalizedStoichiometry<ID, CMP> ustoich = mapToUniversals(aa);
      if (excludeIds != null) {
        for (ID id : excludeIds) {
          ustoich.remove(id);
        }
      }
//      System.out.println("universal " + ustoich);
      if (ustoich.getCompartments().size() > 0) {
        Set<ID> mresult = matcher.match(ustoich);
        
        logger.debug("{} {}", ustoich, mresult);
//        print(ustoich);
        if (mresult == null || mresult.isEmpty()) {
          CompartmentalizedStoichiometry<ID, CMP> ustoichRev = new CompartmentalizedStoichiometry<>();
          for (Pair<ID, CMP> p : ustoich.stoichiometry.keySet()) {
            double v = ustoich.stoichiometry.get(p);
            ustoichRev.stoichiometry.put(p, -1 * v);
          }
          
          mresult = matcher.match(ustoichRev);
          
          logger.debug("{} {}", ustoichRev, mresult);
          
        }
        Set<String> names = new HashSet<>();
        ID best = null;
        if (selectFilter != null) {
          best = selectFilter.apply(mresult);
        }
//        int s = 0;
//        for (String n : names) {
//          if (sid.contains(n) && s < n.length()) {
//            best = n;
//            s = n.length();
//          }
//        }
//        if (best == null) {
//          for (String n : names) {
//            if (sid.toLowerCase().contains(n.toLowerCase()) && s < n.length()) {
//              best = n;
//              s = n.length();
//            }
//          }
//        }
        if (mresult != null && !mresult.isEmpty()) {
          report.add(sid, "ustoich", ustoich);
          report.add(sid, "mresult", mresult);
          report.add(sid, "names", names);
          report.add(sid, "best", best);
        }
      }
    }
  }
  
  public abstract void integrateTranslocation();
  public abstract void integrateBasic(Set<ID> excludeIds);
  
  public IntegrationMap<String, ReactionMajorLabel> integrate() {
    IntegrationMap<String, ReactionMajorLabel> integration = new IntegrationMap<>();
    
    integrateTranslocation();
    integrateBasic(excludeIds);
    
    Set<String> both = Sets.intersection(treport.keySet(), mreport.keySet());
    Set<String> columns = Sets.union(treport.getColumns(), mreport.getColumns());
    
    for (String rxnEntry : both) {
      for (String col : columns) {
        report.add(rxnEntry, col, 
            new Tuple2<Object>(
                treport.get(rxnEntry).get(col), 
                mreport.get(rxnEntry).get(col)));
      }
    }
    for (String rxnEntry : treport.keySet()) {
      if (!both.contains(rxnEntry)) {
        for (String col : columns) {
          report.add(rxnEntry, col, 
              new Tuple2<Object>(
                  treport.get(rxnEntry).get(col), 
                  null));
        }
      }
    }
    for (String rxnEntry : mreport.keySet()) {
      if (!both.contains(rxnEntry)) {
        for (String col : columns) {
          report.add(rxnEntry, col, 
              new Tuple2<Object>(
                  null, 
                  mreport.get(rxnEntry).get(col)));
        }
      }
    }
    
    return integration;
  }
}
