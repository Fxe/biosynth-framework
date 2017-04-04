package pt.uminho.sysbio.biosynthframework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.Reaction;

public class BioSynthUtils {

  public static double EPSILON = 0;

  public static class Runtime {

    public static int getNumberOfCores() {
      return java.lang.Runtime.getRuntime().availableProcessors();
    }

    public static long getXmx() {
      return java.lang.Runtime.getRuntime().maxMemory() / 1024 / 1024;
    }

    public static long getUsedMemory() {
      return (java.lang.Runtime.getRuntime().totalMemory() - java.lang.Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    }

    public static long getFreeMemory() {
      return (getXmx() - getUsedMemory());
    }

    public static void runGC() {
      java.lang.Runtime.getRuntime().gc();
    }

    public static long freeMemory() {
      runGC();
      return java.lang.Runtime.getRuntime().freeMemory();
    }
  }

  public static boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
  }

  public static boolean isZero(double finalFluxValue) {
    if (finalFluxValue < EPSILON || finalFluxValue > -EPSILON) return false;
    return true;
  }

  public static String escapeEntry(String entry) {
    String entry_ = entry.replaceAll("@", "_ATSIGN_");
    entry_ = entry_.replaceAll(" ", "_SPACE_");
    entry_ = entry_.replaceAll(",", "_COMMA_");
    entry_ = entry_.replaceAll(";", "_SEMICOLON_");
    return entry_;
  }

  public static String toSymbol(Orientation orientation) {
    if (orientation == null) return "<n>";

    switch (orientation) {
    case LeftToRight: return " =>";
    case RightToLeft: return "<= ";
    case Reversible : return "<=>";
    default: return "<?>";
    }
  }

  public static int hashReaction(Reaction reaction) {
    int hash = 0;
    final int prime = 37;

    int lhsHash = reaction.getLeftStoichiometry().hashCode();
    int rhsHash = reaction.getRightStoichiometry().hashCode();

    Orientation orientation = reaction.getOrientation();
    if (orientation == null) orientation = Orientation.Reversible;
    switch(orientation) {
      case LeftToRight:
        hash = lhsHash * prime + rhsHash;
        break;
      case RightToLeft:
        hash = rhsHash * prime + lhsHash;
        break;
      case Unknown:
      case Reversible:
        hash = lhsHash + rhsHash;
        break;
      default:
        System.err.println("unknown action for " + orientation);
        break;
    }
    
    return hash;
  }

  public static int hashStiochiometry(Map<String, Double> stoichiometryMap) {
    int hash = 0;
    final int p1 = 1987;
    final int p2 = 2011;
    for (String key : stoichiometryMap.keySet()) {
      Double value = stoichiometryMap.get(key);
      hash += (key.hashCode() * p1 * value.hashCode()) ^ p2;
    }
    return hash;
  }

  public static String toDateString(long t, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    Date dt = new Date(t);
    return sdf.format(dt);
  }

  public static long toEpochLong(String dt, String pattern) throws ParseException {
    //    java.util.Locale.Category.FORMAT.toString();
//    String pattern = "yyyy/MM/dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    Date date = sdf.parse(dt);
    long t = date.getTime();
    String check = toDateString(t, pattern);
    if (dt.equals(check)) {
      return t;
    }

    throw new ParseException("check fail - " + dt + " -> " + check, 1);
  }
}
