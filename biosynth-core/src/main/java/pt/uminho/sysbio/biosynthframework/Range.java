package pt.uminho.sysbio.biosynthframework;

public class Range {
  
  public Double inf = null;
  
  public double lb;
  public double ub;
  
  public Range(double lb, double ub) {
    this.lb = lb;
    this.ub = ub;
  }
  
  /**
   * [a, b] such that a = b
   * @return
   */
  public boolean isFixed() {
    return this.lb == this.ub;
  }
  
  /**
   * [a, b] such that a > b
   * @return
   */
  public boolean isNull() {
    return this.lb > this.ub;
  }
  
  /**
   * [a, b] such that a > 0 and a < b
   * @return
   */
  public boolean isPositive() {
    return this.lb < this.ub && this.lb > 0.0;
  }
  
  /**
   * [a, b] such that b < 0 and a < b
   * @return
   */
  public boolean isNegative() {
    return this.lb < this.ub && this.ub < 0.0;
  }
  
  @Override
  public String toString() {
    if (inf != null) {
      String lbStr = (lb <= -1 * inf) ? "-inf" : Double.toString(lb);
      String ubStr = (ub >= inf) ? "inf" : Double.toString(ub);
      return String.format("[%s, %s]", lbStr, ubStr);
    }
    return String.format("[%f, %f]", lb, ub);
  }
}
