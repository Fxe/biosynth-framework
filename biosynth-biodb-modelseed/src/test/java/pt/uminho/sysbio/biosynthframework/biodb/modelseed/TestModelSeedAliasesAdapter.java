package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.ExternalReference;

public class TestModelSeedAliasesAdapter {

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
    String data = 
        "ModelSEED ID\tExternal ID\tSource\n" + 
        "cpd00001\tC00001\tKEGG\n" + 
        "cpd00001\th2o\tBiGG|BiGG1|iAF1260|iAF692|iGT196";
    
    try (InputStream is = new ByteArrayInputStream(data.getBytes())) {
      ModelSeedAliasesAdapter adapter = 
          ModelSeedAliasesAdapter.fromStream(is);
      
      Set<ExternalReference> kegg = adapter.getExternalReferences("cpd00001", "KEGG");
      Set<ExternalReference> bigg = adapter.getExternalReferences("cpd00001", "BiGG");
      
      assertNotNull(adapter.mseedRefMap);
      assertEquals(1, adapter.mseedRefMap.size());
      assertNotNull(adapter.mseedRefMap.get("cpd00001"));
      assertEquals(6, adapter.mseedRefMap.get("cpd00001").size());
      assertNotNull(kegg);
      assertEquals(1, kegg.size());
      assertNotNull(bigg);
      assertEquals(1, bigg.size());
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
