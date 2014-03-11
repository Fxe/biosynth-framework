package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

public class Neo4jMetaboliteIntegration {
	
	private Label integrationLabel;
	protected GraphDatabaseService graphdb;
	protected ExecutionEngine engine;
	
	//need to store propositions
	//matrix cluster proposition to verify satisfying ones

	public Neo4jMetaboliteIntegration(String integrationLabel, GraphDatabaseService graphdb) {
		this.integrationLabel = DynamicLabel.label(integrationLabel);
		this.engine = new ExecutionEngine(graphdb);
	}
	
	public void initialize() {
//		this.integrationLabel = DynamicLabel.label(integrationLabel);
//		this.engine = new ExecutionEngine(graphdb);
		Map<String, Object> params = new HashMap<> ();
		params.put("label", integrationLabel.toString());
		Iterator<Node> iterator = engine.execute(
				"MATCH (integration:IntegrationSet {label:{label}}) RETURN integration"
				, params).columnAs("integration");
		
		List<Node> nodes = IteratorUtil.asList(iterator);
		if (nodes.isEmpty()) {
			engine.execute("CREATE (:IntegrationSet {label:{label}})", params);
		} else if (nodes.size() > 1) {
			throw new RuntimeException("ai ai");
		} else {
			Node node = nodes.iterator().next();
			System.out.println(node.getProperty("label"));
		}
	}
	
	public void getCluster(Long id) {
		Map<String, Object> params = new HashMap<> ();
		params.put("id", id);
		engine.execute("MATCH (c:" + integrationLabel + " {id:{id}}) RETURN c", params);
	}
	
	public List<Long> getAllMetaboliteId() {
		List<Long> res = new ArrayList<> ();
		ExecutionResult result = engine.execute("MATCH (c:" + integrationLabel + ") RETURN c");
		Iterator<Node> iterator = result.columnAs("c");
		List<Node> nodes = IteratorUtil.asList(iterator);
		for (Node n : nodes) {
			res.add(n.getId());
		}
		
		return res;
	}
	
	public void resetIntegration() {
		Map<String, Object> params = new HashMap<> ();
		params.put("label", integrationLabel.toString());
		engine.execute("MATCH (c:" + integrationLabel + ") DELETE c");
		engine.execute("MATCH (c:IntegrationSet {label:{label}}) DELETE c", params);
	}
	
	public void generateSingleTonCluster(CompoundNodeLabel compoundDb, String entry) {
		//IF node exists and has no integrate link then create a new cluster and links to it
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", entry);
		engine.execute("MATCH (c:Compound:" + compoundDb + " {entry:{entry}})", params);
	}
	
	public void generateClusters(String predicate) {
//		START i=node(363496) 
//		MATCH path=(cpd:BiGG {entry:"udpglcur"})-[:HasCrossreferenceTo*]-(end) 
//		FOREACH (k IN nodes(path) | MERGE (k)<-[:Integrates]-(i)) RETURN i, path
	}
	
	public void clusterNodeToIntegratedMetabolite(Long id) {
		
	}
}

