package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;

public class BiosUniversalMetaboliteNode extends BiodbEntityNode {

  public BiosUniversalMetaboliteNode(Node node, String databasePath) {
    super(node, databasePath);
    if (!this.node.hasLabel(CurationLabel.UniversalMetabolite)) {
      throw new IllegalArgumentException("Invalid UniversalMetabolite node: " + node);
    }
  }

  public Set<BiodbMetaboliteNode> getMetabolites() {
    Set<BiodbMetaboliteNode> metabolites = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, IntegrationRelationshipType.has_universal_metabolite)) {
      metabolites.add(new BiodbMetaboliteNode(n, databasePath));
    }
    return metabolites;
  }
  
  public Set<BiodbMetaboliteNode> getMetabolites(MetaboliteMajorLabel database) {
    Set<BiodbMetaboliteNode> metabolites = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, IntegrationRelationshipType.has_universal_metabolite)) {
      if (n.hasLabel(database)) {
        metabolites.add(new BiodbMetaboliteNode(n, databasePath));        
      }
    }
    return metabolites;
  }
}
