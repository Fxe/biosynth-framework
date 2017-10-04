package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Token;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.stat.descriptive.rank.Median;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class DictionaryBaseIntegrationEngine implements BaseIntegrationEngine {

  private static final Logger logger = LoggerFactory.getLogger(DictionaryBaseIntegrationEngine.class);
  
  public Map<String, Dictionary> dictionaryMap = new HashMap<> ();
  public Map<String, Trie> trieMap = new HashMap<> ();
  public Map<String, Trie> trieWholeMap = new HashMap<> ();
  
  public Set<String> ids = new HashSet<> ();
  
  public double cut = 0.1;
  public double mcut = 0.05;
  
  public void setup() {
    for (String dict : dictionaryMap.keySet()) {
      TrieBuilder b =  Trie.builder();
      TrieBuilder bw =  Trie.builder();
      bw.onlyWholeWords();
      Dictionary dictionary = dictionaryMap.get(dict);
      b.addKeywords(dictionary.dictionary.keySet());
      bw.addKeywords(dictionary.dictionary.keySet());
      Trie t = b.build();
      Trie tw = bw.build();
      logger.info("build trie for {}", dict);
      
      trieMap.put(dict, t);
      trieWholeMap.put(dict, tw);
    }
  }
  
  public Set<String> getSignificant(Map<String, Integer> s, int total) {
    Set<String> tokens = new HashSet<> ();;
    int i = 0;
    double[] values = new double[s.size()];
    Map<String, Double> aaa = new HashMap<> ();
    for (String t : s.keySet()) {
      double r = s.get(t) / (double)total;
      aaa.put(t, r);
      values[i++] = r;
      if (r > cut) {
//        System.out.println("[SIG+] " + t + "\t" + r);
        tokens.add(t);
      } else {
//        System.out.println("[SIG-] " + t + "\t" + r);
      }
    }
    Median medianKernel = new Median();
    medianKernel.setData(values);
    double median = medianKernel.evaluate();
    for (String t : aaa.keySet()) {
      double r = median / aaa.get(t);
      if (r < mcut) {
//        System.out.println("[SIG*] " + t + "\t" + r);
        tokens.add(t);
      }
    }

    tokens.remove("");
    
    return tokens;
  }
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {
    IntegrationMap<String, MetaboliteMajorLabel> imap = new IntegrationMap<>();
    
    for (String dict : trieMap.keySet()) {
      Trie trie = trieMap.get(dict);
      
      Map<String, Integer> p = new HashMap<> ();
      Map<String, Integer> s = new HashMap<> ();
      
      int total = ids.size();
      for (String id : ids) {
        if (id != null && !id.isEmpty()) {
          Emit large = null;
          int size = 0;
          List<Token> tokens = new ArrayList<> ();
          for (Token tk : trie.tokenize(id)) {
            tokens.add(tk);
            if (tk.getEmit() != null) {
//              System.out.println(tk.getEmit().size());
              if (tk.getEmit().size() > size) {
                size = tk.getEmit().size();
                large = tk.getEmit();
              }
            }
          }
          
          String prefix = null;
          String suffix = null;
          if (large != null) {
            if (large.getStart() != 0) {
              prefix = id.substring(0, large.getStart());
            }
            if (prefix != null) {
              CollectionUtils.increaseCount(p, prefix, 1);
            }
            
            suffix = id.substring(large.getEnd() + 1, id.length());
            if (suffix != null) {
              CollectionUtils.increaseCount(s, suffix, 1);
            }
            
//            System.out.println(prefix + "<" + large.getKeyword() + ">" + suffix);
          }
          
//          if (large != null) {
//            System.out.println("\t[L]: "  + large + " " + id);
//            for (Token tk : tokens) {
//              if (tk.getEmit() == null) {
//                System.out.println("\t"  + tk.getFragment());
//              } else if (tk.getEmit().getKeyword().equals(large.getKeyword())) {
//                System.out.println("\t"  + tk.getFragment());
//              }
//            }
//          }

        }
      }
      
      Set<String> suffixes = getSignificant(s, total);
      Set<String> prefixes = getSignificant(p, total);
      System.out.println(suffixes);
      System.out.println(prefixes);
      
      Trie trieWhole = trieWholeMap.get(dict);
      
      Dictionary dictionary = dictionaryMap.get(dict);
      for (String id : ids) {
        String str = id;
        str = removeAnyPrefix(str, prefixes);
        str = removeAnySuffix(str, suffixes);
//        System.out.println(id + " " + str);
        for (Token tk : trieWhole.tokenize(str)) {
          Emit emit = tk.getEmit();
          if (emit != null) {
            if (str.equals(emit.getKeyword())) {
              ExternalReference eref = dictionary.dictionary.get(emit.getKeyword());
              imap.addIntegration(id, MetaboliteMajorLabel.valueOf(eref.source), eref.entry);
            }
          }
        }
      }
    }
    
    return imap;
  }
  
  public String removeAnyPrefix(String str, Set<String> pre) {
    for (String p : pre) {
      if (str.startsWith(p)) {
        str = StringUtils.removeStart(str, p);
        break;
      }
    }
    return str;
  }
  
  public String removeAnySuffix(String str, Set<String> suf) {
    for (String s : suf) {
      if (str.endsWith(s)) {
        str = StringUtils.removeEnd(str, s);
        break;
      }
    }
    return str;
  }

}