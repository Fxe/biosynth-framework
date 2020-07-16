package pt.uminho.sysbio.biosynthframework.integration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

/**
 * Filters the subset of edge pairs that are 
 * relevant for similarity computation
 * @author Filipe Liu
 *
 */
public class FilterGammaSubset {
  
  private static Logger logger = LoggerFactory.getLogger(FilterGammaSubset.class);
  
  public static class PairSet<E> extends HashSet<Tuple2<E>> {

    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean add(Tuple2<E> e) {
      if (this.contains(new Tuple2<E>(e.e2, e.e1))) {
        return false;
      }
      return super.add(e);
    }
    
//    @Override
//    public boolean remove(Object o) {
//      if (o instanceof Tuple2) {
//        @SuppressWarnings({ "unchecked", "rawtypes" })
//        Tuple2<E> e = (Tuple2) o;
//        if (this.contains(new Tuple2<E>(e.e2, e.e1))) {
//          return super.remove(e);
//        }
//      }
//      return super.remove(o);
//    }
  }
  
  private final BiodbGraphDatabaseService graphDatabaseService;
  
  public Set<MetabolitePropertyLabel> properties = new HashSet<>();
  public Set<MetaboliteMajorLabel> databases = new HashSet<>();
  public Set<ExternalReference> entities = new HashSet<>();
  public List<ConnectedComponents<Long>> ccList = new ArrayList<> ();
//  public Map<MetabolitePropertyLabel, RelationshipType> labelToRelationshipType = new HashMap<> ();
  public boolean acceptXref = true;
  
  public FilterGammaSubset(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = new BiodbGraphDatabaseService(graphDatabaseService);
//    labelToRelationshipType.put(MetabolitePropertyLabel.Name, MetaboliteRelationshipType.has_name);
//    labelToRelationshipType.put(MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
//    labelToRelationshipType.put(MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);
//    pt.uminho.sysbio.biosynthframework.util.
  }
  
  public Set<Long> collectIds() {
    Set<Long> valid = new HashSet<> ();
    
    for (MetaboliteMajorLabel db : databases) {
      Set<Long> all = new HashSet<> ();
      for (Node n : Iterators.asIterable(graphDatabaseService.findNodes(db))) {
        if (n.hasLabel(GlobalLabel.Metabolite)) {
          boolean proxy = (boolean) n.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
          if (!proxy) {
            all.add(n.getId());
          }
        }
      }
      logger.info("{}: {}", db, all.size());
      valid.addAll(all);
    }
    
    for (ExternalReference e : entities) {
      BiodbMetaboliteNode n = graphDatabaseService.getMetabolite(e);
      if (n != null) {
        valid.add(n.getId());
      } else {
        logger.warn("Metabolite not found: {}", e);
      }
    }
    
    return valid;
  }
  
  public Set<Tuple2<Long>> getCrossreferenceTuples() {
    PairSet<Long> result = new PairSet<>();
    
    Set<Long> valid = collectIds();

    for (long id : valid) {
      Node node = graphDatabaseService.getNodeById(id);
      for (Relationship r : node.getRelationships(MetaboliteRelationshipType.has_crossreference_to)) {
        long otherId = r.getOtherNode(node).getId();
        if (valid.contains(otherId)) {
          result.add(new Tuple2<Long>(id, otherId));
        }
      }
    }
    
    return result;
  }
  
  public Set<Tuple2<Long>> filter() {
    Set<Tuple2<Long>> result = new HashSet<>();
    
    //First, we collect all IDs that participate in the filter
    Set<Long> valid = collectIds();
    
    for (MetabolitePropertyLabel p : properties) {
      Set<Long> all = new HashSet<> ();
      Set<String> t = new HashSet<> ();
      
      Set<Tuple2<Long>> pairs = new HashSet<> ();
      for (Node n : Iterators.asIterable(graphDatabaseService.findNodes(p))) {
        all.add(n.getId());
        
        Set<Long> linked = new HashSet<> ();
        
        for (Relationship r : n.getRelationships()) {
          t.add(r.getType().name());
          linked.add(r.getOtherNode(n).getId());
        }
        
        linked.retainAll(valid);
        
        List<Long> larray = new ArrayList<> (linked);
        
        if (larray.size() < 1000) {
          pairs.addAll(CollectionUtils.getAllPairs(larray));
        } else {
          System.out.println(larray.size() + " " + Neo4jUtils.getPropertiesMap(n));
        }
      }
      logger.info("{}: {} {} pairs: {}", p, all.size(), t, pairs.size());
      result.addAll(pairs);
    }
    
    if (acceptXref) {
      Set<Tuple2<Long>> pairs = getCrossreferenceTuples();
      logger.info("References: {} pairs: {}", valid.size(), pairs.size());
      result.addAll(pairs);
    }
    
    for (ConnectedComponents<Long> ccs : ccList) {
      Set<Tuple2<Long>> pairs = new HashSet<>();
      for (Set<Long> cc : ccs) {
        Set<Set<Long>> combs = CollectionUtils.makePairs(Sets.intersection(cc, valid));
        for (Set<Long> p : combs) {
          Iterator<Long> it = p.iterator();
          Tuple2<Long> t = new Tuple2<Long>(it.next(), it.next());
          pairs.add(t);
        }
      }
      logger.info("CC: {} pairs: {}", ccs.size(), pairs.size());
      result.addAll(pairs);
    }
    
    return result;
  }
}
