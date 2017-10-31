package pt.uminho.sysbio.biosynthframework;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class BiodbGraphDatabaseService implements GraphDatabaseService {

  private final GraphDatabaseService service;
  
  public BiodbGraphDatabaseService(GraphDatabaseService service) {
    this.service = service;
  }
  
  public Node getNodeByIdAndLabel(long id, Label label) {
    Node node = this.getNodeById(id);
    
    if (node != null && !node.hasLabel(label)) {
      return null;
    }
    
    return node;
  }
  
  public Node getMetabolite(String entry, MetaboliteMajorLabel database) {
    return this.findNode(database, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
  }
  
  public Node getMetaboliteProperty(String key, MetabolitePropertyLabel property) {
    return this.findNode(property, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, key);
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
  
  public Node getOrCreateMetabolite(String entry, MetaboliteMajorLabel database) {
    Node cpdNode = getMetabolite(entry, database);
    if (cpdNode == null) {
      cpdNode = this.createNode(GlobalLabel.Metabolite, database);
      cpdNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, database.toString());
      String e = fixEntry(entry, database);
      cpdNode.setProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, e);
      cpdNode.setProperty(Neo4jDefinitions.PROXY_PROPERTY, true);
    }
    return cpdNode;
  }
  
  public Node getOrCreateMetaboliteProperty(String key, MetabolitePropertyLabel property) {
    Node propNode = getMetaboliteProperty(key, property);
    if (propNode == null) {
      propNode = this.createNode(GlobalLabel.MetaboliteProperty, property);
      propNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, property.toString());
//      String e = fixEntry(entry, database);
      propNode.setProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, key);
    }
    return propNode;
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
    Neo4jUtils.setCreatedTimestamp(node);
    Neo4jUtils.setUpdatedTimestamp(node);
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
    return service.getNodeById(id);
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

  @Override
  public Node findNode(Label label, String key, Object value) {
    return service.findNode(label, key, value);
  }

  @Override
  public ResourceIterator<Node> findNodes(Label label) {
    return service.findNodes(label);
  }
  
  public Set<Node> listNodes(Label label) {
    return Iterators.asSet(service.findNodes(label));
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
