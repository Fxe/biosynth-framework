package pt.uminho.sysbio.biosynth.integration.model;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.collection.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jOptfluxContainerDaoImpl;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.ExtendedMetabolicModelEntity;

public class GPRIntegration {

	private static final Logger LOGGER = LoggerFactory.getLogger(GPRIntegration.class);
	
	public GPRIntegration(){
		
	}
	//GeneRule Name GRPStr NormalizedGPRTree
	//String modelId Map<String, GeneRule> geneRules 
	public void scaffoldGpr(String modelName, Map<String, GeneRule> geneRules, GraphDatabaseService graphDatabaseService){
//		Container container = new Container(new JSBMLReader(
//				sbmlPath, "", false));
//		Neo4jOptfluxContainerDaoImpl modelDao = 
//				new Neo4jOptfluxContainerDaoImpl(graphDatabaseService);
//		ExtendedMetabolicModelEntity model = 
//				modelDao.getMetabolicModelByEntry(container.getModelName());
		Node modelNode = (Node) graphDatabaseService.findNode(GlobalLabel.MetabolicModel, "entry", modelName);
		Set<Node> rxNodes = Neo4jUtils.collectNodeRelationshipNodes(modelNode, MetabolicModelRelationshipType.has_reaction);
		for (Node node : rxNodes){
			String entry = (String) node.getProperty("entry");
			String[] entryParts = entry.split("@");
			GeneRule geneRule = geneRules.get(entryParts[0]);
			if (!(geneRule.getNormalizedTree() == null)){
				GraphGPREntity gprEntity = new GraphGPREntity();
				//gprEntity.setEntry("");
				gprEntity.addProperty("rule", geneRule.getRule());
				gprEntity.setReaction(geneRule.getName());
				gprEntity.setNormalizedTree(geneRule.getNormalizedTree());
				gprEntity.setMajorLabel(GPRMajorLabels.Root.toString());
				gprEntity.addLabel(MetabolicModelLabel.ModelGPR.toString());
				Neo4jGraphNormalizedGPR neo4jDao = new Neo4jGraphNormalizedGPR(graphDatabaseService, modelName);
				Long id = neo4jDao.saveGPR("", gprEntity).getId();
				Node gprNode = graphDatabaseService.getNodeById(id);
				node.createRelationshipTo(gprNode, DynamicRelationshipType.withName(GPRRelationshipType.has_gpr.toString()));
				Set<Node> leafNodes = getLeafs(gprNode, graphDatabaseService);
				setNodeRelationships(modelNode, leafNodes, DynamicRelationshipType.withName(GPRRelationshipType.has_gpr_gene.toString()));
			}
		}
	}
	
	public void setNodeRelationships(Node node, Set<Node> nodes, RelationshipType type){
		for (Node n : nodes){
	
			boolean relationExists = false;
			Iterable<Relationship> relations = node.getRelationships(Direction.OUTGOING, type);
			for (Relationship relation : relations){
				Node endNode = relation.getEndNode();
				if (endNode.getId() == n.getId()){
					relationExists = true; 
	//				for (String key : relationshipEntity.getProperties().keySet()) {
	//					relation.setProperty(key, relationshipEntity.getProperties().get(key));
	//				}
				}
			}
			if (!relationExists){
				Relationship relationship = node.createRelationshipTo(n, type);
	//			for (String key : relationshipEntity.getProperties().keySet()) {
	//				relationship.setProperty(key, relationshipEntity.getProperties().get(key));
	//			}
			}	
		}
	}
	
	public Set<Node> getLeafs(Node rootNode, GraphDatabaseService graphDatabaseService){
		Set<Node> leafNodes = new HashSet<Node>();
		TraversalDescription td = graphDatabaseService.traversalDescription()
				.uniqueness(Uniqueness.NODE_GLOBAL)
				.breadthFirst()
				.relationships(GPRRelationshipType.has_logical_operator, Direction.OUTGOING)
				.relationships(GPRRelationshipType.has_leaf, Direction.OUTGOING);
		
		Traverser results = td.traverse(rootNode);
		ResourceIterator<Node> nodes = results.nodes().iterator();
		while (nodes.hasNext()){
			Node node = nodes.next();
			if (node.hasLabel(DynamicLabel.label(GPRMajorLabels.Leaf.toString()))){
				leafNodes.add(node);
			}
		}
		return leafNodes;
	}
	
	
	public Map<String, Integer> clearGpr(String modelEntry, GraphDatabaseService graphDatabaseService){
		
		Map<String, Integer> deletionInfo = new HashMap<String, Integer>();
		Map<String, Integer> temp = new HashMap<String, Integer>();

		ResourceIterator<Node> modelNodes = null;
		
		
		if (modelEntry.equals("all")){
			modelNodes = graphDatabaseService
			.findNodes(DynamicLabel.label(GlobalLabel.MetabolicModel.toString()));
		} else {
			modelNodes = graphDatabaseService.findNodes(
					DynamicLabel.label(GlobalLabel.MetabolicModel.toString()), 
					"entry", modelEntry);	
		}
		
		for (Node modelNode : Iterators.asIterable(modelNodes)){
			temp = clearModelGpr(modelNode, graphDatabaseService);
			addToMap(temp, deletionInfo);
		}
		
		return deletionInfo;
	}
	
	public Map<String, Integer> clearModelGpr(Node modelNode, GraphDatabaseService graphDatabaseService){
		Set<Node> nodesToRemove = new HashSet<Node>();
		Map<String, Integer> deletionInfo = new HashMap<String, Integer>();
		Set<Relationship> relationsToRemove = new HashSet<Relationship>();
		Map<String, Integer> temp = new HashMap<String, Integer>();

		
		
		TraversalDescription td = graphDatabaseService.traversalDescription()
							.uniqueness(Uniqueness.NODE_GLOBAL)
							.breadthFirst()
							.relationships(GPRRelationshipType.has_gpr_gene, Direction.OUTGOING)
							.relationships(MetabolicModelRelationshipType.has_reaction, Direction.OUTGOING)
							.relationships(GPRRelationshipType.has_gpr, Direction.OUTGOING)
							.relationships(GPRRelationshipType.has_logical_operator, Direction.OUTGOING)
							.relationships(GPRRelationshipType.has_leaf, Direction.OUTGOING);
					
		Traverser results = td.traverse(modelNode);
		ResourceIterator<Node> nodes = results.nodes().iterator();
		boolean notToRemove = false;
		while (nodes.hasNext()){
			Node node = nodes.next();
			if (node.hasLabel(DynamicLabel.label(GlobalLabel.MetabolicModel.toString())) || 
				node.hasLabel(DynamicLabel.label(MetabolicModelLabel.ModelReaction.toString()))){
				//LOGGER.debug("NODE NOT TO REMOVE: " + node.getLabels());
				notToRemove = true;
			}
			if (!notToRemove){
				nodesToRemove.add(node);
				//LOGGER.debug("NODE TO REMOVE: " + node.getProperty("major-label").toString());
				java.util.Iterator<Relationship> relations = node.getRelationships().iterator();
				while (relations.hasNext()){
					Relationship relation = relations.next();
					relationsToRemove.add(relation);
					//LOGGER.debug("RELATION TO REMOVE: " + relation.getType().toString());
				}
			}
			notToRemove = false;
		}
		LOGGER.debug("NODES SIZE: " + nodesToRemove.size());
		LOGGER.debug("RELATIONS SIZE: " + relationsToRemove.size());
		temp = deleteGprRelationships(relationsToRemove);
		deletionInfo = addToMap(temp, deletionInfo);
	
		temp = deleteGprNodes(nodesToRemove);
		deletionInfo = addToMap(temp, deletionInfo);
		
		return deletionInfo;
	}
	
	
	public Map<String, Integer> deleteGprNodes(Set<Node> nodes){
		Map<String, Integer> deletionInfo = new HashMap<String, Integer>();
		
		for (Node node : nodes){
			java.util.Iterator<Relationship> relsAttached = node.getRelationships().iterator();
            while(relsAttached.hasNext())
            {
                Relationship relAttach = relsAttached.next();                   
                relAttach.delete();
            }
			
			String label = node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY).toString();
			int count = deletionInfo.containsKey(label) ? deletionInfo.get(label) : 0;
			deletionInfo.put(label, count + 1);
			node.delete();
		}
		return deletionInfo;
	}
	
	public Map<String, Integer> deleteGprRelationships(Set<Relationship> relations){
		Map<String, Integer> deletionInfo = new HashMap<String, Integer>();
		
		for (Relationship relation : relations){
			String type = relation.getType().toString();
			int count = deletionInfo.containsKey(type) ? deletionInfo.get(type) : 0;
			deletionInfo.put(type, count + 1);
			relation.delete();
		}
		return deletionInfo;
	}
	
	public Map<String, Integer> addToMap(Map<String, Integer> temp, Map<String, Integer> map){
		for (String key : temp.keySet()){
			map.put(key, temp.get(key));
		}
		return map;
	}
	
	
}
