package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.LayoutNode;
import pt.uminho.sysbio.biosynthframework.io.BiosynthDao;

public interface LayoutNodeDao extends BiosynthDao<LayoutNode> {
  public Map<Long, LayoutNode> listNodes(Set<Long> nodeIds);
  public void updateAnnotation(long nodeId, Map<String, 
                               Map<Long, String>> annotation,
                               String referenceType);
}
