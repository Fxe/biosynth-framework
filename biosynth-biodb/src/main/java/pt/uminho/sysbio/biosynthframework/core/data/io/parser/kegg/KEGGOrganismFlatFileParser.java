package pt.uminho.sysbio.biosynthframework.core.data.io.parser.kegg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.AbstractKeggFlatFileParser;

public class KEGGOrganismFlatFileParser extends AbstractKeggFlatFileParser{

	private static String _TAX_TAB = "TAXONOMY";
	
	public KEGGOrganismFlatFileParser(String flatfile) {
		super(flatfile);
		
		this.parseContent();
		
		
		int taxIndex = this.getTabIndex( _TAX_TAB);
		if ( taxIndex >= 0) {
			String taxContent = this.tabContent_.get( taxIndex);
			Pattern tabPattern = Pattern.compile( "TAX:([0-9]+)");
			//System.out.println( taxContent);
			Matcher parser = tabPattern.matcher( taxContent);
			while ( parser.find()) {
				//System.out.println( parser.group(1));
				this.tabContent_.put(taxIndex, parser.group(1));
			}
		}
	}

	public String getEntry() {
		int tabIndex = this.getTabIndex("ENTRY");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getName() {
		int tabIndex = this.getTabIndex("NAME");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getTaxonomy() {
		int tabIndex = this.getTabIndex( _TAX_TAB);
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getAnnotation() {
		int tabIndex = this.getTabIndex("ANNOTATION");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getDefinition() {
		int tabIndex = this.getTabIndex("DEFINITION");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
}
