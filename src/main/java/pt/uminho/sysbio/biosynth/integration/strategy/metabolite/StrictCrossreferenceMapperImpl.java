package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class StrictCrossreferenceMapperImpl extends AbstractNeo4jClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(StrictCrossreferenceMapperImpl.class);
	private final static RelationshipType CROSSREFERENCE_RELATIONSHIP = MetaboliteRelationshipType.has_crossreference_to;
	
	public StrictCrossreferenceMapperImpl(
			GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		
		this.initialNodeLabel = GlobalLabel.Metabolite;
	}

	@Override
	public Set<Long> execute() {
		Set<Long> explored = new HashSet<> ();
		explored.add(initialNode.getId());
		collect(initialNode, explored, 1);
		return explored;
	}
	
	public void collect(Node node, Set<Long> explored, int depth) {
		MetaboliteMajorLabel db = null;
		try {
			db = MetaboliteMajorLabel.valueOf((String) node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, ""));
		} catch (IllegalArgumentException e) {
			return;
		}
				
		for (Relationship r : node.getRelationships(CROSSREFERENCE_RELATIONSHIP)) {
			Node other = r.getOtherNode(node);
			LOGGER.trace("[{}] {}|- {}:{} -- {}:{}", depth, StringUtils.repeat(' ', depth - 1), node, Neo4jUtils.getLabels(node), other, Neo4jUtils.getLabels(other));
			//node is an invalid xref if points to two instances of current db
			if (!explored.contains(other.getId())
					&& other.hasLabel(GlobalLabel.Metabolite)
					&& valid(other, db)) {
				LOGGER.debug("[{}] {}|- [{}]{}:{}", depth, StringUtils.repeat(' ', depth - 1), other.getId(), Neo4jUtils.getLabels(other), other.getProperty("entry", "-"));
				explored.add(other.getId());
				collect(other, explored, depth + 1);
			}
//			break;
		}
	}
	
	public boolean valid(Node node, MetaboliteMajorLabel db) {
		if (Neo4jUtils.collectNodeRelationshipNodes(node, db).size() > 1) {
			return false;
		}
		return true;
	}

}
