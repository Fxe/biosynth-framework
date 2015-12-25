package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

public class LayoutNode extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;
  
  public static enum LayoutNodeType {
    SPECIE, REACTION, REACTION_MARKER
  }

  @MetaProperty
  public String label;
  
  @MetaProperty
  public SubcellularCompartment compartment;
  
  @MetaProperty
  public LayoutNodeType type;
  
  @MetaProperty
  public Double x;
  
  @MetaProperty
  public Double y;
  
  public Map<String, Map<Long, String>> annotation = new HashMap<> ();
  
  public void addAnnotation(String database, long id, String entry) {
    if (!this.annotation.containsKey(database)) {
      this.annotation.put(database, new HashMap<Long, String> ());
    }
    this.annotation.get(database).put(id, entry);
  }
  
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return String.format("LayoutNode[%d:%s, %.2f, %.2f]", id, type, x, y);
  }
}
