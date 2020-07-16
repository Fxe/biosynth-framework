package pt.uminho.sysbio.biosynthframework.integration.neo4j;

import java.util.HashMap;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalReactionNode;

public class Neo4jUniMap extends HashMap<Long, Long> {
  
  private static final long serialVersionUID = 1L;

  private final BiodbGraphDatabaseService service;
  private CurationLabel type;
  public Neo4jUniMap(BiodbGraphDatabaseService service, CurationLabel type) {
    this.service = service;
    this.type = type;
  }
  
  @Override
  public Long get(Object id) {
    org.neo4j.graphdb.Node n = service.getNodeById((long) id);
    Long uid = null;
    switch (type) {
      case UniversalMetabolite:
        if (!n.hasLabel(GlobalLabel.Metabolite)) {
          return null;
        }
        BiodbMetaboliteNode cpdNode = new BiodbMetaboliteNode(n, null);
        BiosUniversalMetaboliteNode ucpd = cpdNode.getUniversalMetabolite();
        if (ucpd != null) {
          uid = ucpd.getId();
        }
        break;
      case UniversalReaction:
        if (!n.hasLabel(GlobalLabel.Reaction)) {
          return null;
        }
        BiodbReactionNode rxnNode = new BiodbReactionNode(n, null);
        BiosUniversalReactionNode urxn = rxnNode.getUniversalReaction();
        if (urxn != null) {
          uid = urxn.getId();
        }
        break;
      default:
        break;
    }
    return uid;
  }
}
