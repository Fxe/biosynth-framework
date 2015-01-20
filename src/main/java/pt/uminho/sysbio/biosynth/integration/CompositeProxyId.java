package pt.uminho.sysbio.biosynth.integration;

public class CompositeProxyId {
	private Long id;
	private String entry;
	private String source;
	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry; }
	
	public String getSource() { return source;}
	public void setSource(String source) { this.source = source;}
	
	@Override
	public String toString() {
		return String.format("%s[%d:%s]", source, id, entry);
	}
}
