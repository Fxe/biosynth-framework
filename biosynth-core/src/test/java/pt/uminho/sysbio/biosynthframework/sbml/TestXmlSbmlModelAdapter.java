package pt.uminho.sysbio.biosynthframework.sbml;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.EntityType;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class TestXmlSbmlModelAdapter {

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
  public void testCoreModel() throws IOException {
//    InputStream is = new FileInputStream("/var/biomodels/sbml/yeast_7.6.xml");
    InputStream is = new FileInputStream("/var/biomodels/sbml/iAdipocytes1809.xml");
    XmlStreamSbmlReader reader = new XmlStreamSbmlReader(is);
    XmlSbmlModel xmodel = reader.parse();
    XmlSbmlModelAdapter adapter = new XmlSbmlModelAdapter(xmodel);
    int gprs = 0;
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String mrxnEntry = xrxn.getAttributes().get("id");
      if (mrxnEntry != null) {
        String gpr = adapter.getGpr(mrxnEntry);
        if (gpr != null) {
          gprs++;              
        }
      }
    }
    
    System.out.println(CollectionUtils.print(CollectionUtils.count(adapter.xrxnType)));
    System.out.println(CollectionUtils.print(CollectionUtils.count(adapter.xspiType)));
    System.out.println(CollectionUtils.print(adapter.getDefaultDrains()));
    System.out.println(gprs);
  }
  
//  @Test
  public void test1() {
    ZipContainer zipContainer = null;
    
    try {
      zipContainer = new ZipContainer("/var/biomodels/fungis.zip");
      for (ZipRecord zr : zipContainer.getInputStreams()) {
        System.out.println(zr);
        XmlStreamSbmlReader reader = new XmlStreamSbmlReader(zr.is);
        
        XmlSbmlModel xmodel = reader.parse();
        XmlSbmlModelAdapter adapter = new XmlSbmlModelAdapter(xmodel);
        int gprs = 0;
        for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
          String mrxnEntry = xrxn.getAttributes().get("id");
          if (mrxnEntry != null) {
            String gpr = adapter.getGpr(mrxnEntry);
            if (gpr != null) {
              System.out.println(gpr);
              gprs++;              
            }
          }
        }
        
        System.out.println(CollectionUtils.print(CollectionUtils.count(adapter.xrxnType)));
        System.out.println(CollectionUtils.print(CollectionUtils.count(adapter.xspiType)));
        System.out.println(CollectionUtils.print(adapter.getDefaultDrains()));
        System.out.println(gprs);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(zipContainer);
    }
    
    assertEquals(true, true);
  }

//  @Test
  public void test2() {
    ZipContainer zipContainer = null;
    
    try {
      zipContainer = new ZipContainer("/tmp/joana_model/test_models_huge.zip");
      for (ZipRecord zr : zipContainer.getInputStreams()) {
        System.out.println(zr);
        XmlStreamSbmlReader reader = new XmlStreamSbmlReader(zr.is);
        
        XmlSbmlModel xmodel = reader.parse();
        XmlSbmlModelAdapter adapter = new XmlSbmlModelAdapter(xmodel);
        int gprs = 0;
        for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
          String mrxnEntry = xrxn.getAttributes().get("id");
          if (mrxnEntry != null) {
            String gpr = adapter.getGpr(mrxnEntry);
            if (gpr != null) {
              gprs++;              
            }
          }
        }
        System.out.println(gprs);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(zipContainer);
    }
    
    assertEquals(true, true);
  }

}
