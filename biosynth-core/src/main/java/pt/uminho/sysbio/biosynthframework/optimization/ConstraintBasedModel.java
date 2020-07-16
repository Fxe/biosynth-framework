package pt.uminho.sysbio.biosynthframework.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Range;

public class ConstraintBasedModel {
  public double[][] matrix;
  public List<Range> bounds;
  public Map<String, Integer> spiIndexMap;
  public Map<String, Integer> rxnIndexMap;
  
  public ConstraintBasedModel(double[][] matrix, 
                              List<Range> bounds, 
                              Map<String, Integer> spiIndexMap, 
                              Map<String, Integer> rxnIndexMap) {
    this.matrix = matrix;
    this.bounds = new ArrayList<>(bounds);
    this.spiIndexMap = new HashMap<>(spiIndexMap);
    this.rxnIndexMap = new HashMap<>(rxnIndexMap);
  }
  
  public double[][] getBoundsAsMatrix() {
    double[][] bounds = new double[this.bounds.size()][2];
    for (int i = 0; i < bounds.length; i++) {
      Range r = this.bounds.get(i);
      bounds[i][0] = r.lb;
      bounds[i][1] = r.ub;
    }
    return bounds;
  }
}
