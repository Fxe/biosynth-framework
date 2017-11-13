package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class BiodbMetaboliteNode extends BiodbEntityNode {

  public BiodbMetaboliteNode(Node node) {
    super(node);
    if (!node.hasLabel(GlobalLabel.Metabolite)) {
      throw new IllegalArgumentException("invalid node: missing " + GlobalLabel.Metabolite);
    }
  }
  
  public MetaboliteMajorLabel getDatabase() {
    return MetaboliteMajorLabel.valueOf((String) getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
  }
  
  public Set<BiodbPropertyNode> getMetaboliteProperty(MetabolitePropertyLabel property) {
    Set<BiodbPropertyNode> result = new HashSet<> ();
    for (Node node : Neo4jUtils.collectNodeRelationshipNodes(node, property)) {
      result.add(new BiodbPropertyNode(node));
    }
    return result;
  }
  
  public static Map<Long, String> getOwnerAny(Node prop) {
    Map<Long, String> result = new HashMap<> ();
    for (Relationship r : prop.getRelationships(Direction.INCOMING)) {
      Node prop2 = r.getOtherNode(prop);
      if (prop2.hasLabel(GlobalLabel.MetaboliteProperty) && 
          r.getProperty("source").equals("inferred")) {
        result.put(prop2.getId(), prop2.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, "error").toString());
      }
    }
    
    return result;
  }
  
  public Map<Long, String> getOwner(Node prop) {
    Map<Long, String> o = getOwnerAny(prop);
    Map<Long, String> result = new HashMap<> ();
    
    for (long id : o.keySet()) {
      Node i = getGraphDatabase().getNodeById(id);
      if (Neo4jUtils.exitsRelationshipBetween(node, i, Direction.BOTH)) {
        result.put(id, o.get(id));
      }
    }
    
    return result;
  }
}
