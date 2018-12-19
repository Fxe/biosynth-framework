package pt.uminho.sysbio.biosynthframework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.QueryExecutionException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.collection.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosGenomeNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosTaxonomyNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalCompartmentNode;
import pt.uminho.sysbio.biosynthframework.neo4j.GenomeDatabase;

public class BiodbGraphDatabaseService implements GraphDatabaseService {

  private static final Logger logger = LoggerFactory.getLogger(BiodbGraphDatabaseService.class);

  private final GraphDatabaseService service;
  public String databasePath;
  
  public String getDatabasePath() {
    return databasePath;
  }
  
  private Node createUniversalCompartmentNode(SubcellularCompartment compartment) {
    String symbol = null;
    switch (compartment) {
      case CYTOSOL: symbol = "c"; break;
      case EXTRACELLULAR: symbol = "e"; break;
      case BOUNDARY: symbol = "b"; break;
      case PERIPLASM: symbol = "p"; break;
      case MITOCHONDRIA: symbol = "m"; break;
      case NUCLEUS: symbol = "n"; break;
      case GOLGI: symbol = "g"; break;
      case RETICULUM: symbol = "r"; break;
      case VACUOLE: symbol = "v"; break;
      case PEROXISOME: symbol = "x"; break;
      default:
        break;
    }
    
    if (symbol == null) {
      return null;
    }
    
    logger.warn("Generate node for SubcellularCompartment: {}", compartment);
    
    Node ucmpNode = this.createNode(CurationLabel.UniversalCompartment);
    ucmpNode.setProperty("bios_scmp", compartment.toString());
    ucmpNode.setProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, symbol);
    Neo4jUtils.setTimestamps(ucmpNode);
    return ucmpNode;
  }
  
  public BiosUniversalCompartmentNode getUniversalCompartment(SubcellularCompartment compartment) {
    Node n = Neo4jUtils.getUniqueResult(
        this.findNodes(CurationLabel.UniversalCompartment, "bios_scmp", compartment.toString()));
    if (n != null) {
      return new BiosUniversalCompartmentNode(n, databasePath);
    }
    
    n = createUniversalCompartmentNode(compartment);
    if (n != null) {
      return new BiosUniversalCompartmentNode(n, databasePath);
    } else {
      logger.warn("failed to generate UniversalCompartment for: {}", compartment);
      return null;
    }
  }

  public void setDatabasePath(String databasePath) {
    logger.info("setting database edata path to: {}", databasePath);
    this.databasePath = databasePath;
  }

  public BiodbGraphDatabaseService(GraphDatabaseService service) {
    this.service = service;
    if (service instanceof BiodbGraphDatabaseService) {
      this.databasePath = ((BiodbGraphDatabaseService)service).databasePath;
    }
  }
  
  public void exportExternalProperties(Node node, Map<String, Object> properties) {
    if (databasePath != null) {
      ObjectMapper om = new ObjectMapper();
      File dataFile = new File(this.databasePath + "/" + node.getId() + ".json");
      try (OutputStream os = new FileOutputStream(dataFile)) {
        om.writeValue(os, properties);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.warn("database path is not set to store external properties");
    }
  }
  
  public static Node merge(Node a, Node b) {
    Set<Long> inc = new HashSet<>();
    Set<Long> out = new HashSet<>();
    Map<Long, RelationshipType> t = new HashMap<>();
    Map<Long, Map<String, Object>> p = new HashMap<>();
    Map<Long, Node> o = new HashMap<>();
    
    for (Relationship r : b.getRelationships(Direction.INCOMING)) {
      logger.debug("<- {}, {}, {}", r.getId(), r.getType(), r.getAllProperties());
      inc.add(r.getId());
      t.put(r.getId(), r.getType());
      p.put(r.getId(), r.getAllProperties());
      o.put(r.getId(), r.getOtherNode(b));
      r.delete();
    }
    
    for (Relationship r : b.getRelationships(Direction.OUTGOING)) {
      logger.debug("-> {}, {}, {}", r.getId(), r.getType(), r.getAllProperties());
      out.add(r.getId());
      t.put(r.getId(), r.getType());
      p.put(r.getId(), r.getAllProperties());
      o.put(r.getId(), r.getOtherNode(b));
      r.delete();
    }
    
    for (long i : inc) {
      Relationship r = o.get(i).createRelationshipTo(a, t.get(i));
      Neo4jUtils.setPropertiesMap(p.get(i), r);
    }
    for (long i : out) {
      Relationship r = a.createRelationshipTo(o.get(i), t.get(i));
      Neo4jUtils.setPropertiesMap(p.get(i), r);
    }
    b.delete();
    
    logger.info("moved {} relationships", t.size());
    
    return a;
  }

  public Node getNodeByIdAndLabel(long id, Label label) {
    Node node = this.getNodeById(id);

    if (node != null && !node.hasLabel(label)) {
      return null;
    }

    return node;
  }

  public BiodbEntityNode getEntityNode(String entry, Label label) {
    Node node = this.findNode(label, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
    BiodbEntityNode entity = null;
    if (node != null) {
      entity = new BiodbEntityNode(node, databasePath);
    }

    return entity;
  }

  public BiodbMetaboliteNode getMetabolite(String entry, MetaboliteMajorLabel database) {
    Node node = this.findNode(database, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);;
    if (node != null && node.hasLabel(GlobalLabel.Metabolite)) {
      return new BiodbMetaboliteNode(node, databasePath);
    }
    return null;
  }

  public BiodbReactionNode getReaction(String entry, ReactionMajorLabel database) {
    Node node = this.findNode(database, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);;
    if (node != null && node.hasLabel(GlobalLabel.Reaction)) {
      return new BiodbReactionNode(node, databasePath);
    }
    return null;
  }

  public Map<ExternalReference, Long> translate(Collection<ExternalReference> refs) {
    Map<ExternalReference, Long> result = new HashMap<> ();
    for (ExternalReference ref : refs) {
      result.put(ref, null);
      try {
        MetaboliteMajorLabel database = MetaboliteMajorLabel.valueOf(ref.source);
        Node node = getMetabolite(ref.entry, database);
        if (node != null) {
          result.put(ref, node.getId());
        }
      } catch (Exception e) {
        logger.debug("{} not a MetaboliteMajorLabel", ref);
      }
      try {
        ReactionMajorLabel database = ReactionMajorLabel.valueOf(ref.source);
        Node node = getReaction(ref.entry, database);
        if (node != null) {
          result.put(ref, node.getId());
        }
      } catch (Exception e) {
        logger.debug("{} not a ReactionMajorLabel", ref);
      }
    }
    return result;
  }

  public BiosTaxonomyNode getTaxonomy(String ncbiTax) {
    Node node = this.getNodeByEntryAndLabel(ncbiTax, GlobalLabel.NcbiTaxonomy);
    if (node != null) {
      return getTaxonomy(node.getId());
    }
    return null;
  }
  
  public BiosTaxonomyNode getTaxonomy(long id) {
    Node node = this.getNodeById(id);
    BiosTaxonomyNode bnode = null;
    if (node != null) {
      bnode = new BiosTaxonomyNode(node, databasePath);
    }
    return bnode;
  }
  
  public BiosGenomeNode getGenome(long id) {
    Node node = this.getNodeById(id);
    BiosGenomeNode genomeNode = null;
    if (node != null) {
      genomeNode = new BiosGenomeNode(node, databasePath);
    }
    return genomeNode;
  }
  
  public BiosMetabolicModelNode getMetabolicModel(String entry) {
    Node node = this.getEntityNode(entry, GlobalLabel.MetabolicModel);
    BiosMetabolicModelNode modelNode = null;
    if (node != null) {
      modelNode = new BiosMetabolicModelNode(node, databasePath);
    }
    return modelNode;
  }
  
  public BiosMetabolicModelNode getMetabolicModel(long id) {
    Node node = this.getNodeById(id);
    BiosMetabolicModelNode modelNode = null;
    if (node != null) {
      modelNode = new BiosMetabolicModelNode(node, databasePath);
    }
    return modelNode;
  }

  public BiodbMetaboliteNode getMetabolite(ExternalReference ref) {
    return getMetabolite(ref.entry, MetaboliteMajorLabel.valueOf(ref.source));
  }

  public BiodbMetaboliteNode getMetabolite(long id) {
    return new BiodbMetaboliteNode(this.getNodeById(id), databasePath);
  }

  public BiodbReactionNode getReaction(long id) {
    return new BiodbReactionNode(this.getNodeById(id), databasePath);
  }

  public BiodbReactionNode getReaction(ExternalReference ref) {
    return getReaction(ref.entry, ReactionMajorLabel.valueOf(ref.source));
  }

  public BiodbPropertyNode getMetaboliteProperty(String key, MetabolitePropertyLabel property) {
    Node n = this.findNode(property, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, key);
    if (n == null) {
      return null;
    }
    return new BiodbPropertyNode(n, databasePath);
  }

  public static String fixEntry(String entry, MetaboliteMajorLabel database) {
    switch (database) {
    case ChEBI:
      return entry.startsWith("CHEBI:") ? entry : "CHEBI:".concat(entry);
    default:
      break;
    }

    return entry;
  }

  public boolean exists(MetaboliteMajorLabel db, String entry) {
    return getMetabolite(entry, db) != null;
  }

  public boolean exists(MetabolitePropertyLabel prop, String value) {
    return getMetaboliteProperty(value, prop) != null;
  }

  public Node getOrCreateNode(Label label, String key, Object value) {
    return getOrCreateNode(label, key, value, false);
  }

  public Node getOrCreateNode(Label label, String key, Object value, boolean proxy) {
    //    this.execute(query, parameters)
    Node node = this.findNode(label, key, value);
    if (node == null) {
      node = this.createNode(label);
      node.setProperty(key, value);
      node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, label.toString());
      if (proxy) {
        node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, true);
      }
    }

    return node;
  }

  public BiodbMetaboliteNode getOrCreateMetabolite(String entry, MetaboliteMajorLabel database) {
    Node cpdNode = getMetabolite(entry, database);
    if (cpdNode == null) {
      cpdNode = this.createNode(GlobalLabel.Metabolite, database);
      cpdNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, database.toString());
      String e = fixEntry(entry, database);
      cpdNode.setProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, e);
      cpdNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, true);
    }
    return new BiodbMetaboliteNode(cpdNode, databasePath);
  }


  public BiodbPropertyNode getOrCreateMetaboliteProperty(String key, MetabolitePropertyLabel property) {
    Node propNode = getMetaboliteProperty(key, property);
    if (propNode == null) {
      propNode = this.createNode(GlobalLabel.MetaboliteProperty, property);
      propNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, property.toString());
      //      String e = fixEntry(entry, database);
      propNode.setProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, key);
    }
    return new BiodbPropertyNode(propNode, databasePath);
  }

  public Node getNodeByEntryAndLabel(String entry, Label label) {
    return Neo4jUtils.getNodeByEntry(label, entry, service);
  }

  public Node getModelNodeById(long id) {
    return getNodeByIdAndLabel(id, GlobalLabel.MetabolicModel);
  }

  public Node getModelNodeByEntry(String entry) {
    return getNodeByEntryAndLabel(entry, GlobalLabel.MetabolicModel);
  }

  @Override
  public Node createNode() {
    Node node = service.createNode();
    Neo4jUtils.setTimestamps(node);
    return node;
  }

  @Override
  public Node createNode(Label... labels) {
    Node node = this.createNode();
    for (Label l : labels) {
      node.addLabel(l);
    }

    return node;
  }

  @Override
  public Long createNodeId() {
    Node node = service.createNode();
    return node.getId();
  }

  @Override
  public Node getNodeById(long id) {
    Node node = service.getNodeById(id);
    return resolveNode(node);
  }

  @Override
  public Relationship getRelationshipById(long id) {
    return service.getRelationshipById(id);
  }

  @Override
  public boolean isAvailable(long timeout) {
    return service.isAvailable(timeout);
  }

  @Override
  public void shutdown() {
    service.shutdown();
  }

  @Override
  public Transaction beginTx() {
    return service.beginTx();
  }

  @Override
  public <T> TransactionEventHandler<T> registerTransactionEventHandler(TransactionEventHandler<T> handler) {
    return service.registerTransactionEventHandler(handler);
  }

  @Override
  public <T> TransactionEventHandler<T> unregisterTransactionEventHandler(TransactionEventHandler<T> handler) {
    return service.unregisterTransactionEventHandler(handler);
  }

  @Override
  public KernelEventHandler registerKernelEventHandler(KernelEventHandler handler) {
    return service.registerKernelEventHandler(handler);
  }

  @Override
  public KernelEventHandler unregisterKernelEventHandler(KernelEventHandler handler) {
    return service.unregisterKernelEventHandler(handler);
  }

  @Override
  public Schema schema() {
    return service.schema();
  }

  @Override
  public IndexManager index() {
    return service.index();
  }

  @Override
  public TraversalDescription traversalDescription() {
    return service.traversalDescription();
  }

  @Override
  public BidirectionalTraversalDescription bidirectionalTraversalDescription() {
    return service.bidirectionalTraversalDescription();
  }



  @Override
  public ResourceIterable<Node> getAllNodes() {
    return service.getAllNodes();
  }

  @Override
  public ResourceIterable<Relationship> getAllRelationships() {
    return service.getAllRelationships();
  }

  @Override
  public ResourceIterator<Node> findNodes(Label label, String key, Object value) {
    return service.findNodes(label, key, value);
  }
  
  public Node resolveNode(Node n) {
    if (n == null) {
      return n;
    }
    
    if (n.hasLabel(GlobalLabel.Metabolite)) {
      return new BiodbMetaboliteNode(n, databasePath);
    }
    if (n.hasLabel(GlobalLabel.Reaction)) {
      return new BiodbReactionNode(n, databasePath);
    }
    if (n.hasLabel(GlobalLabel.EXTERNAL_DATA)) {
      return new BiosExternalDataNode(n, databasePath);
    }
    
    return n;
  }

  @Override
  public Node findNode(Label label, String key, Object value) {
    return resolveNode(service.findNode(label, key, value));
  }

  @Override
  public ResourceIterator<Node> findNodes(Label label) {
    return service.findNodes(label);
  }

  public Set<Node> listNodes(Label label) {
    return Iterators.asSet(service.findNodes(label));
  }

  public Set<BiodbMetaboliteNode> listMetabolites() {
    Set<BiodbMetaboliteNode> result = new HashSet<> ();
    for (MetaboliteMajorLabel db : MetaboliteMajorLabel.values()) {
      result.addAll(listMetabolites(db));
    }

    return result;
  }

  public Set<BiodbMetaboliteNode> listMetabolites(MetaboliteMajorLabel database) {
    Set<BiodbMetaboliteNode> result = new HashSet<> ();
    for (Node node : listNodes(database)) {
      if (node.hasLabel(GlobalLabel.Metabolite)) {
        result.add(new BiodbMetaboliteNode(node, databasePath));
      }
    }
    return result;
  }
  
  public Set<BiodbPropertyNode> listMetaboliteProperties(MetabolitePropertyLabel property) {
    Set<BiodbPropertyNode> result = new HashSet<> ();
    for (Node node : listNodes(property)) {
      if (node.hasLabel(GlobalLabel.MetaboliteProperty)) {
        result.add(new BiodbPropertyNode(node, databasePath));
      }
    }
    return result;
  }

  public Set<BiodbReactionNode> listReactions() {
    Set<BiodbReactionNode> result = new HashSet<> ();
    for (ReactionMajorLabel db : ReactionMajorLabel.values()) {
      result.addAll(listReactions(db));
    }

    return result;
  }

  public Set<BiodbReactionNode> listReactions(ReactionMajorLabel database) {
    Set<BiodbReactionNode> result = new HashSet<> ();
    for (Node node : listNodes(database)) {
      if (node.hasLabel(GlobalLabel.Reaction)) {
        result.add(new BiodbReactionNode(node, databasePath));
      }
    }
    return result;
  }
  
  public Set<BiosGenomeNode> listGenomes(GenomeDatabase database) {
    Set<BiosGenomeNode> result = new HashSet<> ();
    for (Node node : listNodes(database)) {
//      if (node.hasLabel(GlobalLabel.Reaction)) {
        result.add(new BiosGenomeNode(node, databasePath));
//      }
    }
    return result;
  }
  
  public Set<BiosMetabolicModelNode> listMetabolicModels() {
    Set<BiosMetabolicModelNode> result = new HashSet<> ();
    for (Node node : listNodes(GlobalLabel.MetabolicModel)) {
      result.add(new BiosMetabolicModelNode(node, databasePath));
    }

    return result;
  }

  public Set<Node> listNodes(Label label, String key, Object value) {
    return Iterators.asSet(service.findNodes(label, key, value));
  }

  @Override
  public ResourceIterable<Label> getAllLabelsInUse() {
    return service.getAllLabelsInUse();
  }

  @Override
  public ResourceIterable<RelationshipType> getAllRelationshipTypesInUse() {
    return service.getAllRelationshipTypesInUse();
  }

  @Override
  public ResourceIterable<Label> getAllLabels() {
    return service.getAllLabels();
  }

  @Override
  public ResourceIterable<RelationshipType> getAllRelationshipTypes() {
    return service.getAllRelationshipTypes();
  }

  @Override
  public ResourceIterable<String> getAllPropertyKeys() {
    return service.getAllPropertyKeys();
  }

  @Override
  public Transaction beginTx(long timeout, TimeUnit unit) {
    return service.beginTx();
  }

  @Override
  public Result execute(String query) throws QueryExecutionException {
    return service.execute(query);
  }

  @Override
  public Result execute(String query, long timeout, TimeUnit unit) throws QueryExecutionException {
    return service.execute(query, timeout, unit);
  }

  @Override
  public Result execute(String query, Map<String, Object> parameters) throws QueryExecutionException {
    return service.execute(query, parameters);
  }

  @Override
  public Result execute(String query, Map<String, Object> parameters, long timeout, TimeUnit unit)
      throws QueryExecutionException {
    return service.execute(query, parameters, timeout, unit);
  }

}
