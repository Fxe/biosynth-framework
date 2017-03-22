package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.parser.IGenericMetaboliteParser;

public class KeggCompoundFlatFileParser extends AbstractKeggFlatFileParser implements IGenericMetaboliteParser {

	public KeggCompoundFlatFileParser(String flatfile) {
		super(flatfile);
		this.parseContent();
	}
	
	@Override
	public String getEntry() {
		int tabIndex = this.getTabIndex("ENTRY");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}

	@Override
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
	
	@Override
	public String getFormula() {
		String entry = this.getEntry();
		
		String content = "";
		
		if ( entry.charAt(0) == 'C') {
			int tabIndex = this.getTabIndex("FORMULA");
			content = this.tabContent_.get(tabIndex);
		} else if ( entry.charAt(0) == 'G') {
			int tabIndex = this.getTabIndex("COMPOSITION");
			content = this.tabContent_.get(tabIndex);
		} else {
			System.err.println("ERROR ENTRY TYPE: " + entry);
		}
		return content;
	}
	
	public Double getMass() {
		int tabIndex = this.getTabIndex("EXACT_MASS");
		String value = this.tabContent_.get(tabIndex);
		if (value == null) return null;
		double v = Double.parseDouble(value);
		return v;
	}
	
	public Double getMolWeight() {
		int tabIndex = this.getTabIndex("MOL_WEIGHT");
		String value = this.tabContent_.get(tabIndex);
		if (value == null) return null;
		double v = Double.parseDouble(value);
		return v;
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
	
	public List<KeggCompoundMetaboliteCrossreferenceEntity> getCrossReferences() {
		List<KeggCompoundMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
		int tabIndex = this.getTabIndex("DBLINKS");
		String content = this.tabContent_.get(tabIndex);
		if (content == null) return crossReferences;
		String[] xrefs = content.split("\n");
		for (int i = 0; i < xrefs.length; i++) {
			String[] xrefPair = xrefs[i].trim().split(": ");
			for (String refValue : xrefPair[1].trim().split(" +")) {
				KeggCompoundMetaboliteCrossreferenceEntity xref = new KeggCompoundMetaboliteCrossreferenceEntity(
						ReferenceType.DATABASE, xrefPair[0], refValue);
				crossReferences.add(xref);
			}
		}
		return crossReferences;
	}
	
	@Override
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

	
	
	public Set<String> getSimilarity() {
		Set<String> similarMetaboliteIdSet = new HashSet<String> ();
		
		Pattern metaboliteIdPattern = Pattern.compile( "([CDG][0-9]+)");
		String content = this.getRemark();
		
		if ( content == null || content.isEmpty()) return similarMetaboliteIdSet;
		
		Matcher matcher = metaboliteIdPattern.matcher( this.getRemark());
		while ( matcher.find()) {
			similarMetaboliteIdSet.add( matcher.group(1));
		}
		
		return similarMetaboliteIdSet;
	}
	
	public String getMetaboliteClass() {
		String cpdId = this.getEntry();
		if ( cpdId.charAt(0) == 'G') {
			return "Glycan";
		} else if ( cpdId.charAt(0) == 'C'){
			return "Compound";
		}
		
		return "Error";
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
	
	
}
