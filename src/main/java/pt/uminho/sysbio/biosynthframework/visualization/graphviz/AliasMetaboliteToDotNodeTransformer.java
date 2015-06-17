package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public class AliasMetaboliteToDotNodeTransformer<M extends Metabolite> 
extends MetaboliteToDotNodeTransformer<M> {
  
  private Map<String, String> aliasMap = new HashMap<> ();
  private Map<String, String> colorMap = new HashMap<> ();
  
  public AliasMetaboliteToDotNodeTransformer() { }
  public AliasMetaboliteToDotNodeTransformer(Map<String, String> aliasMap) {
    this.aliasMap = aliasMap;
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
      node.setLabel(cpd.getEntry());
    }
    
    if ((color = this.colorMap.get(cpd.getEntry())) != null) {
      node.setColor(color);
      node.setFontcolor(color);
    }


    return node;
  }

}
