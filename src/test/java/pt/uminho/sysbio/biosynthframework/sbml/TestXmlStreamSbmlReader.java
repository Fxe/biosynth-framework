package pt.uminho.sysbio.biosynthframework.sbml;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestXmlStreamSbmlReader {

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
  public void test() throws IOException {
    XmlStreamSbmlReader sbmlReader = new XmlStreamSbmlReader(new FileInputStream("D:/var/sbml/yeast_6.06/yeast_6.06.xml"));
    XmlSbmlModel model = sbmlReader.parse();
    for (XmlSbmlSpecie specie : model.getSpecies()) {
      System.out.println(specie.getAttributes());
      for (String type : specie.getListOfAnnotations().keySet()) {
        System.out.println("\t" + type);
        for (XmlObject o : specie.getListOfAnnotations().get(type)) {
          System.out.println("\t\t" + o.getAttributes().get("resource"));
        }
      }
    }
    fail("Not yet implemented");
  }

}
