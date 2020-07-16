package pt.uminho.sysbio.biosynthframework.integration.assembler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public abstract class AbstractNeo4jAssemblePlugin implements AssemblePlugin {

  private static final Logger logger = LoggerFactory.getLogger(AbstractNeo4jAssemblePlugin.class);
  
  protected final BiodbGraphDatabaseService service;
  protected final Set<String> ignoreAttributes = new HashSet<>();
  protected final Set<String> ignoreRelationships = new HashSet<>();

  public AbstractNeo4jAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    this.service = new BiodbGraphDatabaseService(graphDatabaseService);
    
    ignoreAttributes.add("major_label");
    ignoreAttributes.add("orientation");
    ignoreAttributes.add("translocation");
    ignoreAttributes.add("frameId");
    ignoreAttributes.add("created_at");
    ignoreAttributes.add("updated_at");
    ignoreAttributes.add("entry");
    ignoreAttributes.add("proxy");
    ignoreAttributes.add("source");
    ignoreAttributes.add("name");
    
    ignoreRelationships.add(ReactionRelationshipType.left_component.toString());
    ignoreRelationships.add(ReactionRelationshipType.right_component.toString());
    ignoreRelationships.add(ReactionRelationshipType.has_crossreference_to.toString());
    ignoreRelationships.add(ReactionRelationshipType.has_name.toString());
    ignoreRelationships.add(ReactionRelationshipType.has_ec_number.toString());
  }
  
  
  public Set<BiodbMetaboliteNode> filter(Collection<ExternalReference> refs, String source) {
    Set<BiodbMetaboliteNode> cpds = new HashSet<> ();
//    for (ExternalReference ref: refs.stream()
//                                    .filter(e -> e.source.equals(source))
//                                    .collect(Collectors.toSet())) {
//      BiodbMetaboliteNode cpdNode = service.getMetabolite(ref);
//      cpds.add(cpdNode);
//    }
    for (ExternalReference ref: refs) {
      if (source.equals(ref.source)) {
        BiodbMetaboliteNode cpdNode = service.getMetabolite(ref);
        cpds.add(cpdNode);
      }
    }
    return cpds;
  }
  
  protected void collectAttributes(Map<String, Object> result, Node node) {
    
    for (String attribute : node.getAllProperties().keySet()) {
      if (!ignoreAttributes.contains(attribute)) {
        Object o = node.getAllProperties().get(attribute);
        if (!(o instanceof String) || (o instanceof String && !DataUtils.empty(o))) {
          if (result.containsKey(attribute)) {
            result.put(attribute, node.getProperty(attribute));
          } else {
            logger.warn("unknown attribute detected: [{}] -> [{}]", attribute, node.getProperty(attribute));
          }
        }

      }
    }
  }
  
  public Set<BiodbReactionNode> getReactionNodes(Collection<ExternalReference> refs, ReactionMajorLabel database) {
    return getReactionNodes(refs, database.toString());
  }
  
  public Set<BiodbReactionNode> getReactionNodes(Collection<ExternalReference> refs, String source) {
    Set<BiodbReactionNode> rxns = new HashSet<> ();
//    for (ExternalReference ref: refs.stream()
//                                    .filter(e -> e.source.equals(source))
//                                    .collect(Collectors.toSet())) {
//      BiodbReactionNode cpdNode = service.getReaction(ref);
//      rxns.add(cpdNode);
//    }
    for (ExternalReference ref: refs) {
      if (source.equals(ref.source)) {
        BiodbReactionNode cpdNode = service.getReaction(ref);
        rxns.add(cpdNode);
      }
    }
    return rxns;
  }
  
  public static Set<String> setupStringArray(String string, String split) {
    Set<String> strs = null;
    if (!DataUtils.empty(string)) {
      strs = new TreeSet<>();
      for (String str : string.split(split)) {
        if (!DataUtils.empty(str)) {
          strs.add(str.trim());
        }
      }
    }
    
    return strs;
  }
  

  @Override
  public abstract Map<String, Object> assemble(Set<ExternalReference> refs);

}
