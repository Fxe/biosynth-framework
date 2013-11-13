package edu.uminho.biosynth.core.data.io.parser.kegg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.kegg.components.KeggMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.parser.IGenericMetaboliteParser;

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
		if (content == null || content.isEmpty()) return this.getEntry();
		
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
	
	public double getMass() {
		int tabIndex = this.getTabIndex("EXACT_MASS");
		String value = this.tabContent_.get(tabIndex);
		if (value == null) return Double.MIN_VALUE;
		double v = Double.parseDouble(value);
		return v;
	}
	
	public double getMolWeight() {
		int tabIndex = this.getTabIndex("MOL_WEIGHT");
		String value = this.tabContent_.get(tabIndex);
		if (value == null) return Double.MIN_VALUE;
		double v = Double.parseDouble(value);
		return v;
	}
	
	@Override
	public String getComment() {
		int tabIndex = this.getTabIndex("COMMENT");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	@Override
	public String getRemark() {
		int tabIndex = this.getTabIndex("REMARK");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public List<KeggMetaboliteCrossReferenceEntity> getCrossReferences() {
		List<KeggMetaboliteCrossReferenceEntity> crossReferences = new ArrayList<> ();
		int tabIndex = this.getTabIndex("DBLINKS");
		String content = this.tabContent_.get(tabIndex);
		String[] xrefs = content.split("\n");
		for (int i = 0; i < xrefs.length; i++) {
			String[] xrefPair = xrefs[i].trim().split(": ");
			KeggMetaboliteCrossReferenceEntity xref = new KeggMetaboliteCrossReferenceEntity(
					GenericCrossReference.Type.DATABASE, xrefPair[0], xrefPair[1]);
			crossReferences.add(xref);
		}
		return crossReferences;
	}
	
	@Override
	public Set<String> getReactions() {
		int tabIndex = this.getTabIndex("REACTION");
		String content = this.tabContent_.get(tabIndex);
		
		Set<String> reactionIdSet = new HashSet<String> ();
		if ( content == null || content.isEmpty()) return reactionIdSet;
		
		Pattern reactionIdPattern = Pattern.compile( "(R[0-9]+)");
		Matcher matcher = reactionIdPattern.matcher( content);
		while ( matcher.find()) {
			reactionIdSet.add( matcher.group(1));
		}
		
		return reactionIdSet;
	}
	

	
	@Override
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
	
	@Override
	public Set<String> getEnzymes() {
		int tabIndex = this.getTabIndex("ENZYME");
		
		String content = this.tabContent_.get(tabIndex);
		
		Set<String> enzymeIdSet = new HashSet<String> ();
		if ( content == null || content.isEmpty()) return enzymeIdSet;
		
		
		String[] enzymeIdArray = content.split("[\\s+]");
		for (int i = 0; i < enzymeIdArray.length; i++) {
			if ( !enzymeIdArray[i].isEmpty()) enzymeIdSet.add(enzymeIdArray[i]);
		}
		
		return enzymeIdSet;
	}
}
