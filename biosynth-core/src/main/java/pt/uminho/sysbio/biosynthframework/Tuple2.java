package pt.uminho.sysbio.biosynthframework;

public class Tuple2<E> {
  public E e1;
  public E e2;
  
  public Tuple2(E e1, E e2) {
    this.e1 = e1;
    this.e2 = e2;
  }
  
  @Override
  public int hashCode() {
    int h1 = 0;
    int h2 = 0;
    if (e1 != null) {
      h1 = 7 * e1.hashCode();
    }
    if (e2 != null) {
      h2 = e2.hashCode();
    }
    return h1 + h2;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Tuple2)) {
      return false;
    }
    
    Tuple2<E> t = (Tuple2<E>) o;
    boolean eq1 = e1 == null && t.e1 == null;
    boolean eq2 = e2 == null && t.e2 == null;
    
    if (e1 != null && t.e1 != null) {
      eq1 = e1.equals(t.e1);
    }
    if (e2 != null && t.e2 != null) {
      eq2 = e2.equals(t.e2);
    }
    return eq1 && eq2;
  }
}
