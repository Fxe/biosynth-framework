package pt.uminho.sysbio.biosynth.integration.report;

import java.util.Map;

public interface BiodbReporter<R> {
  public R buildReport(Map<String, Object> params);
}
