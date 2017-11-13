package pt.uminho.sysbio.biosynthframework.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.Tuple2;

public class TheBestIntegrationMethod {
  
  private static final Logger logger = LoggerFactory.getLogger(TheBestIntegrationMethod.class);
  
  public static TheBestIntegrationMethod buildThesisMethod(
      final GraphDatabaseService gds, 
      final Set<MetaboliteMajorLabel> databases,
      final Set<MetabolitePropertyLabel> properties) {
    
    Set<Long> valid = new HashSet<> ();
    TheBestIntegrationMethod integration = new TheBestIntegrationMethod();
    
    for (MetaboliteMajorLabel db : databases) {
      Set<Long> all = new HashSet<> ();
//      dataTx = graphDataService.beginTx();
      
      for (Node n : Iterators.asIterable(gds.findNodes(db))) {
        if (n.hasLabel(GlobalLabel.Metabolite)) {
          boolean proxy = (boolean) n.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
          if (!proxy) {
            all.add(n.getId());
          }
        }
      }
      logger.info("{}: {}", db, all.size());
      valid.addAll(all);
//      dataTx.failure(); dataTx.close();
    }
    
    
    for (MetabolitePropertyLabel p : properties) {
      Set<Long> all = new HashSet<> ();
      Set<String> t = new HashSet<> ();
//      dataTx = graphDataService.beginTx();
      
      
      Set<Tuple2<Long>> pairs = new HashSet<> ();
      for (Node n : Iterators.asIterable(gds.findNodes(p))) {
        all.add(n.getId());
        
        Set<Long> linked = new HashSet<> ();
        
        for (Relationship r : n.getRelationships()) {
          t.add(r.getType().name());
          linked.add(r.getOtherNode(n).getId());
        }
        
        linked.retainAll(valid);
        
        List<Long> larray = new ArrayList<> (linked);
        
        if (larray.size() < 1000) {
          for (int i = 0; i < linked.size(); i++) {
            long a = larray.get(i);
            for (int j = i + 1; j < linked.size(); j++) {
              long b = larray.get(j);
              pairs.add(new Tuple2<Long>(a, b));
              integration.add(a, b);
            }
          }
        } else {
          System.out.println(larray.size() + " " + Neo4jUtils.getPropertiesMap(n));
        }
      }
      logger.info("{}: {} {} pairs: {}", p, all.size(), t, pairs.size());
//      dataTx.failure(); dataTx.close();
    }
    
    integration.F = new BiFunction<Long, Long, Double>() {
      @Override
      public Double apply(Long t, Long u) {
        Node a = gds.getNodeById(t);
        Node b = gds.getNodeById(u);
        Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
            a, MetaboliteRelationshipType.has_molecular_formula);
        Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
            b, MetaboliteRelationshipType.has_molecular_formula);
        
        if (!Sets.intersection(sa, sb).isEmpty()) {
          return 0.1;
        }
        
        return 0.0;
      }
    };
    
    integration.N = new BiFunction<Long, Long, Double>() {
      @Override
      public Double apply(Long t, Long u) {
        Node a = gds.getNodeById(t);
        Node b = gds.getNodeById(u);
        Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
            a, MetaboliteRelationshipType.has_name);
        Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
            b, MetaboliteRelationshipType.has_name);
        
        int m = Sets.intersection(sa, sb).size();
        
        return m * 0.1;
      }
    };
    
    integration.X = new BiFunction<Long, Long, Double>() {
      @Override
      public Double apply(Long t, Long u) {
        Node a = gds.getNodeById(t);
        Node b = gds.getNodeById(u);
        Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
            a, MetaboliteRelationshipType.has_crossreference_to);
        Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
            b, MetaboliteRelationshipType.has_crossreference_to);
        
        if (!Sets.intersection(sa, sb).isEmpty()) {
          return 1.0;
        }
        
        return 0.0;
      }
    };
    
    integration.S = new BiFunction<Long, Long, Double>() {
      @Override
      public Double apply(Long t, Long u) {
        return 0.0;
      }
    };
    
    integration.R = new BiFunction<Long, Long, Double>() {
      @Override
      public Double apply(Long t, Long u) {
        return 0.0;
      }
    };
    
    return integration;
  }
  
//  Map<ConnectedComponents<?> 
  public BiFunction<Long, Long, Double> N;
  public BiFunction<Long, Long, Double> X;
  public BiFunction<Long, Long, Double> S;
  public BiFunction<Long, Long, Double> F;
  public BiFunction<Long, Long, Double> R;
  public BiFunction<Long, Long, Double> Z;
  public BiFunction<Long, Long, Double> C;
  
  private Map<Tuple2<Long>, Double> scoreBoard = new HashMap<> ();
  
  public Set<Long> getObjects() {
    Set<Long> result = new HashSet<> ();
    for (Tuple2<Long> t : scoreBoard.keySet()) {
      result.add(t.e1);
      result.add(t.e2);
    }
    return result;
  }
  
  public void add(long a, long b) {
    scoreBoard.put(new Tuple2<Long>(a, b), 0.0);
  }
  
  public Map<Tuple2<Long>, Double> getScoreBoard() {
    return scoreBoard;
  }

  public void setScoreBoard(Map<Tuple2<Long>, Double> scoreBoard) {
    this.scoreBoard = scoreBoard;
  }

  public double N(long a, long b) {
    return N.apply(a, b);
  }
  
  public double X(long a, long b) {
    return X.apply(a, b);
  }
  
  public double S(long a, long b) {
    return S.apply(a, b);
  }
  
  public double F(long a, long b) {
    return F.apply(a, b);
  }
  
  public double R(long a, long b) {
    return R.apply(a, b);
  }
  
  public double Z(long a, long b) {
    if (Z == null) {
      return 0;
    }
    return Z.apply(a, b);
  }
  
  public double C(long a, long b) {
    if (C == null) {
      return 0;
    }
    return C.apply(a, b);
  }
  
  public Map<Tuple2<Long>, Double> score() {
    Map<Tuple2<Long>, Double> result = new HashMap<> ();
    for (Tuple2<Long> t : scoreBoard.keySet()) {
      long a = t.e1;
      long b = t.e2;
      result.put(t, this.score(a, b));
    }
    
    scoreBoard = result;
    
    return this.scoreBoard;
  }
  
  public double score(long a, long b) {
    return N(a, b) + F(a, b) + S(a, b) + X(a, b) + R(a, b);
  }
  
  public double score2(long a, long b) {
    score2(a, b, null);
    return (N(a, b)  + S(a, b) + X(a, b) + R(a, b) + Z(a, b) + C(a, b)) * (1 + F(a, b));
  }
  
  public double score2(long a, long b, Map<String, Double> result) {
    double n = N(a, b);
    double s = S(a, b);
    double x = X(a, b);
    double r = R(a, b);
    double z = Z(a, b);
    double c = C(a, b);
    double f = F(a, b);
    if (result != null) {
      result.put("n", n);
      result.put("s", s);
      result.put("x", x);
      result.put("r", r);
      result.put("z", z);
      result.put("c", c);
      result.put("f", f);
    }
    return (n + s + x + r + z + c) * (1 + f);
  }
}
