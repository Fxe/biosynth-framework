package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jCrosslinksReporter extends AbstractNeo4jReporter {

  public Neo4jCrosslinksReporter(GraphDatabaseService service) {
    super(service);
  }
  
  public static int getMax(Map<String, Integer> map) {
    int max = 0;
    for (Integer i : map.values()) {
      if (i > max) max = i;
    }
    return max;
  }

  public static Map<String, Integer> countLinkFrequency(Node cpdNode) {
    Map<String, Integer> linkFreq = new HashMap<> ();
    for (Relationship relationship : cpdNode.getRelationships(MetaboliteRelationshipType.has_crossreference_to)) {
      Node other = relationship.getOtherNode(cpdNode);
      if (other.hasLabel(GlobalLabel.Metabolite)) {
        CollectionUtils.increaseCount(linkFreq, (String) other.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY), 1);
      }
    }
    
    return linkFreq;
  }
  
  public static void reportGraphVizRefGraph(String[][] matrix) {
    List<String> targetDb = Arrays.asList(matrix[0]);
    Set<String> database = new HashSet<> ();
    for (int i = 0; i < matrix[0].length; i++) {
      String db = matrix[0][i];
      if (!db.trim().isEmpty()) {
        database.add(db);
      }
    }

    for (int i = 1; i < matrix.length; i++) {
      String db = matrix[i][0];
      if (!db.trim().isEmpty()) {
        database.add(db);
      }
    }
    for (String db : database) {
      System.out.println(db);
    }
    System.out.println("------------");
    for (int i = 1; i < matrix.length; i++) {
      String db = matrix[i][0];
      for (int j = 1; j < matrix[i].length; j++) {
        int value = Integer.parseInt(matrix[i][j]);
        if (value > 0) {
          String dbDst = targetDb.get(j);
          String edgeLine = String.format("%s -> %s [ label=\"%d\"]", db, dbDst, value);
          System.out.println(edgeLine);
        }
      }
    }
  }
  
  public Dataset<MetaboliteMajorLabel, MetaboliteMajorLabel, Integer> reportCrosslinks2(
      MetaboliteMajorLabel[] databases) {
    Dataset<MetaboliteMajorLabel, MetaboliteMajorLabel, Integer> dataset = new Dataset<>();
    
    for (MetaboliteMajorLabel database : databases) {
//      System.out.println(database);
      Map<MetaboliteMajorLabel, Integer> outRefCount = new HashMap<> ();
      for (Node cpdNode : service.listNodes(database)) {
        if (cpdNode.hasLabel(GlobalLabel.Metabolite)) {
          Map<MetaboliteMajorLabel, Set<Long>> aa = new HashMap<> ();
          for (Relationship rel : cpdNode.getRelationships(
              MetaboliteRelationshipType.has_crossreference_to, Direction.OUTGOING)) {
            Node refNode = rel.getOtherNode(cpdNode);
            if (refNode.hasLabel(GlobalLabel.Metabolite)) {
              String dbStr = (String) refNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
              MetaboliteMajorLabel refDb = MetaboliteMajorLabel.valueOf(dbStr);
              if (!aa.containsKey(refDb)) {
                aa.put(refDb, new HashSet<Long> ());
              }
              aa.get(refDb).add(refNode.getId());
//              CollectionUtils.increaseCount(outRefCount, refDb, 1);
            }
          }
          for (MetaboliteMajorLabel refDb : aa.keySet()) {
            CollectionUtils.increaseCount(outRefCount, refDb, aa.get(refDb).size());
          }
//          for (Node refNode : Neo4jUtils.collectNodeRelationshipNodes(cpdNode, MetaboliteRelationshipType.has_crossreference_to)) {
//            if (refNode.hasLabel(GlobalLabel.Metabolite)) {
//              String dbStr = (String) refNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
//              MetaboliteMajorLabel refDb = MetaboliteMajorLabel.valueOf(dbStr);
//              CollectionUtils.increaseCount(outRefCount, refDb, 1);
//            }
//          }
        }
      }
      
//      System.out.println(database + " " + outRefCount);
      for (MetaboliteMajorLabel refDb : outRefCount.keySet()) {
        dataset.add(database, refDb, outRefCount.get(refDb));
      }
    }
    
    return dataset;
  }
  
  public void report() {
    for (MetaboliteMajorLabel database : MetaboliteMajorLabel.values()) {
      
      List<Map<String, Integer>> freqList = new ArrayList<> ();
      Set<String> databases = new HashSet<> ();
      int max = 0;
      for (Node cpdNode : service.listNodes(database)) {
        Map<String, Integer> map = countLinkFrequency(cpdNode); 
        int mapMax = getMax(map);
        databases.addAll(map.keySet());
        if (mapMax > max) max = mapMax;
        freqList.add(map);
      }
      
      int[][] freqMatrix = new int[databases.size()][max + 1];
      Map<String, Integer> dbIndexMap = new HashMap<> ();
      List<String> dbList = new ArrayList<> ();
      int c = 0;
      for (String db : databases) {
        dbList.add(db);
        dbIndexMap.put(db, c++);
      }
      
      for (Map<String, Integer> map : freqList) {
        for (String db : map.keySet()) {
          int freq = map.get(db);
          freqMatrix[dbIndexMap.get(db)][freq]++;
        }
      }
      
      System.out.println(database);
      for (int i = 0; i < freqMatrix.length; i++) {
        System.out.print(dbList.get(i) + "\t");
        for (int j = 0; j < freqMatrix[i].length; j++) {
          System.out.print(freqMatrix[i][j] + "\t");
        }
        System.out.println();
      }
    }
  }
}
