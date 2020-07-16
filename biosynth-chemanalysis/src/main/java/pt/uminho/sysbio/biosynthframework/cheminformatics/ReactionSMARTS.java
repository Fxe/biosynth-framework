package pt.uminho.sysbio.biosynthframework.cheminformatics;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class ReactionSMARTS {
  
  public List<SMARTS> l = new ArrayList<> ();
  public List<SMARTS> r = new ArrayList<> ();
  
  public ReactionSMARTS(String smarts) {
    if (!smarts.contains(">>")) {
      throw new IllegalArgumentException("not a reaction SMARTS: " + smarts);
    }
    
    String p[] = smarts.split(">>");
    for (String s : p[0].split("\\.")) {
      l.add(new SMARTS(s));
    }
    for (String s : p[1].split("\\.")) {
      r.add(new SMARTS(s));
    }
  }
  
  @Override
  public String toString() {
    return String.format("%s>>%s", Joiner.on('.').join(l), Joiner.on('.').join(r));
  }
}
