package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser;

public class KeggTokens {
	
	static final public String KEY_REGEXP = "^([A-Z_]+)";
	static final public String PROP_KEY_VALUE_SEPARATOR_REGEXP = "(\\s{2,}|\\t+)";
	static final public String KEY_VALUES_REGEXP = KEY_REGEXP + PROP_KEY_VALUE_SEPARATOR_REGEXP + "(.+)"; 
	static final public String END_OF_FILE_REGEXP = "///";
	
	static final public String ECNUMBER_REGEXP = "(\\d+\\.\\d+\\.\\d+\\.\\d+[a-z]?|\\d+\\.\\d+\\.\\d+\\.\\-|\\d+\\.\\d+\\.[\\d+\\-].\\-|\\d+\\.[\\d+\\-]\\.\\-.\\-)";
	static final public String GENE_DEFINITION_EC_REGEXP = "\\(EC:([^\\)]+)\\)";
	static final public String KOG_GENES_REGEXP = "([A-Z]+):\\s+(.+)";
	static final public String GENE_WITH_NAME = "([^\\(]+)\\(([^\\)]+)\\)";
	static final public String MODULE_WITH_NAME = "(M\\d+)\\s+(.+)";
	static final public String PATHWAY_WITH_NAME = "([a-z]+\\d+)\\s+(.+)";
	static final public String ORTHOLOGY_WITH_NAME = "(K\\d+)\\s+(.+)";
	static final public String COMPOUND_WITH_NAME = "(C\\d+)\\s+(.+)";
	static final public String REACTION_WITH_NAME = "(R\\d+)\\s+(.+)";
	static final public String EC_NUMBER_REACTION = "\\[RN:(R\\d+)\\]";

	static final public String ENTRY = "ENTRY";
	static final public String NAME = "NAME";
	static final public String DEFINITION = "DEFINITION";
	static final public String AASEQ = "AASEQ";
	static final public String NTSEQ = "NTSEQ";
	static final public String GENES = "GENES";
	static final public String MODULE = "MODULE";
	static final public String PATHWAY = "PATHWAY";
	static final public String ORTHOLOGY = "ORTHOLOGY";
	static final public String COMPOUND = "COMPOUND";
	static final public String REACTION = "REACTION";

}
