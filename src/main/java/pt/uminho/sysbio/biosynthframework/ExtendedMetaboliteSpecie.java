package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class ExtendedMetaboliteSpecie extends DefaultMetaboliteSpecie implements PropertyContainer {

	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> properties = new HashMap<> ();
	
	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
		if (properties.containsKey("entry")) {
			this.entry = (String) properties.get("entry");
		}
		if (properties.containsKey("name")) {
			this.name = (String) properties.get("name");
		}
		if (properties.containsKey("formula")) {
			this.formula = (String) properties.get("formula");
		}
		if (properties.containsKey("source")) {
			this.source = (String) properties.get("source");
		}
		if (properties.containsKey("description")) {
			this.description = (String) properties.get("description");
		}
	}
	
	
}
