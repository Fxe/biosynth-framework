package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.Iterators;
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
import pt.uminho.sysbio.biosynthframework.ReferenceSource;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;
import pt.uminho.sysbio.biosynthframework.io.ExtendedMetabolicModelDao;

/**
 * Temporary DAO to manage metabolic model entities<br/>
 * OptfluxContainer - because they are based on the container instances
 * @author Filipe
 *
 */
public class Neo4jOptfluxContainerDaoImpl extends AbstractNeo4jDao implements ExtendedMetabolicModelDao {

  private final static Logger logger = LoggerFactory.getLogger(Neo4jOptfluxContainerDaoImpl.class);
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
        graphDatabaseService.listNodes(
            GlobalLabel.MetabolicModel, "entry", entry));

    if (node == null) {
      logger.debug("Metabolite Specie [{}:{}] not found", GlobalLabel.MetabolicModel, entry);
      return null;
    }

    return getMetabolicModelById(node.getId());
  }

  @Override
  public ExtendedMetabolicModelEntity saveMetabolicModel(
      ExtendedMetabolicModelEntity mmd) {

    try {
      Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.MetabolicModel, "entry", mmd.getEntry(), graphDatabaseService);
      Map<String, Object> properties = a.extractProperties(mmd, ExtendedMetabolicModelEntity.class);
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      mmd.setId(node.getId());
    } catch (Exception e) {
      logger.error("E - {}", e.getMessage());
      e.printStackTrace();
      return null;
    }

    return mmd;
  }

  @Override
  public Set<Long> getAllMetabolicModelIds() {
    Set<Long> res = new HashSet<> ();
    for (Node node : graphDatabaseService
        .listNodes(GlobalLabel.MetabolicModel)) {
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
    logger.trace("Query: {}", query);
    //		System.out.println(query);
    for (Object o : Iterators.asList(graphDatabaseService.execute(query).columnAs("n"))) {
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
  
  public Node getGcmpNode(SubcellularCompartment gcmp) {
    Node gcmpNode = Neo4jUtils.getUniqueResult(
        graphDatabaseService.listNodes(
            GlobalLabel.SubcellularCompartment, "entry", gcmp.toString()));
    if (gcmpNode == null) {
      gcmpNode = graphDatabaseService.createNode();
      gcmpNode.addLabel(GlobalLabel.SubcellularCompartment);
      Neo4jUtils.setCreatedTimestamp(gcmpNode);
      Neo4jUtils.setUpdatedTimestamp(gcmpNode);
      logger.warn("CREATE [V] {} -> {} [{}]{}", gcmp, gcmpNode.getId(), Neo4jUtils.getLabels(gcmpNode), Neo4jUtils.getPropertiesMap(gcmpNode));
    }
    
    return gcmpNode;
  }
  
  @Override
  public void updateCompartment(DefaultSubcellularCompartmentEntity cmp) {
    if (cmp == null || cmp.getId() == null) {
      logger.trace("invalid SubcellularCompartment {}", cmp);
      return;
    }
    
    Node node = graphDatabaseService.getNodeById(cmp.getId());;
    
    if (node == null || !node.hasLabel(GlobalLabel.SubcellularCompartment)) {
      logger.trace("{} not a SubcellularCompartment", node);
      return;
    }
    
    Relationship linkToGcmp = node.getSingleRelationship(
        MetabolicModelRelationshipType.has_crossreference_to, Direction.BOTH);
    
//    Node gcmp = Neo4jUtils.getSingleRelationshipNode(
//        node, MetabolicModelRelationshipType.has_crossreference_to);
    SubcellularCompartment actual = cmp.getCompartment();    
    if (actual == null) {
      actual = SubcellularCompartment.UNKNOWN;
    }
    Node actualGcmpNode = getGcmpNode(actual);
    
    //no link -> create new
    if (linkToGcmp == null) {
      Relationship r = node.createRelationshipTo(
          actualGcmpNode, MetabolicModelRelationshipType.has_crossreference_to);
      logger.debug("CREATE [E] [{}] -[{}]-> [{}]",
          r.getStartNode().getId(),
          r.getType().name(),
          r.getEndNode().getId());
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
    //exist link check if is equal
    } else {
      Node gcmp = linkToGcmp.getOtherNode(node);
      SubcellularCompartment previous = SubcellularCompartment.valueOf(
          (String) gcmp.getProperty("entry"));
      if (!previous.equals(actual)) {
        logger.debug("DELETE [E] [{}] -[{}]-> [{}]",
            linkToGcmp.getStartNode().getId(),
            linkToGcmp.getType().name(),
            linkToGcmp.getEndNode().getId());
        linkToGcmp.delete();
        
        Relationship r = node.createRelationshipTo(
            actualGcmpNode, MetabolicModelRelationshipType.has_crossreference_to);
        logger.debug("CREATE [E] [{}] -[{}]-> [{}]",
            r.getStartNode().getId(),
            r.getType().name(),
            r.getEndNode().getId());
        Neo4jUtils.setCreatedTimestamp(r);
        Neo4jUtils.setUpdatedTimestamp(r);
      }
    }
  }

  @Override
  public DefaultSubcellularCompartmentEntity getCompartmentById(Long id) {
    Node node = graphDatabaseService.getNodeById(id);
    
    if (node == null || !node.hasLabel(GlobalLabel.SubcellularCompartment)) {
      logger.trace("{} not a SubcellularCompartment", node);
      return null;
    }
    
    DefaultSubcellularCompartmentEntity cmp = Neo4jMapper.nodeToSubcellularCompartment(node);
    SubcellularCompartment compartment = SubcellularCompartment.UNKNOWN;
    //get generic compartment
    Relationship r = null; 
    for (Relationship r_ : node.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r != null) {
        logger.warn("Error multiple compartment annotations [{}] -> [{}]", 
            r.getId(), r_.getId());
      }
      r = r_;
    }
    
    if (r != null) {
      Node gcmp = r.getOtherNode(node);
      compartment = 
          SubcellularCompartment.valueOf((String) gcmp.getProperty("entry"));
    }
    
    cmp.setCompartment(compartment);
    return cmp;
  }

  @Override
  public DefaultSubcellularCompartmentEntity getCompartmentByModelAndEntry(
      ExtendedMetabolicModelEntity model, String cmpEntry) {
    if (!cmpEntry.contains("@")) {
      cmpEntry = String.format("%s@%s", cmpEntry, model.getEntry());
    }
    
    Node cmpNode = Neo4jUtils.getUniqueResult(
        graphDatabaseService.listNodes(GlobalLabel.SubcellularCompartment, 
                                     "entry", cmpEntry));
    return this.getCompartmentById(cmpNode.getId());
  }

  @Override
  public DefaultSubcellularCompartmentEntity saveCompartment(
      ExtendedMetabolicModelEntity mmd,
      DefaultSubcellularCompartmentEntity cmp) {

    try {
      String entry = String.format("%s@%s", cmp.getEntry(), mmd.getEntry());
      Node node = Neo4jUtils.getOrCreateNode(GlobalLabel.SubcellularCompartment, "entry", entry, graphDatabaseService);
      Map<String, Object> properties = a.extractProperties(cmp, DefaultSubcellularCompartmentEntity.class);
      properties.remove("entry");
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());
      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_compartment);
    } catch (Exception e) {
      logger.error("E - {}", e.getMessage());
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
    Node spiNode = graphDatabaseService.getNodeById(id);
    if (spiNode == null || !spiNode.hasLabel(MetabolicModelLabel.MetaboliteSpecie)) {
      return null;
    }
    ExtendedMetaboliteSpecie spi = new ExtendedMetaboliteSpecie();
    Neo4jMapper.nodeToPropertyContainer(spiNode, spi);
    spi.setId(spiNode.getId());
    spi.setCrossreferences(getMetaboliteCrossreferences(spi.getId()));
    spi.setComparment((String) spiNode.getProperty("comparment", null));
    
    int reactionDegree = Neo4jUtils.collectNodeRelationshipNodeIds(spiNode, 
        MetabolicModelRelationshipType.left_component,
        MetabolicModelRelationshipType.right_component).size();
    
    spi.setReactionDegree(reactionDegree);
    
    return spi;
  }

  public List<DefaultMetaboliteSpecieReference> getMetaboliteCrossreferences(Long id) {
    List<DefaultMetaboliteSpecieReference> refs = new ArrayList<> ();
    Node mcpdNode = graphDatabaseService.getNodeById(id);
    for (Node cpdNode : Neo4jUtils.collectNodeRelationshipNodes(
        mcpdNode, MetabolicModelRelationshipType.has_crossreference_to)) {
      
      //accept only has_crossreference_to links to Metabolite
      if (cpdNode.hasLabel(GlobalLabel.Metabolite)) {
        DefaultMetaboliteSpecieReference reference = new DefaultMetaboliteSpecieReference();
        reference.setId(cpdNode.getId());
        reference.setRef((String) cpdNode.getProperty("major_label"));
        reference.setValue((String) cpdNode.getProperty("entry"));
        reference.setType(ReferenceType.DATABASE);
        reference.setSource(ReferenceSource.UNKNOWN);
        refs.add(reference);
      } else {
        logger.warn("ModelMetabolite -[has_crossreference_to]-> {}", Neo4jUtils.getLabels(cpdNode));
      }
    }
    
    for (Node spiNode : Neo4jUtils.collectNodeRelationshipNodes(
        mcpdNode, MetabolicModelRelationshipType.has_specie)) {
      for (Node cpdNode : Neo4jUtils.collectNodeRelationshipNodes(
          spiNode, MetabolicModelRelationshipType.has_crossreference_to)) {
        //accept only has_crossreference_to links to Metabolite
        if (cpdNode.hasLabel(GlobalLabel.Metabolite)) {
          DefaultMetaboliteSpecieReference reference = new DefaultMetaboliteSpecieReference();
          reference.setId(cpdNode.getId());
          reference.setRef((String) cpdNode.getProperty("major_label"));
          reference.setValue((String) cpdNode.getProperty("entry"));
          reference.setType(ReferenceType.DATABASE);
          reference.setSource(ReferenceSource.INHERITED);
          refs.add(reference);
        } else {
          logger.warn("ModelSpecie -[has_crossreference_to]-> {}", Neo4jUtils.getLabels(cpdNode));
        }
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
    
    if (!spiEntry.contains("@")) {
      spiEntry = String.format("%s@%s", spiEntry, model.getEntry());
    }
    
    Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
        .listNodes(MetabolicModelLabel.MetaboliteSpecie, "entry", spiEntry));

    if (node == null) {
      logger.debug("Metabolite Specie [{}:{}] not found", MetabolicModelLabel.MetaboliteSpecie, spiEntry);
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
      Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.MetaboliteSpecie, "entry", entry, graphDatabaseService);
      Map<String, Object> properties = a.extractProperties(spi, ExtendedMetaboliteSpecie.class);
      properties.remove("entry");
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());
      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_specie);

      String cmpEntry = String.format("%s@%s", spi.getComparment(), mmd.getEntry());
      Node cmpNode = Neo4jUtils.getUniqueResult(graphDatabaseService
          .listNodes(GlobalLabel.SubcellularCompartment, "entry", cmpEntry));
      node.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.in_compartment);
    } catch (Exception e) {
      logger.error("E - {}", e.getMessage());
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
      ExtendedMetabolicModelEntity model, String rxnEntry) {
    
    if (!rxnEntry.contains("@")) {
      rxnEntry = String.format("%s@%s", rxnEntry, model.getEntry());
    }
    
    Node node = Neo4jUtils.getUniqueResult(graphDatabaseService
        .listNodes(MetabolicModelLabel.ModelReaction, "entry", rxnEntry));

    if (node == null) {
      logger.debug("Metabolite Specie [{}:{}] not found", MetabolicModelLabel.ModelReaction, rxnEntry);
      return null;
    }
    
    return this.getModelReactionById(node.getId());
  }

  @Override
  public OptfluxContainerReactionEntity saveModelReaction(
      ExtendedMetabolicModelEntity mmd,
      OptfluxContainerReactionEntity rxn) {

    try {
      String entry = String.format("%s@%s", rxn.getEntry(), mmd.getEntry());
      Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.ModelReaction, "entry", entry, graphDatabaseService);
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
      logger.error("E - {}", e.getMessage());
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
        .listNodes(MetabolicModelLabel.MetaboliteSpecie, "entry", spiEntry_));

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
    
    for (Node spiNode : Neo4jUtils.collectNodeRelationshipNodes(
        node, MetabolicModelRelationshipType.has_specie)) {
      cpd.getSpecies().put(spiNode.getId(), this.getModelMetaboliteSpecieById(spiNode.getId()));
    }
    return cpd;
  }

  @Override
  public ExtendedModelMetabolite getModelMetaboliteByModelAndEntry(
      ExtendedMetabolicModelEntity model, String spiEntry) {
    if (!spiEntry.contains("@")) {
      spiEntry = String.format("%s@%s", spiEntry, model.getEntry());
    }
    
    Node cpdNode = Neo4jUtils.getUniqueResult(graphDatabaseService
        .listNodes(MetabolicModelLabel.ModelMetabolite, 
                                     "entry", spiEntry));
    return this.getModelMetaboliteById(cpdNode.getId());
  }

  @Override
  public ExtendedModelMetabolite saveModelMetabolite(
      ExtendedMetabolicModelEntity mmd,
      ExtendedModelMetabolite cpd) {

    if (cpd.getSpecies().isEmpty()) return null;

    Node mmdNode = graphDatabaseService.getNodeById(mmd.getId());

    try {
      String entry = String.format("%s@%s", cpd.getEntry(), mmd.getEntry());
      Node node = Neo4jUtils.getOrCreateNode(MetabolicModelLabel.ModelMetabolite, "entry", entry, graphDatabaseService);
      logger.debug("Created {}", node);
      Map<String, Object> properties = a.extractProperties(cpd, ExtendedModelMetabolite.class);
      properties.remove("entry");
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);

      for (ExtendedMetaboliteSpecie spi : cpd.getSpecies().values()) {
        if (spi.getId() == null) this.saveModelMetaboliteSpecie(mmd, spi);
        Node spiNode = graphDatabaseService.getNodeById(spi.getId());
        node.createRelationshipTo(spiNode, MetabolicModelRelationshipType.has_specie);
      }

      mmdNode.createRelationshipTo(node, MetabolicModelRelationshipType.has_metabolite);
      cpd.setId(node.getId());
    } catch (Exception e) {
      logger.error("E - {}", e.getMessage());
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

  @Override
  public Set<Long> getAllModelSubsystemsIds(ExtendedMetabolicModelEntity model) {
    Set<Long> res = new HashSet<> ();
    Node mmdNode = graphDatabaseService.getNodeById(model.getId());
    res.addAll(Neo4jUtils.collectNodeRelationshipNodeIds(
        mmdNode, MetabolicModelRelationshipType.has_subsystem));
    return res;
  }

}
