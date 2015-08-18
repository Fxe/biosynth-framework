package pt.uminho.sysbio.biosynthframework.core.data.io.parser.swissprot;

import java.util.HashSet;
import java.util.Set;

public class ExPASyEnzymeFlatFileParser extends AbstractSwissProtFlatFileParser {

	public ExPASyEnzymeFlatFileParser(String flatfile) {
		super(flatfile);
		
		this.parseContent();
	}
	
	public String getName() {
		if ( !this.tabContent_.containsKey("DE")) return null;
		
		return this.tabContent_.get("DE");
	}
	
	public String getId() {
		if ( !this.tabContent_.containsKey("ID")) return null;
		
		return this.tabContent_.get("ID");
	}
	
	public Set<String> getGeneEntrys() {
		Set<String> ret = new HashSet<String> ();
		
		if ( !this.tabContent_.containsKey("DR")) return ret;
		
		String DR = this.tabContent_.get("DR");
		String[] entrys = DR.split(";");
		for (String entry : entrys) {
			String[] pair = entry.split(",");
			ret.add(pair[0].trim());
		}
		return ret;
	}
	
}
