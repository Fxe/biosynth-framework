package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class KeggMetaboliteAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  public KeggMetaboliteAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    //comments //remark
    
    Set<BiodbMetaboliteNode> cpdNodes = filter(
        refs, MetaboliteMajorLabel.LigandCompound.toString());
    Set<BiodbMetaboliteNode> glNodes = filter(
        refs, MetaboliteMajorLabel.LigandGlycan.toString());
    Set<BiodbMetaboliteNode> drNodes = filter(
        refs, MetaboliteMajorLabel.LigandDrug.toString());
    
    
    
    Set<String> comments = null;
    Set<String> remarks = null;
    String mass = null;
    String composition = null;
    return null;
  }

  public void compound(BiodbMetaboliteNode cpdNode) {
    /*
     * remark // Same as: D08458 ATC code: R05DA04 Drug group: DG01076
     * comment Generic compound in reaction hierarchy
     * mass
     */
  }
  
  public void drug(BiodbMetaboliteNode drNode) {
    /*
     * remark //  Chemical group: DG01312
     * comment Tricyclic antidepressant, Iminodibenzyl derivative
     */
  }
  
  public void glycan(BiodbMetaboliteNode glNode) {
    /*
     * remark //  Same as: C04794
     * comment Fuc3NAc:3-acetamido-3,6-dideoxy-D-galactose[PMID:11728403]
     * mass
     * composition
     */
  }
}
