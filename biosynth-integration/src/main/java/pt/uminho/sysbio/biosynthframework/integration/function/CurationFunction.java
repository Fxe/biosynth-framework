package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.Set;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;

public class CurationFunction<T> implements BiFunction<T, T, Double> {
  
  private static Logger logger = LoggerFactory.getLogger(CurationFunction.class);
  
  public double alpha = 5.0;
  public double beta = -1.0;

  private final ConnectedComponents<T> ccs;

  public CurationFunction(ConnectedComponents<T> ccs) {
    this(ccs, 5.0, -1.0);
  }
  
  public CurationFunction(ConnectedComponents<T> ccs, 
                          double alpha, 
                          double beta) {
    this.ccs = ccs;
    this.alpha = alpha;
    this.beta = beta;
  }
  
  @Override
  public Double apply(T t, T u) {
    Set<T> a = ccs.getConnectedComponentOf(t);
    Set<T> b = ccs.getConnectedComponentOf(u);
    
    logger.debug("<{}, {}> A: {}", t, u, a);
    logger.debug("<{}, {}> B: {}", t, u, b);
    
    if (a == null || b == null || 
        a.isEmpty() || b.isEmpty()) {
      return 0.0;
    }
    
    if (a.equals(b)) {
      return alpha;
    } else {
      return beta;
    }
  }

}
