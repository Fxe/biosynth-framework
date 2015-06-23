package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.Reaction;

public class MetabolicNetworkDotGenerator {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetabolicNetworkDotGenerator.class);
  
  private String pad = ".75";
  private String ranksep = ".75";
  private String nodesep = ".25";
  private Set<String> replicateNode = new HashSet<> ();
  private Map<String, Metabolite> metaboliteMap = new HashMap<> ();
  private List<Reaction> reactionList = new ArrayList<> ();
  private MetaboliteToDotNodeTransformer<Metabolite> metaboliteTransformer = null;
  private ReactionToDotNodeTransformer<Reaction> reactionTransformer = null;
  
  
  public MetabolicNetworkDotGenerator() { }
    
  public String getPad() { return pad;}
  public void setPad(String pad) { this.pad = pad;}

  public String getRanksep() { return ranksep;}
  public void setRanksep(String ranksep) { this.ranksep = ranksep;}

  public String getNodesep() { return nodesep;}
  public void setNodesep(String nodesep) { this.nodesep = nodesep;}

  public void addNodeReplication(String node) {
    replicateNode.add(node);
  }
  
  public MetaboliteToDotNodeTransformer<Metabolite> getMetaboliteTransformer() {
    return metaboliteTransformer;
  }

  public void setMetaboliteTransformer(
      MetaboliteToDotNodeTransformer<Metabolite> metaboliteTransformer) {
    this.metaboliteTransformer = metaboliteTransformer;
  }
  
  public void setReactionTransformer(
      ReactionToDotNodeTransformer<Reaction> reactionTransformer) {
    this.reactionTransformer = reactionTransformer;
  }

  public void addReaction(Reaction rxn) {
    reactionList.add(rxn);
    for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
      if (!metaboliteMap.containsKey(cpdEntry)) {
        GenericMetabolite cpd = new GenericMetabolite();
        cpd.setEntry(cpdEntry);
        metaboliteMap.put(cpdEntry, cpd);
      }
    }
    
    for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
      if (!metaboliteMap.containsKey(cpdEntry)) {
        GenericMetabolite cpd = new GenericMetabolite();
        cpd.setEntry(cpdEntry);
        metaboliteMap.put(cpdEntry, cpd);
      }
    }
  }
  
  private Map<String, String> remap() {
    String prefix = "NODE";
    int counter = 0;
    Map<String, String> idMap = new HashMap<> ();
    for (String cpdEntry : metaboliteMap.keySet()) {
      idMap.put(cpdEntry, prefix + (counter++));
    }
    for (Reaction rxn : reactionList) {
      idMap.put(rxn.getEntry(), prefix + (counter++));
    }
    return idMap;
  }
  
  public String build() {
    Map<String, String> idMap = remap();
    Map<String, String> idMap_ = new HashMap<String, String> ();
    for (String k : idMap.keySet()) {
      String v = idMap.get(k);
      idMap_.put(v, k);
    }
    
    Set<Pair<String, String>> edgePairs = new HashSet<> ();
    List<String> lines = new ArrayList<> ();
    lines.add("digraph {");
    
//    lines.add("graph [pad=\".75\", ranksep=\"2\", nodesep=\"0.25\"];");
    lines.add(String.format("graph [pad=\"%s\", ranksep=\"%s\", nodesep=\"%s\"];", 
          pad, ranksep, nodesep));
    
    Map<String, String> replicationMap = new HashMap<> ();
    
    for (Reaction rxn : reactionList) {
      DotNode node = new DotNode();
      if (reactionTransformer != null) {
        node = reactionTransformer.toDotNode(rxn);
      } else {
        node.setLabel(String.format("\"%s\"", rxn.getEntry()));
        node.setShape(GraphVizShape.BOX);
      }
      
      node.id = idMap.get(rxn.getEntry());
      
      lines.add(node.toString());
      
      for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
        String src = idMap.get(cpdEntry);
        String dst = idMap.get(rxn.getEntry());
        if (replicateNode.contains(cpdEntry)) {
          String prev = src;
          src = String.format("%s_%s", src, dst);
          replicationMap.put(src, prev);
        }
        Pair<String, String> pair = new ImmutablePair<>(src, dst);
        edgePairs.add(pair);
      }
      for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
        String src = idMap.get(rxn.getEntry());
        String dst = idMap.get(cpdEntry);
        if (replicateNode.contains(cpdEntry)) {
          String prev = dst;
          dst = String.format("%s_%s", dst, src);
          replicationMap.put(dst, prev);
        }
        Pair<String, String> pair = new ImmutablePair<>(src, dst);
        edgePairs.add(pair);
      }
    }
    
    for (Metabolite cpd : metaboliteMap.values()) {
      if (!replicateNode.contains(cpd.getEntry())) {
        DotNode node = null;
        if (metaboliteTransformer != null) {
          node = metaboliteTransformer.toDotNode(cpd);
        } else {
          node = new DotNode();
          node.setLabel(String.format("\"%s\"", cpd.getEntry()));
          node.setShape(GraphVizShape.PLAINTEXT);
        }
        
        node.id = idMap.get(cpd.getEntry());
        lines.add(node.toString());
      }
    }
    
    for (String id : replicationMap.keySet()) {
      String virtualNode = replicationMap.get(id);
      String cpdEntry = idMap_.get(virtualNode);
      Metabolite cpd = metaboliteMap.get(cpdEntry);
      DotNode node = null;
      if (metaboliteTransformer != null) {
        node = metaboliteTransformer.toDotNode(cpd);
      } else {
        node = new DotNode();
        node.setLabel(String.format("\"%s\"", cpd.getEntry()));
        
      }
      node.setShape(GraphVizShape.PLAINTEXT);
      node.id = id;
      lines.add(node.toString());
    }
    
    
    for (Pair<String, String> p : edgePairs) {
//      String dotEdgeStr = String.format("%s->%s", p.getLeft(), p.getRight());
      String dotEdgeStr = String.format("%s->%s", p.getRight(), p.getLeft());
      lines.add(dotEdgeStr);
//      System.out.println(dotEdgeStr);
    }

    
//    for (Reaction rxn : reactionList) {
//      switch (rxn.getOrientation()) {
//        case LeftToRight:
//          for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
//            String dotEdgeStr = String.format("%s->%s", idMap.get(cpdEntry), idMap.get(rxn.getEntry()));
//            lines.add(dotEdgeStr);
//          }
//          for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
//            String dotEdgeStr = String.format("%s->%s", idMap.get(rxn.getEntry()), idMap.get(cpdEntry));
//            lines.add(dotEdgeStr);
//          }
//          break;
//        case RightToLeft:
//          for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
//            String dotEdgeStr = String.format("%s->%s", idMap.get(cpdEntry), idMap.get(rxn.getEntry()));
//            lines.add(dotEdgeStr);
//          }
//          for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
//            String dotEdgeStr = String.format("%s->%s", idMap.get(rxn.getEntry()), idMap.get(cpdEntry));
//            lines.add(dotEdgeStr);
//          }
//          break;
//        case Reversible:
//          for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
//            String dotEdgeStr = String.format("%s->%s", idMap.get(cpdEntry), idMap.get(rxn.getEntry()));
//            lines.add(dotEdgeStr);
//          }
//          for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
//            String dotEdgeStr = String.format("%s->%s", idMap.get(rxn.getEntry()), idMap.get(cpdEntry));
//            lines.add(dotEdgeStr);
//          }
//          break;
//        default:
//          LOGGER.warn("!!!! {}", rxn.getOrientation());
//          break;
//      }
//    }
    
    lines.add("}");
    return StringUtils.join(lines, '\n');
  }


}
