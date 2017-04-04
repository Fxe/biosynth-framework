package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

  public void testModel(String SBML_PATH) throws IOException {
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
  System.out.println(sbmlReader.rejectedElements);

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
  Function<Object, String> f = new Function<Object, String>() {
    
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
    rxnMap.put(reaction.getAttributes().get("id"), reaction);
    System.out.println(SbmlUtils.gprTreeToString(reaction.getGpr(), f));
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
    System.out.println(SBML_PATH);
    
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
