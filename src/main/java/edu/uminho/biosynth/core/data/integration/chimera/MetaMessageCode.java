package edu.uminho.biosynth.core.data.integration.chimera;

public interface MetaMessageCode {
	public static String MULTIPLE_FORMULAS_UNPARSEABLE = "III000010";
	public static String MULTIPLE_FORMULAS = "III000011";
	public static String MULTIPLE_INCHIS = "III000012";
	
	public static String CROSSREFERENCE_FULLY_CONNECTED = "III000020";
	public static String CROSSREFERENCE_DISJOINT_SET = "III000021";
	public static String CROSSREFERENCE_DISJOINT_ELEMENT = "III000022";
	
	public static String MULTIPLE_SOURCES_KEGG_CPD =    "III000901";
	public static String MULTIPLE_SOURCES_KEGG_GL =     "III000902";
	public static String MULTIPLE_SOURCES_KEGG_DR =	    "III000903";
	public static String MULTIPLE_SOURCES_META =        "III000904";
	public static String MULTIPLE_SOURCES_CHEBI_P =	    "III000905";
	public static String MULTIPLE_SOURCES_BIGG =        "III000906";
	public static String MULTIPLE_SOURCES_MNX =         "III000907";
	public static String MULTIPLE_SOURCES_SEED =        "III000908";
}

