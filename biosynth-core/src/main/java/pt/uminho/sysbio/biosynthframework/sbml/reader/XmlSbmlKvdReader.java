package pt.uminho.sysbio.biosynthframework.sbml.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class XmlSbmlKvdReader extends AbstractXmlSbmlReader implements XmlSbmlComponentReader {
  
  private static final Logger logger = LoggerFactory.getLogger(XmlSbmlKvdReader.class);
  
  protected static final String KEY_VALUE_DATA_LIST = "listOfKeyValueData";
  protected static final String KEY_VALUE_DATA_ITEM = "data";
  
  public List<XmlObject> parseKeyValueDataList(XMLEventReader xmlEventReader) throws XMLStreamException {
    return this.parseKeyValueDataList(xmlEventReader, null);
  }
  
  public List<XmlObject> parseKeyValueDataList(XMLEventReader xmlEventReader, Map<String, Integer> rejectedElements) throws XMLStreamException {
    List<XmlObject> kvd = new ArrayList<> ();
    boolean read = true;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        switch (startElement.getName().getLocalPart()) {
          case KEY_VALUE_DATA_ITEM:
            XmlObject xo = new XmlObject();
            xo.setAttributes(getAttributes(startElement));
            kvd.add(xo);
            break;
          default:
            logger.trace("ignored +++ {}", startElement.getName().getLocalPart());
            if (rejectedElements != null) {
              CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            }
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          case KEY_VALUE_DATA_ITEM: break;
          case KEY_VALUE_DATA_LIST:
            read = false;
            break;
          default:
            logger.trace("ignored --- {}", endElement.getName().getLocalPart());
            break;
        }
      }
    }
    return kvd;
  }
}
