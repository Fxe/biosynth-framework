package pt.uminho.sysbio.biosynthframework.thread;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiodbThread<T, R> implements Runnable {
  
  private static final Logger logger = LoggerFactory.getLogger(BiodbThread.class);
  
  public int id;
  protected final Function<T, R> function;
  public T task;
  public R result;
  public final BiodbThreadRunner<T, R> master;
  
  public BiodbThread(int id, Function<T, R> f, BiodbThreadRunner<T, R> master) {
    logger.info("Thread [{}]: created", id);
    this.master = master;
    this.id = id;
    this.function = f;
  }
  
  @Override
  public void run() {
    T task = null;
    while ((task = master.getTask()) != null) {
      try {
        result = this.function.apply(task);
        if (result != null) {
          master.setResult(task, result);
        }
      } catch (Exception e) {
        master.setErrors(task, e);
      }
    }
  }
}
