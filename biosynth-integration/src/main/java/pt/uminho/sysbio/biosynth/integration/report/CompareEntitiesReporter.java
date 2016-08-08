package pt.uminho.sysbio.biosynth.integration.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class CompareEntitiesReporter implements BiodbReporter<Object> {
  
  private final GraphDatabaseService service;
  
  public static class CompareEntitiesReport {
    
    public Set<String> properties = new HashSet<> ();
    public List<Map<String, Object>> entityProperties = new ArrayList<> ();
    public Set<String> lpropertiesType = new HashSet<> ();
    public List<Map<String, Set<Long>>> lentityIds = new ArrayList<> ();
    public Map<Long, Set<Long>> linkedNodeToLinks = new HashMap<> ();
  }
  
  public CompareEntitiesReporter(GraphDatabaseService service) {
    this.service = service;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public CompareEntitiesReport buildReport(Map<String, Object> params) {
    int max = 5;
    Set<Long> ids = new HashSet<> ();
    if (params.containsKey("max") && params.get("max") instanceof Integer) {
      max = (int) params.get("max");
    }
    
    if (params.containsKey("ids")) {
      Object o = params.get("ids");
      if (o instanceof Set) {
        ids = (Set<Long>) params.get("ids");
      }
      if (o instanceof ArrayList) {
        ArrayList<?> olist = (ArrayList<?>) params.get("ids");
        ids = new HashSet<> ();
        for (Object olisto : olist) {
          if (olisto instanceof Long) {
            Long e = (Long) olisto;
            ids.add(e);
          }
          if (olisto instanceof String) {
            Long e =  Long.parseLong(olisto.toString());
            ids.add(e);
          }
          if (olisto instanceof Integer) {
            Long e = Long.parseLong(olisto.toString());
            ids.add(e);
          }
        }
      }
    }
    
    
    return buildReport(ids, max);
  }

  public CompareEntitiesReport buildReport(Set<Long> ids, int max) {
    CompareEntitiesReport report = new CompareEntitiesReport();
//    Set<String> properties = new HashSet<> ();
//    List<Map<String, Object>> entityProperties = new ArrayList<> ();
//    Set<RelationshipType> lpropertiesType = new HashSet<> ();
    List<Map<String, List<Map<String, Object>>>> lentityProperties = new ArrayList<> ();
//    List<Map<RelationshipType, Set<Long>>> lentityIds = new ArrayList<> ();
//    Map<Long, Set<Long>> linkedNodeToLinks = new HashMap<> ();
    
    for (long id : ids) {
      Node entityNode = service.getNodeById(id);
      Map<String, Object> p = Neo4jUtils.getPropertiesMap(entityNode);
      report.properties.addAll(p.keySet());
      report.entityProperties.add(p);
      
      Map<String, List<Map<String, Object>>> lprop = new HashMap<> ();
      Map<String, Set<Long>> lids = new HashMap<> ();
      for (Relationship r : entityNode.getRelationships()) {
        String rtype = r.getType().name();
        report.lpropertiesType.add(rtype);
        if (!lprop.containsKey(rtype)) {
          lprop.put(rtype, new ArrayList<Map<String, Object>> ());
          lids.put(rtype, new HashSet<Long> ());
        }
        Node other = r.getOtherNode(entityNode);
        
        if (!report.linkedNodeToLinks.containsKey(other.getId())) {
          report.linkedNodeToLinks.put(other.getId(), new HashSet<Long> ());
        }
        report.linkedNodeToLinks.get(other.getId()).add(r.getId());
        lids.get(rtype).add(other.getId());
        
        Map<String, Object> eprop = Neo4jUtils.getPropertiesMap(other);
        Map<String, Object> rprop = Neo4jUtils.getPropertiesMap(r);
        if (lprop.get(rtype).size() < max) {
          lprop.get(rtype).add(eprop);
          lprop.get(rtype).add(rprop);
        }
      }
      lentityProperties.add(lprop);
      report.lentityIds.add(lids);
    }
    
    return report;
  }
}
