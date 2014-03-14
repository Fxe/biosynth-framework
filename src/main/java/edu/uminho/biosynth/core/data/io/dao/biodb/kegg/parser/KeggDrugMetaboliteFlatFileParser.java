package edu.uminho.biosynth.core.data.io.dao.biodb.kegg.parser;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeegDrugMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.io.parser.kegg.AbstractKeggFlatFileParser;

public class KeggDrugMetaboliteFlatFileParser extends AbstractKeggFlatFileParser {

	public KeggDrugMetaboliteFlatFileParser(String flatfile) {
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
		int tabIndex = this.getTabIndex("FORMULA");
		String content = this.tabContent_.get(tabIndex);
		
		return content;
	}
	
	public Double getMass() {
		int tabIndex = this.getTabIndex("EXACT_MASS");
		String value = this.tabContent_.get(tabIndex);
		if (value == null) return null;

		return Double.parseDouble(value);
	}
	
	public Double getMolWeight() {
		int tabIndex = this.getTabIndex("MOL_WEIGHT");
		String value = this.tabContent_.get(tabIndex);
		if (value == null) return null;
		
		return Double.parseDouble(value);
	}
	
	public String getComponent() {
		int tabIndex = this.getTabIndex("COMPONENT");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getSource() {
		int tabIndex = this.getTabIndex("SOURCE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getSequence() {
		int tabIndex = this.getTabIndex("SEQUENCE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getStructureMap() {
		int tabIndex = this.getTabIndex("STR_MAP");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getOtherMap() {
		int tabIndex = this.getTabIndex("OTHER_MAP");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getProduct() {
		int tabIndex = this.getTabIndex("PRODUCT");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getTarget() {
		int tabIndex = this.getTabIndex("TARGET");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getActivity() {
		int tabIndex = this.getTabIndex("ACTIVITY");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getMetabolism() {
		int tabIndex = this.getTabIndex("METABOLISM");
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
	
	public List<KeegDrugMetaboliteCrossreferenceEntity> getCrossReferences() {
		List<KeegDrugMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
		int tabIndex = this.getTabIndex("DBLINKS");
		String content = this.tabContent_.get(tabIndex);
		if (content == null) return crossReferences;
		String[] xrefs = content.split("\n");
		for (int i = 0; i < xrefs.length; i++) {
			String[] xrefPair = xrefs[i].trim().split(": ");
			for (String refValue : xrefPair[1].trim().split(" +")) {
				KeegDrugMetaboliteCrossreferenceEntity xref = new KeegDrugMetaboliteCrossreferenceEntity(
						GenericCrossReference.Type.DATABASE, xrefPair[0], refValue);
				crossReferences.add(xref);
			}
		}
		return crossReferences;
	}
}
