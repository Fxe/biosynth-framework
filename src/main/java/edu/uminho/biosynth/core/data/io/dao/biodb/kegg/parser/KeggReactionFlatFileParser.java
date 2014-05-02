package edu.uminho.biosynth.core.data.io.dao.biodb.kegg.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KeggReactionFlatFileParser extends AbstractKeggFlatFileParser {

//	KEGG REACTION
//
//	Web page      	Flat file
//	Entry	ENTRY X X
//	Name	NAME ?
//	Definition	DEFINITION X X
//	Equation	EQUATION X X
//	(blank)	(gif image)
//	Remark	REMARK X X
//	Comment	COMMENT X X
//	RPair	RPAIR X
//	Enzyme	ENZYME X
//	Pathway	PATHWAY X
//	Orthology	ORTHOLOGY
//	Reference	REFERENCE

	
	public KeggReactionFlatFileParser(String flatfile) {
		super(flatfile);
		
		this.parseContent();
	}

	public String getEntry() {
		int tabIndex = this.getTabIndex("ENTRY");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getName() {
		int index = this.getTabIndex("NAME");
		String value = this.getContent(index);
		return value;
	}
	
	public String getEquation() {
		int index = this.getTabIndex("EQUATION");
		if (index < 0) return null;
		String value = this.getContent(index);
		return value;
	}
	
	public String getDefinition() {
		int index = this.getTabIndex("DEFINITION");
		if (index < 0) return null; 
		String value = this.getContent(index);
		return value;
	}
	
	public String getComment() {
		System.out.println(this.tabContent_);
		int tabIndex = this.getTabIndex("COMMENT");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}

	public String getRemark() {
		int tabIndex = this.getTabIndex("REMARK");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public List<String> getEnzymes() {
		List<String> enzymeIdSet = new ArrayList<String> ();
		int tabIndex = this.getTabIndex("ENZYME");
		
		String content = this.tabContent_.get(tabIndex);
		
		if ( content == null || content.isEmpty()) return enzymeIdSet;
		
		String[] enzymeIdArray = content.split("[\\s+]");
		for (int i = 0; i < enzymeIdArray.length; i++) {
			if ( !enzymeIdArray[i].isEmpty()) enzymeIdSet.add(enzymeIdArray[i]);
		}
		
		return enzymeIdSet;
	}
	
	public List<String> getRPairs() {
		int tabIndex = this.getTabIndex("RPAIR");
		String content = this.tabContent_.get(tabIndex);
		List<String> rpairList = new ArrayList<> ();
		if ( content == null || content.isEmpty()) return rpairList;
		Pattern rpairIdPattern = Pattern.compile( "([rpRP]+[0-9]+)");
		Matcher matcher = rpairIdPattern.matcher( content);
		while ( matcher.find()) {
			rpairList.add( matcher.group(1));
		}
		
		return rpairList;
	}
	
	public List<String> getPathways() {
		int tabIndex = this.getTabIndex("PATHWAY");
		String content = this.tabContent_.get(tabIndex);
		List<String> pathwayList = new ArrayList<> ();
		if ( content == null || content.isEmpty()) return pathwayList;
		Pattern pathwayIdPattern = Pattern.compile( "([a-zA-Z]+[0-9]+)");
		Matcher matcher = pathwayIdPattern.matcher( content);
		while ( matcher.find()) {
			pathwayList.add( matcher.group(1));
		}
		
		return pathwayList;
	}
	
	public List<String> getOrthologies() {
		int tabIndex = this.getTabIndex("ORTHOLOGY");
		String content = this.tabContent_.get(tabIndex);
		List<String> orthologyList = new ArrayList<> ();
		if ( content == null || content.isEmpty()) return orthologyList;
		Pattern orthologyIdPattern = Pattern.compile( "([a-zA-Z]+[0-9]+)");
		Matcher matcher = orthologyIdPattern.matcher( content);
		while ( matcher.find()) {
			orthologyList.add( matcher.group(1));
		}
		
		return orthologyList;
	}
}
