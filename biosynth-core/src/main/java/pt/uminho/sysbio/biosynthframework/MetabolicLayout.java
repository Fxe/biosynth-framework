package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetabolicLayout extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;
  
  public static class LayoutEdge {
    public long fromNodeId;
    public long toNodeId;
  }
  
  public static class Wut {
    public String ai;
    public Map<String, Map<Long, String>> annotation = new HashMap<> ();
    
    public void addAnnotation(String database, long id, String entry) {
      if (!this.annotation.containsKey(database)) {
        this.annotation.put(database, new HashMap<Long, String> ());
      }
      this.annotation.get(database).put(id, entry);
    };
  }
  
  public static class LayoutReaction {
    public LayoutNode primary;
    public List<LayoutNode> markers = new ArrayList<> ();
  }
  
  public Map<String, Map<String, Map<String, Object>>> reactions = new HashMap<> ();
  public Map<String, Map<String, Set<Long>>> metabolites = new HashMap<> ();
  public Map<?, ?> cpd = new HashMap<> ();
  public Map<?, ?> rxn = new HashMap<> ();
  
  public Map<Long, LayoutNode> nodes = new HashMap<> ();
  public Map<Long, LayoutEdge> edges = new HashMap<> ();
  
  public void addLayoutNode(String database, String entry, LayoutNode node) {
    if (node.id == null || node.x == null || node.y == null) {
      throw new RuntimeException("Invalid LayoutNode");
    }
    if (!metabolites.containsKey(database)) {
      metabolites.put(database, new HashMap<String, Set<Long>> ());
    }
    if (!metabolites.get(database).containsKey(entry)) {
      metabolites.get(database).put(entry, new HashSet<Long> ());
    }
    metabolites.get(database).get(entry).add(node.id);
    nodes.put(node.id, node);
  }
  
  public void addLayoutReaction(String database, String entry, 
                                LayoutReaction rxn) {
    if (!reactions.containsKey(database)) {
      reactions.put(database, new HashMap<String, Map<String, Object>> ());
    }
    if (!reactions.get(database).containsKey(entry)) {
      reactions.get(database).put(entry, new HashMap<String, Object> ());
    }
    
    nodes.put(rxn.primary.id, rxn.primary);
    Set<Long> markers = new HashSet<> ();
    for (LayoutNode n : rxn.markers) {
      markers.add(n.id);
      nodes.put(n.id, n);
    }
    reactions.get(database).get(entry).put("rxn", rxn.primary.id);
    reactions.get(database).get(entry).put("markers", markers);
  }
}
