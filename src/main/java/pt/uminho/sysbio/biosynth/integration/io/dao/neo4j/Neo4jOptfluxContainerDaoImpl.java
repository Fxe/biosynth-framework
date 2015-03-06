package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.AbstractNeo4jDao;
import pt.uminho.sysbio.biosynthframework.DefaultMetaboliteSpecie;
import pt.uminho.sysbio.biosynthframework.DefaultModelMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.DefaultSubcellularCompartmentEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionLeft;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionRight;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;
import pt.uminho.sysbio.biosynthframework.io.OptfluxMetabolicModelDao;

/**
 * Temporary DAO to manage metabolic model entities<br/>
 * OptfluxContainer - because they are based on the container instances
 * @author Filipe
 *
 */
public class Neo4jOptfluxContainerDaoImpl extends AbstractNeo4jDao implements OptfluxMetabolicModelDao {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jOptfluxContainerDaoImpl.class);
	private AnnotationPropertyContainerBuilder a;
	
	public Neo4jOptfluxContainerDaoImpl(GraphDatabaseService graphDatabaseService) {
		super(graphDatabaseService);
		a = new AnnotationPropertyContainerBuilder();
	}
	
	@Override
	public OptfluxContainerMetabolicModelEntity getMetabolicModelById(long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(GlobalLabel.MetabolicModel)) {
			return null;
		}
		OptfluxContainerMetabolicModelEntity mmd = Neo4jMapper.nodeToMetabolicModel(node);
		return mmd;
	}

	@Override
	public OptfluxContainerMetabolicModelEntity getMetabolicModelByEntry(
			String entry) {
		Node node = Neo4jUtils.getUniqueResult(
				graphDatabaseService.findNodesByLabelAndProperty(
						GlobalLabel.MetabolicModel, "entry", entry));
		return getMetabolicModelById(node.getId());
	}

	@Override
	public OptfluxContainerMetabolicModelEntity saveMetabolicModel(
			OptfluxContainerMetabolicModelEntity mmd) {
		
		try {
			Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.MetabolicModel, "entry", mmd.getEntry(), executionEngine);
			Map<String, Object> properties = a.extractProperties(mmd, OptfluxContainerMetabolicModelEntity.class);
			Neo4jUtils.setPropertiesMap(properties, node);
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
		} catch (Exception e) {
			LOGGER.error("E - {}", e.getMessage());
			return null;
		}
		
		return mmd;
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
	public List<OptfluxContainerMetabolicModelEntity> findMetabolicModelBySearchTerm(
			String search) {
		List<OptfluxContainerMetabolicModelEntity> res = new ArrayList<> ();
		String query = String.format("MATCH (n:%s) WHERE n.entry =~ '%s' RETURN n", GlobalLabel.MetabolicModel, search);
		LOGGER.trace("Query: {}", query);
//		System.out.println(query);
		for (Object o : IteratorUtil.asList(executionEngine.execute(query).columnAs("n"))) {
			Node node = (Node) o;
			OptfluxContainerMetabolicModelEntity model = this.getMetabolicModelById(node.getId());
			if (model != null) res.add(model);
		}
		return res;
	}

	@Override
	public List<OptfluxContainerMetabolicModelEntity> findAll(int page, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultSubcellularCompartmentEntity getCompartmentById(
			Long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(GlobalLabel.SubcellularCompartment)) {
			return null;
		}
		DefaultSubcellularCompartmentEntity cmp = Neo4jMapper.nodeToSubcellularCompartment(node);
		return cmp;
	}

	@Override
	public DefaultSubcellularCompartmentEntity getCompartmentByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String cmpEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultSubcellularCompartmentEntity saveCompartment(
			OptfluxContainerMetabolicModelEntity mmd,
			DefaultSubcellularCompartmentEntity cmp) {
		
		try {
			String entry = String.format("%s@%s", cmp.getEntry(), mmd.getEntry());
			Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.SubcellularCompartment, "entry", entry, executionEngine);
			Map<String, Object> properties = a.extractProperties(cmp, DefaultSubcellularCompartmentEntity.class);
			properties.remove("entry");
			Neo4jUtils.setPropertiesMap(properties, node);
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
			
			Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());
			mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_compartment);
		} catch (Exception e) {
			LOGGER.error("E - {}", e.getMessage());
			return null;
		}
		
		return cmp;
	}

	@Override
	public Set<Long> getAllModelCompartmentIds(
			OptfluxContainerMetabolicModelEntity model) {
		Set<Long> res = new HashSet<> ();
		Node mmdNode = graphDatabaseService.getNodeById(model.getId());
		res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_compartment));
		return res;
	}

	@Override
	public Set<String> getAllModelCompartmentEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetaboliteSpecie getModelMetaboliteSpecieById(
			Long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(MetabolicModelLabel.MetaboliteSpecie)) {
			return null;
		}
		DefaultMetaboliteSpecie spi = Neo4jMapper.nodeToMetaboliteSpecie(node);
		return spi;
	}

	@Override
	public DefaultMetaboliteSpecie getModelMetaboliteSpecieByByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultMetaboliteSpecie saveModelMetaboliteSpecie(
			OptfluxContainerMetabolicModelEntity mmd,
			DefaultMetaboliteSpecie spi) {
		
		try {
			String entry = String.format("%s@%s", spi.getEntry(), mmd.getEntry());
			Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.MetaboliteSpecie, "entry", entry, executionEngine);
			Map<String, Object> properties = a.extractProperties(spi, DefaultMetaboliteSpecie.class);
			properties.remove("entry");
			Neo4jUtils.setPropertiesMap(properties, node);
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
			
			Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());
			mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_specie);
			
			String cmpEntry = String.format("%s@%s", spi.getComparment(), mmd.getEntry());
			Node cmpNode = Neo4jUtils.getUniqueResult(graphDatabaseService
					.findNodesByLabelAndProperty(GlobalLabel.SubcellularCompartment, "entry", cmpEntry));
			node.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.in_compartment);
		} catch (Exception e) {
			LOGGER.error("E - {}", e.getMessage());
			return null;
		}
		return spi;
	}

	@Override
	public Set<Long> getAllModelSpecieIds(
			OptfluxContainerMetabolicModelEntity model) {
		Set<Long> res = new HashSet<> ();
		Node mmdNode = graphDatabaseService.getNodeById(model.getId());
		res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_specie));
		return res;
	}

	@Override
	public Set<String> getAllModelSpecieEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerReactionEntity getModelReactionById(Long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(MetabolicModelLabel.ModelReaction)) {
			return null;
		}
		OptfluxContainerReactionEntity rxn = Neo4jMapper.nodeToModelReaction(node);
//		rxn.setRight(right);
		return rxn;
	}

	@Override
	public OptfluxContainerReactionEntity getModelReactionByByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OptfluxContainerReactionEntity saveModelReaction(
			OptfluxContainerMetabolicModelEntity mmd,
			OptfluxContainerReactionEntity rxn) {
		
		try {
			String entry = String.format("%s@%s", rxn.getEntry(), mmd.getEntry());
			Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.ModelReaction, "entry", entry, executionEngine);
			Map<String, Object> properties = a.extractProperties(rxn, OptfluxContainerReactionEntity.class);
			properties.remove("entry");
			Neo4jUtils.setPropertiesMap(properties, node);
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
			
			Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());
			mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_reaction);
			
			for (OptfluxContainerReactionLeft  l : rxn.getLeft()) {
				createStoichiometryLink(l.getCpdEntry(), mmd.getEntry(), node, 
						a.extractProperties(l, OptfluxContainerReactionLeft.class), MetabolicModelRelationshipType.left_component);
			}
			for (OptfluxContainerReactionRight r : rxn.getRight()) {
				createStoichiometryLink(r.getCpdEntry(), mmd.getEntry(), node, 
						a.extractProperties(r, OptfluxContainerReactionRight.class), MetabolicModelRelationshipType.right_component);
			}
		} catch (Exception e) {
			LOGGER.error("E - {}", e.getMessage());
			return null;
		}
		
		return rxn;
	}
	
	public void createStoichiometryLink(String spiEntry, String mmdEntry, Node rxn, Map<String, Object> properties, MetabolicModelRelationshipType r) {
		String spiEntry_ = String.format("%s@%s", spiEntry, mmdEntry);
		Node spiNode = Neo4jUtils.getUniqueResult(graphDatabaseService
				.findNodesByLabelAndProperty(MetabolicModelLabel.MetaboliteSpecie, "entry", spiEntry_));
		
		Relationship relationship = rxn.createRelationshipTo(spiNode, r);
		Neo4jUtils.setPropertiesMap(properties, relationship);
	}

	@Override
	public Set<Long> getAllModelReactionIds(
			OptfluxContainerMetabolicModelEntity model) {
		Set<Long> res = new HashSet<> ();
		Node mmdNode = graphDatabaseService.getNodeById(model.getId());
		res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_reaction));
		return res;
	}

	@Override
	public Set<String> getAllModelReactionEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultModelMetaboliteEntity getModelMetaboliteById(Long id) {
		Node node = graphDatabaseService.getNodeById(id);
		if (node == null || !node.hasLabel(MetabolicModelLabel.ModelMetabolite)) {
			return null;
		}
		DefaultModelMetaboliteEntity cpd = Neo4jMapper.nodeToModelMetabolite(node);
		return cpd;
	}

	@Override
	public DefaultModelMetaboliteEntity getModelMetaboliteByModelAndEntry(
			OptfluxContainerMetabolicModelEntity model, String spiEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultModelMetaboliteEntity saveModelMetabolite(
			OptfluxContainerMetabolicModelEntity mmd,
			DefaultModelMetaboliteEntity cpd) {
		
		if (cpd.getSpecies().isEmpty()) return null;
		
		Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());
		
		try {
			String entry = String.format("%s@%s", cpd.getEntry(), mmd.getEntry());
			Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.ModelMetabolite, "entry", entry, executionEngine);
			LOGGER.debug("Created {}", node);
			Map<String, Object> properties = a.extractProperties(cpd, DefaultModelMetaboliteEntity.class);
			properties.remove("entry");
			Neo4jUtils.setPropertiesMap(properties, node);
			node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
			
			for (DefaultMetaboliteSpecie spi : cpd.getSpecies()) {
				if (spi.getId() == null) this.saveModelMetaboliteSpecie(mmd, spi);
				Node spiNode = graphDatabaseService.getNodeById(spi.getId());
				node.createRelationshipTo(spiNode, MetabolicModelRelationshipType.has_specie);
			}
			
			mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_metabolite);
			cpd.setId(node.getId());
		} catch (Exception e) {
			LOGGER.error("E - {}", e.getMessage());
			return null;
		}
		
		return cpd;
	}

	@Override
	public Set<Long> getAllModelMetaboliteIds(
			OptfluxContainerMetabolicModelEntity model) {
		Set<Long> res = new HashSet<> ();
		Node mmdNode = graphDatabaseService.getNodeById(model.getId());
		res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_metabolite));
		return res;
	}

	@Override
	public Set<String> getAllModelMetaboliteEntries(
			OptfluxContainerMetabolicModelEntity model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteModelMetabolite(DefaultModelMetaboliteEntity mcpd) {
		Node cpdNode = graphDatabaseService.getNodeById(mcpd.getId());
		if (cpdNode.hasLabel(MetabolicModelLabel.ModelMetabolite)) {
			Neo4jUtils.deleteAllRelationships(cpdNode);
		}
		cpdNode.delete();
	}

}
