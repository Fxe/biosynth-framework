package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.EntityType;
import pt.uminho.sysbio.biosynthframework.ExtendedMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.ExtendedMetaboliteSpecie;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ExtendedMetabolicModelDao;

public class TestNeo4jOptfluxContainerDaoImpl {
  
  private static final Logger logger = LoggerFactory.getLogger(TestNeo4jOptfluxContainerDaoImpl.class);
  private static GraphDatabaseService graphDatabaseService;
  
  private ExtendedMetabolicModelDao dao;
  private Transaction tx;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
/*
    logger.info("initialize db ...");
    graphDatabaseService = HelperNeo4jConfigInitializer
        .initializeNeo4jDataDatabaseConstraints(
            TestNeo4jConfiguration.NEO_DATA_DB);
  */  
    
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
/*
    if (graphDatabaseService != null) {
      logger.info("shutdown db ...");
      graphDatabaseService.shutdown();
    }
*/
  }

  @Before
  public void setUp() throws Exception {
/*
    dao = new Neo4jOptfluxContainerDaoImpl(graphDatabaseService);
    tx = graphDatabaseService.beginTx();
*/
  }

  @After
  public void tearDown() throws Exception {
/*
    tx.success();
    tx.close();
*/
  }

  @Test
  public void test() {
/*
    ExtendedMetaboliteSpecie spi = dao.getModelMetaboliteSpecieById(1765674L);
    System.out.println(spi);
    System.out.println(spi.getComparment());
*/
  }
  /*
  @Test
  public void test_get_reaction() {
    OptfluxContainerReactionEntity rxn = dao.getModelReactionById(1765719L);
    System.out.println(rxn);
  }

  @Test
  public void test_get_reaction2() {
    OptfluxContainerReactionEntity rxn = dao.getModelReactionById(1765764L);
    System.out.println(rxn);
    rxn.setEntityType(EntityType.BIOMASS);
    dao.updateModelReaction(rxn);
  }
*/
}
