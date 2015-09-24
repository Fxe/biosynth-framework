package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class ChebiParentClusteringStrategy extends AbstractNeo4jClusteringStrategy {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ChebiParentClusteringStrategy.class);	
	
	public ChebiParentClusteringStrategy(
			GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		this.initialNodeLabel = CompoundNodeLabel.ChEBI;
	}
	
	private static final RelationshipType relationshipType = MetaboliteRelationshipType.chebi_parent;

	@Override
	public Set<Long> execute() {
		LOGGER.debug("Cluster: " + initialNode);
		
		Set<Long> ids = new HashSet<> ();
		
		//add self
		ids.add(this.initialNode.getId());
		
		//should use traversal engine
		for (Path path : db.traversalDescription()
						   .depthFirst()
						   .relationships(relationshipType)
						   .evaluator(Evaluators.all())
						   .traverse(initialNode)) {
			
			Long eid = path.endNode().getId();
			LOGGER.trace(String.format("[%d] - %s", eid, path));
			ids.add(eid);
		}
		
		
//		for (Relationship relationship : this.initialNode
//				.getRelationships(MetaboliteRelationshipType.ChEBI_Parent)) {
//			
//			
//			Node other = relationship.getOtherNode(initialNode);
//			
//			LOGGER.trace(String.format("%s:%s - %s", 
//					other, Neo4jUtils.getLabels(other), Neo4jUtils.getPropertiesMap(other)));
//			
//			if (other.hasLabel(CompoundNodeLabel.ChEBI) 
//					&& other.hasProperty("parentId") 
//					&& !((boolean)other.getProperty("proxy", true))) {
//				String parent = other.getProperty("parentId").toString();
//				String entry = this.initialNode.getProperty("entry").toString();
//				if (entry.equals(parent)) ids.add(other.getId());
//			}
//		}
		
//		if (!ids.isEmpty()) ids.add(this.initialNode.getId());
		
		LOGGER.debug("Result: " + ids);
		return ids;
	}

}
