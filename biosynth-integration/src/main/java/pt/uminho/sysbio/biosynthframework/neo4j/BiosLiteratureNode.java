package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosLiteratureNode extends BiodbEntityNode {

  public BiosLiteratureNode(Node node, String databasePath) {
    super(node, databasePath);
  }

  public Set<BiosSupplementaryFileNode> getSupplementaryFiles() {
    Set<BiosSupplementaryFileNode> supNodes = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, GenericRelationship.has_supplementary_file)) {
      BiosSupplementaryFileNode supNode = new BiosSupplementaryFileNode(n, databasePath);
      supNodes.add(supNode);
    }
    return supNodes;
  }
}
