package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class IdPattern {
  
  private List<List<String>> ppattern = new ArrayList<> ();
  private List<List<String>> spattern = new ArrayList<> ();
  
  public void addPrefix(String g, Collection<String> l) {
    add(g, l, ppattern);
  }
  
  public void addSuffix(String g, Collection<String> l) {
    add(g, l, spattern);
  }
  
  public void add(String g, Collection<String> l, List<List<String>> ll) {
    List<String> singleton = new ArrayList<> ();
    if (g != null) {
      singleton.add(g);
    }
    ll.add(singleton);
    if (l != null) {
      ll.add(new ArrayList<String> (l));
    } else {
      ll.add(new ArrayList<String> ());
    }
  }
  
  public String getGlobalSuffix(int i) {
    List<String> l = spattern.get(i);
    
    if (!l.isEmpty()) {
      return l.get(0);
    }
    
    return null;
  }
  
  public List<String> getLocalSuffix(int i) {
    List<String> l = spattern.get(i);
    
    if (!l.isEmpty()) {
      return l;
    }
    
    return null;
  }
  
  public String getGlobalPrefix(int i) {
    List<String> l = ppattern.get(i);
    
    if (!l.isEmpty()) {
      return l.get(0);
    }
    
    return null;
  }
  
  public List<String> getLocalPrefix(int i) {
    List<String> l = ppattern.get(i);
    
    if (!l.isEmpty()) {
      return l;
    }
    
    return null;
  }
  
  public void apply(EntryPattern entry) {
    for (int i = 0; i < this.ppattern.size(); i+=2) {
      String gprefix = this.getGlobalPrefix(i);
      List<String> lprefix = this.getLocalPrefix(i + 1);
      if (gprefix != null) {
        entry.splicePrefix(gprefix);
      }
      if (lprefix != null) {
        for (String s : lprefix) {
          if (entry.trim.startsWith(s)) {
            entry.splicePrefix(s);
            break;
          }
        }
      }
    }
    
//    System.out.println(spattern);
    
    for (int i = 0; i < this.spattern.size(); i+=2) {
      String gsuffix = this.getGlobalSuffix(i);
      List<String> lsuffix = this.getLocalSuffix(i + 1);
      if (gsuffix != null) {
        entry.spliceSuffix(gsuffix);
      }
      if (lsuffix != null) {
        for (String s : lsuffix) {
          if (entry.spliceSuffix(s)) {
            break;
          }
        }
      }
    }
//    for (int i = 0; i < this.spattern.size(); i+=2) {
//      String gprefix = this.getGlobalPrefix(i);
//      List<String> lprefix = this.getLocalPrefix(i + 1);
//      if (gprefix != null) {
//        entry.splicePrefix(gprefix);
//      }
//      if (lprefix != null) {
//        for (String s : lprefix) {
//          if (entry.trim.startsWith(s)) {
//            entry.splicePrefix(s);
//            break;
//          }
//        }
//      }
//    }
  }
  
  @Override
  public String toString() {
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ppattern.size(); i+=2) {
      List<String> g = ppattern.get(i);
      List<String> l = ppattern.get(i + 1);
      if (!g.isEmpty()) {
        sb.append(g.iterator().next());
      }
      if (!l.isEmpty()) {
        String block = String.format("(%s)?", StringUtils.join(l, '|'));
        sb.append(block);
      }
    }
    sb.append("<ID>");
    
    for (int i = spattern.size(); i > 0; i-=2) {
      List<String> g = spattern.get(i - 2);
      List<String> l = spattern.get(i - 1);
      
      if (!l.isEmpty()) {
        String block = String.format("(%s)?", StringUtils.join(l, '|'));
        sb.append(block);
      }
      if (!g.isEmpty()) {
        sb.append(g.iterator().next());
      }
      
//      System.out.println(l + " " + g);
//      if (!g.isEmpty()) {
//        sb.append(g.iterator().next());
//      }
//      if (!l.isEmpty()) {
//        String block = String.format("(%s)?", StringUtils.join(l, '|'));
//        sb.append(block);
//      }
    }
    
    return sb.toString();
  }
}
