package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundRelationshipType;

public class ChebiParentClusteringStrategy extends AbstractNeo4jClusteringStrategy {

	public ChebiParentClusteringStrategy() {
		this.initialNodeLabel = CompoundNodeLabel.ChEBI;
	}
	
	@Override
	public Set<Long> execute() {
		Set<Long> ids = new HashSet<> ();
		
		for (Relationship relationship : this.initialNode
				.getRelationships(CompoundRelationshipType.HasCrossreferenceTo)) {
			
			Node other = relationship.getOtherNode(initialNode);
			
			if (other.hasLabel(CompoundNodeLabel.ChEBI) 
					&& other.hasProperty("parent") 
					&& !((boolean)other.getProperty("proxy"))) {
				String parent = other.getProperty("parent").toString();
				String entry = this.initialNode.getProperty("entry").toString();
				if (parent.equals(entry)) ids.add(other.getId());
			}
		}
		
		if (!ids.isEmpty()) ids.add(this.initialNode.getId());
		
		return ids;
	}

}
