package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

public class KeggOrthologyEntity extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;
  
  @MetaProperty
  protected String definition;
  
  protected Set<String> reactions = new HashSet<>();
  protected Set<String> cog = new HashSet<>();
  protected Set<String> go = new HashSet<>();
  protected Set<String> modules = new HashSet<>();
  protected Set<String> pathways = new HashSet<>();
  protected Map<String, Set<String>> genes = new HashMap<>();

  public String getDefinition() { return definition;}
  public void setDefinition(String definition) { this.definition = definition;}

  public Set<String> getReactions() { return reactions;}
  public void setReactions(Set<String> reactions) {
    if (reactions != null) {
      this.reactions = new HashSet<>(reactions);
    }
  }

  public Set<String> getCog() {
    return cog;
  }

  public void setCog(Set<String> cog) {
    this.cog = cog;
  }

  public Set<String> getGo() {
    return go;
  }

  public void setGo(Set<String> go) {
    this.go = go;
  }

  public Set<String> getModules() { return modules;}
  public void setModules(Set<String> modules) { this.modules = modules;}

  public Set<String> getPathways() { return pathways;}
  public void setPathways(Set<String> pathways) { this.pathways = pathways;}

  public Map<String, Set<String>> getGenes() {
    return genes;
  }

  public void setGenes(Map<String, Set<String>> genes) {
    this.genes = genes;
  }
  
  
}
