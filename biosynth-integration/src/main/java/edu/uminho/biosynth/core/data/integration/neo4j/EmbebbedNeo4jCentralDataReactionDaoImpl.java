package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class EmbebbedNeo4jCentralDataReactionDaoImpl extends AbstractNeo4jDao<CentralDataReactionEntity> implements ReactionDao<CentralDataReactionEntity> {

	public EmbebbedNeo4jCentralDataReactionDaoImpl(GraphDatabaseService graphdb) {
		super(graphdb);
		// TODO Auto-generated constructor stub
	}

	@Override
	public CentralDataReactionEntity getReactionById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataReactionEntity getReactionByEntry(String entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CentralDataReactionEntity saveReaction(
			CentralDataReactionEntity reaction) {
		
		Map<String, Object> params = reaction.getProperties();
		params.put("entry", reaction.getEntry());
		
		String labels = StringUtils.join(reaction.getLabels(), ':');
		
		String cypherQuery = String.format(
				"MERGE (rxn:%s {entry:{entry}})"
				, labels, reaction.getEntry());
		
		System.out.println(cypherQuery);
		
//		this.engine.execute(cypherQuery, params);
		
		return null;
	}

	@Override
	public Set<Long> getAllReactionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CentralDataReactionEntity nodeToObject(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

}
