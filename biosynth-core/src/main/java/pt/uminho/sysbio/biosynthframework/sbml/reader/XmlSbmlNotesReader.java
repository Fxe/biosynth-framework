package pt.uminho.sysbio.biosynthframework.sbml.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class XmlSbmlNotesReader extends AbstractXmlSbmlReader implements XmlSbmlComponentReader {
  
  public List<String> parseNotes(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    return this.parseNotes(xmlEventReader, specieStartElement, null);
  }
  
  public List<String> parseNotes(XMLEventReader xmlEventReader, 
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
