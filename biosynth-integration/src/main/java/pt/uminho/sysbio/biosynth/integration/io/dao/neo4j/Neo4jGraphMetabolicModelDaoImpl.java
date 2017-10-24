package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Iterators;

import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.io.MetabolicModelDao;

@Deprecated
public class Neo4jGraphMetabolicModelDaoImpl implements MetabolicModelDao<
DefaultMetabolicModelEntity,
DefaultMetabolicModelEntity,
DefaultMetabolicModelEntity,
DefaultMetabolicModelEntity,
DefaultMetabolicModelEntity> {

	private BiodbGraphDatabaseService graphDatabaseService;
	
	public Neo4jGraphMetabolicModelDaoImpl(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = new BiodbGraphDatabaseService(graphDatabaseService);
	}
	
	@Override
	public DefaultMetabolicModelEntity getMetabolicModelById(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
//		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getMetabolicModelByEntry(String entry) {
		Node node = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodes(
						GlobalLabel.MetabolicModel, "entry", entry));
		if (node == null || node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
//		DefaultMetabolicModel mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return null;
	}

	@Override
	public List<DefaultMetabolicModelEntity> findMetabolicModelBySearchTerm(
			String search) {
		DefaultMetabolicModelEntity mmd = getMetabolicModelByEntry(search);
		
//		executionEngine.execute(query, params)
		List<DefaultMetabolicModelEntity> res = new ArrayList<> ();
		
		if (mmd != null) res.add(mmd);
		return res;
	}

	@Override
	public List<DefaultMetabolicModelEntity> findAll(int page, int size) {
		String query = String.format("MATCH (n:%s) RETURN n ORDER BY ID(n) SKIP %d LIMIT %d;", 
				GlobalLabel.MetabolicModel, page * size, size);
		List<Object> oo = Iterators.asList(graphDatabaseService.execute(query).columnAs("n"));
		List<DefaultMetabolicModelEntity> res = new ArrayList<> ();
//		for (Object o : oo) res.add(Neo4jMapper.nodeToMetabolicModel((Node) o));
		return res;
	}

	@Override
	public Set<Long> getAllMetabolicModelIds() {
		Set<Long> res = new HashSet<> ();
		for (Node node : graphDatabaseService.listNodes(
		    GlobalLabel.MetabolicModel)) {
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
	public DefaultMetabolicModelEntity getCompartmentById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getCompartmentByModelAndEntry(
			DefaultMetabolicModelEntity model, String cmpEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity saveMetabolicModel(
			DefaultMetabolicModelEntity mmd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity saveCompartment(
			DefaultMetabolicModelEntity mmd,
			DefaultMetabolicModelEntity cmp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelCompartmentIds(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelCompartmentEntries(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getModelMetaboliteSpecieById(
			Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getModelMetaboliteSpecieByByModelAndEntry(
			DefaultMetabolicModelEntity model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity saveModelMetaboliteSpecie(
			DefaultMetabolicModelEntity mmd,
			DefaultMetabolicModelEntity spi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelSpecieIds(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelSpecieEntries(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getModelReactionById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getModelReactionByByModelAndEntry(
			DefaultMetabolicModelEntity model, String rxnEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity saveModelReaction(
			DefaultMetabolicModelEntity mmd,
			DefaultMetabolicModelEntity rxn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getAllModelReactionIds(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelReactionEntries(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getModelMetaboliteById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity getModelMetaboliteByModelAndEntry(
			DefaultMetabolicModelEntity model, String cpdEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetabolicModelEntity saveModelMetabolite(
			DefaultMetabolicModelEntity mmd,
			DefaultMetabolicModelEntity cpd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteModelMetabolite(DefaultMetabolicModelEntity mcpd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getAllModelMetaboliteIds(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllModelMetaboliteEntries(
			DefaultMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

  @Override
  public boolean updateModelReaction(DefaultMetabolicModelEntity rxn) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<Long> getAllModelSubsystemsIds(DefaultMetabolicModelEntity model) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateCompartment(DefaultMetabolicModelEntity cmp) {
    // TODO Auto-generated method stub
    
  }
}
