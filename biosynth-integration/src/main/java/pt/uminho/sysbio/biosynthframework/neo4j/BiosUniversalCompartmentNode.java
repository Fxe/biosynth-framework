package pt.uminho.sysbio.biosynthframework.neo4j;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class BiosUniversalCompartmentNode extends BiodbEntityNode {

  public BiosUniversalCompartmentNode(Node node, String databasePath) {
    super(node, databasePath);
  }

  public SubcellularCompartment getCompartment() {
    String scmp = (String) this.getProperty("bios_scmp");
    return SubcellularCompartment.valueOf(scmp);
  }
  
  @Override
  public String toString() {
    return String.format("UCmp[%s]", getCompartment());
  }
}
