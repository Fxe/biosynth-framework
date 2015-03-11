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
	}
	
	
}
