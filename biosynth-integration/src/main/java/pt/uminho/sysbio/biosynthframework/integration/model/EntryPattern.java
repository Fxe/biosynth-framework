package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class EntryPattern implements Comparable<EntryPattern> {

  public final String base;
  public List<String> prefixes = new ArrayList<> ();
  public List<String> suffixes = new ArrayList<> ();
  public String trim;

  public EntryPattern(String base) {
    this.base = base;
    this.trim = base;
  }
  
  public String getTrim() {
    return trim;
  }
  
  public String getTrim(int rexp, int lexp) {
    List<String> blocks = new ArrayList<> ();
    for (int i = 0; i < rexp && prefixes.size() - 1 - i >= 0; i++) {
      blocks.add(prefixes.get(prefixes.size() - 1 - i));
    }
    
    blocks.add(trim);
    
    for (int i = 0; i < lexp && suffixes.size() - 1 - i >= 0; i++) {
      blocks.add(suffixes.get(suffixes.size() - 1 - i));
    }
    
    return StringUtils.join(blocks, "");
  }

  @Override
  public int hashCode() {
    return this.trim.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof EntryPattern) {
      EntryPattern other = (EntryPattern) obj;
      return this.trim.equals(other.trim);
    }

    return false;
  }

  @Override
  public int compareTo(EntryPattern o) {
    return this.trim.compareTo(o.trim);
  }

  public boolean splicePrefix(String prefix) {
    if (!prefix.isEmpty() && trim.startsWith(prefix)) {
      trim = StringUtils.removeStart(trim, prefix);
      prefixes.add(prefix);
      return true;
    }
    return false;
  }

  public String lastPrefix() {
    if (prefixes.isEmpty()) {
      return null;
    }

    return prefixes.get(prefixes.size() - 1);
  }

  public String getSuffix(int index) {
    if (suffixes.size() > index) {
      return suffixes.get(index);
    }

    return null;
  }

  public boolean suffixContains(String value, boolean c) {
    for (String s : suffixes) {
      if (c) {
        if (s.equals(value)) {
          return true;
        }
      } else {
        if (s.toLowerCase().equals(value.toLowerCase())) {
          return true;
        }
      }

    }

    return false;
  }

  public boolean spliceSuffix(String suffix) {
    if (!suffix.isEmpty() && trim.endsWith(suffix)) {
      trim = StringUtils.removeEnd(trim, suffix);
      suffixes.add(suffix);
      return true;
    }
    return false;
  }
  
  @Override
  public String toString() {
    List<String> blocks = new ArrayList<> ();
    blocks.addAll(this.prefixes);
    blocks.add(trim);
    blocks.addAll(this.suffixes);
    return StringUtils.join(blocks, ' ');
  }
}
