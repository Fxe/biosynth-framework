package pt.uminho.sysbio.biosynthframework.util;

public class BiodbUtils {
  public static String detectKeggDatabase(String id) {
    if (id != null) {
      /*
       * CXXXXX
       * GXXXXX
       * DXXXXX
       */
      if (id.length() == 6) {
        switch (id.charAt(0)) {
          case 'C': return "LigandCompound";
          case 'G': return "LigandGlycan";
          case 'D': return "LigandDrug";
        default:
          break;
        }
      }
    }
    return null;
  }
}
