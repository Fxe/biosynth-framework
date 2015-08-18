package pt.uminho.sysbio.biosynthframework.core.data.io.parser.kegg;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.AbstractKeggFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.IGenericReactionParser;

public class KEGGReactionFlatFileParser extends AbstractKeggFlatFileParser implements IGenericReactionParser {
	
	public static boolean VERBOSE = false;
	
	public KEGGReactionFlatFileParser( String flatfile) {
		super( flatfile);
		this.parseContent();
	}
	
	@Override
	public String getEntry() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getName() {
		int index = this.getTabIndex("name");
		String value = this.getContent(index);
		if (VERBOSE) System.out.println(value);
		return value;
	}
	
	public String getEquation() {
		int index = this.getTabIndex("equation");
		if (index < 0) System.err.println("KEGGReactionFlatFileParser::EQUATION NOT FOUND");
		String value = this.getContent(index);
		if (VERBOSE) System.out.println(value);
		return value;
	}
	
	public String getRemark() {
		int index = this.getTabIndex("remark");
		if (index < 0) return ""; 
		String value = this.getContent(index);
		if (VERBOSE) System.out.println(value);
		return value;
	}
	
	public Set<String> getSimilarReactions() {
		Set<String> srxn = new HashSet<String> ();
		String remark = getRemark();
		if (!remark.isEmpty()) {
			if (remark.contains("Same as")) {
				Pattern tabPattern = Pattern.compile( "(R[0-9]+)");
				Matcher parser = tabPattern.matcher( remark);
				while ( parser.find()) {
					srxn.add( parser.group(1));
				}
			} else {
				System.err.println("REMARK -> " + remark);
			}
		}
		return srxn;
	}
	
	public Set<String> getRPair() {
		Set<String> rpairID = new HashSet<String> ();
		int index = this.getTabIndex("rpair");
		if (index < 0) return rpairID;
		
		String value = this.getContent(index);
		if (VERBOSE) System.out.println(value);
		Pattern tabPattern = Pattern.compile( "(RP[0-9]+)");
		Matcher parser = tabPattern.matcher( value);
		while ( parser.find()) {
			rpairID.add( parser.group(1));
		}
		return rpairID;
	}
	
	public Set<String> getEnzymes() {
		Set<String> enzymes = new HashSet<String> ();
		int index = this.getTabIndex("enzyme");
		if (index < 0) return enzymes;
		
		String value = this.getContent(index);
		if (VERBOSE) System.out.println(value);
		String[] ecnList = value.replaceAll("\\s+", " ").split(" ");
		enzymes.addAll(Arrays.asList(ecnList));
		return enzymes;
	}

}
