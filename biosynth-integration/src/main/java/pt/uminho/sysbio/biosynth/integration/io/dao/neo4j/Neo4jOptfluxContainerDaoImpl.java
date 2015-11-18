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
import pt.uminho.sysbio.biosynthframework.DefaultMetaboliteSpecieReference;
import pt.uminho.sysbio.biosynthframework.DefaultSubcellularCompartmentEntity;
import pt.uminho.sysbio.biosynthframework.ExtendedMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.ExtendedMetaboliteSpecie;
import pt.uminho.sysbio.biosynthframework.ExtendedModelMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionLeft;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionRight;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;
import pt.uminho.sysbio.biosynthframework.io.ExtendedMetabolicModelDao;

/**
 * Temporary DAO to manage metabolic model entities<br/>
 * OptfluxContainer - because they are based on the container instances
 * @author Filipe
 *
 */
public class Neo4jOptfluxContainerDaoImpl extends AbstractNeo4jDao implements ExtendedMetabolicModelDao {

  private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jOptfluxContainerDaoImpl.class);
  private AnnotationPropertyContainerBuilder a;

  public Neo4jOptfluxContainerDaoImpl(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
    a = new AnnotationPropertyContainerBuilder();
  }

  @Override
  public ExtendedMetabolicModelEntity getMetabolicModelById(long id) {
    Node node = graphDatabaseService.getNodeById(id);
    if (node == null || !node.hasLabel(GlobalLabel.MetabolicModel)) {
      return null;
    }
    ExtendedMetabolicModelEntity mmd = new ExtendedMetabolicModelEntity();
    Neo4jMapper.nodeToPropertyContainer(node, mmd);
    mmd.setId(node.getId());
    return mmd;
  }

  @Override
  public ExtendedMetabolicModelEntity getMetabolicModelByEntry(
      String entry) {
    Node node = Neo4jUtils.getUniqueResult(
        graphDatabaseService.findNodesByLabelAndProperty(
            GlobalLabel.MetabolicModel, "entry", entry));

    if (node == null) {
      LOGGER.debug("Metabolite Specie [{}:{}] not found", GlobalLabel.MetabolicModel, entry);
      return null;
    }

    return getMetabolicModelById(node.getId());
  }

  @Override
  public ExtendedMetabolicModelEntity saveMetabolicModel(
      ExtendedMetabolicModelEntity mmd) {

    try {
      Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.MetabolicModel, "entry", mmd.getEntry(), executionEngine);
      Map<String, Object> properties = a.extractProperties(mmd, ExtendedMetabolicModelEntity.class);
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      mmd.setId(node.getId());
    } catch (Exception e) {
      LOGGER.error("E - {}", e.getMessage());
      e.printStackTrace();
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
  public List<ExtendedMetabolicModelEntity> findMetabolicModelBySearchTerm(
      String search) {
    List<ExtendedMetabolicModelEntity> res = new ArrayList<> ();
    String query = String.format("MATCH (n:%s) WHERE n.entry =~ '%s' RETURN n", GlobalLabel.MetabolicModel, search);
    LOGGER.trace("Query: {}", query);
    //		System.out.println(query);
    for (Object o : IteratorUtil.asList(executionEngine.execute(query).columnAs("n"))) {
      Node node = (Node) o;
      ExtendedMetabolicModelEntity model = this.getMetabolicModelById(node.getId());
      if (model != null) res.add(model);
    }
    return res;
  }

  @Override
  public List<ExtendedMetabolicModelEntity> findAll(int page, int size) {
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
      ExtendedMetabolicModelEntity model, String cmpEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DefaultSubcellularCompartmentEntity saveCompartment(
      ExtendedMetabolicModelEntity mmd,
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
      e.printStackTrace();
      return null;
    }

    return cmp;
  }

  @Override
  public Set<Long> getAllModelCompartmentIds(
      ExtendedMetabolicModelEntity model) {
    Set<Long> res = new HashSet<> ();
    Node mmdNode = graphDatabaseService.getNodeById(model.getId());
    res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_compartment));
    return res;
  }

  @Override
  public Set<String> getAllModelCompartmentEntries(
      ExtendedMetabolicModelEntity model) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExtendedMetaboliteSpecie getModelMetaboliteSpecieById(
      Long id) {
    Node node = graphDatabaseService.getNodeById(id);
    if (node == null || !node.hasLabel(MetabolicModelLabel.MetaboliteSpecie)) {
      return null;
    }
    ExtendedMetaboliteSpecie spi = new ExtendedMetaboliteSpecie();
    Neo4jMapper.nodeToPropertyContainer(node, spi);
    spi.setId(node.getId());
    spi.setCrossreferences(getMetaboliteCrossreferences(spi.getId()));
    spi.setComparment((String) node.getProperty("comparment", null));

    return spi;
  }

  public List<DefaultMetaboliteSpecieReference> getMetaboliteCrossreferences(Long id) {
    List<DefaultMetaboliteSpecieReference> refs = new ArrayList<> ();
    Node node = graphDatabaseService.getNodeById(id);
    for (Node cpd : Neo4jUtils.collectNodeRelationshipNodes(node, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (cpd.hasLabel(GlobalLabel.Metabolite)) {
        DefaultMetaboliteSpecieReference reference = new DefaultMetaboliteSpecieReference();
        reference.setId(cpd.getId());
        reference.setRef((String) cpd.getProperty("major_label"));
        reference.setValue((String) cpd.getProperty("entry"));
        reference.setType(ReferenceType.DATABASE);
        refs.add(reference);
      } else {
        LOGGER.warn("Specie -[has_crossreference_to]-> {}", Neo4jUtils.getLabels(cpd));
      }
    }
    return refs;
  }

  public List<GenericCrossreference> getCrossreferences(Long id) {
    List<GenericCrossreference> refs = new ArrayList<> ();
    Node node = graphDatabaseService.getNodeById(id);
    for (Node refNode : Neo4jUtils.collectNodeRelationshipNodes(node, MetabolicModelRelationshipType.has_crossreference_to)) {
      GenericCrossreference reference = new GenericCrossreference();
      reference.setId(refNode.getId());
      reference.setRef((String) refNode.getProperty("major_label"));
      reference.setValue((String) refNode.getProperty("entry"));
      reference.setType(ReferenceType.DATABASE);
      refs.add(reference);
    }
    return refs;
  }

  @Override
  public ExtendedMetaboliteSpecie getModelMetaboliteSpecieByByModelAndEntry(
      ExtendedMetabolicModelEntity model, String spiEntry) {
    String spiEntry_ = String.format("%s@%s", spiEntry, model.getEntry());
    Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
        .findNodesByLabelAndProperty(MetabolicModelLabel.MetaboliteSpecie, "entry", spiEntry_));

    if (node == null) {
      LOGGER.debug("Metabolite Specie [{}:{}] not found", MetabolicModelLabel.MetaboliteSpecie, spiEntry_);
      return null;
    }

    return this.getModelMetaboliteSpecieById(node.getId());
  }

  @Override
  public ExtendedMetaboliteSpecie saveModelMetaboliteSpecie(
      ExtendedMetabolicModelEntity mmd,
      ExtendedMetaboliteSpecie spi) {

    try {
      String entry = String.format("%s@%s", spi.getEntry(), mmd.getEntry());
      Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.MetaboliteSpecie, "entry", entry, executionEngine);
      Map<String, Object> properties = a.extractProperties(spi, ExtendedMetaboliteSpecie.class);
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
      e.printStackTrace();
      return null;
    }
    return spi;
  }

  @Override
  public Set<Long> getAllModelSpecieIds(
      ExtendedMetabolicModelEntity model) {
    Set<Long> res = new HashSet<> ();
    Node mmdNode = graphDatabaseService.getNodeById(model.getId());
    res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_specie));
    return res;
  }

  @Override
  public Set<String> getAllModelSpecieEntries(
      ExtendedMetabolicModelEntity model) {
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
    rxn.setCrossreferences(getCrossreferences(rxn.getId()));
    //		rxn.setRight(right);
    return rxn;
  }

  @Override
  public OptfluxContainerReactionEntity getModelReactionByByModelAndEntry(
      ExtendedMetabolicModelEntity model, String spiEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public OptfluxContainerReactionEntity saveModelReaction(
      ExtendedMetabolicModelEntity mmd,
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
      e.printStackTrace();
      return null;
    }

    return rxn;
  }
  
  @Override
  public boolean updateModelReaction(OptfluxContainerReactionEntity rxn) {
    if (rxn == null || rxn.getId() == null) {
      return false;
    }
    
    Node rxnNode = this.graphDatabaseService.getNodeById(rxn.getId());
    if (!rxnNode.hasLabel(MetabolicModelLabel.ModelReaction)) {
      return false;
    }
    
    rxnNode.setProperty("entityType", rxn.getEntityType().toString());
    
    return true;
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
      ExtendedMetabolicModelEntity model) {
    Set<Long> res = new HashSet<> ();
    Node mmdNode = graphDatabaseService.getNodeById(model.getId());
    res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_reaction));
    return res;
  }

  @Override
  public Set<String> getAllModelReactionEntries(
      ExtendedMetabolicModelEntity model) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExtendedModelMetabolite getModelMetaboliteById(Long id) {
    Node node = graphDatabaseService.getNodeById(id);
    if (node == null || !node.hasLabel(MetabolicModelLabel.ModelMetabolite)) {
      return null;
    }
    ExtendedModelMetabolite cpd = new ExtendedModelMetabolite();
    Neo4jMapper.nodeToPropertyContainer(node, cpd);
    cpd.setId(node.getId());
    cpd.setCrossreferences(getMetaboliteCrossreferences(node.getId()));
    return cpd;
  }

  @Override
  public ExtendedModelMetabolite getModelMetaboliteByModelAndEntry(
      ExtendedMetabolicModelEntity model, String spiEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ExtendedModelMetabolite saveModelMetabolite(
      ExtendedMetabolicModelEntity mmd,
      ExtendedModelMetabolite cpd) {

    if (cpd.getSpecies().isEmpty()) return null;

    Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());

    try {
      String entry = String.format("%s@%s", cpd.getEntry(), mmd.getEntry());
      Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.ModelMetabolite, "entry", entry, executionEngine);
      LOGGER.debug("Created {}", node);
      Map<String, Object> properties = a.extractProperties(cpd, ExtendedModelMetabolite.class);
      properties.remove("entry");
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      for (ExtendedMetaboliteSpecie spi : cpd.getSpecies()) {
        if (spi.getId() == null) this.saveModelMetaboliteSpecie(mmd, spi);
        Node spiNode = graphDatabaseService.getNodeById(spi.getId());
        node.createRelationshipTo(spiNode, MetabolicModelRelationshipType.has_specie);
      }

      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_metabolite);
      cpd.setId(node.getId());
    } catch (Exception e) {
      LOGGER.error("E - {}", e.getMessage());
      e.printStackTrace();
      return null;
    }

    return cpd;
  }

  @Override
  public Set<Long> getAllModelMetaboliteIds(
      ExtendedMetabolicModelEntity model) {
    Set<Long> res = new HashSet<> ();
    Node mmdNode = graphDatabaseService.getNodeById(model.getId());
    res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(mmdNode, MetabolicModelRelationshipType.has_metabolite));
    return res;
  }

  @Override
  public Set<String> getAllModelMetaboliteEntries(
      ExtendedMetabolicModelEntity model) {
    // TODO Auto-generated method stub

    return null;
  }

  @Override
  public void deleteModelMetabolite(ExtendedModelMetabolite mcpd) {
    Node cpdNode = graphDatabaseService.getNodeById(mcpd.getId());
    if (cpdNode.hasLabel(MetabolicModelLabel.ModelMetabolite)) {
      Neo4jUtils.deleteAllRelationships(cpdNode);
    }
    cpdNode.delete();
  }

}
