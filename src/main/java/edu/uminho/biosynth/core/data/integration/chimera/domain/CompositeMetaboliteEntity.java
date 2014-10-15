package edu.uminho.biosynth.core.data.integration.chimera.domain;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;

public class CompositeMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = -3944065514458458881L;
	
	private Map<String, Object> fields = new HashMap<> ();
	private Map<String,  Map<String, Object>> properties = new HashMap<> ();

	/**
	 * 
	 * @return <b>Descriptive</b> properties of the composite metabolite
	 */
	public Map<String, Object> getFields() { return fields;}
	public void setFields(Map<String, Object> fields) { this.fields = fields;}
	
	/**
	 * @return <b>Concrete</b> properties of the composite metabolite
	 */
	public Map<String, Map<String, Object>> getProperties() { return properties;}
	public void setProperties(Map<String,  Map<String, Object>> properties) { this.properties = properties;}
	
}
