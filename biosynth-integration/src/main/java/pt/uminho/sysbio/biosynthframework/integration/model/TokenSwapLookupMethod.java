package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class TokenSwapLookupMethod implements LookupMethod {
  //public Map<String, String> swap = new HashMap<> ();
  public List<Pair<String, String>> swap = new ArrayList<> ();
  public Set<String> acceptedTokens = new HashSet<> ();

  public void addSwap(String s1, String s2) {
    this.swap.add(new ImmutablePair<String, String>(s1, s2));
  }

  @Override
  public String lookup(EntryPattern pattern) {
    String base = pattern.trim;
    //  String suffix = "";
    for (int i = 0; i < pattern.suffixes.size(); i++) {
      String token = pattern.suffixes.get(pattern.suffixes.size() - 1 - i);
      if (acceptedTokens.contains(token)) {
        base += token;
      }
    }
    for (Pair<String, String> p : swap) {
      if (base.contains(p.getLeft())) {
        base = base.replace(p.getLeft(), p.getRight());
      }
    }
    //  if (pattern.suffixes.size() > 0) {
    //    suffix = pattern.suffixes.get(pattern.suffixes.size() - 1);
    //  }
    //  if (swap.containsKey(suffix)) {
    //    base += swap.get(suffix);
    //  }
//    System.out.println(pattern);
//    System.out.println(base + " " + pattern.trim);
    return base;
  }
}
