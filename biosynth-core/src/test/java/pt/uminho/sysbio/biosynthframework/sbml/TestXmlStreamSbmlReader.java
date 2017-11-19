package pt.uminho.sysbio.biosynthframework.sbml;


import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.BFunction;
import pt.uminho.sysbio.biosynthframework.SimpleMetabolicModel;
import pt.uminho.sysbio.biosynthframework.SimpleModelBuilder;
import pt.uminho.sysbio.biosynthframework.io.Exporter;
import pt.uminho.sysbio.biosynthframework.util.AutoFileReader;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.InputStreamSet;
import pt.uminho.sysbio.biosynthframework.util.SbmlUtils;

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

  public static void testStrem(String f) {
    AutoFileReader reader = new AutoFileReader(f, null);
    InputStreamSet iss = reader.getStreams();
    System.out.println(iss);
    IOUtils.closeQuietly(reader);
  }

  @Test
  public void testStreams() {
    //    testStrem("/var/data.zip");
    //    testStrem("/iCyc792_export.sbml");


    //    reader.getFileType("/var/data.zip");
    //    reader.getFileType("/iCyc792_export.sbml");
    //    reader.getFileType("/iBif_temp.xml");
  }

  @Test
  public void aaa() throws XMLStreamException {
    String data = 
        "<species metaid=\"_321136\" id=\"Cell\" speciesType=\"STCell\" "
            + "compartment=\"environment\" boundaryCondition=\"true\"/>";
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
        new ByteArrayInputStream(data.getBytes()));
    XMLEvent event = xmlEventReader.nextEvent();
    assertTrue(event.isStartDocument());
    event = xmlEventReader.nextEvent();
    assertTrue(event.isStartElement());

    StartElement element = event.asStartElement();
    System.out.println(element.getName());
    System.out.println(element.getSchemaType());
    event = xmlEventReader.nextEvent();
    event = xmlEventReader.nextEvent();
    //    XmlStreamSbmlReader reader = null;
    //    reader.parseSpecie(xmlEventReader, specieStartElement)
  }

  public XmlSbmlModel testModel(String SBML_PATH) throws IOException {
    //  XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    //  try {
    //    XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
    //        new ByteArrayInputStream("<a>omg</a>".getBytes()));
    //    System.out.println(xmlInputFactory.getClass().getName());
    //    System.out.println(xmlEventReader.getClass().getName());
    //  } catch (XMLStreamException e) {
    //    // TODO Auto-generated catch block
    //    e.printStackTrace();
    //  }


    XmlStreamSbmlReader sbmlReader = new XmlStreamSbmlReader(
        new FileInputStream(SBML_PATH));
    XmlSbmlModel model = sbmlReader.parse();
    System.out.println(model.getNotes());
    for (XmlUnitDefinition xud : model.units) {
      System.out.println(xud.getAttributes());
      System.out.println("\t" + xud.getListOfAnnotations());
      System.out.println("\t" + xud.listOfUnits);
    }


    //  System.out.println(model.getSbmlAttributes());
    //  System.out.println(model);
    //  for (XmlSbmlCompartment compartment : model.getCompartments()) {
    //    System.out.println(compartment);
    //    break;
    //  }
    //  for (XmlSbmlSpecie specie : model.getSpecies()) {
    //    System.out.println("Attributes: " + specie);
    //    for (String type : specie.getListOfAnnotations().keySet()) {
    //      System.out.println("\t" + type);
    //      for (XmlObject o : specie.getListOfAnnotations().get(type)) {
    //        System.out.println("\t\t" + o.getAttributes().get("resource"));
    //      }
    //    }
    //    System.out.println("Notes: " + specie.getNotes());
    //    break;
    //  }
    //  
    BFunction<Object, String> f = new BFunction<Object, String>() {

      @Override
      public String apply(Object t) {
        if (t instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, String> m = (Map<String, String>) t;
          if (m.containsKey("geneProduct")) {
            return m.get("geneProduct");
          }
        }
        return t.toString();
      }
    };

    Map<String, XmlSbmlReaction> rxnMap = new HashMap<> ();
    for (XmlSbmlReaction reaction : model.getReactions()) {
      System.out.println("Attributes: " + reaction);
      System.out.println("Notes: " + reaction.getNotes());
      for (XmlObject o : reaction.getListOfParameters()) {
        System.out.println("  * " +  o);
      }
      for (String type : reaction.getListOfAnnotations().keySet()) {
        System.out.println("Annotation: " + type);
        for (XmlObject o : reaction.getListOfAnnotations().get(type)) {
          System.out.println("\t" + o.getAttributes());
        }
      }
      for (XmlObject o : reaction.getListOfReactants()) {
        System.out.println("  - " +  o);
      }
      for (XmlObject o : reaction.getListOfProducts()) {
        System.out.println("  + " +  o);
      }
      for (XmlObject o : reaction.getListOfModifiers()) {
        System.out.println("  M " +  o);
      }
      rxnMap.put(reaction.getAttributes().get("id"), reaction);
      //    System.out.println(SbmlUtils.gprTreeToString(reaction.getGpr()));
      System.out.println(SbmlUtils.gprTreeToString(reaction.getGpr(), f));
      break;
    }

    //  {
    //    XmlSbmlReaction reaction = rxnMap.get("R_EX_glc__e");
    //    System.out.println("============ test rxn " + reaction);
    //    System.out.println("Attributes: " + reaction);
    //    System.out.println("Notes: " + reaction.getNotes());
    //    for (XmlObject o : reaction.getListOfParameters()) {
    //      System.out.println("  * " +  o);
    //    }
    //    for (String type : reaction.getListOfAnnotations().keySet()) {
    //      System.out.println("Annotation: " + type);
    //      for (XmlObject o : reaction.getListOfAnnotations().get(type)) {
    //        System.out.println("\t" + o.getAttributes());
    //      }
    //    }
    //    for (XmlObject o : reaction.getListOfReactants()) {
    //      System.out.println("  - " +  o);
    //    }
    //    for (XmlObject o : reaction.getListOfProducts()) {
    //      System.out.println("  + " +  o);
    //    }
    //  }


    for (XmlObject parameter : model.getListOfParameters()) {
      System.out.println(parameter);
      break;
    }

    for (XmlObject fluxBound : model.getFluxBounds()) {
      System.out.println(fluxBound);
      break;
    }
    System.out.println(sbmlReader.rejectedElements);
    return model;
  }

  //  @Test
  public void testScanner() throws IOException {
    XmlStreamProfiller profiller = new XmlStreamProfiller();
    InputStream is = new FileInputStream("/var/biomodels/sbml/iFF708.xml");
    profiller.scan(is);
    System.out.println("Shared");
    System.out.println(DataUtils.toString(profiller.getSharedTags(), "\n", "\t"));
    System.out.println(profiller.getNamespaces());
  }

  @Test
  public void test() throws IOException {
    //    final String SBML_PATH = "/var/biomodels/dirty/iAbaylyiV4.xml";
    //    final String SBML_PATH = "/var/biomodels/dirty/iGT196.xml";
    //    final String SBML_PATH = "/var/biomodels/joana/iVM679.xml";
    //    final String SBML_PATH = "/var/biomodels/2batch/iJL480.xml";
    String SBML_PATH = "/var/biomodels/joana/iMP429_fixed.xml";
    SBML_PATH = "/var/biomodels/sbml/iFF708.xml";
    SBML_PATH = "/var/biomodels/sbml/hsa/RECON1.xml";
    SBML_PATH = "/var/biomodels/dirty/sMtb.xml";
    SBML_PATH = "/var/biomodels/sbml/iTO977.xml";
    //    SBML_PATH = "/var/biomodels/joana/iSB1139.xml";
    //    SBML_PATH = "/var/biomodels/joana/iJL432.xml";
    System.out.println(SBML_PATH);
    XmlSbmlModel xmodel = testModel(SBML_PATH);
    SimpleModelBuilder<String> builder = SimpleModelBuilder.fromXmlSbmlModel(xmodel);
    SimpleMetabolicModel<String> model = builder.build();
    Exporter.exportToFiles(model, "/tmp/trash/test.cpd.tsv", "/tmp/trash/test.rxn.tsv");
    //    SBML_PATH = "/var/biomodels/hvo_26_01_17.xml";

    //    final String SBML_PATH = "D:/var/biomodels/sbml/hsa/MODEL6399676120.xml";
    //    final String SBML_PATH = "D:/var/yeast/sbml/yeast_7.6_cobra.xml";
    //    final String SBML_PATH = "/var/human/Recon2_v02.xml";
    //    final String SBML_PATH = "/var/human/Recon1.xml";
    //    final String SBML_PATH = "D:/var/biomodels/sbml/iSce926/iSce926_M11.xml";
    //    final String SBML_PATH = "D:/var/yeast/sim/iTO977_R_biomass_wild_iAZ900.xml";
    //    

  }

}
