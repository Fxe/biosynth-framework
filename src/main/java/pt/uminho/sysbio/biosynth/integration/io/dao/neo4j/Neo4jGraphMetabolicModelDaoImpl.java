package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;

import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModel;
import pt.uminho.sysbio.biosynthframework.io.MetabolicModelDao;

public class Neo4jGraphMetabolicModelDaoImpl implements MetabolicModelDao<DefaultMetabolicModel> {

	private GraphDatabaseService graphDatabaseService;
	private ExecutionEngine executionEngine;
	
	public Neo4jGraphMetabolicModelDaoImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	@Override
	public DefaultMetabolicModel getMetabolicModelById(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return mmd;
	}

	@Override
	public DefaultMetabolicModel getMetabolicModelByEntry(String entry) {
		Node node = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodesByLabelAndProperty(
						GlobalLabel.MetabolicModel, "entry", entry));
		if (node == null || node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return mmd;
	}

	@Override
	public List<DefaultMetabolicModel> findMetabolicModelBySearchTerm(
			String search) {
		DefaultMetabolicModel mmd = getMetabolicModelByEntry(search);
		List<DefaultMetabolicModel> res = new ArrayList<> ();
		if (mmd != null) res.add(mmd);
		return res;
	}

	@Override
	public Set<Long> getAllCompartmentIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllMetaboliteSpecieIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllReactionSpecieIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllCompartmentEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllMetaboliteSpecieEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllReactionSpecieEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getCompartmentById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getMetaboliteSpecieById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getReactionSpecieById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getCompartmentByEntry(String entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getMetaboliteSpecieByEntry(String entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getReactionSpecieByEntry(String entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<DefaultMetabolicModel> findAll(int page, int size) {
		String query = String.format("MATCH (n:%s) RETURN n ORDER BY ID(n) SKIP %d LIMIT %d;", 
				GlobalLabel.MetabolicModel, page * size, size);
		List<Object> oo = IteratorUtil.asList(executionEngine.execute(query).columnAs("n"));
		List<DefaultMetabolicModel> res = new ArrayList<> ();
		for (Object o : oo) res.add(Neo4jMapper.nodeToMetabolicModel((Node) o));
		return res;
	}


}
