package pt.uminho.sysbio.biosynthframework;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.TraversalDescription;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
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
    return service.createNode();
  }

  @Override
  public Node createNode(Label... labels) {
    return service.createNode(labels);
  }

  @Override
  public Node getNodeById(long id) {
    return service.getNodeById(id);
  }

  @Override
  public Relationship getRelationshipById(long id) {
    return service.getRelationshipById(id);
  }

  @Deprecated
  @Override
  public Iterable<Node> getAllNodes() {
    return service.getAllNodes();
  }

  @Override
  public ResourceIterable<Node> findNodesByLabelAndProperty(Label label, String key, Object value) {
    return service.findNodesByLabelAndProperty(label, key, value);
  }

  @Deprecated
  @Override
  public Iterable<RelationshipType> getRelationshipTypes() {
    return service.getRelationshipTypes();
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

}
