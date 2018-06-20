package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.report.neo4j.TableModelIntegration.Source;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jStatisticsForTheKnowledgeExpasionReporter extends AbstractNeo4jReporter {

  public BMap<String, String> istructureToIupac = new BHashMap<>();
  public BMap<String, String> istructureToTraditional = new BHashMap<>();
  public BMap<String, String> sstructureToIupac = new BHashMap<>();
  public BMap<String, String> sstructureToTraditional = new BHashMap<>();
  public BMap<String, String> mstructureToIupac = new BHashMap<>();
  public BMap<String, String> mstructureToTraditional = new BHashMap<>();
  
  public Neo4jStatisticsForTheKnowledgeExpasionReporter(GraphDatabaseService service) {
    super(service);
  }

  public boolean isIupac(String name) {
    Set<String> i = istructureToIupac.bget(name);
    Set<String> s = sstructureToIupac.bget(name);
    Set<String> m = mstructureToIupac.bget(name);
    
    return (i != null && !i.isEmpty()) || 
           (s != null && !s.isEmpty()) || 
           (m != null && !m.isEmpty());
  }
  
  public boolean isTraditional(String name) {
    Set<String> i = istructureToTraditional.bget(name);
    Set<String> s = sstructureToTraditional.bget(name);
    Set<String> m = mstructureToTraditional.bget(name);
    
    return (i != null && !i.isEmpty()) || 
           (s != null && !s.isEmpty()) || 
           (m != null && !m.isEmpty());
  }
  
  public Dataset<MetaboliteMajorLabel, String, Integer> report(MetaboliteMajorLabel[] databases) {
    Dataset<MetaboliteMajorLabel, String, Integer> data = new Dataset<>();
    
    for (MetaboliteMajorLabel database : databases) {
      Map<String, Integer> count = count(database);
      data.dataset.put(database, count);
    }
    
    return data;
  }
  
  public static void load(
      BMap<String, String> structureToIupac, 
      BMap<String, String> structureToTraditional, 
      String path, int sindex, int iupacIndex, int tradIndex) {
    
    try (InputStream is = new FileInputStream(path)) {
      List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
      for (String l : lines) {
        String[] p = l.split("\t");
        String structure = p[sindex];
        String iupac = p[iupacIndex];
        String trad = p[tradIndex];
        structureToIupac.put(structure, iupac);
        structureToTraditional.put(structure, trad);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public Map<String, Integer> count(MetaboliteMajorLabel database) {
    Map<String, Integer> count = new HashMap<>();
    //  BiodbMetaboliteNode cpdNode = service.getMetabolite("META:PYRUVATE", MetaboliteMajorLabel.MetaCyc);
    for (BiodbMetaboliteNode cpdNode : service.listMetabolites(database)) {
      if (cpdNode.hasLabel(GlobalLabel.Metabolite) && !cpdNode.isProxy()) {
        for (Relationship r : cpdNode.getRelationships()) {
          Node node = r.getOtherNode(cpdNode);
          if (node.hasLabel(GlobalLabel.MetaboliteProperty)) {
            BiodbPropertyNode propertyNode = new BiodbPropertyNode(node, null);
            String sourceStr = (String) r.getProperty(
                "source", Source.Resource.toString());
            if (sourceStr.equals("inferred")) {
              sourceStr = Source.Inferred.toString();
            }
            Source source = Source.valueOf(sourceStr);
            String property = (String) propertyNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);

            if (node.hasLabel(MetabolitePropertyLabel.Name)) {

              String name = propertyNode.getValue();
              boolean isIupac = isIupac(name);
              boolean isTraditional = isTraditional(name);
              boolean isOpsin = node.hasLabel(MetabolitePropertyLabel.IUPACName);
              if (isIupac) {
                property = "IUPACName";
              } else if (isTraditional) {
                property = "TraditionalName";
              } else if (isOpsin) {
                property = "OpsinName";
              } else {
                property = "RegularName";
              }
            }
            CollectionUtils.increaseCount(count, source + "_" + property, 1);
            //          System.out.println(source + "\t" + property + "\t" + r.getType().name() + "\t" + Neo4jUtils.getLabelsAsString(node));
          }
        }
      }

    }
    //  System.out.println(cpdNode.getEntry());

    return count;
  }
}
