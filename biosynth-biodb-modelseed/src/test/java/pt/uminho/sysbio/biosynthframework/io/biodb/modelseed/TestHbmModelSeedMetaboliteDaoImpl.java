package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import static org.junit.Assert.assertNotNull;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.io.biodb.modelseed.HbmModelSeedMetaboliteDaoImpl;

public class TestHbmModelSeedMetaboliteDaoImpl {

  protected final static String HBM_CFG = "/var/java_config/hbm_mysql_biobase_test.cfg.xml";
  protected static SessionFactory sessionFactory;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
//    sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(
//        new File(HBM_CFG), 
//        ModelSeedMetaboliteEntity.class);
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
//    sessionFactory.close();
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    MetaboliteDao<ModelSeedMetaboliteEntity> dao = 
        new HbmModelSeedMetaboliteDaoImpl(sessionFactory);
    assertNotNull(dao);
//    fail("Not yet implemented");
  }

}
