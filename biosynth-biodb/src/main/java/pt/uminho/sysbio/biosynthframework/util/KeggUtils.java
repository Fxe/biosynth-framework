package pt.uminho.sysbio.biosynthframework.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeggUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(KeggUtils.class);
  
  private static Map<String, String> keggMonosaccharideCodes = null;
  
  public static Map<String, String> getKeggMonosaccharideCodes() {
    if (keggMonosaccharideCodes == null) {
      logger.info("[INIT] keggMonosaccharideCodes");
      keggMonosaccharideCodes = new HashMap<> ();
      //Hexose
      keggMonosaccharideCodes.put("Hex", "C00738");
      keggMonosaccharideCodes.put("Glc", "C00031");
      keggMonosaccharideCodes.put("Galf", "C21066");
      keggMonosaccharideCodes.put("Gal", "C00124");
      keggMonosaccharideCodes.put("LGal", "C01825");
      keggMonosaccharideCodes.put("Man", "C00159");
      keggMonosaccharideCodes.put("All", "C01487");
      keggMonosaccharideCodes.put("LAlt", "C21032");
      keggMonosaccharideCodes.put("Gul", "C06465");
      keggMonosaccharideCodes.put("LIdo", "C21050");
      keggMonosaccharideCodes.put("Tal", "C06467");
      //Pentose
      keggMonosaccharideCodes.put("Ribf", "C00121");
      keggMonosaccharideCodes.put("Rib", "C21057");
      keggMonosaccharideCodes.put("Araf", "C21067");
      keggMonosaccharideCodes.put("Ara", "C00216");
      keggMonosaccharideCodes.put("LAraf", "C06115");
      keggMonosaccharideCodes.put("LAra", "C00259");
      keggMonosaccharideCodes.put("Xyl", "C00181");
      keggMonosaccharideCodes.put("Lyx", "C00476");
      //Tetrose
      keggMonosaccharideCodes.put("Ery", "C01796");
      keggMonosaccharideCodes.put("Tho", "C06463");
      //Heptose
      keggMonosaccharideCodes.put("Lgro-manHep", "C21043");
      keggMonosaccharideCodes.put("gro-manHep", "C21042");
      //Deoxysugar
      keggMonosaccharideCodes.put("L6dAlt", "C21033");
      keggMonosaccharideCodes.put("6dTal", "C21058");
      keggMonosaccharideCodes.put("Fuc", "C01018");
      keggMonosaccharideCodes.put("LFuc", "C01019");
      keggMonosaccharideCodes.put("Rha", "C01684");
      keggMonosaccharideCodes.put("LRha", "C00507");
      keggMonosaccharideCodes.put("Qui", "C02522");
      keggMonosaccharideCodes.put("2dGlc", "C00586");
      keggMonosaccharideCodes.put("dRib", "C01801");
      //Di-deoxysugar
      keggMonosaccharideCodes.put("Oli", "C21054");
      keggMonosaccharideCodes.put("Tyv", "C21062");
      keggMonosaccharideCodes.put("Asc", null); //huh ?
      keggMonosaccharideCodes.put("Abe", "C06471");
      keggMonosaccharideCodes.put("Par", "C21055");
      keggMonosaccharideCodes.put("Dig", "C21045");
      keggMonosaccharideCodes.put("Col", "C03348");
      //Amino sugar
      keggMonosaccharideCodes.put("GlcN", "C00329");
      keggMonosaccharideCodes.put("GalN", "C02262");
      keggMonosaccharideCodes.put("ManN", "C03570");
      keggMonosaccharideCodes.put("AllN", "C21038");
      keggMonosaccharideCodes.put("LAltN", "C21035");
      keggMonosaccharideCodes.put("GulN", "C21048");
      keggMonosaccharideCodes.put("LIdoN", "C21051");
      keggMonosaccharideCodes.put("TalN", "C21060");
      //Amino sugar
      keggMonosaccharideCodes.put("GlcNAc", "C00140");
      keggMonosaccharideCodes.put("GalNAc", "C01132");
      keggMonosaccharideCodes.put("ManNAc", "C00645");
      keggMonosaccharideCodes.put("AllNAc", "C21039");
      keggMonosaccharideCodes.put("LAltNAc", "C21036");
      keggMonosaccharideCodes.put("GulNAc", "C21049");
      keggMonosaccharideCodes.put("LIdoNAc", "C21052");
      keggMonosaccharideCodes.put("TalNAc", "C21061");
      keggMonosaccharideCodes.put("FucNAc", "C15480");
      keggMonosaccharideCodes.put("LFucNAc", "C21046");
      keggMonosaccharideCodes.put("LRhaNAc", "C21056");
      keggMonosaccharideCodes.put("QuiNAc", "C15481");
      //Uronic acid
      keggMonosaccharideCodes.put("GlcA", "C00191");
      keggMonosaccharideCodes.put("GalA", "C00333");
      keggMonosaccharideCodes.put("ManA", "C02024");
      keggMonosaccharideCodes.put("AllA", "C21037");
      keggMonosaccharideCodes.put("LAltA", "C21034");
      keggMonosaccharideCodes.put("GulA", "C21047");
      keggMonosaccharideCodes.put("LGulA", "C06477");
      keggMonosaccharideCodes.put("LIdoA", "C06472");
      keggMonosaccharideCodes.put("TalA", "C21059");
      //Sialic acid
      keggMonosaccharideCodes.put("Sia", null);
      keggMonosaccharideCodes.put("Neu", "C06469");
      keggMonosaccharideCodes.put("Neu5Ac", "C00270");
      keggMonosaccharideCodes.put("Neu5Gc", "C03410");
      //ugar alcohol
      keggMonosaccharideCodes.put("Ery-ol", "C00503");
      keggMonosaccharideCodes.put("Ara-ol", "C01904");
      keggMonosaccharideCodes.put("Xyl-ol", "C00379");
      keggMonosaccharideCodes.put("Rib-ol", "C00474");
      keggMonosaccharideCodes.put("Glc-ol", "C00794");
      keggMonosaccharideCodes.put("Gal-ol", "C01697");
      keggMonosaccharideCodes.put("Man-ol", "C00392");
      //Ketose
      keggMonosaccharideCodes.put("Psi", "C06468");
      keggMonosaccharideCodes.put("Fruf", "C00095");
      keggMonosaccharideCodes.put("Fru", "C05003");
      keggMonosaccharideCodes.put("LSor", "C19679");
      keggMonosaccharideCodes.put("Tag", "C00795");
      keggMonosaccharideCodes.put("Xul", "C00310");
      keggMonosaccharideCodes.put("Sed", "C02076");
      //Others
      keggMonosaccharideCodes.put("Api", "C21040");
      keggMonosaccharideCodes.put("Bac", "C21041");
      keggMonosaccharideCodes.put("The", "C16287");
      keggMonosaccharideCodes.put("Aco", "Acofriose");
      keggMonosaccharideCodes.put("Cym", "C08234");
      keggMonosaccharideCodes.put("Mur", "C06470");
      keggMonosaccharideCodes.put("MurNAc", "C02713");
      keggMonosaccharideCodes.put("MurNGc", "C21053");
      keggMonosaccharideCodes.put("Dha", "C21044");
      keggMonosaccharideCodes.put("Kdo", "C21063");
      keggMonosaccharideCodes.put("Kdn", "C20934");
      
      keggMonosaccharideCodes.put("Dol", "C00381");
      
      keggMonosaccharideCodes.put("P", "C00009");
      keggMonosaccharideCodes.put("S", "C00059");
    }
    return keggMonosaccharideCodes;
  }
  
  public static Map<String, Integer> parseComposition(String composition) {
    if (composition == null) {
      return null;
    }
    
    Map<String, Integer> result = new HashMap<> ();
    
    String[] parts = composition.split(" ");
//    System.out.println(composition);
    Map<String, Integer> monosaccharideCount = new HashMap<> ();
    
    for (String monosaccharide : parts) {
      int last = monosaccharide.lastIndexOf(")");
      //get block after the last (
      Integer count = Integer.parseInt(monosaccharide.trim().substring(last + 1));
      //remove first char (
      String code = monosaccharide.trim().substring(1, last);
//      System.out.println("\t" + monosaccharide + " " + code + " -> "  + count);
      CollectionUtils.increaseCount(monosaccharideCount, code, count);
    }
    Map<String, String> codeMap = getKeggMonosaccharideCodes();
    for (String code : monosaccharideCount.keySet()) {
      String cpdEntry = codeMap.get(code);
      if (cpdEntry == null) {
//        System.out.println("what ? " + code);
//        System.out.println(monosaccharideCount);
        return null;
      }
      
      result.put(cpdEntry, monosaccharideCount.get(code));
    }
    
    return result;
  }
}
