package pt.uminho.sysbio.biosynth.integration;

import java.util.Map;

public class GraphPropertyEntity extends AbstractGraphEntity {

//	private static final String KEY_PROPERTY = "key";
	
	public String uniqueProperty = "key";
	
	public GraphPropertyEntity(String key, Object value) {
		this.properties.put(key, value);
	}
	
	public GraphPropertyEntity(Map<String, Object> propertyMap) {
		this.properties = propertyMap;
	}
	
	public Object getUniqueKey() {
		return this.properties.get(uniqueProperty);
	}
	public void setUniqueKey(Object value) {
		this.properties.put(uniqueProperty, value);
	}
	
	public String getRelationshipMajorLabel() {
		return null;
	}
}
