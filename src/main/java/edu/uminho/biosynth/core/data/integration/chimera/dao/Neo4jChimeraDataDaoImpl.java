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

import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CompositeMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataReactionProperty;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import scala.collection.convert.Wrappers.SeqWrapper;

public class Neo4jChimeraDataDaoImpl implements IntegrationDataDao {
	
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
		if (graphDatabaseService != null) {
			this.graphDatabaseService = graphDatabaseService;
			this.executionEngine = new ExecutionEngine(graphDatabaseService);
		}
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
	
	public Map<String, List<Object>> getCompositeNode2(Long id) {
		Map<String, List<Object>> data = new HashMap<> ();
		
		Node root = graphDatabaseService.getNodeById(id);
		if (!root.hasLabel(CompoundNodeLabel.Compound)) {
			throw new RuntimeException("Invalid Node " + id + " " + root.getLabels());
		}
		
		for (Relationship relationship : root.getRelationships()) {
			
			Node other = relationship.getOtherNode(root);
			Set<String> labels = new HashSet<> ();
			for (Label label: IteratorUtil.asSet(other.getLabels())) labels.add(label.toString());
			if (other.hasLabel(CompoundNodeLabel.Compound)) {
				labels.remove("Compound");
				IntegratedMetaboliteCrossreferenceEntity xref = new IntegratedMetaboliteCrossreferenceEntity();
//				GenericCrossReference xref = new GenericCrossReference();
				xref.setType(GenericCrossReference.Type.DATABASE);
				xref.setRef(labels.iterator().next());
				xref.setValue((String)other.getProperty("entry"));
				createAndAdd(data, "crossreferences", xref);
			} else {
				String property = other.getPropertyKeys().iterator().next();
				Object value = other.getProperty(property);
				createAndAdd(data, property, value);
			}
		}
		System.out.println("DATA: " + data);
		return data;
	}
	
	@Override
	public Map<String, List<Object>> getCompositeNode(Long id) {
//		System.out.println("Loading composite node -> " + id);
		//Build Self Node
		Map<String, Object> root = this.getEntryProperties(id);
		String sourceEntry = (String) root.get("entry");
		
//		Node tempNode = graphDatabaseService.getNodeById(id);
		
//		System.out.println("NODE -> " + tempNode);
//		System.out.println("     -> " + tempNode.getProperty("entry") + tempNode.getLabels());
//		System.out.println("     -> " + IteratorUtil.asList(tempNode.getRelationships()));
		
		Map<String, List<Object>> data = new HashMap<> ();
//		Map<String, List<Object>> data = this.getCompositeNode2(id);
		
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
//		System.out.println(query);
		ExecutionResult res = this.executionEngine.execute(query);
		
		List<Object> list = IteratorUtil.asList(res.columnAs(res.columns().iterator().next()));
//		System.out.println("Found " + list.size());
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
				.getAllNodesWithLabel(GlobalLabel.Metabolite)) {
			
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
			
			if (r.getType().equals(MetaboliteRelationshipType.has_crossreference_to)) {
				
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
//		int count = 0;
//		Label l = DynamicLabel.label(label);
//		for (Node n : GlobalGraphOperations.at(graphDatabaseService).getAllNodesWithLabel(l)) {
//			if (n.hasProperty("proxy") && !(boolean) n.getProperty("proxy")) {
//				count++;
//			}
//		}
		
		return this.getEntitiesByLabel(label).size();
	}
	
	@Override
	public Set<Long> getEntitiesByLabel(String label) {
		Set<Long> set = new HashSet<> ();
		
		String cypherQuery = String.format("MATCH (cpd:%s {proxy:false}) RETURN ID(cpd) AS id", label);
		
		ExecutionResult executionResult = executionEngine.execute(cypherQuery);
		while (executionResult.columnAs("id").hasNext()) {
			set.add((Long)executionResult.columnAs("id").next());
		}

//		set = (Set<Long>) IteratorUtil.asSet(executionResult.columnAs("id"));
//		for (Object id : ) {
//			set.add((Long) id);
//		}
		
//		Label l = DynamicLabel.label(label);
		
//		for (Node n : GlobalGraphOperations.at(graphDatabaseService).getAllNodesWithLabel(l)) {
//			if (n.hasProperty("proxy") && !(boolean) n.getProperty("proxy")) {
//				set.add(n.getId());
//			}
//		}
		
		return set;
	}

	@Override
	public Set<String> getAllMajorMetaboliteLabels() {
		Set<String> majorLabels = new HashSet<> ();
		for (MetaboliteMajorLabel majorLabel : MetaboliteMajorLabel.values()) {
			majorLabels.add(majorLabel.toString());
		}
		
		return majorLabels;
	}

	@Override
	public List<GraphPropertyEntity> collectAllPropertyFromIds(
			String major, String uniqueKey, Long... ids) {
		List<GraphPropertyEntity> propertyEntities = new ArrayList<> ();
		
		Label label = DynamicLabel.label(major);
		
		for (Long nodeId : ids) {
			Node node = this.graphDatabaseService.getNodeById(nodeId);
			for (Relationship relationship : node.getRelationships()) {
				Node nodeProp = relationship.getOtherNode(node);
				if (nodeProp.hasLabel(label)) {
					GraphPropertyEntity propertyEntity = 
							new GraphPropertyEntity(uniqueKey, nodeProp.getProperty(uniqueKey));
					
					propertyEntity.setId(nodeProp.getId());
					propertyEntity.setUniqueKey(uniqueKey);
					propertyEntity.setMajorLabel(major);
					
					for (Label l : nodeProp.getLabels()) propertyEntity.getLabels().add(l.toString());
					for (String key : nodeProp.getPropertyKeys()) propertyEntity.getProperties().put(key, nodeProp.getProperty(key));
					
					propertyEntities.add(propertyEntity);
				}
			}
		}
		return propertyEntities;
	}

	@Override
	public Set<Long> collectEntityProperties(List<Long> entities,
			String... properties) {
		
		Set<Long> propertyIdList = new HashSet<> ();
		
		for (Long nodeId : entities) {
			Node node = this.graphDatabaseService.getNodeById(nodeId);
			for (Relationship relationship : node.getRelationships()) {
				Node propNode = relationship.getOtherNode(node);
				for (String propLabel : properties) {
					if (propNode.hasLabel(DynamicLabel.label(propLabel))) propertyIdList.add(propNode.getId());
				}
			}
		}
		
		return propertyIdList;
	}

	@Override
	public CentralDataReactionProperty getReactionProperty(Long id) {
		throw new RuntimeException("AAAAHH !");
	}

	@Override
	public GraphPropertyEntity getMetaboliteProperty(Long id) {
		Node node = this.graphDatabaseService.getNodeById(id);
		GraphPropertyEntity entity = null;
				
		
		for (String key : node.getPropertyKeys()) {
			entity = new GraphPropertyEntity(key, node.getProperty(key));
			entity.getProperties().put(key, node.getProperty(key));
		}
		for (Label label : node.getLabels()) entity.addLabel(label.toString());
		return entity;
	}

	@Override
	public Set<String> collectEntityLabels(Long id) {
		Node node = this.graphDatabaseService.getNodeById(id);
		Set<String> res = new HashSet<> ();
		for (Label l : node.getLabels()) {
			res.add(l.toString());
		}
		return res;
	}

}
