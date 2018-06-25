package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;

public class BiodbPropertyNode extends BiosExternalDataNode {

  private static final Logger logger = LoggerFactory.getLogger(BiodbPropertyNode.class);
  
  public BiodbPropertyNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public Relationship connectToProperty(BiodbPropertyNode other, RelationshipType t) {
    if (other == null) {
      return null;
    }
    
    Relationship r = null;
    if (!Neo4jUtils.exitsRelationshipBetween(this, other, Direction.BOTH)) {
      logger.debug("[{}] -[{}]-> [{}]", this.getValue(), t, other.getValue());
      r = this.createRelationshipTo(other, t);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
    } else {
      for (Relationship relationship : this.getRelationships(t)) {
        if (relationship.getOtherNode(this).getId() == other.getId()) {
          r = relationship;
        }
      }
    }
    
    return r;
  }

  public String getValue() {
    if (this.hasLabel(GlobalLabel.EXTERNAL_DATA)) {
      logger.trace("Node[{}] EXTERNAL DATA !", this.getId());
      
      if (databasePath == null) {
        logger.warn("Unable to load external data. [databasePath] must be assigned");
        return "ERROR_DATABASE_PATH_NOT_ASSIGNED";
      }
      File dataFile = new File(this.databasePath + "/edata/" + this.getId() + ".json");
      logger.trace("Node[{}] EXTERNAL DATA ! {}", this.getId(), dataFile);
      if (!dataFile.exists() || !dataFile.isFile()) {
        logger.warn("Unable to load external data. File not found: {}", dataFile);
        return "ERROR_FILE_NOT_FOUND";
      }
      loadExternalData();
      if (eproperties == null) {
        return "ERROR_PARSE_FILE";
      }
    }
    return (String) getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
  }

  public Set<BiodbMetaboliteNode> getMetabolites(MetaboliteRelationshipType hasName) {
    Set<BiodbMetaboliteNode> cpdNodes = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, MetaboliteRelationshipType.has_name)) {
      if (n.hasLabel(GlobalLabel.Metabolite)) {
        cpdNodes.add(new BiodbMetaboliteNode(n, databasePath));
      }
    }
    
    return cpdNodes;
  }
}
