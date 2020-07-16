package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggOrthologyEntity;

public class TestRestKeggOrthologDaoImpl {

  private RestKeggOrthologDaoImpl dao;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new RestKeggOrthologDaoImpl();
//    dao.setLocalStorage("/var/biodb/kegg");
//    dao.setUseLocalStorage(true);
//    dao.setSaveLocalStorage(true);
//    dao.setDatabaseVersion("86.0");
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testK00873() {
    KeggOrthologyEntity e = dao.getByEntry("K00873");
    assertNotNull(e);
    assertEquals("K00873", e.getEntry());
    assertEquals("PK, pyk", e.getName());
    assertEquals("pyruvate kinase [EC:2.7.1.40]", e.getDefinition());
    assertEquals(5, e.getReactions().size());
    assertEquals(4, e.getModules().size());
  }
  
  @Test
  public void testK22024() {
    KeggOrthologyEntity e = dao.getByEntry("K22024");
    assertNotNull(e);
    assertEquals("K22024", e.getEntry());
    assertEquals("pdxA2", e.getName());
    assertEquals("4-phospho-D-threonate 3-dehydrogenase / 4-phospho-D-erythronate 3-dehydrogenase [EC:1.1.1.408 1.1.1.409]", e.getDefinition());
    assertEquals(0, e.getReactions().size());
    assertEquals(0, e.getModules().size());
  }

}
