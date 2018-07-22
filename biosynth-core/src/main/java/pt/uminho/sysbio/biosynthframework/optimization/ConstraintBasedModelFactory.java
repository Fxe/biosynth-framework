package pt.uminho.sysbio.biosynthframework.optimization;

import java.util.Map;

public interface ConstraintBasedModelFactory {
  public double[][] getMatrix();
  public double[][] getBounds();
  public Map<String, Integer> getSpeciesIndexMap();
  public Map<String, Integer> getReactionsIndexMap();
  
  public ConstraintBasedModel build();
}
