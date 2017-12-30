package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggCompoundMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.RestBiocycMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.parser.BioCycMetaboliteXMLParser;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class TestRestBiocycMetaboliteDaoImpl {

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
    
    RestBiocycMetaboliteDaoImpl dao = new RestBiocycMetaboliteDaoImpl();
    
//    RestKeggCompoundMetaboliteDaoImpl dao = new RestKeggCompoundMetaboliteDaoImpl();
//    dao.setDatabaseVersion("test");
//    dao.setUseLocalStorage(true);
//    dao.setSaveLocalStorage(true);
//    dao.setLocalStorage("/tmp/trash/kegg");
//    dao.getMetaboliteByEntry("C00001");
    
    dao.setLocalStorage("/tmp/trash/biocyc");
    dao.setDatabaseVersion("test");
    
    dao.setLocalStorage("/var/biodb/biocyc");
    dao.setDatabaseVersion("21.1");
    
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    dao.setPgdb("META");
//    dao.getMetaboliteByEntry("META:CPD-882");
//    dao.getMetaboliteByEntry("META:WATER");
    for (String e : dao.getAllMetaboliteEntries()) {
      BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry(e);
    }
    
//    dao.getAllMetaboliteEntries();
  }

}
