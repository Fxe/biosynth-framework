package pt.uminho.sysbio.biosynthframework.sbml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestXmlSbmlModelAutofix {

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

  }

  @Test
  public void test1() throws IOException {
    String modelPath = "/var/biomodels/iBROKEN.xml";
    modelPath = "/var/biomodels/joana/iCAC490.xml"; //iCAC490.xml
//    modelPath = "/var/biomodels/joana/iCyc792.xml"; //iCyc792.xml
    modelPath = "/var/biomodels/2batch/iJB785.xml"; //iJB785.xml
    modelPath = "/var/biomodels/test/iTZ479.xml"; //iTZ479.xml
//    modelPath = "/var/biomodels/joana_bigg/iCHOv1.xml"; //iCHOv1.xml
    
    XmlStreamSbmlReader reader = new XmlStreamSbmlReader(modelPath);
    XmlSbmlModel xmodel = reader.parse();
    XmlSbmlModelValidator validator = new XmlSbmlModelValidator(xmodel);
    XmlSbmlModelValidator.initializeDefaults(validator);
    List<XmlMessage> msgs = validator.validate();
    for (XmlMessage m : msgs) {
      System.out.println(m);
    }
    
    System.out.println("----------------------------------");
    
    XmlSbmlModelAutofix autofix = new XmlSbmlModelAutofix();
    autofix.group.put(MessageCategory.STOICH_NO_VALUE, 
        new XmlMessageGroup(MessageCategory.STOICH_NO_VALUE, MessageType.WARN, "assume value 1"));
    autofix.fix(xmodel, msgs);
    
    System.out.println("-------FIX------------------------");
    
    for (XmlMessage m : autofix.messages) {
      System.out.println(m);
    }
    for (XmlMessageGroup g : autofix.group.values()) {
      System.out.println(g);
    }
    
    System.out.println("-------FINAL----------------------");
    XmlSbmlModelValidator validator2 = new XmlSbmlModelValidator(xmodel);
    XmlSbmlModelValidator.initializeDefaults(validator2);
    List<XmlMessage> msgs2 = validator2.validate();
    for (XmlMessage m : msgs2) {
      System.out.println(m);
    }
    assertEquals(true, true);
  }
}
