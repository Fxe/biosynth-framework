package pt.uminho.sysbio.biosynthframework.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelReactionNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jConstraintBasedModelFactory implements ConstraintBasedModelFactory {

  private static final Logger logger = LoggerFactory.getLogger(SbmlConstraintBasedModelFactory.class);
  
  private BiosMetabolicModelNode model;
  private Map<String, Integer> spiIndexMap = new HashMap<>();
  private Map<String, Integer> rxnIndexMap = new HashMap<>();
  
  public Neo4jConstraintBasedModelFactory(BiosMetabolicModelNode model) {
    this.model = model;
  }
  
  public void init(BiosMetabolicModelNode model) {
    int i = 0;
    for (BiosModelSpeciesNode n : model.getMetaboliteSpecies()) {
      String id = n.getSid();
      spiIndexMap.put(id, i++);
    }
    i = 0;
    for (BiosModelReactionNode n : model.getModelReactions()) {
      String id = n.getSid();
      rxnIndexMap.put(id, i++);
    }
  }
  
  @Override
  public double[][] getMatrix() {
    double[][] matrix = new double[spiIndexMap.size()][rxnIndexMap.size()];
    
    for (BiosModelReactionNode n : model.getModelReactions()) {
      String id = n.getSid();
      Integer rxnIndex = this.rxnIndexMap.get(id);
      
      logger.debug("{} -[index]-> {}", id, rxnIndex);
      
      
      for (Relationship r : n.getRelationships(MetabolicModelRelationshipType.left_component)) {
        Node spi = r.getOtherNode(n);
        String s = (String) spi.getProperty("id");
        String value = null;
        Integer spiIndex = this.spiIndexMap.get(s);
        
        logger.debug("{} -[reactant]-> {} {} -> {} [{}]", id, value, s, spiIndex, r.getAllProperties());
        
        if (DataUtils.empty(value)) {
          value = "1";
        }
        
        matrix[spiIndex][rxnIndex] = -1 * Double.parseDouble(value);
      }
      for (Relationship r : n.getRelationships(MetabolicModelRelationshipType.right_component)) {
        Node spi = r.getOtherNode(n);
        String s = (String) spi.getProperty("id");
        String value = null;
        Integer spiIndex = this.spiIndexMap.get(s);
        
        logger.debug("{} -[product]-> {} {} -> {} [{}]", id, value, s, spiIndex, r.getAllProperties());
        
        if (DataUtils.empty(value)) {
          value = "1";
        }
        
        matrix[spiIndex][rxnIndex] = Double.parseDouble(value);
      }
    }
    
    return matrix;
  }

  @Override
  public double[][] getBounds() {
    double[][] bounds = new double[this.rxnIndexMap.size()][2];
    for (BiosModelReactionNode rxn : model.getModelReactions()) {
      String id = rxn.getSid();
      
      Tuple2<String> bound = null;
      String lb = null;
      String ub = null;
//      for (String method : boundReaders.keySet()) {
//        bound = boundReaders.get(method).getReactionBounds(id);
//        if (bound != null) {
//          lb = bound.e1;
//          ub = bound.e2;
//          break;
//        }
//      }
      
      Integer rxnIndex = this.rxnIndexMap.get(id);
      if (!DataUtils.empty(lb)) {
        bounds[rxnIndex][0] = Double.parseDouble(lb);
      } else {
        logger.warn("[{}] no lower bound detected assume default -1000", id);
        bounds[rxnIndex][0] = -1000;
      }
      if (!DataUtils.empty(ub)) {
        bounds[rxnIndex][1] = Double.parseDouble(ub);
      } else {
        logger.warn("[{}] no upper bound detected assume default 1000", id);
        bounds[rxnIndex][1] =  1000;
      }
      
    }
    return bounds;
  }

  @Override
  public Map<String, Integer> getSpeciesIndexMap() {
    return new HashMap<>(spiIndexMap);
  }

  @Override
  public Map<String, Integer> getReactionsIndexMap() {
    return new HashMap<>(rxnIndexMap);
  }

  @Override
  public ConstraintBasedModel build() {
    List<Range> bounds = new ArrayList<> ();
    double[][] bs = getBounds();
    double[][] matrix = getMatrix();
    for (int i = 0; i < rxnIndexMap.size(); i++) {
      bounds.add(new Range(bs[i][0], bs[i][1]));
    }
    return new ConstraintBasedModel(matrix, bounds, spiIndexMap, rxnIndexMap);
  }

}
