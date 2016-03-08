package pt.uminho.sysbio.biosynth.integration.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public class ReferenceMatrixReporter {

  private GraphDatabaseService graphDataService;
  
  public ReferenceMatrixReporter(GraphDatabaseService graphDataService) {
    this.graphDataService = graphDataService;
  }
  
//  @Override
  public void generateReport() {
    List<MetaboliteMajorLabel> a = new ArrayList<> (Arrays.asList(MetaboliteMajorLabel.values()));
    Map<MetaboliteMajorLabel, Integer> indexMap = new HashMap<> ();
    for (int i = 0; i < a.size(); i++) {
      MetaboliteMajorLabel label = a.get(i);
      indexMap.put(label, i);
    }
    int[][] matrix = new int[a.size()][a.size()];
    Set<Long> visitedEdges = new HashSet<> ();
    for (MetaboliteMajorLabel label : a) {
      for (Node n : GlobalGraphOperations.at(graphDataService)
                                         .getAllNodesWithLabel(label)) {
        for (Relationship r : n.getRelationships(MetaboliteRelationshipType.has_crossreference_to)) {
          if (!visitedEdges.contains(r.getId())) {
            if (r.getOtherNode(n).hasLabel(GlobalLabel.Metabolite)) {
              Node src = r.getStartNode();
              Node dst = r.getEndNode();
              MetaboliteMajorLabel srcL = MetaboliteMajorLabel.valueOf(
                  (String)src.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
              MetaboliteMajorLabel dstL = MetaboliteMajorLabel.valueOf(
                  (String)dst.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
              int srcIndex = indexMap.get(srcL);
              int dstIndex = indexMap.get(dstL);
              matrix[srcIndex][dstIndex]++;
            }
          }
          visitedEdges.add(r.getId());
        }
      }
    }
    
    for (int i = 0; i < matrix.length; i++) {
      System.out.print(a.get(i) + "\t");
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(matrix[i][j] + "\t");
      }
      System.out.println();
    }
  }
}
