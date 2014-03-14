package edu.uminho.biosynth.core.data.io.dao.biodb.kegg.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.io.parser.kegg.AbstractKeggFlatFileParser;

public class KeggGlycanMetaboliteFlatFileParser extends AbstractKeggFlatFileParser {

	public KeggGlycanMetaboliteFlatFileParser(String flatfile) {
		super(flatfile);
		
		this.parseContent();
	}

	public String getEntry() {
		int tabIndex = this.getTabIndex("ENTRY");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getName() {
		int tabIndex = this.getTabIndex("NAME");
		String content = this.tabContent_.get(tabIndex);
		if (content == null || content.isEmpty()) return null;
		
		String[] names = content.split(";");
		StringBuilder sb = new StringBuilder();
		int i;
		for (i = 0; i < (names.length - 1); i++) {
			sb.append(names[i].trim()).append(';');
		}
		
		sb.append(names[i].trim());
		
		return sb.toString();
	}
	
	public String getFormula() {
		int tabIndex = this.getTabIndex("COMPOSITION");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public String getMass() {
		int tabIndex = this.getTabIndex("MASS");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public List<String> getReactions() {
		int tabIndex = this.getTabIndex("REACTION");
		String content = this.tabContent_.get(tabIndex);
		
		List<String> reactionIdSet = new ArrayList<> ();
		if ( content == null || content.isEmpty()) return reactionIdSet;
		
		Pattern reactionIdPattern = Pattern.compile( "(R[0-9]+)");
		Matcher matcher = reactionIdPattern.matcher( content);
		while ( matcher.find()) {
			reactionIdSet.add( matcher.group(1));
		}
		
		return reactionIdSet;
	}
	
	public List<String> getPathways() {
		int tabIndex = this.getTabIndex("PATHWAY");
		String content = this.tabContent_.get(tabIndex);
		List<String> pathwayIdSet = new ArrayList<> ();
		if ( content == null || content.isEmpty()) return pathwayIdSet;
		Pattern reactionIdPattern = Pattern.compile( "(map[0-9]+)");
		Matcher matcher = reactionIdPattern.matcher( content);
		while ( matcher.find()) {
			pathwayIdSet.add( matcher.group(1));
		}
		
		return pathwayIdSet;
	}
	
	public String getMetaboliteClass() {
		int tabIndex = this.getTabIndex("CLASS");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getComment() {
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
		int tabIndex = this.getTabIndex("ENZYME");
		
		String content = this.tabContent_.get(tabIndex);
		
		List<String> enzymeIdSet = new ArrayList<String> ();
		if ( content == null || content.isEmpty()) return enzymeIdSet;
		
		
		String[] enzymeIdArray = content.split("[\\s+]");
		for (int i = 0; i < enzymeIdArray.length; i++) {
			if ( !enzymeIdArray[i].isEmpty()) enzymeIdSet.add(enzymeIdArray[i]);
		}
		
		return enzymeIdSet;
	}
	
	public List<KeggGlycanMetaboliteCrossreferenceEntity> getCrossReferences() {
		List<KeggGlycanMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
		int tabIndex = this.getTabIndex("DBLINKS");
		String content = this.tabContent_.get(tabIndex);
		if (content == null) return crossReferences;
		String[] xrefs = content.split("\n");
		for (int i = 0; i < xrefs.length; i++) {
			String[] xrefPair = xrefs[i].trim().split(": ");
			for (String refValue : xrefPair[1].trim().split(" +")) {
				KeggGlycanMetaboliteCrossreferenceEntity xref = new KeggGlycanMetaboliteCrossreferenceEntity(
						GenericCrossReference.Type.DATABASE, xrefPair[0], refValue);
				crossReferences.add(xref);
			}
		}
		return crossReferences;
	}
}
