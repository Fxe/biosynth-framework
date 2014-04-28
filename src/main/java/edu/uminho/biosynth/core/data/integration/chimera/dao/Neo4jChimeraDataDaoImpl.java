package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.impl.core.NodeProxy;
import org.neo4j.tooling.GlobalGraphOperations;
import org.springframework.beans.factory.annotation.Autowired;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CompositeMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundRelationshipType;
import edu.uminho.biosynth.core.data.integration.neo4j.MetaboliteMajorLabel;
import scala.collection.convert.Wrappers.SeqWrapper;

public class Neo4jChimeraDataDaoImpl implements ChimeraDataDao {
	
	private static Logger LOGGER = Logger.getLogger(Neo4jChimeraDataDaoImpl.class);
	
	@Autowired
	private GraphDatabaseService graphDatabaseService;
	
	private ExecutionEngine executionEngine;
	
	public Neo4jChimeraDataDaoImpl() { }
	
	@PostConstruct
	public void initialize() {
		if (executionEngine == null) {
			this.setGraphDatabaseService(graphDatabaseService);
		}
		LOGGER.info(String.format("%s initialized !", this));
	}

	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}

	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	public Node getMetaboliteNodeByEntry(String entry, CompoundNodeLabel type) {
		Node node = null;
		ResourceIterable<Node> res = this.graphDatabaseService.findNodesByLabelAndProperty(type, "entry", entry);
		Iterator<Node> i = res.iterator();
		while (i.hasNext()) {
			//entry should have unique constraint therefore no more 
			//than one node should be found per compoud label
			if (node != null) {
				System.err.println("error duplicate entry missing unique constraint");
			}
			node = i.next();
		}
		
		return node;
	}

	@Override
	public List<Long> getClusterByQuery(String query) {
		List<Long> clusterElements = new ArrayList<> ();
		ExecutionResult res = this.executionEngine.execute(query);
		List<Object> nodeList = IteratorUtil.asList(res.columnAs(res.columns().iterator().next()));
		for (Object obj: nodeList) {
			if (obj instanceof SeqWrapper) {
				for (Object node: (SeqWrapper<?>) obj) {
					Long id;
					if (node instanceof Long) {
						id = (Long) node;
					} else {
						NodeProxy proxy = (NodeProxy) node;
						System.out.println(proxy.getLabels());
						id = proxy.getId();
					}
					clusterElements.add(id);
				}
			} else {
				Node node = (Node) obj;
				clusterElements.add(node.getId());
			}
		}
		return clusterElements;
	}

	private void createAndAdd(Map<String, List<Object>> map, String key, Object value) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<>());
		}
		map.get(key).add(value);
	}
	
	@Override
	public Map<String, List<Object>> getCompositeNode(Long id) {
//		System.out.println("Loading composite node -> " + id);
		//Build Self Node
		Map<String, Object> root = this.getEntryProperties(id);
		String sourceEntry = (String) root.get("entry");
		
		
		Map<String, List<Object>> data = new HashMap<> ();
//		data.put("Model", new ArrayList<> ());
		//START cpd=node(0) MATCH composite=(cpd)-[*1..1]->(c) RETURN nodes(composite);
		//START cpd=node(0) MATCH path=(cpd)-[*1..1]->(c) RETURN collect(c)
//		ExecutionResult res = this.executionEngine.execute(String.format(
//				"START cpd=node(%d) MATCH path=(cpd)-[*1..1]->(c)-[:Isomorphic]->(i) RETURN collect(c);", id));
//		ExecutionResult res = this.engine.execute(String.format(
//				"START cpd=node(%d) MATCH composite=(cpd)-[*1..1]->(c) RETURN distinct nodes(composite);", id));
		
		String query = 
		String.format("START cpd=node(%d) MATCH path1=(cpd)-[*1..1]->(c) RETURN c AS ret "
				+ "UNION START cpd=node(%d) MATCH path2=(cpd)-[*1..1]->(c2)-[:Isomorphic]->(i) RETURN i AS ret", id, id);
		
		ExecutionResult res = this.executionEngine.execute(query);
		
		List<Object> list = IteratorUtil.asList(res.columnAs(res.columns().iterator().next()));
		for (Object obj: list) {
			NodeProxy proxy = null;
			if (obj instanceof SeqWrapper) {
				for (Object node: (SeqWrapper<?>) obj) {
//					System.out.println(node);
					proxy = (NodeProxy) node;
				}
			} else if (obj instanceof NodeProxy) {
				proxy = (NodeProxy) obj;
			} else {
				throw new RuntimeException("Error query result: id=" + id + " " + obj.getClass().getSimpleName());
			}
			
			if (proxy != null) {
//				System.out.println(proxy.getId() + " -> " +  proxy.getLabels());
				if (id.equals(proxy.getId())) {
					for (Label label : proxy.getLabels()) {
						sourceEntry = sourceEntry.concat(label.name()).concat(":");
					}
					sourceEntry = sourceEntry.concat((String) proxy.getProperty("entry"));
//					System.out.println("SELF !");
				} else {
//					System.out.println(proxy.getLabels());
//					System.out.println(proxy.getPropertyKeys());
					Set<String> labels = new HashSet<> ();
					for (Label label: IteratorUtil.asSet(proxy.getLabels()))
						labels.add(label.toString());
					if (labels.contains("Compound")) {
						labels.remove("Compound");
//						System.out.println("Adding Crossreference");
						//These Compound Labels -> Crossreferences !
						IntegratedMetaboliteCrossreferenceEntity xref = new IntegratedMetaboliteCrossreferenceEntity();
//						GenericCrossReference xref = new GenericCrossReference();
						xref.setType(GenericCrossReference.Type.DATABASE);
						xref.setRef(labels.iterator().next());
						
						xref.setValue((String)proxy.getProperty("entry"));
						createAndAdd(data, "crossreferences", xref);
					} else if (labels.contains("IsotopeFormula")) {
						String property = "isoFormula";
						Object value = proxy.getProperty("formula");
						createAndAdd(data, property, value);
					} else {
//						System.out.println("Adding Property");
						String property = proxy.getPropertyKeys().iterator().next();
						Object value = proxy.getProperty(property);
						createAndAdd(data, property, value);
						//Other Labels -> Properties
					}
				}
//				clusterElements.add(proxy.getId());
			}
			
			if (data.containsKey("crossreferences")) {
				for (Object xref: data.get("crossreferences")) {
					IntegratedMetaboliteCrossreferenceEntity xref_ = (IntegratedMetaboliteCrossreferenceEntity)xref;
					xref_.setSource(sourceEntry);
				}
			}
		}
		
		return data;
	}

	@Override
	public Map<String, Object> getEntryProperties(Long id) {
		Map<String, Object> propsMap = new HashMap<> ();
		ExecutionResult res = this.executionEngine.execute(
				String.format("START cpd=node(%d) RETURN cpd;", id));
		
		List<?> list = IteratorUtil.asList(res.columnAs("cpd"));
		Node node = Node.class.cast(list.iterator().next());
//		System.out.println(node);
		String labels = StringUtils.join(node.getLabels(), ":");
		
//		String isoFormula = null;
//		for (Relationship r : node.getRelationships(CompoundRelationshipType.HasFormula)) {
//			Node formulaNode = r.getOtherNode(node);
//			for (Relationship r_ : formulaNode.getRelationships(CompoundRelationshipType.Isomorphic)) {
//				Node isoFormulaNode = r_.getOtherNode(formulaNode);
//				isoFormula = (String) isoFormulaNode.getProperty("formula");
//			}
//		}
//		
//		if (isoFormula != null) {
//			propsMap.put("isoFormula", isoFormula);
//		}
		for (String prop : node.getPropertyKeys()) {
			propsMap.put(prop, node.getProperty(prop));
		}
		
//		for (Object obj : list) {
//			Node node = (Node)obj;
//			for (String prop : node.getPropertyKeys()) {
//				propsMap.put(prop, node.getProperty(prop));
//			}
//			labels = StringUtils.join(node.getLabels(), ":");
////			for (Label label : node.getLabels()) {
////				labels = labels.concat(label.name()).concat(":");
////			}
//		}
		
//		labels = labels.substring(0, labels.length() - 1);
		
		propsMap.put("labels", labels);
		propsMap.put("isProxy", false);
		if (propsMap.containsKey("entry") && propsMap.keySet().size() <= 3) {
			propsMap.put("isProxy", true);
		}
		
//		System.out.println("PROPS " + propsMap);
		
		return propsMap;
	}
	
	public List<Long> getAllPropertiesId(String propertyType) {
		List<Long> result = new ArrayList<> ();
		
		for (Node node : GlobalGraphOperations.at(graphDatabaseService)
				.getAllNodesWithLabel(DynamicLabel.label(propertyType))) {
			result.add(node.getId());
		}
		
		return result;
	}

	@Override
	public List<Long> getAllMetaboliteIds() {
		List<Long> idList = new ArrayList<> ();
		for (Node n : GlobalGraphOperations.at(graphDatabaseService)
				.getAllNodesWithLabel(CompoundNodeLabel.Compound)) {
			
			if (!((Boolean) n.getProperty("proxy"))) idList.add(n.getId());
		}
		return idList;
	}

	@Override
	public List<String> getAllProperties() {
		List<String> res = new ArrayList<> ();
		for (Label label : GlobalGraphOperations.at(graphDatabaseService)
				.getAllLabels()) {
			res.add(label.toString());
		}
		return res;
	}

	@Override
	public List<Long> listAllPropertyIds(String property) {
		List<Long> res = new ArrayList<> ();
		// TODO Auto-generated method stub
		return res;
	}

	@Override
	public Node getCompositeNode(String entry, Label... labels) {
		String labelString = StringUtils.join(labels, ":");
		String query = String.format("MATCH cpd:%s {entry:{entry}} RETURN cpd", labelString);
		Map<String, Object> params = new HashMap<> ();
		params.put("entry", entry);
		ExecutionResult res = this.executionEngine.execute(query, params );

		List<Object> nodes = IteratorUtil.asList(res.columnAs("cpd"));
		
		if (nodes.isEmpty()) return null;
		if (nodes.size() > 1) {
			LOGGER.warn(String.format("Multiple Records for [%s, %s]", entry, labels));
		}
		
		return Node.class.cast(nodes.iterator().next());
	}

	@Override
	public CompositeMetaboliteEntity getCompositeMetabolite(Long id) {
		Node node = this.graphDatabaseService.getNodeById(id);
		if (!node.hasLabel(CompoundNodeLabel.Compound)) {
			LOGGER.warn(String.format("[%d] not a metabolite record", id));
			return null;
		}
		if ((Boolean)node.getProperty("proxy")) return null;
		CompositeMetaboliteEntity cpd = new CompositeMetaboliteEntity();
		String entry = (String) node.getProperty("entry");
		cpd.setEntry(entry);
		
		for (String p:node.getPropertyKeys()) cpd.getFields().put(p, node.getProperty(p));
		for (Relationship r:node.getRelationships()) {
			Node n = r.getEndNode();
			
			if (r.getType().equals(CompoundRelationshipType.HasCrossreferenceTo)) {
				
			} else {
				Label label = n.getLabels().iterator().next();
				Map<String, Object> data = new HashMap<> ();
				for (String p:n.getPropertyKeys()) data.put(p, n.getProperty(p));
				cpd.getProperties().put(label.toString(), data);
			}
		}
		
		return cpd;
	}

	@Override
	public int countByLabel(String label) {
		int count = 0;
		Label l = DynamicLabel.label(label);
		for (Node n : GlobalGraphOperations.at(graphDatabaseService).getAllNodesWithLabel(l)) {
			if (n.hasProperty("proxy") && !n.hasProperty("proxy")) {
				count++;
			}
		}
		return count;
	}

	@Override
	public Set<String> getAllMajorMetaboliteLabels() {
		Set<String> majorLabels = new HashSet<> ();
		for (MetaboliteMajorLabel majorLabel : MetaboliteMajorLabel.values()) {
			majorLabels.add(majorLabel.toString());
		}
		
		return majorLabels;
	}

}
