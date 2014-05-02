package edu.uminho.biosynth.core.data.io.parser.kegg;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import edu.uminho.biosynth.core.data.io.dao.biodb.kegg.parser.AbstractKeggFlatFileParser;

public class KEGGRPairFlatFileParser extends AbstractKeggFlatFileParser{

	public static boolean VERBOSE = false;
	
	public KEGGRPairFlatFileParser(String flatfile) {
		super(flatfile);
		this.parseContent();
	}
	
	public String getName() {
		int index = this.getTabIndex("name");
		String value = this.getContent(index);
if (VERBOSE) System.out.println(value);
		return value;
	}
	
	public String getEntry1() {
		int index = this.getTabIndex("entry1");
		String value = this.getContent(index);
		Pattern tabPattern = Pattern.compile( "((C|G)[0-9]{5})");
		Matcher parser = tabPattern.matcher( value);
		String entryCompound = null;
		while ( parser.find()) {
			entryCompound = parser.group(1);
		}
		return entryCompound;
	}
	
	public String getEntry2() {
		int index = this.getTabIndex("entry2");
		String value = this.getContent(index);
		Pattern tabPattern = Pattern.compile( "((C|G)[0-9]{5})");
		Matcher parser = tabPattern.matcher( value);
		String entryCompound = null;
		while ( parser.find()) {
			entryCompound = parser.group(1);
		}
		return entryCompound;
	}
	
	public String getType() {
		int index = this.getTabIndex("type");
		String value = this.getContent(index);
if (VERBOSE) System.out.println(value);
		return value;
	}
	
	public Set<String> getRelatedPairs() {
		Set<String> rpairSet = new HashSet<String> ();
		int index = this.getTabIndex("relatedpair");
		if (index < 0) {
			System.err.println("KEGGRPairFlatFileParser::getRelatedPairs - RELATEDPAIR NOT FOUND");
			return rpairSet;
		}
		String value = this.getContent(index);
		String[] relatedRPairs = value.split("\\s+");
if (VERBOSE) System.out.println(Arrays.toString(relatedRPairs));
		rpairSet.addAll( Arrays.asList(relatedRPairs));
		return rpairSet;
	}
	
	public Set<String> getReactions() {
		int index = this.getTabIndex("reaction");
		Set<String> reactionSet = new HashSet<String> ();
		if (index < 0) {
			System.err.println("KEGGRPairFlatFileParser::getReactions - REACTIONS NOT FOUND");
			return reactionSet;
		}
		String value = this.getContent(index);
		String[] reactionArray = value.split("\\s+");
if (VERBOSE) System.out.println(value);
		reactionSet.addAll( Arrays.asList(reactionArray));
		return reactionSet;
	}

}
