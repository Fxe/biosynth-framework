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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class TrieIdBaseIntegrationEngine implements BaseIntegrationEngine {

  private static final Logger logger = LoggerFactory.getLogger(TrieIdBaseIntegrationEngine.class);
  
  private Map<MetaboliteMajorLabel, Trie> trieMap = new HashMap<> ();
  private Map<MetaboliteMajorLabel, Trie> trieWholeMap = new HashMap<> ();
  
  public boolean ignoreCase = false;
  public Set<String> ids = new HashSet<> ();
  public double cut = 0.1;
  
  public void setup(MetaboliteMajorLabel db, Set<String> words) {
    TrieBuilder b =  Trie.builder();
    if (ignoreCase) {
      b.ignoreCase();
    }
    
    b.addKeywords(words);
    Trie t = b.build();
    b.onlyWholeWords();
    Trie tw = b.build();
    
    logger.info("build trie for {}", db);
    
    trieMap.put(db, t);
    trieWholeMap.put(db, tw);
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
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {
    IntegrationMap<String, MetaboliteMajorLabel> imap = new IntegrationMap<>();
    
    for (MetaboliteMajorLabel db : trieMap.keySet()) {
      Trie trie = trieMap.get(db);
      
      Map<String, Integer> p = new HashMap<> ();
      Map<String, Integer> s = new HashMap<> ();
      
      int total = ids.size();
      for (String id : ids) {
        if (id != null && !id.isEmpty()) {
          Emit large = null;
          int size = 0;
          List<Token> tokens = new ArrayList<> ();
          for (Token tk : trie.tokenize(id)) {
//            System.out.println(tk.getFragment() + " " + tk.getEmit());
            
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
          
//          System.out.println("\t[L]: "  + large);
//          for (Token tk : tokens) {
//            if (tk.getEmit() == null) {
//              System.out.println("\t"  + tk.getFragment());
//            } else if (tk.getEmit().getKeyword().equals(large.getKeyword())) {
//              System.out.println("\t"  + tk.getFragment());
//            }
//          }
        }
      }
      
      Set<String> suffixes = new HashSet<> ();
      Set<String> prefixes = new HashSet<> ();
      for (String prefix : p.keySet()) {
        double r = p.get(prefix) / (double)total;
        if (r > cut) {
//          System.out.println("[P] " + prefix + " " + r);
          prefixes.add(prefix);
        }
      }
      for (String t : s.keySet()) {
        double r = s.get(t) / (double)total;
        if (r > cut) {
//          System.out.println("[S] " + t + " " + r);
          suffixes.add(t);
        }
      }
      
      Trie trieWhole = trieWholeMap.get(db);
      
      for (String id : ids) {
        String str = id;
        str = removeAnyPrefix(str, prefixes);
        str = removeAnySuffix(str, suffixes);
//        System.out.println(str);
        for (Token tk : trieWhole.tokenize(str)) {
          Emit emit = tk.getEmit();
          if (emit != null) {
            if (str.equals(emit.getKeyword())) {
              imap.addIntegration(id, db, emit.getKeyword());
            }
//            System.out.println(emit.getKeyword());
          }
        }
      }
    }
    
    
    
    return imap;
  }

}
