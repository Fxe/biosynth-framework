package pt.uminho.sysbio.biosynth.integration.model;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;

public class GraphGPRGeneEntity extends AbstractGraphNodeEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private String model;
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
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.properties.put("name", name);
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.properties.put("model", model);
	}
	
	
}
