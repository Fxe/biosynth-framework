package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.io.File;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;

public class BiodbPropertyNode extends BiosExternalDataNode {

  private static final Logger logger = LoggerFactory.getLogger(BiodbPropertyNode.class);
  
  public BiodbPropertyNode(Node node, String databasePath) {
    super(node, databasePath);
  }

  public String getValue() {
    if (this.hasLabel(GlobalLabel.EXTERNAL_DATA)) {
      logger.trace("Node[{}] EXTERNAL DATA !", this.getId());
      
      if (databasePath == null) {
        return "ERROR_DATABASE_PATH_NOT_ASSIGNED";
      }
      File dataFile = new File(this.databasePath + "/edata/" + this.getId() + ".json");
      logger.trace("Node[{}] EXTERNAL DATA ! {}", this.getId(), dataFile);
      if (!dataFile.exists() || !dataFile.isFile()) {
        return "ERROR_FILE_NOT_FOUND";
      }
      loadExternalData();
      if (eproperties == null) {
        return "ERROR_PARSE_FILE";
      }
    }
    return (String) getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
  }
}
