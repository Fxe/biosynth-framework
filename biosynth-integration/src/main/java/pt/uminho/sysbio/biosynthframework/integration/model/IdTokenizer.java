package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class IdTokenizer {
  
  private static final Logger logger = LoggerFactory.getLogger(IdTokenizer.class);
  
  public static double minRatio = 0.01;
  public String splitDelimiter = "_";
  public Map<String, EntryPattern> patterns = new HashMap<> ();
//  public List<EntryPattern> patterns = new ArrayList<> ();
  
  public Map<EntryPattern, List<EntryPattern>> getUniqueTrims() {
    
    Map<EntryPattern, List<EntryPattern>> result = new HashMap<> ();
    for (EntryPattern p : this.patterns.values()) {
      if (!result.containsKey(p)) {
        result.put(p, new ArrayList<EntryPattern> ());
      }
      result.get(p).add(p);
    }
    
    return result;
  }
//  public List<List<String>> ppattern = new ArrayList<> ();
//  public List<List<String>> spattern = new ArrayList<> ();
  
  public IdPattern generatePattern(Set<String> strings) {
    
    
    for (String str : strings) {
      patterns.put(str, new EntryPattern(str));
    }

    String gprefix = IdTokenizer.globalPrefix(patterns.values());
    Map<String, Integer> lprefixes = localPrefixByDelimiter(patterns.values(), "_");
    String gsuffix = IdTokenizer.globalSuffix(patterns.values());
    Map<String, Integer> lsuffixes = localSuffixByDelimiter(patterns.values(), "_");
    logger.info("gprefix: {}, lprefix: {}, gsuffix: {}, lsuffix: {}", gprefix, lprefixes, gsuffix, lsuffixes);
//    List<String> pgLst = new ArrayList<> ();
//    List<String> plLst = new ArrayList<> ();
//    List<String> sgLst = new ArrayList<> ();
//    List<String> slLst = new ArrayList<> ();
//    pgLst.add(gprefix);
//    plLst.addAll(lprefixes.keySet());
//    sgLst.add(gsuffix);
//    slLst.addAll(lsuffixes.keySet());
    
    IdPattern p = new IdPattern();
    p.addPrefix(gprefix, lprefixes.keySet());
    p.addSuffix(gsuffix, lsuffixes.keySet());
//    p.ppattern.add(pgLst);
//    p.ppattern.add(plLst);
//    p.spattern.add(sgLst);
//    p.spattern.add(slLst);

    String prev_gprefix = gprefix;
    Map<String, Integer> prev_lprefixes = new HashMap<> (lprefixes);
    String prev_gsuffix = gsuffix;
    Map<String, Integer> prev_lsuffixes = new HashMap<> (lsuffixes);

    while (gprefix != null && !gprefix.isEmpty() ||
        !lprefixes.isEmpty() ||
        gsuffix != null && !gsuffix.isEmpty() ||
        !lsuffixes.isEmpty()) {

      gprefix = IdTokenizer.globalPrefix(patterns.values());
      lprefixes = IdTokenizer.localPrefixByDelimiter(patterns.values(), splitDelimiter);
      gsuffix = IdTokenizer.globalSuffix(patterns.values());
      lsuffixes = IdTokenizer.localSuffixByDelimiter(patterns.values(), splitDelimiter);


//      pgLst = new ArrayList<> ();
//      plLst = new ArrayList<> ();
//      sgLst = new ArrayList<> ();
//      slLst = new ArrayList<> ();
//      pgLst.add(gprefix);
//      plLst.addAll(lprefixes.keySet());
//      sgLst.add(gsuffix);
//      slLst.addAll(lsuffixes.keySet());
//      p.ppattern.add(pgLst);
//      p.ppattern.add(plLst);
//      p.spattern.add(sgLst);
//      p.spattern.add(slLst);
      
      p.addPrefix(gprefix, lprefixes.keySet());
      p.addSuffix(gsuffix, lsuffixes.keySet());

      logger.info("gprefix: {}, lprefix: {}, gsuffix: {}, lsuffix: {}", gprefix, lprefixes, gsuffix, lsuffixes);
      //   logger.info("gprefix: {}, lprefix: {}, gsuffix: {}, lsuffix: {}", pgLst, plLst, sgLst, slLst);
      //   logger.info("ppattern: {}, spattern: {}", ppattern, spattern);

      if (gprefix.equals(prev_gprefix) && lprefixes.equals(prev_lprefixes) && 
          gsuffix.equals(prev_gsuffix) && lsuffixes.equals(prev_lsuffixes)) {
        break;
      }

      prev_gprefix = gprefix;
      prev_lprefixes = new HashMap<> (lprefixes);
      prev_gsuffix = gsuffix;
      prev_lsuffixes = new HashMap<> (lsuffixes);
    }
    
    Map<EntryPattern, List<EntryPattern>> uniqueTrims = new HashMap<> ();
    
    for (EntryPattern ep : patterns.values()) {
      if (!uniqueTrims.containsKey(ep)) {
        uniqueTrims.put(ep, new ArrayList<EntryPattern> ());
      }
      uniqueTrims.get(ep).add(ep);
    }
    
//    System.out.println(p.spattern);
//    System.out.println(p.ppattern);

//    System.out.println(sb);
    logger.info("species: {}, patterns: {} of {}", strings.size(), uniqueTrims.size(), patterns.size());
    return p;
  }
  
  public static String prefix(Set<String> strings) {
    String prefix = StringUtils.getCommonPrefix(strings.toArray(new String[0]));
    if (prefix.contains("_")) {
      return prefix.split("_")[0].concat("_");
    }
    return prefix;
  }
  
  public static String suffix(Set<String> strings) {
    String[] ref = new String[strings.size()];
    int i = 0;
    for (String s : strings) {
      ref[i++] = StringUtils.reverse(s);
    }
    return StringUtils.reverse(StringUtils.getCommonPrefix(ref));
  }
  
  public static String globalPrefix(Collection<EntryPattern> entryPatterns) {
    Set<String> s = new HashSet<> ();
    for (EntryPattern ep : entryPatterns) {
      s.add(ep.trim);
    }
    
    String prefix = prefix(s);
    if (prefix != null && !prefix.isEmpty()) {
      for (EntryPattern ep : entryPatterns) {
        ep.splicePrefix(prefix);
      }
    }
    
    return prefix;
  }
  
  public static String globalSuffix(Collection<EntryPattern> entryPatterns) {
    Set<String> s = new HashSet<> ();
    for (EntryPattern ep : entryPatterns) {
      s.add(ep.trim);
    }
    
    String suffix = suffix(s);
    if (suffix != null && !suffix.isEmpty()) {
      for (EntryPattern ep : entryPatterns) {
        ep.spliceSuffix(suffix);
      }
    }
    
    return suffix;
  }
  
  
  public static Map<String, Integer> localPrefixByDelimiter(
      Collection<EntryPattern> patterns, String delimiter) {
    Map<String, Integer> prefixes = new HashMap<> ();
    Map<String, List<EntryPattern>> prefixToPattern = new HashMap<> ();
    for (EntryPattern pattern : patterns) {
      if (pattern.trim.contains(delimiter)) {
        String[] data = pattern.trim.split(delimiter);
        String prefix = data[0] + delimiter;
        CollectionUtils.increaseCount(prefixes, prefix, 1);
        if (!prefixToPattern.containsKey(prefix)) {
          prefixToPattern.put(prefix, new ArrayList<EntryPattern> ());
        }
        prefixToPattern.get(prefix).add(pattern);
      }
    }
    
    Map<String, Integer> filter = new HashMap<> ();
    for (String p : prefixes.keySet()) {
      int total = prefixes.get(p);
      double ratio = (double) total / patterns.size();
      if (ratio > minRatio) {
        for (EntryPattern pattern : prefixToPattern.get(p)) {
          pattern.splicePrefix(p);
        }
        filter.put(p, total);
      }
    }
    
    return filter;
  }
  
  public static Map<String, Integer> localSuffixByDelimiter(
      Collection<EntryPattern> patterns, String delimiter) {
    Map<String, Integer> suffixes = new HashMap<> ();
    Map<String, List<EntryPattern>> suffixToPattern = new HashMap<> ();
    for (EntryPattern pattern : patterns) {
      if (pattern.trim.contains(delimiter)) {
        String[] data = pattern.trim.split(delimiter);
        String suffix = delimiter + data[data.length - 1];
        CollectionUtils.increaseCount(suffixes, suffix, 1);
        if (!suffixToPattern.containsKey(suffix)) {
          suffixToPattern.put(suffix, new ArrayList<EntryPattern> ());
        }
        suffixToPattern.get(suffix).add(pattern);
      }
    }
    
    Map<String, Integer> filter = new HashMap<> ();
    for (String p : suffixes.keySet()) {
      int total = suffixes.get(p);
      double ratio = (double) total / patterns.size();
//      logger.info("{}: {}", p, ratio);
      if (ratio > minRatio) {
        for (EntryPattern pattern : suffixToPattern.get(p)) {
          pattern.spliceSuffix(p);
        }
        filter.put(p, total);
      }
    }
    
    return filter;
  }
}
