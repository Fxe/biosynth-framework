package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.HashMap;
import java.util.Map;

public class XmlObject {
	private Map<String, String> attributes = new HashMap<> ();

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
}
