package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser;



public class KeggGenomeFlatFileParser extends AbstractKeggFlatFileParser {
	
	public KeggGenomeFlatFileParser(String flatfile) {
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
	
	public String getAnnotation() {
		int index = this.getTabIndex("ANNOTATION");
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
		int tabIndex = this.getTabIndex("COMMENT");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}

	public String getlinhage() {
		int tabIndex = this.getTabIndex("LINEAGE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getDataSource() {
		int tabIndex = this.getTabIndex("DATA_SOURCE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getOriginalDB() {
		int tabIndex = this.getTabIndex("ORIGINAL_DB");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getKeywords() {
		int tabIndex = this.getTabIndex("KEYWORDS");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getDisease() {
		int tabIndex = this.getTabIndex("DISEASE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getReference() {
		int tabIndex = this.getTabIndex("REFERENCE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getAuthors() {
		int tabIndex = this.getTabIndex("AUTHORS");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getTitle() {
		int tabIndex = this.getTabIndex("TITLE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getJornal() {
		int tabIndex = this.getTabIndex("JOURNAL");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getChromosome() {
		int tabIndex = this.getTabIndex("CHROMOSOME");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getSequence() {
		int tabIndex = this.getTabIndex("SEQUENCE");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getLength() {
		int tabIndex = this.getTabIndex("LENGTH");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
	
	public String getStatistics() {
		int tabIndex = this.getTabIndex("STATISTICS");
		String content = this.tabContent_.get(tabIndex);
		return content;
	}
}
