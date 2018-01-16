package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.integration.model.IntegrationMap;
import pt.uminho.sysbio.biosynthframework.util.GraphUtils;

public class ReferencePropagation {
  
  private static final Logger logger = LoggerFactory.getLogger(ReferencePropagation.class);
  
  public BMap<String, String> idToCmp = new BHashMap<>();
  public IntegrationMap<String, MetaboliteMajorLabel> imap = new IntegrationMap<>();
  
  public void addReference(String id, String cmp,
                           MetaboliteMajorLabel database, String dbEntry) {
    idToCmp.put(id, cmp);
    imap.addIntegration(id, database, dbEntry);
  }
  
  public Set<Set<String>> getConflicts(IntegrationMap<String, MetaboliteMajorLabel> imap) {
    Set<Set<String>> result = new HashSet<>();
    
    for (String cmp : idToCmp.values()) {
      UndirectedGraph<String, Object> g = new SimpleGraph<>(Object.class);
      Set<String> ids = idToCmp.bget(cmp);
      for (String id : ids) {
        Map<MetaboliteMajorLabel, Set<String>> references = imap.get(id);
        Set<String> add = new HashSet<>();
        add.add(id);
        for (MetaboliteMajorLabel database : references.keySet()) {
          for (String dbEntry : references.get(database)) {
            add.add(String.format("%s@%s", dbEntry, database));
          }
        }
        GraphUtils.addConnectedSet(g, add);
      }
      
      Set<String> visited = new HashSet<>();
      for (String v : g.vertexSet()) {
        if (!visited.contains(v)) {
          Set<String> cc = GraphUtils.getConnectedVertex(v, g);

          Set<String> i = Sets.intersection(cc, idToCmp.keySet());
          
          logger.trace("[{}] {} {}", v, i, cc);
          
          if (i.size() > 1) {
            result.add(i);
          }
          visited.addAll(cc);
        }
      }
    }
    
    return result;
  }
  
  public Set<Set<String>> getConflicts() {
    return this.getConflicts(this.imap);
  }
  
  public IntegrationMap<String, MetaboliteMajorLabel> propagate(boolean safe, ConnectedComponents<String> ccs) {
    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<>();
    UndirectedGraph<String, Object> g = new SimpleGraph<>(Object.class);
    
    if (ccs != null) {
      for (Set<String> cc : ccs) {
        GraphUtils.addConnectedSet(g, cc);
      }
    }
    
    for (String id : imap.keySet()) {
      Map<MetaboliteMajorLabel, Set<String>> references = imap.get(id);
      Set<String> add = new HashSet<>();
      add.add(id);
      for (MetaboliteMajorLabel database : references.keySet()) {
        for (String dbEntry : references.get(database)) {
          add.add(String.format("%s@%s", dbEntry, database));
        }
      }
      GraphUtils.addConnectedSet(g, add);
    }
    
    Set<String> visited = new HashSet<>();
    for (String v : g.vertexSet()) {
      if (!visited.contains(v)) {
        Set<String> cc = GraphUtils.getConnectedVertex(v, g);

        Set<String> i = Sets.intersection(cc, idToCmp.keySet());
        
        logger.trace("[{}] {} {}", v, i, cc);
        
        Set<ExternalReference> references = new HashSet<>();
        for (String dbEntry : cc) {
          if (!i.contains(dbEntry)) {
            String database = dbEntry.split("@")[1];
            references.add(new ExternalReference(dbEntry.split("@")[0], database));
          }
        }
        
        for (String id: i) {
          for (ExternalReference reference : references) {
            result.addIntegration(id, 
                MetaboliteMajorLabel.valueOf(reference.source), 
                reference.entry);
          }
        }
        
        visited.addAll(cc);
      }
    }
    
    if (safe) {
      Set<Set<String>> conflicts = this.getConflicts(result);
      if (!conflicts.isEmpty()) {
        //restore conflicts
        for (Set<String> c : conflicts) {
          logger.warn("[RESTORE] {}", c);
          for (String id : c) {
            result.put(id, this.imap.get(id));
          }

        }
        logger.warn("detected conflicts: {}", conflicts.size());
      }
      
      conflicts = this.getConflicts(result);
    }
    
    return result;
  }
  
  public IntegrationMap<String, MetaboliteMajorLabel> propagate() {
    return this.propagate(false, null);
  }
}
