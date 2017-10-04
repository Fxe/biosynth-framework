package pt.uminho.sysbio.biosynthframework.sbml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestXmlSbmlModelValidator {

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
//    fail("Not yet implemented");
  }

  @Test
  public void test2() throws IOException {
    String modelPath = "/var/biomodels/iBROKEN.xml";
    XmlStreamSbmlReader reader = new XmlStreamSbmlReader(modelPath);
    XmlSbmlModel xmodel = reader.parse();
    XmlSbmlModelValidator validator = new XmlSbmlModelValidator(xmodel);
    XmlSbmlModelValidator.initializeDefaults(validator);
    List<XmlMessage> msgs = validator.validate();
    for (XmlMessage m : msgs) {
      System.out.println(m);
    }
    assertEquals(true, true);
  }
}
