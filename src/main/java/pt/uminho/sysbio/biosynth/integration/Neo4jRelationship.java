package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.Map;

public class Neo4jRelationship {
	
	private long id;
	public long getId() { return id;}
	public void setId(long id) { this.id = id;}
	
	private String type;
	public String getType() { return type;}
	public void setType(String type) { this.type = type;}
	
	private String direaction;
	public String getDireaction() { return direaction;}
	public void setDireaction(String direaction) { this.direaction = direaction;}
	public void setDireaction(Object direaction) { this.direaction = direaction.toString();}

	private Map<String, Object> properties = new HashMap<> ();
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	
}
