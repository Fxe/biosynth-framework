package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.model.JsonModel;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlStreamSbmlReader;
import pt.uminho.sysbio.biosynthframework.util.GprUtils;

public class EtlModels extends AbstractNeo4jEtl {
  
  private static final Logger logger = LoggerFactory.getLogger(EtlModels.class);
  
  public EtlModels(GraphDatabaseService service) {
    super(service);
  }
  
  public static String standardizeGpr(String gpr) {
    return GprUtils.toLexicographicString(gpr).replaceAll(" & ", " and ").replaceAll(" \\| ", " or ");
  }
  
  public Node makeModelGeneNode(BiosMetabolicModelNode modelNode, String gene) {
    String key = gene + "@" + modelNode.getEntry();
    logger.info("[MERGE] ModelGene -> {}", key);
    Node geneNode = this.service.getOrCreateNode(MetabolicModelLabel.ModelGene, 
        Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, key);
    geneNode.setProperty("id", gene);
    return geneNode;
  }
  
  public Node makeGprNode(BiosMetabolicModelNode modelNode, Node rxnNode, String gpr) {
    Set<String> genes = new HashSet<>();
    String lgpr = "";
    try {
      genes = GprUtils.getVariables(gpr);
      lgpr = standardizeGpr(gpr);
    } catch (Exception e) {
      lgpr = e.getMessage();
    }
    
    logger.debug("Genes: {}", genes);
    String key = gpr + "@" + modelNode.getEntry();
    logger.info("[MERGE] ModelGPR -> {}", key);
    Node gprNode = this.service.getOrCreateNode(MetabolicModelLabel.ModelGPR, 
        Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, key);
    gprNode.setProperty("lexicographic_string", lgpr);
    Neo4jUtils.setUpdatedTimestamp(gprNode);
    if (!Neo4jUtils.exitsRelationshipBetween(rxnNode, gprNode, Direction.BOTH)) {
      logger.info("[LINK] {} -> {}", rxnNode, gprNode);
      rxnNode.createRelationshipTo(gprNode, MetabolicModelRelationshipType.has_gpr);
    }
    
    for (String g : genes) {
      Node geneNode = makeModelGeneNode(modelNode, g);
      if (!Neo4jUtils.exitsRelationshipBetween(geneNode, gprNode, Direction.BOTH)) {
        logger.info("[LINK] {} -> {}", gprNode, geneNode);
        gprNode.createRelationshipTo(geneNode, MetabolicModelRelationshipType.has_gpr_gene);
      }
      if (!Neo4jUtils.exitsRelationshipBetween(geneNode, modelNode, Direction.BOTH)) {
        logger.info("[LINK] {} -> {}", modelNode, geneNode);
        modelNode.createRelationshipTo(geneNode, MetabolicModelRelationshipType.has_gpr_gene);
      }
    }
//    rxn.createRelationshipTo(gprNode, MetabolicModelRelationshipType.has_gpr_gene);
    return gprNode;
  }
  
  public Long etlJsonModel(InputStream is, String entry) {
    Long modelId = null;
    
    try {
      JsonModel model = null;
      ObjectMapper om = new ObjectMapper();
      model = om.readValue(is, JsonModel.class);
      modelId = etlJsonModel(model, entry);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return modelId;
  }
  
  public Long etlJsonModel(JsonModel model, String entry) {
    JsonModelEtl etl = new JsonModelEtl(service);
    return etl.loadModel(model, entry);
  }
  
  public Long etlXml(InputStream is, String entry) {
    
    Long modelId = null;
    try {
      XmlStreamSbmlReader reader = new XmlStreamSbmlReader(is);
      XmlSbmlModel xmodel = reader.parse();
      modelId = TheStaticModelLoader.loadModel(xmodel, entry, service);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return modelId;
  }

  @Override
  public void etl(String e) {
    // TODO Auto-generated method stub
  }
}
