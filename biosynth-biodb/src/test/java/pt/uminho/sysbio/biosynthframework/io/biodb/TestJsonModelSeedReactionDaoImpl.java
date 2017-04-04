package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class TestJsonModelSeedReactionDaoImpl {

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
    Resource reactionsJson = new FileSystemResource("/var/biodb/modelseed/Reactions.json");
    JsonModelSeedReactionDaoImpl dao = new JsonModelSeedReactionDaoImpl(reactionsJson);
    int records = dao.getAllReactionEntries().size();
    
    assertEquals(34696, records);
  }

}
