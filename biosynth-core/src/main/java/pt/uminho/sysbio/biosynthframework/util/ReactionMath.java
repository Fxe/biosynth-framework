package pt.uminho.sysbio.biosynthframework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class ReactionMath {

  private static final Logger logger = LoggerFactory.getLogger(ReactionMath.class);
  
  public static<K> Map<K, Double> removeZero(Map<K, Double> s) {
    Map<K, Double> s_ = new HashMap<> ();
    for (K k : s.keySet()) {
      double v = s.get(k);
      if (v != 0) {
        s_.put(k, v);
      }
    }
    return s_;
  }
  
  public static<K> Map<K, Double> divide(Map<K, Double> s, double v) {
    Map<K, Double> s_ = new HashMap<> ();
    for (K k : s.keySet()) {
      s_.put(k, s.get(k) / v);
    }
    return s_;
  }

  public static<K> Map<K, Double> scale(Map<K, Double> s, double v) {
    Map<K, Double> s_ = new HashMap<> ();
    for (K k : s.keySet()) {
      s_.put(k, s.get(k) * v);
    }
    return s_;
  }

  public static<K> Map<K, Double> sum(Map<K, Double> s1, Map<K, Double> s2) {
    Map<K, Double> s_ = new HashMap<> ();
    Set<K> i = Sets.intersection(s1.keySet(), s2.keySet());
    for (K k : s1.keySet()) {
      double v = s1.get(k);
      if (i.contains(k)) {
        double v2 = s2.get(k);
        s_.put(k, v + v2);
      } else {
        s_.put(k, v);
      }
    }
    //put remaining
    for (K k : s2.keySet()) {
      double v = s2.get(k);
      if (!i.contains(k)) {
        s_.put(k, v);
      }
    }
    return s_;
  }

  public static Map<Long, Double> rxnMathSumNeu(Map<Long, Double> rxn1, 
      Map<Long, Double> rxn2,
      long n) {

    if (rxn1.containsKey(n) && rxn2.containsKey(n)) {
      double x = rxn1.get(n);
      double y = rxn2.get(n);
      logger.debug("{} [+{}+] {}", x, n, y);
      if (Math.abs(y) != 1) {
        rxn2 = divide(rxn2, y);
        y = rxn2.get(n);
      }
      // x + k * y = 0
      double scale = (-1 * x) / y;
      Map<Long, Double> rxn2_ = scale(rxn2, scale);
      double y_ = rxn2_.get(n);
      logger.debug("Scale {} by {}", rxn2, scale);
      logger.debug("{} [+{}+] {}", x, n, y_);
      Map<Long, Double> rxn3 = sum(rxn1, rxn2_);
      logger.debug("{}", rxn3);
      return rxn3;
    } else {
      logger.warn("specie {} not in both reactions");
      return null;
    }

  }

  public static long[] biomasses = new long[] {
      1817753, //r_4041@ymn_7_6_cobra
      1818208, //r_2133@ymn_7_6_cobra
      1818242, //r_2110@ymn_7_6_cobra
      1765560, //r_2133@ymn6_06_cobra yeast 6 biomass pseudoreaction
      1765574, //r_2110@ymn6_06_cobra yeast 5 biomass pseudoreaction
      1768452, //R_CBIOMASS
      1768047, //R_NBIOMASS
      1757270, //R_biomass_SC5_notrace
      1805715, //R_biomass_wild
      1805714, //R_biomass_core
      1811008, //R_1095
      1820826, //R_Biomass@iPS189
      1750339, //R_BiomassHP_published@iIT341
      1832817, //R_biomass_Mtb_9_60atp
  };

  public static long[][] reduce = {
      //r_4041@ymn_7_6_cobra
      { 1817753}, { 1817181, 1814827, //1 Amino Acids (tRNA) 1-20
        1818582, 1814208, //2
        1818570, 1815915, //3
        1819402, 1816459, //4
        1819087, 1815903,
        1817615, 1815661,
        1818565, 1814216,
        1819461, 1816387,
        1819067, 1815500,
        1819898, 1816408,
        1817977, 1814579,
        1816703, 1815082,
        1817662, 1815663,
        1817212, 1814811,
        1817975, 1815667,
        1817143, 1814830,
        1819043, 1815932,
        1818915, 1815658,
        1817440, 1815792,
        1819109, 1815465,
        1818283, 1814219, //lipid
      }, 
      //r_2133@ymn_7_6_cobra
      { 1818208}, { 1817181, 1814827,
        1818582, 1814208,
        1818570, 1815915,
        1819402, 1816459,
        1819087, 1815903,
        1817615, 1815661,
        1818565, 1814216,
        1819461, 1816387,
        1819067, 1815500,
        1819898, 1816408,
        1817977, 1814579,
        1816703, 1815082,
        1817662, 1815663,
        1817212, 1814811,
        1817975, 1815667,
        1817143, 1814830,
        1819043, 1815932,
        1818915, 1815658,
        1817440, 1815792,
        1819109, 1815465,
        1818283, 1814219, //lipid
      },
      //r_2110@ymn_7_6_cobra
      { 1818242}, { 1818283, 1814219, //lipid
      },
      //r_2133@ymn6_06_cobra
      { 1765560}, { 1765625, 1762144, //lipid
        1765274, 1762809,
        1765273, 1762810,
        1764382, 1762710,
        1764630, 1762847,
        1764052, 1762128,
        1764938, 1762588,
        1764610, 1762843,
        1765123, 1763400,
        1764033, 1762141,
        1764038, 1762829,
        1764667, 1762412,
        1764408, 1762719,
        1764985, 1762826,
        1764473, 1762583,
        1764989, 1763576,
        1765489, 1762825,
        1764343, 1762714,
        1763893, 1762731,
        1764713, 1762720,
        1764647, 1762268,
      },
      //r_2110@ymn_7_6_cobra
      { 1765574}, { 1765625, 1762144, //lipid
      },
      //R_biomass_wild@iAZ900
      { 1805715}, { 1806139, 1805438,
        1806298, 1804784,
        1806553, 1804342,
        1805950, 1804346,
        1805596, 1805016,
        1805649, 1805547,
        1805841, 1805472,
        1805718, 1805423,
        1806772, 1804879,
        1805675, 1804282,
        1805673, 1804455,
        1806024, 1804185,
        1806670, 1805211,
        1806541, 1805338,
        1805676, 1805562,
        1805633, 1805162,
        1805825, 1805151,
        1806368, 1804291,
        1806054, 1804987,
        1806916, 1804287,
      },
      //R_biomass_core@iAZ900
      { 1805714}, { 1806139, 1805438,
        1806298, 1804784,
        1806553, 1804342,
        1805950, 1804346,
        1805596, 1805016,
        1805649, 1805547,
        1805841, 1805472,
        1805718, 1805423,
        1806772, 1804879,
        1805675, 1804282,
        1805673, 1804455,
        1806024, 1804185,
        1806670, 1805211,
        1806541, 1805338,
        1805676, 1805562,
        1805633, 1805162,
        1805825, 1805151,
        1806368, 1804291,
        1806054, 1804987,
        1806916, 1804287,
      },
      //R_NBIOMASS
      { 1768047}, { 1767052, 1765822}, //R_NLIPIDS
      //R_CBIOMASS
      { 1768452}, { 1768037, 1765822}, //R_CLIPIDS
  };

//  public static Map<Long, Set<Long>> detect(long brxnId) {
//    Map<Long, Set<Long>> rxnToSpiMap = new HashMap<> ();
//    Map<Long, Double> biomass1S = service.getStoichiometry(brxnId);
//    System.out.println(service.getSubType(brxnId));
//    for (long spiId : biomass1S.keySet()) {
//      double v = biomass1S.get(spiId);
//      String name = service.getNamePropertyById(spiId);
//      Set<Long> rxnSet = service.stoichToDegree.get(spiId);
//      Set<Long> nonBiomass = new HashSet<> ();
//      for (long rxn : rxnSet) {
//        EntityType type = EntityType.valueOf(service.getSubType(rxn));
//        if (!type.equals(EntityType.BIOMASS)) {
//          nonBiomass.add(rxn);
//        }
//        //        System.out.println(rxn + " -> " + type);
//      }
//      int degree = nonBiomass.size();
//      logger.info("degree: {} : {} {} -> {}", degree, spiId, v, name);
//
//      if (degree == 1) {
//        long rxnId = nonBiomass.iterator().next();
//        if (!rxnToSpiMap.containsKey(rxnId)) {
//          rxnToSpiMap.put(rxnId, new HashSet<> ());
//        }
//        rxnToSpiMap.get(rxnId).add(spiId);
//        //        System.out.println(nonBiomass);
//      }
//    }
//
//    return rxnToSpiMap;
//  }

//  public static<K> void printDiff(Map<K, Double> actual, Map<K, Double> prev, FileBiodbService dbService) {
//    Map<K, Double> lost = new HashMap<> ();
//    Map<K, Double> added = new HashMap<> ();
//    Map<K, Double> unchanged = new HashMap<> ();
//    Map<K, Double> change = new HashMap<> ();
//    Set<K> both = Sets.intersection(actual.keySet(), prev.keySet());
//    for (K id : prev.keySet()) {
//      String e = dbService.getEntryById((long)id);
//      String n = dbService.getNamePropertyById((long)id);
//      //      logger.info("AP {} {} {} {}", id, e, n, prev.get(id));
//      //      logger.info("AA {} {} {} {}", id, e, n, actual.get(id));
//      if (actual.containsKey(id) && actual.get(id) != 0.0) {
//        double p = prev.get(id);
//        double a = actual.get(id);
//        if (p != a) {
//          change.put(id, a);
//        } else {
//          unchanged.put(id, a);
//        }
//      } else {
//        lost.put(id, prev.get(id));
//      }
//    }
//
//    for (K key : actual.keySet()) {
//      if (!both.contains(key)) {
//        added.put(key, actual.get(key));
//      }
//    }
//
//    for (K id : lost.keySet()) {
//      String e = dbService.getEntryById((long)id);
//      String n = dbService.getNamePropertyById((long)id);
//      //      logger.info("DD {} {}", id, lost.get(id));
//      logger.info("DD {} {} {} {}", id, e, n, lost.get(id));
//    }
//    for (K id : added.keySet()) {
//      String e = dbService.getEntryById((long)id);
//      String n = dbService.getNamePropertyById((long)id);
//      //      logger.info("DD {} {}", id, lost.get(id));
//      logger.info("II {} {} {} {}", id, e, n, added.get(id));
//    }
//    for (K id : change.keySet()) {
//      String e = dbService.getEntryById((long)id);
//      String n = dbService.getNamePropertyById((long)id);
//      //      logger.info("DD {} {}", id, lost.get(id));
//      logger.info("UU {} {} {} {} {}", id, e, n, prev.get(id), actual.get(id));
//    }
//  }
//
//  public static Map<Long, Double> reduce(Map<Long, Double> stoich, Map<Long, Long> reduceMap, BiodbService dbService) {
//    //    Map<Long, Double> reduced = new HashMap<> ();
//    for (long rxnId : reduceMap.keySet()) {
//      long spiId = reduceMap.get(rxnId);
//
//      //      long rxnAId = biomass1;
//      long rxnBId = rxnId;
//      String spiEntry = dbService.getEntryById(spiId);
//      String rxnAEntry = "TARGET";
//      String rxnBEntry = dbService.getEntryById(rxnBId);
//      logger.info("Remove {} - {}", spiEntry, dbService.getNamePropertyById(spiId));
//      logger.info("{} [+{}+] {}", rxnAEntry, spiEntry, rxnBEntry);
//      logger.info("{} [+{}+] {}", "TARGET", dbService.getNamePropertyById(spiId), dbService.getNamePropertyById(rxnBId));
//
//      Map<Long, Double> s_ = rxnMathSumNeu(stoich, dbService.getStoichiometry(rxnId), spiId);
//
//      //      printDiff(s_, biomass1S);
//      stoich = removeZero(s_);
//    }
//    Map<Long, Double> a = removeZero(stoich);
//    for (long k : a.keySet()) {
//      double v = a.get(k);
//      if (v < 0) {
//        String str = dbService.getEntryById(k) + " " + dbService.getNamePropertyById(k);
//        logger.debug("--- {} {} {}", v, k, str);
//      }
//    }
//    for (long k : a.keySet()) {
//      double v = a.get(k);
//      if (v > 0) {
//        String str = dbService.getEntryById(k) + " " + dbService.getNamePropertyById(k);
//        logger.debug("+++ {} {} {}", v, k, str);
//      }
//    }
//    System.out.println(a);
//
//    return a;
//  }



//  public static void scanBiomasses() {
//    for (long brxnId : biomasses) {
//      String biomassEntry = service.getEntryById(brxnId);
//      if (biomassEntry != null) {
//        //      long biomass1 = 1817753L; //r_4041@ymn_7_6_cobra
//        //RXN_ID -> SPI_ID => reduce biomass with reaction [RXN_ID] 
//        //                    neutralize specie [SPI_ID]
//        logger.info("Biomass: [{}]{}", brxnId, biomassEntry);
//        Map<Long, Set<Long>> rxnToSpiMap = detect(brxnId);
//
//        for (long rxnId : rxnToSpiMap.keySet()) {
//          long spiId = rxnToSpiMap.get(rxnId).iterator().next();
//          String spiEntry = service.getEntryById(spiId);
//          String rxnEntry = service.getEntryById(rxnId);
//          String spiName = service.getNamePropertyById(spiId);
//          System.out.println(rxnId + "\t" + rxnEntry + "\t" + spiId + "\t" + spiEntry + "\t" + spiName);
//        }
//      } else {
//        logger.warn("entity {} not loaded", brxnId);
//      }
//    }
//  }

  public static Map<Long, Map<Long, Long>> build() {
    Map<Long, Map<Long, Long>> biomassFixMap = new HashMap<> ();
    for (int i = 0; i < reduce.length; i+=2) {
      long rxnId = reduce[i][0];
      Map<Long, Long> reduceMap = new HashMap<> ();
      //      Map<Long, Double> s = service.getStoichiometry(rxnId);
      for (int j = 0; j < reduce[i + 1].length; j+=2) {
        reduceMap.put(reduce[i + 1][j], reduce[i + 1][j + 1]);
      }

      biomassFixMap.put(rxnId, reduceMap);
    }
    return biomassFixMap;
  }

}
