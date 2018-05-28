package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg.KeggDrugTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;

public class TestBiosNeo4jGraphDatabaseService {

  private Transaction tx;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
    dao.setLocalStorage("D:/var/biodb/kegg");
    dao.setDatabaseVersion("84.0");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    KeggDrugMetaboliteEntity cpd = dao.getByEntry("D05511");
    KeggDrugTransform transform = new KeggDrugTransform();
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> mol = 
        gcpd.getConnectedEntities().get("has_mdl_mol_file");
    AbstractGraphNodeEntity mnode = mol.iterator().next().getRight();
    GraphDatabaseService graphDatabaseService = 
        HelperNeo4jConfigInitializer.initializeNeo4jDatabase("D:\\tmp\\biodb\\neo4j");
    BiodbGraphDatabaseService service = new BiodbGraphDatabaseService(graphDatabaseService);
    service.databasePath = "D:\\tmp\\biodb\\neo4j";
    tx = service.beginTx();
    BiodbMetaboliteNode node = service.getMetabolite("D05511", MetaboliteMajorLabel.LigandDrug);
//    Node node = service.findNode(MetaboliteMajorLabel.LigandDrug, "entry", "D05511");
    System.out.println(node.getClass().getSimpleName());
    System.out.println(node.getAllProperties());
    BiodbPropertyNode propertyNode = node.getMetaboliteProperty(MetabolitePropertyLabel.MDLMolFile);
    System.out.println(propertyNode.getClass().getSimpleName());
    System.out.println(propertyNode.getAllProperties());
    System.out.println(propertyNode.getValue());
    
    Neo4jGraphMetaboliteDaoImpl gdao = new Neo4jGraphMetaboliteDaoImpl(service);
    gdao.databasePath = "D:\\tmp\\biodb\\neo4j";
//    gdao.saveMetabolite("", gcpd);
    tx.failure();
    tx.close();
    service.shutdown();
  }

}
