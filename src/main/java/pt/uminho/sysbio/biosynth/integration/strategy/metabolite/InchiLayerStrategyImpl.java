package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.strategy.AbstractNeo4jClusteringStrategy;

public class InchiLayerStrategyImpl extends AbstractNeo4jClusteringStrategy {

	private final static Logger LOGGER = LoggerFactory.getLogger(InchiLayerStrategyImpl.class);
	
	private final static String FIKHB_RELATIONSHIP_TYPE = "FIKHB";
	private final static String SIKHB_RELATIONSHIP_TYPE = "SIKHB";
	
	public InchiLayerStrategyImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		
		this.initialNodeLabel = GlobalLabel.Metabolite;
	}

	@Override
	public Set<Long> execute() {
		Set<Long> result = new HashSet<> ();
		result.add(initialNode.getId());
		
		Set<Long> inchiNodeIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(initialNode, MetaboliteRelationshipType.HasInChI);
		
		for (Long inchiNodeId : inchiNodeIdSet) {
			Node inchiNode = db.getNodeById(inchiNodeId);
			Set<Long> a = Neo4jUtils.collectNodeRelationshipNodeIds(inchiNode, DynamicRelationshipType.withName(FIKHB_RELATIONSHIP_TYPE));
			Set<Long> b = Neo4jUtils.collectNodeRelationshipNodeIds(inchiNode, DynamicRelationshipType.withName(SIKHB_RELATIONSHIP_TYPE));
			if (a.isEmpty() || b.isEmpty()) {
				throw new RuntimeException("empty");
			}
			if (a.size() > 1 || b.size() > 1) {
				throw new RuntimeException("size");
			}
			
			Node fikhbNode = db.getNodeById(a.iterator().next());
			Node sikhbNode = db.getNodeById(b.iterator().next());
			
			Set<Long> a_ = Neo4jUtils.collectNodeRelationshipNodeIds(fikhbNode, DynamicRelationshipType.withName(FIKHB_RELATIONSHIP_TYPE));
			Set<Long> b_ = Neo4jUtils.collectNodeRelationshipNodeIds(sikhbNode, DynamicRelationshipType.withName(SIKHB_RELATIONSHIP_TYPE));
			Set<Long> intersection = new HashSet<> (a_);
			intersection.retainAll(b_);
			
			LOGGER.debug("Inchis: " + intersection.size());
			
			for (Long inchiNodeId_ : intersection) {
				Node inchiNode_ = db.getNodeById(inchiNodeId_);
				result.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(
						inchiNode_, MetaboliteRelationshipType.HasInChI));
			}

		}
//		Set<Long> inchiNodeIdSet = Neo4jUtils.collectNodeRelationshipNodeIds(initialNode, MetaboliteRelationshipType.HasInChI);
		
		return result;
	}

}
