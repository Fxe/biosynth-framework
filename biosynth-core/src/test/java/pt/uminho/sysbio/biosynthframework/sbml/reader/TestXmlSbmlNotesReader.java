package pt.uminho.sysbio.biosynthframework.sbml.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestXmlSbmlNotesReader {

  private XmlSbmlNotesReader reader = null;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    reader = new XmlSbmlNotesReader();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testReadNotes() throws XMLStreamException {
    String data = 
        "<notes><body xmlns=\"http://www.w3.org/1999/xhtml\">"
      + "<p>GENE_ASSOCIATION: FAS1_KLULA_KLLA0B02717g</p>"
      + "<p>SUBSYSTEM: Biotin metabolism</p>"
      + "<p>PROTEIN_CLASS: 4.2.1.59</p>"
      + "</body></notes>";
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
        new ByteArrayInputStream(data.getBytes()));
    XMLEvent event = xmlEventReader.nextEvent();
    assertTrue(event.isStartDocument());
    event = xmlEventReader.nextEvent();
    assertTrue(event.isStartElement());
    
    String notes = reader.parseNotes(xmlEventReader, event.asStartElement());

    assertEquals(data, notes);
  }

}
