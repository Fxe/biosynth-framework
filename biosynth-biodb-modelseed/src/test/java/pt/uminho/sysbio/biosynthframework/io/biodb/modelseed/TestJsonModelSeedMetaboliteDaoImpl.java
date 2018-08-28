package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.biodb.modelseed.JsonModelSeedMetaboliteDaoImpl;

public class TestJsonModelSeedMetaboliteDaoImpl {

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
  public void listRecords() {
    Resource compoundsJson = new FileSystemResource("/var/biodb/modelseed/Compounds.json");
    JsonModelSeedMetaboliteDaoImpl dao = new JsonModelSeedMetaboliteDaoImpl(compoundsJson);
    int records = dao.getAllMetaboliteEntries().size();
    
    assertEquals(27692, records);
  }

  @Test
  public void readAllRecords() {
    Resource compoundsJson = new FileSystemResource("/var/biodb/modelseed/Compounds.json");
    JsonModelSeedMetaboliteDaoImpl dao = new JsonModelSeedMetaboliteDaoImpl(compoundsJson);
    for (String e : dao.getAllMetaboliteEntries()) {
      ModelSeedMetaboliteEntity cpd = dao.getMetaboliteByEntry(e);
      assertNotNull(cpd);
      assertNotNull(cpd.getEntry());
    }
//    int records = dao.getAllMetaboliteEntries().size();
//    
//    assertEquals(27692, records);
  }
}
