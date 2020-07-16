package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggModuleEntity;

public class TestRestKeggModuleDaoImpl {

  private RestKeggModuleDaoImpl dao;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new RestKeggModuleDaoImpl();
//    dao.setLocalStorage("/var/biodb/kegg");
//    dao.setUseLocalStorage(true);
//    dao.setSaveLocalStorage(true);
//    dao.setDatabaseVersion("86.0");
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testM00001() {
    KeggModuleEntity e = dao.getByEntry("M00001");
    assertNotNull(e);
    assertEquals("M00001", e.getEntry());
    assertEquals("Glycolysis (Embden-Meyerhof pathway), glucose => pyruvate", e.getName());
    assertEquals("Pathway module; Carbohydrate and lipid metabolism; Central carbohydrate metabolism", e.getModuleClass());
    assertEquals(14, e.getOrthology().size());
    assertEquals(11, e.getCompounds().size());
    assertEquals(11, e.getOrthologyReaction().size());
  }

}
