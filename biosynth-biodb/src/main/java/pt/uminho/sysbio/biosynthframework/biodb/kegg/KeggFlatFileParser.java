package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class KeggFlatFileParser {

  private static final Logger logger = LoggerFactory.getLogger(KeggFlatFileParser.class);

  public int getKeyLevel(String k) {
    int level = 0;
    for (int i = 0; i < k.length() && k.charAt(i) == ' '; i+=2) {
      level++;
    }

    return level;
  }
  
  public static Map<String, Set<String>> parseLine(String l, String token) {
    Map<String, Set<String>> result = new HashMap<>();
    int split = l.indexOf(token);
    if (split < 0) {
      return null;
    } else {
      String rule = l.substring(0, split).trim();
      String rn = l.substring(split);
      
      if (!result.containsKey(rule)) {
        result.put(rule, new HashSet<String>());
      }
      if (rn.contains("RN:")) {
        rn = rn.split("RN:")[1].trim();
        if (rn.endsWith("]")) {
          rn = rn.substring(0, rn.length() - 1);
        }
        for (String rxnEntry : rn.split("\\s+")) {
          if (!DataUtils.empty(rxnEntry)) {
            result.get(rule).add(rxnEntry);
          }
        }
      }
    }
    
    return result;
  }
  
  public static Map<String, Set<String>> parseOrthology(String str) {
    Map<String, Set<String>> result = new HashMap<>();
    if (!DataUtils.empty(str)) {
      for (String l : str.split("\n")) {
        Map<String, Set<String>> r = parseLine(l, "  ");
        if (r == null) {
          logger.debug("unable to find split token (double space) for: {}. using single space", l);
          r = parseLine(l, " ");
        }
        
        if (r != null) {
          result.putAll(r);
        } else {
          logger.warn("unable to find split token for: {}", l);
        }
      }
    }
    
    return result;
  }
  
  public static Map<Tuple2<String>, Set<String>> parseOrthologyReaction(String str) {
    Map<Tuple2<String>, Set<String>> result = new HashMap<>();
    if (!DataUtils.empty(str)) {
      for (String l : str.split("\n")) {
        int split = l.indexOf(" ");
        if (split < 0) {
          logger.warn("unable to find split token (space) for: {}", l);
        } else {
          String rxn = l.substring(0, split).trim();
          Set<String> rxns = new HashSet<>(Arrays.asList(rxn.trim().split(",")));
          String step = l.substring(split);
          String p[] = step.split(" -> ");
          result.put(new Tuple2<String>(p[0].trim(), p[1].trim()), rxns);
        }
      }
    }
    return result;
  }

  public Map<String, Set<String>> parseGenes(String str) {
    Map<String, Set<String>> result = new HashMap<>();
    String[] genesByOrg = str.split("\n");
    for (String line : genesByOrg) {
      if (line.contains(":")) {
        String[] p = line.split(":");
        String org = p[0];
        String genes = p[1];
        logger.trace("ORG[{}] GENES[{}]", org, genes);

        if (result.put(org, new HashSet<String>()) != null) {
          logger.warn("duplicate organims in gene data: {}", org);
        }

        for (String gene : genes.trim().split("\\s+")) {
          if (gene.contains("(")) {
            gene = gene.substring(0, gene.indexOf('('));
          }
          if (!result.get(org).add(gene)) {
            logger.warn("duplicate gene / organim: {}", gene, org);
          }
        }
      } else {
        logger.warn("no ':' in {}", line);
      }
    }
    return result;
  }
  
  public static Map<String, Set<String>> parseDblinks(String str) {
    Map<String, Set<String>> dblinks = new HashMap<>();
    if (!DataUtils.empty(str)) {
      for (String l : str.split("\n")) {
        String p[] = l.split(":");
        if (p.length > 1) {
          String db = p[0].trim();
          for (String e : p[1].split("\\s+")) {
            if (!DataUtils.empty(e)) {
              if (!dblinks.containsKey(db)) {
                dblinks.put(db, new HashSet<String>());
              }
              dblinks.get(db).add(e.trim());
            }
          }
        }
      }
    }

    
    return dblinks;
  }

  public static Set<String> getIdentifers(String str, String prefix) {
    Set<String> identifiers = new HashSet<>();
    if (!DataUtils.empty(str)) {
      for (String l : str.split("\n")) {
        String p[] = l.split("\\s+");
        if (p.length > 0 && p[0].trim().startsWith(prefix)) {
          identifiers.add(p[0].trim());
        }
      }
    }
    
    return identifiers;
  }
  
  public Map<String, Object> parse(String data) {
    Map<String, Object> result = new HashMap<>();
    String cursor = null;
    String[] lines = data.split("\n");

    //0123456789** KEGG records have keys 
    //DEFINITION__ of size 10 with 2 spaces
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.equals("///")) {
        break;
      }
      logger.debug("[LINE:{}] {}", i, line);
      String key = line.substring(0, 12);
      String value = line.substring(12);
      int level = getKeyLevel(key);
      if (key.trim().isEmpty()) {
        if (cursor != null) {
          result.put(cursor, result.get(cursor) + "\n" + value);
        } else {
          logger.warn("found empty key with no previous cursor: {}", line);
        }
      } else {
        if (level == 0) {
          cursor = key.trim();
          result.put(cursor, value);
        }
      }
      logger.debug("[C:{}][K:{}][{}] [V:{}]", cursor, key, level, value);
    }
    return result;
  }


}
