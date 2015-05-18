package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.strategy.DebuggableInstanceMapper;

public class DebugStrictCrossreferenceMapperImpl extends StrictCrossreferenceMapperImpl
implements DebuggableInstanceMapper {

  private final static Logger LOGGER = LoggerFactory.getLogger(StrictCrossreferenceMapperImpl.class);
  private final static RelationshipType CROSSREFERENCE_RELATIONSHIP = MetaboliteRelationshipType.has_crossreference_to;
  
  private Map<String, Object> root = new HashMap<> ();
  
  public DebugStrictCrossreferenceMapperImpl(
      GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }
  
  @Override
  public Map<String, Object> getDebugInformation() {
    return root;
  }

  @Override
  public Set<Long> execute() {
    root.clear();
    Set<Long> explored = new HashSet<> ();
    explored.add(initialNode.getId());
    root.put("cpdId", initialNode.getId());
    List<Map<String, Object>> childs = new ArrayList<> ();
    root.put("childs", childs);
    collect2(initialNode, explored, 1, childs);
    return explored;
  }

  public void collect2(Node node, Set<Long> explored, int depth, List<Map<String, Object>> tree) {
    MetaboliteMajorLabel db = null;
    try {
      db = MetaboliteMajorLabel.valueOf((String) node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, ""));
    } catch (IllegalArgumentException e) {
      return;
    }
    
    
    
    for (Relationship r : node.getRelationships(CROSSREFERENCE_RELATIONSHIP)) {
      Node other = r.getOtherNode(node);
      LOGGER.trace("[{}] {}|- {}:{} -- {}:{}", depth, StringUtils.repeat(' ', depth - 1), node, Neo4jUtils.getLabels(node), other, Neo4jUtils.getLabels(other));
      Map<String, Object> treeNode = new HashMap<> ();
      treeNode.put("cpdId", other.getId());
      List<Map<String, Object>> childs = new ArrayList<> ();
      treeNode.put("childs", childs);
      tree.add(treeNode);
      //node is an invalid xref if points to two instances of current db
      if (!explored.contains(other.getId())
          && other.hasLabel(GlobalLabel.Metabolite)
          && valid(other, db)) {
        LOGGER.debug("[{}] {}|- [{}]{}:{}", depth, StringUtils.repeat(' ', depth - 1), other.getId(), Neo4jUtils.getLabels(other), other.getProperty("entry", "-"));
        explored.add(other.getId());
        collect2(other, explored, depth + 1, childs);
      }
//      break;
    }
  }
}
