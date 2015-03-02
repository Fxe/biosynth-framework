package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModel;
import pt.uminho.sysbio.biosynthframework.io.MetabolicModelDao;

@Deprecated
public class Neo4jGraphMetabolicModelDaoImpl implements MetabolicModelDao<
DefaultMetabolicModel,
DefaultMetabolicModel,
DefaultMetabolicModel,
DefaultMetabolicModel,
DefaultMetabolicModel> {

	private GraphDatabaseService graphDatabaseService;
	private ExecutionEngine executionEngine;
	
	public Neo4jGraphMetabolicModelDaoImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	@Override
	public DefaultMetabolicModel getMetabolicModelById(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
//		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return null;
	}

	@Override
	public DefaultMetabolicModel getMetabolicModelByEntry(String entry) {
		Node node = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodesByLabelAndProperty(
						GlobalLabel.MetabolicModel, "entry", entry));
		if (node == null || node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
//		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return null;
	}

	@Override
	public List<DefaultMetabolicModel> findMetabolicModelBySearchTerm(
			String search) {
		DefaultMetabolicModel mmd = getMetabolicModelByEntry(search);
		
//		executionEngine.execute(query, params)
		List<DefaultMetabolicModel> res = new ArrayList<> ();
		
		if (mmd != null) res.add(mmd);
		return res;
	}

	@Override
	public List<DefaultMetabolicModel> findAll(int page, int size) {
		String query = String.format("MATCH (n:%s) RETURN n ORDER BY ID(n) SKIP %d LIMIT %d;", 
				GlobalLabel.MetabolicModel, page * size, size);
		List<Object> oo = IteratorUtil.asList(executionEngine.execute(query).columnAs("n"));
		List<DefaultMetabolicModel> res = new ArrayList<> ();
//		for (Object o : oo) res.add(Neo4jMapper.nodeToMetabolicModel((Node) o));
		return res;
	}

	@Override
	public Set<Long> getAllMetabolicModelIds() {
		Set<Long> res = new HashSet<> ();
		for (Node node : GlobalGraphOperations
				.at(graphDatabaseService)
				.getAllNodesWithLabel(GlobalLabel.MetabolicModel)) {
			res.add(node.getId());
		}
		
		return res;
	}

	@Override
	public Set<String> getAllMetabolicModelEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getCompartmentById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getCompartmentByModelAndEntry(
			DefaultMetabolicModel model, String cmpEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelCompartmentIds(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelCompartmentEntries(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getModelMetaboliteSpecieById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getModelMetaboliteSpecieByByModelAndEntry(
			DefaultMetabolicModel model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelSpecieIds(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelSpecieEntries(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getModelReactionById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getModelReactionByByModelAndEntry(
			DefaultMetabolicModel model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelReactionIds(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelReactionEntries(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getModelMetaboliteById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel getModelMetaboliteByModelAndEntry(
			DefaultMetabolicModel model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelMetaboliteIds(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelMetaboliteEntries(DefaultMetabolicModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel saveMetabolicModel(DefaultMetabolicModel mmd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel saveCompartment(DefaultMetabolicModel mmd,
			DefaultMetabolicModel cmp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel saveModelMetaboliteSpecie(
			DefaultMetabolicModel mmd, DefaultMetabolicModel cmp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel saveModelReaction(DefaultMetabolicModel mmd,
			DefaultMetabolicModel cmp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModel saveModelMetabolite(DefaultMetabolicModel mmd,
			DefaultMetabolicModel cmp) {
		// TODO Auto-generated method stub
		return null;
	}



}
