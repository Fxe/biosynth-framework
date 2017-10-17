package pt.uminho.sysbio.biosynthframework.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Joiner;

public class InputStreamSet {
  
  public Map<String, InputStream> streams = new HashMap<> ();
  
  @Override
  public String toString() {
    return Joiner.on('\n').withKeyValueSeparator("\t").join(streams);
  }
}
