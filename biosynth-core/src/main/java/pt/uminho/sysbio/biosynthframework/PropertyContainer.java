package pt.uminho.sysbio.biosynthframework;

import java.util.Map;

public interface PropertyContainer {
	public Map<String, Object> getProperties();
	public void setProperties(Map<String, Object> properties);
}
