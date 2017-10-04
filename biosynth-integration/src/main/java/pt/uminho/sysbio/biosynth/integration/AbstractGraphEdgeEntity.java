package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class AbstractGraphEdgeEntity extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;

  public Set<String> labels = new HashSet<> ();
  public Map<String, Object> properties = new HashMap<> ();

  public Set<String> getLabels() { return labels;}
  public void setLabels(Set<String> labels) { this.labels = labels;}

  public Map<String, Object> getProperties() { return properties;}
  public void setProperties(Map<String, Object> properties) { this.properties = properties;}

  @Override
  public String toString() {
    return String.format("AbstractGraphEdgeEntity[%d]::%s", id, labels);
  }
}
