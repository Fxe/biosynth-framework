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

import pt.uminho.sysbio.biosynthframework.OptfluxContainerMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.io.MetabolicModelDao;

@Deprecated
public class Neo4jGraphMetabolicModelDaoImpl implements MetabolicModelDao<
OptfluxContainerMetabolicModelEntity,
OptfluxContainerMetabolicModelEntity,
OptfluxContainerMetabolicModelEntity,
OptfluxContainerMetabolicModelEntity,
OptfluxContainerMetabolicModelEntity> {

	private GraphDatabaseService graphDatabaseService;
	private ExecutionEngine executionEngine;
	
	public Neo4jGraphMetabolicModelDaoImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	@Override
	public OptfluxContainerMetabolicModelEntity getMetabolicModelById(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
//		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getMetabolicModelByEntry(String entry) {
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
	public List<OptfluxContainerMetabolicModelEntity> findMetabolicModelBySearchTerm(
			String search) {
		OptfluxContainerMetabolicModelEntity mmd = getMetabolicModelByEntry(search);
		
//		executionEngine.execute(query, params)
		List<OptfluxContainerMetabolicModelEntity> res = new ArrayList<> ();
		
		if (mmd != null) res.add(mmd);
		return res;
	}

	@Override
	public List<OptfluxContainerMetabolicModelEntity> findAll(int page, int size) {
		String query = String.format("MATCH (n:%s) RETURN n ORDER BY ID(n) SKIP %d LIMIT %d;", 
				GlobalLabel.MetabolicModel, page * size, size);
		List<Object> oo = IteratorUtil.asList(executionEngine.execute(query).columnAs("n"));
		List<OptfluxContainerMetabolicModelEntity> res = new ArrayList<> ();
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
	public OptfluxContainerMetabolicModelEntity getCompartmentById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getCompartmentByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String cmpEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity saveMetabolicModel(
			OptfluxContainerMetabolicModelEntity mmd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity saveCompartment(
			OptfluxContainerMetabolicModelEntity mmd,
			OptfluxContainerMetabolicModelEntity cmp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelCompartmentIds(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelCompartmentEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getModelMetaboliteSpecieById(
			Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getModelMetaboliteSpecieByByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity saveModelMetaboliteSpecie(
			OptfluxContainerMetabolicModelEntity mmd,
			OptfluxContainerMetabolicModelEntity spi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelSpecieIds(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelSpecieEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getModelReactionById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getModelReactionByByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String rxnEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity saveModelReaction(
			OptfluxContainerMetabolicModelEntity mmd,
			OptfluxContainerMetabolicModelEntity rxn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelReactionIds(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelReactionEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getModelMetaboliteById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getModelMetaboliteByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String cpdEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity saveModelMetabolite(
			OptfluxContainerMetabolicModelEntity mmd,
			OptfluxContainerMetabolicModelEntity cpd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteModelMetabolite(OptfluxContainerMetabolicModelEntity mcpd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getAllModelMetaboliteIds(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelMetaboliteEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}
}
