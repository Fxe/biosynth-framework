package pt.uminho.sysbio.biosynthframework;

public class Range extends Tuple2<Double> {
  
  public Range(Double lb, Double ub) {
    super(lb, ub);
    this.lb = lb;
    this.ub = ub;
    
    if (this.lb > this.ub) {
      throw new IllegalArgumentException(String.format("invalid range [%d, %d]", lb, ub));
    }
  }

  public Double inf = null;
  
  public final double lb;
  public final double ub;
  
  /**
   * [a, b] such that a = b
   * @return
   */
  public boolean isFixed() {
    return this.e1 == this.e2;
  }
  
  /**
   * [a, b] such that a > b
   * @return
   */
  public boolean isNull() {
    return this.e1 > this.e2;
  }
  
  /**
   * [a, b] such that a > 0 and a < b
   * @return
   */
  public boolean isPositive() {
    return this.e1 < this.e2 && this.e1 > 0.0;
  }
  
  /**
   * [a, b] such that b < 0 and a < b
   * @return
   */
  public boolean isNegative() {
    return this.e1 < this.e2 && this.e2 < 0.0;
  }
  
  @Override
  public String toString() {
    if (inf != null) {
      String lbStr = (e1 <= -1 * inf) ? "-inf" : Double.toString(e1);
      String ubStr = (e2 >= inf) ? "inf" : Double.toString(e2);
      return String.format("[%s, %s]", lbStr, ubStr);
    }
    return String.format("[%f, %f]", e1, e2);
  }
}
