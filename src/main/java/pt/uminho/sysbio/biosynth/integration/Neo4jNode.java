package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Label;

public class Neo4jNode {
	
	private Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	private Set<String> labels = new HashSet<> ();
	
	
	public Set<String> getLabels() {
		return labels;
	}
	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}
	public void setLabels2(Set<Label> labels) {
		this.labels = new HashSet<> ();
		for (Label label : labels) {
			this.labels.add(label.toString());
		}
	}

	private Map<String, Object> propertyContainer;
	public Map<String, Object> getPropertyContainer() {
		return propertyContainer;
	}
	public void setPropertyContainer(Map<String, Object> propertyContainer) {
		this.propertyContainer = propertyContainer;
	}
	
	private Map<Long, Neo4jRelationship> edges = new HashMap<> ();
	private Map<Long, Neo4jNode> nodes = new HashMap<> ();
	private Map<Long, Long> links = new HashMap<> ();
	
	public Map<Long, Neo4jRelationship> getEdges() {
		return edges;
	}
	public void setEdges(Map<Long, Neo4jRelationship> edges) {
		this.edges = edges;
	}
	public Map<Long, Neo4jNode> getNodes() {
		return nodes;
	}
	public void setNodes(Map<Long, Neo4jNode> nodes) {
		this.nodes = nodes;
	}
	public Map<Long, Long> getLinks() {
		return links;
	}
	public void setLinks(Map<Long, Long> links) {
		this.links = links;
	}
	
	
}
