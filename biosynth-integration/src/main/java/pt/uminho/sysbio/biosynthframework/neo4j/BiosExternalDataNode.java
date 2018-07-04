package pt.uminho.sysbio.biosynthframework.neo4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.AbstractBiodbNode;

public class BiosExternalDataNode extends AbstractBiodbNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosExternalDataNode.class);
  
  protected final String databasePath;

  protected Map<String, Object> eproperties;
  protected boolean loaded = false;

  public BiosExternalDataNode(Node node, String databasePath) {
    super(node);
    this.databasePath = databasePath;
    if (databasePath != null && this.hasLabel(GlobalLabel.EXTERNAL_DATA)) {
      loadExternalData();
    }
  }

  public void loadExternalData() {
    if (!loaded) {
      loaded = true;
      File efile = new File(this.databasePath + "/" + this.getId() + ".json");
      if (efile.exists() && efile.isFile()) {
        ObjectMapper om = new ObjectMapper();
        try (InputStream is = new FileInputStream(efile)) {
          Map<String, Object> d = om.readValue(is, om.getTypeFactory().constructMapLikeType(
              HashMap.class, String.class, Object.class));
          if (d != null) {
            eproperties = new HashMap<>(d);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        logger.warn("file not found: {}", efile);
      }
    }
  }

  @Override
  public Object getProperty(String key) {
    if (this.eproperties != null && 
        this.eproperties.containsKey(key)) {
      return this.eproperties.get(key);
    }
    
    return super.getProperty(key);
  }
  
  @Override
  public Object getProperty(String key, Object defaultValue) {
    if (this.eproperties != null && 
        this.eproperties.containsKey(key)) {
      return this.eproperties.get(key);
    }
    
    return super.getProperty(key, defaultValue);
  }
  
  @Override
  public Map<String, Object> getAllProperties() {
    Map<String, Object> properties = super.getAllProperties();
    if (eproperties != null) {
      properties.putAll(eproperties);
    }
    return properties;
  }
  
  @Override
  public String toString() {
    return super.toString();
  }
}
