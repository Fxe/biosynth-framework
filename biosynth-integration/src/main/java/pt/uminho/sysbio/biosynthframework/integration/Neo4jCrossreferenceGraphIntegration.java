package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class Neo4jCrossreferenceGraphIntegration implements GraphIntegration{

  private static final Logger logger = LoggerFactory.getLogger(Neo4jCrossreferenceGraphIntegration.class);

  private final GraphDatabaseService service;

  private static final RelationshipType relationshipType = 
      MetaboliteRelationshipType.has_crossreference_to;
  private Set<Label> excludeMajorLabel;

  public Neo4jCrossreferenceGraphIntegration(GraphDatabaseService service) {
    this.service = service;
  }

  @Override
  public Graph<Long, DefaultEdge> integrate(long id) {
    Node initialNode = service.getNodeById(id);
    Set<Long> nodes = new HashSet<> ();
    for (Path position: service.traversalDescription()
        .depthFirst()
        .relationships(relationshipType)
        .evaluator(new Evaluator() {

          @Override
          public Evaluation evaluate(Path path) {
            Node endNode = path.endNode();
            if (!endNode.hasLabel(GlobalLabel.Metabolite)) {
              logger.debug("Excluded node: {}", Neo4jUtils.getLabels(endNode));
              return Evaluation.EXCLUDE_AND_PRUNE;
            }
            for (Label label : excludeMajorLabel) {
              if (endNode.hasLabel(label)) {
                logger.debug("Exclude and Prune: " + String.format("[%d]%s:%s", endNode.getId(), endNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY), endNode.getProperty("entry")));
                return Evaluation.EXCLUDE_AND_PRUNE;
              }
            }

            return Evaluation.INCLUDE_AND_CONTINUE;
          }
        })
        .traverse(initialNode)) {

      Long eid = position.endNode().getId();
      //      System.out.println(position);
      logger.trace(String.format("[%d] - %s", eid, toString(position)));
      nodes.add(eid);
    }
    return null;
  }

  public String toString(Path path) {
    String result = "";
    for (Object o : path) {
      if (o instanceof Node) {
        Node node = (Node) o;
        try {
          result += String.format("[%d]%s:%s", node.getId(), node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY), node.getProperty("entry"));
        } catch (Exception e) {
          result += String.format("[%d] %s - %s", node.getId(), Neo4jUtils.getLabels(node), Neo4jUtils.getPropertiesMap(node));
        }
      } else if (o instanceof Relationship) {
        //          Relationship relationship = (Relationship) o;
        result += " <?> ";
      } else {
        result += "[ERROR]";
      }
    }
    return result;
  }
}
