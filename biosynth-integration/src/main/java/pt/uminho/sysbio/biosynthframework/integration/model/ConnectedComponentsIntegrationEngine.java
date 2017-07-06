package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public class ConnectedComponentsIntegrationEngine implements IntegrationEngine {

  private final ConnectedComponents<String> ccs;
  
  public ConnectedComponentsIntegrationEngine(ConnectedComponents<String> ccs) {
    this.ccs = ccs;
  }
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate(IntegrationMap<String, MetaboliteMajorLabel> imap) {
    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<> ();
    
    for (String entry : imap.keySet()) {
      Map<MetaboliteMajorLabel, Set<String>> a = imap.get(entry);
      Set<Pair<String, MetaboliteMajorLabel>> keys = new HashSet<> ();
      for (MetaboliteMajorLabel b : a.keySet()) {
        Set<String> ss = a.get(b);
        if (ss != null) {
          for (String s : ss) {
            keys.add(new ImmutablePair<>(s, b));
          }
        }
      }
      
      //expand keys
      Set<String> expand = new HashSet<> ();
      for (Pair<String, MetaboliteMajorLabel> e : keys) {
        if (ccs.containsValue(String.format("%s@%s", e.getLeft(), e.getRight()))) {
          expand.addAll(ccs.getConnectedComponentOf(String.format("%s@%s", e.getLeft(), e.getRight())));
        }
      }
      
      for (String ref : expand) {
        String aa = ref.split("@")[0];
        MetaboliteMajorLabel db = MetaboliteMajorLabel.valueOf(ref.split("@")[1]);
        result.addIntegration(entry, db, aa);
      }
      
//      biodbService.expandReferences(cpdIdSet, -1);
    }
    
    return result;
  }

}
