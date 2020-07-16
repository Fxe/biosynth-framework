package pt.uminho.sysbio.biosynthframework.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiodbThreadRunner<T, R> {
  private static final Logger logger = LoggerFactory.getLogger(BiodbThreadRunner.class);

  //private boolean running = false;
  public List<T> jobs = new ArrayList<> ();
  private Iterator<T> jobIt;
  private Map<T, R> results = new HashMap<> ();
  private final Function<T, R> function;
  private Map<T, Exception> errors = new HashMap<> ();

  private final int t;

  public BiodbThreadRunner(Function<T, R> f) {
    this(4, f);
  }

  public BiodbThreadRunner(int threads, Function<T, R> f) {
    this.t = threads;
    this.function = f;
  }

  public synchronized T getTask() {
    if (jobIt.hasNext()) {
      return jobIt.next();
    }
    return null;
  }

  public synchronized void setResult(T task, R result) {
    this.results.put(task, result);
  }

  public synchronized void setErrors(T task, Exception error) {
    this.errors.put(task, error);
  }

  public synchronized Map<T, Exception> getErrors() {
    return errors;
  }

  public void run() {
    List<Runnable> runners = new ArrayList<> ();
    for (int i = 0; i < t; i++) {
      runners.add(new BiodbThread<>(i, function, this));
    }

    //  this.running = true;
    this.results.clear();
    this.jobIt = jobs.iterator();

    logger.info("running ... jobs: {}", jobs.size());
    long start = System.currentTimeMillis();

    List<Thread> threads = new ArrayList<> ();
    for (Runnable r : runners) {
      Thread t = new Thread(r);
      t.start();
      threads.add(t);
    }

    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    long end = System.currentTimeMillis();
    logger.info("Time: {}", (end - start) / 1000);
    //  this.running = false;
  }
}
