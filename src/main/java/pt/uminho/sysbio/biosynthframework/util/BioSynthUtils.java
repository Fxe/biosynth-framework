package pt.uminho.sysbio.biosynthframework.util;

import pt.uminho.sysbio.biosynthframework.Orientation;

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
		String entry_ = entry.replaceAll(" ", "_SPACE_");
		entry_ = entry_.replaceAll(",", "_COMMA_");
		entry_ = entry_.replaceAll(";", "_SEMICOLON_");
		return entry_;
	}

	public static String toSymbol(Orientation orientation) {
		switch (orientation) {
			case LeftToRight: return " =>";
			case RightToLeft: return "<= ";
			case Reversible : return "<=>";
			default: return "<?>";
		}
	}
}
