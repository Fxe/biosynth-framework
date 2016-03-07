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
//    final String SBML_PATH = "D:/var/biomodels/sbml/hsa/MODEL6399676120.xml";
//    final String SBML_PATH = "D:/var/yeast/sbml/yeast_7.6_cobra.xml";
//    final String SBML_PATH = "/var/human/Recon2_v02.xml";
//    final String SBML_PATH = "/var/human/Recon1.xml";
//    XmlStreamSbmlReader sbmlReader = new XmlStreamSbmlReader(
//        new FileInputStream(SBML_PATH));
//    XmlSbmlModel model = sbmlReader.parse();
//    for (XmlSbmlSpecie specie : model.getSpecies()) {
//      System.out.println(specie.getAttributes());
//      for (String type : specie.getListOfAnnotations().keySet()) {
//        System.out.println("\t" + type);
//        for (XmlObject o : specie.getListOfAnnotations().get(type)) {
//          System.out.println("\t\t" + o.getAttributes().get("resource"));
//        }
//      }
//      break;
//    }
//    
//    for (XmlSbmlReaction reaction : model.getReactions()) {
//      System.out.println(reaction.getAttributes());
//      System.out.println(reaction.getNotes());
//      for (String type : reaction.getListOfAnnotations().keySet()) {
//        System.out.println("\t" + type);
//        for (XmlObject o : reaction.getListOfAnnotations().get(type)) {
//          System.out.println("\t\t" + o.getAttributes().get("resource"));
//        }
//      }
//      break;
//    }
  }

}
