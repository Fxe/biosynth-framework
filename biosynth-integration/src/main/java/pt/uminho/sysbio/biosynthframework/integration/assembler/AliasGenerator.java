package pt.uminho.sysbio.biosynthframework.integration.assembler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

public class AliasGenerator implements Function<Set<Long>, String> {
  
  private static final Logger logger = LoggerFactory.getLogger(AliasGenerator.class);

  private final BiodbGraphDatabaseService service;
  private Label label;
  
  public AliasGenerator(GraphDatabaseService graphDatabaseService, Label label) {
    this.service = new BiodbGraphDatabaseService(graphDatabaseService);
    this.label = label;
  }
  
  @Override
  public String apply(Set<Long> ids) {
    Set<String> alias = new HashSet<>();
    
    int min = Integer.MAX_VALUE;
    for (long id : ids) {
      BiodbEntityNode node = new BiodbEntityNode(service.getNodeById(id), null);
      
      if (node.hasLabel(label)) {
        String e = node.getEntry();
        if (e.length() < min) {
          min = e.length();
        }
        alias.add(e);
      }
    }
    
    logger.info("Alias: {}", alias);
    
    if (alias.isEmpty()) {
      return null;
    }
    String result = alias.iterator().next();
    if (alias.size() > 1) {
      for (String s : alias) {
        if (s.length() == min) {
          result = s;
        }
      }
    }
    return result;
  }

}
