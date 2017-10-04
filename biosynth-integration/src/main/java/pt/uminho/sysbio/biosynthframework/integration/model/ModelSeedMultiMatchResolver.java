package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pt.uminho.sysbio.biosynthframework.BFunction;

public class ModelSeedMultiMatchResolver implements BFunction<List<Set<String>>, List<Set<String>>>{

  @Override
  public List<Set<String>> apply(List<Set<String>> a) {
    List<Set<String>> result = new ArrayList<> ();
    for (int i = 0; i < a.size(); i++) {
      Set<String> s = a.get(i);
      if (s.size() > 1) {
        Map<Integer, String> tmap = new TreeMap<>();
        for (String id : s) {
          int nseq = Integer.parseInt(id.replace("cpd", "0"));
          tmap.put(nseq, id);
        }
        int low = tmap.keySet().iterator().next();
        Set<String> single = new HashSet<> ();
        single.add(tmap.get(low));
        result.add(single);
      } else {
        result.add(new HashSet<> (s));
      }
    }
    return result;
  }

}