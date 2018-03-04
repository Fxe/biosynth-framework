package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XmlUtils {
  
  public static String extractElementAsXml(XMLEventReader xmlEventReader, 
                                           StartElement element,
                                           String elementTag) throws XMLStreamException {
    StringWriter writer = new StringWriter();
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    XMLEventWriter xw = null;
    XMLEvent event = null;
    if (element.isStartElement() &&
        elementTag.equals(
            ((StartElement) element).getName().getLocalPart())) {
      xw = factory.createXMLEventWriter(writer);
      xw.add(element);
      while (xmlEventReader.hasNext()) {
        event = xmlEventReader.nextEvent();
        if (event.isEndElement()
            && ((EndElement) event).getName().getLocalPart().equals(elementTag)) {
          break;
        } else if (xw != null) {
          xw.add(event);
        }
      }
    }
    
    xw.close();
    
    return writer.toString();
  }
}
