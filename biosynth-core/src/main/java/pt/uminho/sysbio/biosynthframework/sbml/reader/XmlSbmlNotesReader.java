package pt.uminho.sysbio.biosynthframework.sbml.reader;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pt.uminho.sysbio.biosynthframework.sbml.XmlUtils;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class XmlSbmlNotesReader extends AbstractXmlSbmlReader implements XmlSbmlComponentReader {
  
  public String parseNotes(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    return this.parseNotes(xmlEventReader, specieStartElement, null);
  }
  
  public String parseNotes(XMLEventReader xmlEventReader, 
      StartElement element, 
      Map<String, Integer> rejectedElements) throws XMLStreamException {
    
//    StringWriter writer = new StringWriter();
//    XMLOutputFactory factory = XMLOutputFactory.newInstance();
//    XMLEventWriter xw = null;
//    XMLEvent event = null;
//    if (element.isStartElement() &&
//        SBML_NOTES.equals(
//            ((StartElement) element).getName().getLocalPart())) {
//      xw = factory.createXMLEventWriter(writer);
//      xw.add(element);
//      while (xmlEventReader.hasNext()) {
//        event = xmlEventReader.nextEvent();
//        if (event.isEndElement()
//            && ((EndElement) event).getName().getLocalPart().equals(SBML_NOTES)) {
//          break;
//        } else if (xw != null) {
//          xw.add(event);
//        }
//      }
//    }
//    
//    xw.close();
    
    return XmlUtils.extractElementAsXml(xmlEventReader, element, SBML_NOTES);
  }
  
  public List<String> parseNotesOld(XMLEventReader xmlEventReader, 
                                 StartElement specieStartElement, 
                                 Map<String, Integer> rejectedElements) throws XMLStreamException {
    boolean read = true;
    boolean readBody = false;
    List<String> notes = new ArrayList<> ();
    String note = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();

        switch (startElement.getName().getLocalPart()) {
          case "p":
          case SBML_NOTES_BODY: readBody = true; break;
          default:
            if (rejectedElements != null) {
              CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            }
            break;
        }
        if (readBody) {
          note = String.format("<%s>", startElement.getName().getLocalPart());
        }
      }
      if (xmlEvent.isCharacters()) {
        String data = xmlEvent.asCharacters().getData();
        if (readBody) {
          note += data.trim();
        }
      }
      
      if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        if (readBody) {
          note += String.format("</%s>", endElement.getName().getLocalPart());
          notes.add(note);
          note = null;
        }
        switch (endElement.getName().getLocalPart()) {
          case SBML_NOTES: read = false; break;
          case "p":
          case SBML_NOTES_BODY: readBody = false; break;
          default: break;
        }

      }
    }
    return notes;
  }
  

}
