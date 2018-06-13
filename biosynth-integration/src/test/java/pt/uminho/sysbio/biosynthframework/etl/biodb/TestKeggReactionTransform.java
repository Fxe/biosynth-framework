package pt.uminho.sysbio.biosynthframework.etl.biodb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg.KeggReactionTransform;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggReactionDaoImpl;

public class TestKeggReactionTransform {

  private KeggReactionTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new KeggReactionTransform();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    RestKeggReactionDaoImpl dao = new RestKeggReactionDaoImpl();
    dao.setLocalStorage("D:/var/biodb/kegg");
    dao.setDatabaseVersion("84.0");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    
    RestKeggReactionDaoImpl dao2 = new RestKeggReactionDaoImpl();
    dao.setLocalStorage("D:/var/biodb/kegg");
    dao.setDatabaseVersion("2015");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    
    for (String e : dao2.getAllEntries()) {
      KeggReactionEntity rxn1 = dao.getByEntry(e);
      KeggReactionEntity rxn2 = dao2.getByEntry(e);
      if (rxn1 != null && rxn2 != null) {
        if (!rxn1.getDefinition().trim().equals(rxn2.getDefinition().trim())) {
          System.out.println(rxn1.getEntry() + " " + rxn2.getEntry());
          System.out.println(rxn1.getDefinition());
          System.out.println(rxn2.getDefinition());
//          transform.apply(rxn1);
        }
        
      }
    }
    fail("Not yet implemented");
  }

}
