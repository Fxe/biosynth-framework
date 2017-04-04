package pt.uminho.sysbio.biosynth.integration.etl;

public class IdentityTransform<T> implements EtlTransform<T, T>{
  @Override
  public T etlTransform(T srcObject) { return srcObject;}

  @Override
  public T apply(T t) {
    return etlTransform(t);
  }
}
