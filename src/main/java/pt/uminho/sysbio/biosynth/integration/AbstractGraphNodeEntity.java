package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class AbstractGraphNodeEntity extends AbstractBiosynthEntity {

	private static final long serialVersionUID = 1L;

	protected String majorLabel;
	
	protected Set<String> labels = new HashSet<> ();
	protected Map<String, Object> properties = new HashMap<> ();
	
	public String uniqueKey;
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> connectedEntities = new ArrayList<> ();
	
	
	public List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> getConnectedEntities() {
		return connectedEntities;
	}
	public void setConnectedEntities(
			List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> connectedEntities) {
		this.connectedEntities = connectedEntities;
	}
	public String getMajorLabel() { return majorLabel;}
	public void setMajorLabel(String majorLabel) { this.majorLabel = majorLabel;}
	
	public Set<String> getLabels() { return labels;}
	public void setLabels(Set<String> labels) { this.labels = labels;}
	public void addLabel(String label) { this.labels.add(label);}
	
	public Map<String, Object> getProperties() { return properties;}
	public void setProperties(Map<String, Object> properties) { this.properties = properties;}
	public void addProperty(String key, Object value) {
		if (value != null) {
			properties.put(key, value);
		}
	}
	public Object getProperty(String key, Object defaultValue) {
		Object value = this.properties.get(key);
		if (value == null) value = defaultValue;
		return value;
	}
	
	@Override
	public String getEntry() { return (String)this.properties.get("entry");}
	@Override
	public void setEntry(String entry) { properties.put("entry", entry);};
	
	@Override
	public String getName() { return (String)this.properties.get("name");}
	public void setName(String name) { this.properties.put("name", name);}
	
	@Override
	public String toString() {
		String str = String.format("%s::%s: %s", majorLabel, labels, this.properties);
		return str;
	}
}
