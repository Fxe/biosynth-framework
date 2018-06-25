package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynthframework.ModelAdapter;
import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.SimpleCompartment;
import pt.uminho.sysbio.biosynthframework.SimpleModelReaction;
import pt.uminho.sysbio.biosynthframework.SimpleModelSpecie;

public class Neo4jModelAdapter implements ModelAdapter{

  private final BiosMetabolicModelNode modelNode;
  
  public Neo4jModelAdapter(BiosMetabolicModelNode modelNode) {
    this.modelNode = modelNode;
  }
  
  public Neo4jModelAdapter(Node node) {
    this(new BiosMetabolicModelNode(node, null));
  }
  
  public Neo4jModelAdapter(long modelId, GraphDatabaseService service) {
    this(service.getNodeById(modelId));
  }
  
  @Override
  public String getGpr(String mrxnEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isTranslocation(String mrxnEntry) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int getReactionSize(String mrxnEntry) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getSpecieCompartment(String spiEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Integer getSpecieDegree(String spiEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isDrain(String mrxnEntry) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isBoundarySpecie(String spiEntry) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Range getBounds(String mrxnEntry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleModelReaction<String> getReaction(String rxnId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleModelSpecie<String> getSpecies(String spiId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleCompartment<String> getCompartment(String cmpId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getReactionIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getSpeciesIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getCompartmentIds() {
    // TODO Auto-generated method stub
    return null;
  }

}
