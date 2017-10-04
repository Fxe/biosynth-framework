package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BFunction;

public class BiGG2AliasMultiMatchResolver implements BFunction<List<Set<String>>, List<Set<String>>> {
  private final BiodbService biodbService;
  
  public BiGG2AliasMultiMatchResolver(BiodbService biodbService) {
    this.biodbService = biodbService;
  }

  @Override
  public List<Set<String>> apply(List<Set<String>> a) {
    List<Set<String>> result = new ArrayList<> ();
    for (int i = 0; i < a.size(); i++) {
      Set<String> bigg2entries = a.get(i);
      Set<String> alias = new HashSet<> ();
      for (String e : bigg2entries) {
        Long id = biodbService.getIdByEntryAndDatabase(e, MetaboliteMajorLabel.BiGG2.toString());
        if (id != null) {
          e = biodbService.getEntityProperty(id, "alias");
        }
        alias.add(e);
      }
      result.add(alias);
    }
    return result;
  }
}
