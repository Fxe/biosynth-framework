package pt.uminho.sysbio.biosynthframework.sbml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

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
//    final String SBML_PATH = "D:/var/biomodels/sbml/iSce926/iSce926_M11.xml";
    final String SBML_PATH = "D:/var/yeast/sim/iTO977_R_biomass_wild_iAZ900.xml";
    
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    try {
      XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
          new ByteArrayInputStream("<a>omg</a>".getBytes()));
      System.out.println(xmlInputFactory.getClass().getName());
      System.out.println(xmlEventReader.getClass().getName());
    } catch (XMLStreamException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    XmlStreamSbmlReader sbmlReader = new XmlStreamSbmlReader(
        new FileInputStream(SBML_PATH));
    XmlSbmlModel model = sbmlReader.parse();
    System.out.println(model.getSbmlAttributes());
    System.out.println(model.getAttributes());
    for (XmlSbmlCompartment compartment : model.getCompartments()) {
      System.out.println(compartment.getAttributes());
      break;
    }
    for (XmlSbmlSpecie specie : model.getSpecies()) {
      System.out.println(specie.getAttributes());
      for (String type : specie.getListOfAnnotations().keySet()) {
        System.out.println("\t" + type);
        for (XmlObject o : specie.getListOfAnnotations().get(type)) {
          System.out.println("\t\t" + o.getAttributes().get("resource"));
        }
      }
      System.out.println(specie.getNotes());
      break;
    }
    
    for (XmlSbmlReaction reaction : model.getReactions()) {
      System.out.println("Attributes: " + reaction.getAttributes());
      System.out.println("Notes: " + reaction.getNotes());
      for (XmlObject o : reaction.getListOfParameters()) {
        System.out.println("  * " +  o.getAttributes());
      }
      for (String type : reaction.getListOfAnnotations().keySet()) {
        System.out.println("\t" + type);
        for (XmlObject o : reaction.getListOfAnnotations().get(type)) {
          System.out.println("\t\t" + o.getAttributes().get("resource"));
        }
      }
      for (XmlObject o : reaction.getListOfReactants()) {
        System.out.println("  - " +  o.getAttributes());
      }
      for (XmlObject o : reaction.getListOfProducts()) {
        System.out.println("  + " +  o.getAttributes());
      }
      break;
    }
    
    
  }

}
