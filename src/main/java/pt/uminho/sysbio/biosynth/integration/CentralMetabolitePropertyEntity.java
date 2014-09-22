package pt.uminho.sysbio.biosynth.integration;

public class CentralMetabolitePropertyEntity extends AbstractCentralEntity {

	private static final String KEY_PROPERTY = "key";
	
	public CentralMetabolitePropertyEntity(String key, Object value) {
		this.properties.put(key, value);
	}
	
	public Object getUniqueKey() {
		return this.properties.get(KEY_PROPERTY);
	}
	public void setUniqueKey(Object value) {
		this.properties.put(KEY_PROPERTY, value);
	}
	
	public String getRelationshipMajorLabel() {
		return null;
	}
}
