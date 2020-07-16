package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.integration.function.CurationFunction;
import pt.uminho.sysbio.biosynthframework.integration.function.Neo4jExtFunction;
import pt.uminho.sysbio.biosynthframework.integration.function.Neo4jFormulaFuncion;
import pt.uminho.sysbio.biosynthframework.integration.function.Neo4jNameFunction;
import pt.uminho.sysbio.biosynthframework.integration.function.Neo4jReactionMatchFunction;
import pt.uminho.sysbio.biosynthframework.integration.function.Neo4jReferenceFunction;
import pt.uminho.sysbio.biosynthframework.integration.function.Neo4jStructureFunction;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.ConnectedComponentsUtils;
import pt.uminho.sysbio.biosynthframework.util.GraphUtils;

public class Neo4jMetaboliteIntegrationService implements MetaboliteIntegrationService<Long> {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jMetaboliteIntegrationService.class);
  
  private final BiodbGraphDatabaseService service;
  
  protected final Set<ConnectedComponents<Long>> curationSets = new HashSet<>();
  protected final Set<MetaboliteMajorLabel> cpdDatabases = new HashSet<>();
  protected final Set<MetabolitePropertyLabel> cpdProperties = new HashSet<>();
  
  public Neo4jMetaboliteIntegrationService(GraphDatabaseService service) {
    this.service = new BiodbGraphDatabaseService(service);
  }
  
  public Set<MetaboliteMajorLabel> getCpdDatabases() { return cpdDatabases;}
  public Set<MetabolitePropertyLabel> getCpdProperties() { return cpdProperties;}
  public Set<ConnectedComponents<Long>> getCurationSets() { return curationSets;}

  public Set<Tuple2<Long>> getPairs() {
    FilterGammaSubset filterGammaSubset = new FilterGammaSubset(service);
    filterGammaSubset.databases.addAll(cpdDatabases);
    filterGammaSubset.properties.addAll(cpdProperties);
    filterGammaSubset.acceptXref = true;
    filterGammaSubset.ccList.addAll(curationSets);
    Set<Tuple2<Long>> pairs = filterGammaSubset.filter();
    
    return pairs;
  }
  
  public static ConnectedComponents<Long> getCpdIntegration(Map<Tuple2<Long>, Double> scores) {
    ConnectedComponents<Long> result = new ConnectedComponents<>();
    
    double high = 0.0;
    double low = Double.MAX_VALUE;
    double accept = 0.25;
    UndirectedGraph<Long, Object> u = new SimpleGraph<>(Object.class);
    for (Tuple2<Long> t : scores.keySet()) {
      double s = scores.get(t);
      if (s > 0.0) {
        if (s > high) {
          high = s;
        }
        if (s < low) {
          low = s;
        }

        if (s > accept) {
          GraphUtils.addVertexIfNotExists(u, t.e1);
          GraphUtils.addVertexIfNotExists(u, t.e2);
          u.addEdge(t.e1, t.e2);
        }
      }
    }
    

    logger.info("min: {}, max: {}", low, high);

    List<Set<Long>> ccList = GraphUtils.getConnectedComponents(u);
    for (Set<Long> cc : ccList) {
      if (!cc.isEmpty()) {
        result.add(cc);
      }
    }
    
    return result;
  }
  
  public TheBestIntegrationMethod setupMetaboliteIntegrationMethod() {
    TheBestIntegrationMethod integration = new TheBestIntegrationMethod();
    
    integration.N = new Neo4jNameFunction(service);
    integration.F = new Neo4jFormulaFuncion(service);
    integration.X = new Neo4jReferenceFunction(service);
    integration.S = new Neo4jStructureFunction(service, true);
    integration.R = new Neo4jReactionMatchFunction(service, null, 10.0, 0.0);
    integration.Z = new Neo4jExtFunction(service);
    if (!curationSets.isEmpty()) {
      ConnectedComponents<Long> curation = new ConnectedComponents<>();
      for (ConnectedComponents<Long> c : curationSets) {
        curation = ConnectedComponentsUtils.merge(curation, c);
      }
//      integration.C = new CurationFunction(curation, configuration.C_ALPHA, configuration.C_BETA);
      integration.C = new CurationFunction<Long>(curation, 100.0, -10);
    }
    
    return integration;
  }
  
  @Override
  public ConnectedComponents<Long> entityResolution() {
    TheBestIntegrationMethod method = setupMetaboliteIntegrationMethod();
    
    Set<Tuple2<Long>> pairs = getPairs();
    for (Tuple2<Long> t : pairs) {
      method.add(t.e1, t.e2);
    }
    
    logger.info("calculate connection strength");
    Map<Tuple2<Long>, Double> scores = method.score2();
    logger.info("done!");
    ConnectedComponents<Long> cpdIntegration = getCpdIntegration(scores);
    
    return cpdIntegration;
  }

}
