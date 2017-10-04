package pt.uminho.sysbio.biosynthframework;

/**
 * Represents a function that accepts one argument and produces a result. 
 * (copy of java 1.8)
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public interface BFunction<T, R> {
  public R apply(T a);
}
