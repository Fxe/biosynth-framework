package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynthframework.ModelAdapter;
import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.SimpleCompartment;
import pt.uminho.sysbio.biosynthframework.SimpleModelReaction;
import pt.uminho.sysbio.biosynthframework.SimpleModelSpecie;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jModelAdapter implements ModelAdapter{

  private final BiosMetabolicModelNode modelNode;
  
  private Map<String, BiodbEntityNode>  cmpNodes = new HashMap<>();
  private Map<String, BiosModelSpeciesNode>  spiNodes = new HashMap<>();
  private Map<String, BiosModelReactionNode> mrxnNodes = new HashMap<>();
  
  public Neo4jModelAdapter(BiosMetabolicModelNode modelNode) {
    this.modelNode = modelNode;
    for (BiodbEntityNode n : this.modelNode.getModelCompartments()) {
      String sid = (String) n.getProperty("id", null);
      cmpNodes.put(sid, n);
    }
    for (BiosModelSpeciesNode n : this.modelNode.getMetaboliteSpecies()) {
      spiNodes.put(n.getSid(), n);
    }
    for (BiosModelReactionNode n : this.modelNode.getModelReactions()) {
      mrxnNodes.put(n.getSid(), n);
    }
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
    BiosModelReactionNode mrxnNode = this.mrxnNodes.get(rxnId);
    if (mrxnNode == null) {
      return null;
    }
    
    SimpleModelReaction<String> mrxn = new SimpleModelReaction<String>(rxnId, 0, 0);
    //TODO: !!
    return mrxn;
  }

  @Override
  public SimpleModelSpecie<String> getSpecies(String spiId) {
    BiosModelSpeciesNode spiNode = this.spiNodes.get(spiId);
    if (spiNode == null) {
      return null;
    }
    String name = (String) spiNode.getProperty("name", null);
    String cmpSid = spiNode.getCompartmentSid();
    SimpleModelSpecie<String> spi = new SimpleModelSpecie<String>(spiId, name, cmpSid);
    return spi;
  }

  @Override
  public SimpleCompartment<String> getCompartment(String cmpId) {
    BiodbEntityNode cmpNode = this.cmpNodes.get(cmpId);
    if (cmpNode == null) {
      return null;
    }
    SimpleCompartment<String> cmp = new SimpleCompartment<String>(cmpId);
    cmp.name = (String) cmpNode.getProperty("name", null);
    return cmp;
  }

  @Override
  public Set<String> getReactionIds() {
    return new HashSet<>(this.mrxnNodes.keySet());
  }
  
  @Override
  public Set<String> getSpeciesIds() {
    return new HashSet<>(this.spiNodes.keySet());
  }

  @Override
  public Set<String> getCompartmentIds() {
    return new HashSet<>(this.cmpNodes.keySet());
  }

}
