package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggCompoundMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGlycanMetaboliteDaoImpl;

public class TestRestKeggCompoundMetaboliteDaoImpl {

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
    RestKeggCompoundMetaboliteDaoImpl dao = new RestKeggCompoundMetaboliteDaoImpl();
    dao.setDatabaseVersion("test");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    dao.setLocalStorage("/tmp/trash/kegg");
    dao.getMetaboliteByEntry("C00001");
    dao.getAllMetaboliteEntries();
    fail("Not yet implemented");
  }

  @Test
  public void testListAllMetabolites() {
    RestKeggCompoundMetaboliteDaoImpl dao = new RestKeggCompoundMetaboliteDaoImpl();
    dao.setDatabaseVersion("test");
    dao.setUseLocalStorage(false);
    dao.setSaveLocalStorage(false);
    List<String> l = dao.getAllMetaboliteEntries();
    assertNotNull(l);
    for (String s : l) {
      assertNotNull(s);
      assertEquals(false, s.isEmpty());
      assertEquals(true, s.startsWith("C"));
      assertEquals(6, s.length());
    }
  }
  
  @Test
  public void testAll() {
    RestKeggCompoundMetaboliteDaoImpl daoCpd = new RestKeggCompoundMetaboliteDaoImpl();
    daoCpd.setDatabaseVersion("test");
    daoCpd.setUseLocalStorage(true);
    daoCpd.setSaveLocalStorage(true);
    daoCpd.setLocalStorage("/tmp/trash/kegg");
    daoCpd.getMetaboliteByEntry("C00001");
    daoCpd.getAllMetaboliteEntries();
    RestKeggDrugMetaboliteDaoImpl daoDr = new RestKeggDrugMetaboliteDaoImpl();
    daoDr.setDatabaseVersion("test");
    daoDr.setUseLocalStorage(true);
    daoDr.setSaveLocalStorage(true);
    daoDr.setLocalStorage("/tmp/trash/kegg");
    daoDr.getMetaboliteByEntry("D00001");
    daoDr.getAllMetaboliteEntries();
    RestKeggGlycanMetaboliteDaoImpl daoGl = new RestKeggGlycanMetaboliteDaoImpl();
    daoGl.setDatabaseVersion("test");
    daoGl.setUseLocalStorage(true);
    daoGl.setSaveLocalStorage(true);
    daoGl.setLocalStorage("/tmp/trash/kegg");
    daoGl.getMetaboliteByEntry("G00001");
    daoGl.getAllMetaboliteEntries();
  }
}
