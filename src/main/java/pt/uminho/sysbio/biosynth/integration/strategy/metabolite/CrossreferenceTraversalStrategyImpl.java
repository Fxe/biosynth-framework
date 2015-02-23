package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public class CrossreferenceTraversalStrategyImpl implements ClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(CrossreferenceTraversalStrategyImpl.class);
	
	private GraphDatabaseService db;
	
	private Node initialNode;
	
	private Set<Label> excludeMajorLabel = new HashSet<> ();
	
	
	public Set<Label> getExcludeMajorLabel() {
		return excludeMajorLabel;
	}

	public void setExcludeMajorLabel(Set<Label> excludeMajorLabel) {
		this.excludeMajorLabel = excludeMajorLabel;
	}

	@Autowired
	public CrossreferenceTraversalStrategyImpl(GraphDatabaseService graphDatabaseService) {
		this.db = graphDatabaseService;
	}
	
	public Node getInitialNode() { return initialNode;}
	public void setInitialNode(Node initialNode) { this.initialNode = initialNode;}

	public GraphDatabaseService getDb() { return db;}
	public void setDb(GraphDatabaseService db) { this.db = db;}
	
//	private RelationshipType relationshipType = CompoundRelationshipType.HasCrossreferenceTo;
	private static final RelationshipType relationshipType = MetaboliteRelationshipType.has_crossreference_to;
	
	@Override
	public Set<Long> execute() {
		Set<Long> nodes = new HashSet<> ();
		for (Path position: db.traversalDescription()
				.depthFirst()
				.relationships(relationshipType)
				.evaluator(new Evaluator() {
					
					@Override
					public Evaluation evaluate(Path path) {
						Node endNode = path.endNode();
						for (Label label : excludeMajorLabel) {
							if (endNode.hasLabel(label)) {
								LOGGER.debug("Exclude and Prune: " + String.format("[%d]%s:%s", endNode.getId(), endNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY), endNode.getProperty("entry")));
								return Evaluation.EXCLUDE_AND_PRUNE;
							}
						}
						
						return Evaluation.INCLUDE_AND_CONTINUE;
					}
				})
				.traverse(initialNode)) {
			
			Long eid = position.endNode().getId();
			LOGGER.trace(String.format("[%d] - %s", eid, toString(position)));
			nodes.add(eid);
		}
		
		return nodes;
	}

	@Override
	public String toString() {
		return String.format("CrossreferenceTraversalStrategyImpl.initial(%d)", initialNode==null? -1L : initialNode.getId());
	}

	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
	}
	
	public String toString(Path path) {
		String result = "";
		for (Object o : path) {
			if (o instanceof Node) {
				Node node = (Node) o;
				result += String.format("[%d]%s:%s", node.getId(), node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY), node.getProperty("entry"));
			} else if (o instanceof Relationship) {
				Relationship relationship = (Relationship) o;
				result += " <?> ";
			} else {
				result += "[ERROR]";
			}
		}
		return result;
	}
}
