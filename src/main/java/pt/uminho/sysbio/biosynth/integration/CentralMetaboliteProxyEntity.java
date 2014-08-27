package pt.uminho.sysbio.biosynth.integration;

public class CentralMetaboliteProxyEntity extends AbstractCentralEntity {
	
	/**
	 * Get the domain tag of the proxy.
	 * 
	 * @return tag
	 */
	public String getTag() {
		return this.majorLabel;
	}
	
	/**
	 * Get the entry of the proxy
	 * 
	 * @return entry of the crossreference link
	 */
	public String getEntry() {
		return (String) this.properties.get("entry");
	}
	
	public void setEntry(String entry) {
		this.properties.put("entry", entry);
	}
}
