package pt.uminho.sysbio.biosynthframework.visualization.escher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscherModel {
  public List<EscherModelMetabolite> metabolites = new ArrayList<> ();
  public List<EscherModelReaction> reactions = new ArrayList<> ();
  public List<EscherModelGene> genes = new ArrayList<> ();
  public Map<String, String> compartments = new HashMap<>();
  public double version;
  public String id;
  
  public EscherModelMetabolite getMetabolite(String id) {
    for (EscherModelMetabolite m : metabolites) {
      if (m.id.equals(id)) {
        return m;
      }
    }
    return null;
  }
}
