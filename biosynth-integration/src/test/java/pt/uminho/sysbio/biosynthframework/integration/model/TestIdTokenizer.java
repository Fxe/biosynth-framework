package pt.uminho.sysbio.biosynthframework.integration.model;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.sbml.XmlStreamSbmlReader;

public class TestIdTokenizer {

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
  
  public void test(InputStream is) throws IOException {
    
    XmlStreamSbmlReader reader = new XmlStreamSbmlReader(is);
    XmlSbmlModel xmodel = reader.parse();
    
    SpecieIntegrationFacade integration = new SpecieIntegrationFacade();
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String id = xspi.getAttributes().get("id");
      String cmp = xspi.getAttributes().get("compartment");
      integration.addSpecie(id, cmp);
    }
    
    integration.generatePatterns();
  }

  @Test
  public void test() {
    String sbmlPath = "/var/biomodels/joana_bigg_old/iJO1366_bigg2.xml";
    sbmlPath = "/var/biomodels/joana_bigg_old/iSB619_bigg2.xml";
    sbmlPath = "/tmp/joana_model/test_models.zip";
    
    String urlPath = sbmlPath;
    InputStream is = null; //file input stream
    ZipInputStream zis = null; //zip file manipulator
    ZipFile zf = null; //zip file pointer
    InputStream rfis = null; //file within zip file pointer
    
    if (urlPath.endsWith(".zip")) {
      try {
//        URL url = new URL(urlPath);
//        URI uri = new URI(urlPath);
//        URLConnection connection = url.openConnection();
//        is = connection.getInputStream();
        is = new FileInputStream(urlPath);
//        File zipFile = new File(uri);
//        FileUtils.copyURLToFile(url, zipFile);
        zis = new ZipInputStream(is);
        zf = new ZipFile(urlPath);
        
        ZipEntry ze = null;
//        zf = new 
        while ((ze = zis.getNextEntry()) != null) {
          Map<String, Object> record = new HashMap<> ();
          record.put("name", ze.getName());
          record.put("size", ze.getSize());
          record.put("compressed_size", ze.getCompressedSize());
          record.put("method", ze.getMethod());
          System.out.println(record);
          try {
            rfis = zf.getInputStream(ze);
//            System.out.println(IOUtils.readLines(rfis).size());
            test(rfis);
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            IOUtils.closeQuietly(rfis);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      try {
        rfis = new FileInputStream(urlPath);
        test(rfis);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.closeQuietly(rfis);
      }
    }
    

  }

}
