package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

/**
 * 
 * @author Filipe
 *
 */
public class AbstractGraphNodeEntity extends AbstractBiosynthEntity {

  private final String ENTRY = Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT;
  
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
	
	public Map<String, Integer> connectionTypeCounter = new HashMap<> ();
	public Map<String, Integer> getConnectionTypeCounter() {
		return connectionTypeCounter;
	}
	public void setConnectionTypeCounter(Map<String, Integer> connectionTypeCounter) {
		this.connectionTypeCounter = connectionTypeCounter;
	}
	public void addConnectionTypeCounter(String type) {
		if (!this.connectionTypeCounter.containsKey(type)) {
			this.connectionTypeCounter.put(type, 0);
		}
		this.connectionTypeCounter.put(type, this.connectionTypeCounter.get(type) + 1);
	}
	public Integer getConnectionTypeCounter(String type) {
		if (!this.connectionTypeCounter.containsKey(type)) {
			return 0;
		}
		return this.connectionTypeCounter.get(type);
	}

	public Map<String, List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>>> connectedEntities = new HashMap<> ();
	public Map<String, List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>>> getConnectedEntities() { return connectedEntities;}
	public void setConnectedEntities(
			List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> connectedEntities) {
		for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : connectedEntities) {
			this.addConnectedEntity(p);
		}
	}
	public void addConnectedEntity(Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> pair) {
		String relationshipType = pair.getLeft().getLabels().iterator().next();
		if (!this.connectedEntities.containsKey(relationshipType)) {
			this.connectedEntities.put(relationshipType, new ArrayList<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> ());
		}
		this.connectedEntities.get(relationshipType).add(pair);
		
		this.addConnectionTypeCounter(relationshipType);
	}
	
	public String getMajorLabel() { return majorLabel;}
	public void setMajorLabel(String majorLabel) { this.majorLabel = majorLabel;}
	
	public Set<String> getLabels() { return labels;}
	public void setLabels(Set<String> labels) { this.labels = labels;}
	public void addLabel(String label) { this.labels.add(label);}
	
	public Map<String, Object> getProperties() { return properties;}
	public void setProperties(Map<String, Object> properties) { 
		this.properties = properties;
		if (properties.containsKey(Neo4jDefinitions.MAJOR_LABEL_PROPERTY)) {
			this.majorLabel = (String) getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, "null_label");
		}
	}
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
	public String getEntry() { return (String) this.properties.get(ENTRY);}
	@Override
	public void setEntry(String entry) { properties.put(ENTRY, entry);};
	
	@Override
	public String getName() { return (String)this.properties.get("name");}
	public void setName(String name) { this.properties.put("name", name);}
	
	@Override
	public String toString() {
		String str = String.format("%d[%s]%s::%s", this.id, majorLabel, getEntry(), labels);
		return str;
	}
}
