package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public class AliasMetaboliteToDotNodeTransformer<M extends Metabolite> 
extends MetaboliteToDotNodeTransformer<M> {
  
  private Map<String, String> aliasMap = new HashMap<> ();
  private Map<String, String> colorMap = new HashMap<> ();
  private Map<String, Map<String, Object>> propertiesMap = new HashMap<> ();
  
  public AliasMetaboliteToDotNodeTransformer() { }
  public AliasMetaboliteToDotNodeTransformer(Map<String, String> aliasMap) {
    this.aliasMap = aliasMap;
  }
  
  public void setProperty(String cpd, String property, Object value) {
    if (!propertiesMap.containsKey(cpd)) {
      propertiesMap.put(cpd, new HashMap<String, Object> ());
    }
    
    propertiesMap.get(cpd).put(property, value);
  }
  
  public void setAlias(String cpd, String alias) {
    this.aliasMap.put(cpd, alias);
  }
  
  public void setColor(String cpd, String color) {
    this.colorMap.put(cpd, color);
  }
  
  @Override
  public DotNode toDotNode(M cpd) {
    DotNode node = new DotNode();
    String alias = null;
    String color = null;
    
    if ((alias = this.aliasMap.get(cpd.getEntry())) != null) {
      node.setLabel(alias);
    } else {
      node.setLabel(String.format("\"%s\"", cpd.getEntry()));
    }
    
    if ((color = this.colorMap.get(cpd.getEntry())) != null) {
      node.setColor(color);
      node.setFontcolor(color);
    }
    
    if (propertiesMap.containsKey(cpd.getEntry())) {
      Map<String, Object> props = propertiesMap.get(cpd.getEntry());
      for (String k : props.keySet()) {
        node.setProperty(k, props.get(k));
      }
    }
    
    node.setShape(GraphVizShape.PLAINTEXT);

    return node;
  }

}
