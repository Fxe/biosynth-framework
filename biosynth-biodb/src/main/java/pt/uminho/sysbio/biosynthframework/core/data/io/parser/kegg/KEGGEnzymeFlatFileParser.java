package pt.uminho.sysbio.biosynthframework.core.data.io.parser.kegg;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.AbstractKeggFlatFileParser;

public class KEGGEnzymeFlatFileParser extends AbstractKeggFlatFileParser {
	
	private final String geneRegex = "(\\w+):\\s+([a-zA-Z0-9-_ ()\\.]+)?";
	
	public KEGGEnzymeFlatFileParser( String flatfile) {
		super( flatfile);
		
		this.parseContent();
	}
	
	public String getName() {
		int tabIndex = this.getTabIndex("NAME");
		String content = this.tabContent_.get(tabIndex);
		String[] names = content.split("[\t \n]");
		StringBuilder sb = new StringBuilder();
		for (String name: names) sb.append(name);
		return sb.toString();
	}
	
	public Map<String, String> getOrganisms() {
		HashMap<String, String> organisms =
				new HashMap<String, String> ();
		
		int tabIndex = this.getTabIndex("GENES");
		if ( tabIndex < 0) {
			//TODO: DO SOME ERROR !
			System.err.println("KeggEnzymeFlatFileParser::getOrganisms - INDEX < 0");
			return null;
		}
		
		Pattern tabPattern = Pattern.compile( geneRegex);
		
		Matcher parser = tabPattern.matcher( this.getContent( tabIndex));
		
		while ( parser.find()) {
			//KILL SOME PERFORMANCE !
			if ( organisms.containsKey( parser.group(1)))
				System.err.println("ERRO::50062 - " + parser.group(1));
			organisms.put( parser.group(1), parser.group(2));
		}
		
		return organisms;
	}
}
