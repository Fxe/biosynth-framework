package pt.uminho.sysbio.biosynthframework.sbml.reader;

import java.util.ArrayList;
import java.util.HashMap;
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

public class XmlSbmlAnnotationReader extends AbstractXmlSbmlReader implements XmlSbmlComponentReader {
  
  protected static final String KEY_VALUE_DATA_LIST = "listOfKeyValueData";
  protected static final String KEY_VALUE_DATA_ITEM = "data";
  
  private final XmlSbmlKvdReader kvdReader;
  
  public XmlSbmlAnnotationReader(XmlSbmlKvdReader kvdReader) {
    this.kvdReader = kvdReader;
  }
  
  private static final Logger logger = LoggerFactory.getLogger(XmlSbmlAnnotationReader.class);
  
  public Map<String, List<XmlObject>> parseAnnotation(XMLEventReader xmlEventReader) throws XMLStreamException {
    return parseAnnotation(xmlEventReader, null);
  }
  
  public Map<String, List<XmlObject>> parseAnnotation(XMLEventReader xmlEventReader, 
      Map<String, Integer> rejectedElements) throws XMLStreamException {
    logger.trace("+++ reading annotation");
    Map<String, List<XmlObject>> annotation = new HashMap<> ();
    boolean read = true;
    String bqbiolOntology = null;
//    XmlObject rdfListItem_ = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        
        switch (startElement.getName().getLocalPart()) {
          case KEY_VALUE_DATA_LIST:
            List<XmlObject> kvdArray = kvdReader.parseKeyValueDataList(xmlEventReader, rejectedElements);
            if (!annotation.containsKey(KEY_VALUE_DATA_LIST)) {
              annotation.put(KEY_VALUE_DATA_LIST, new ArrayList<XmlObject> ());
            }
            annotation.get(KEY_VALUE_DATA_LIST).addAll(kvdArray);
            break;
          case RDF_RDF: break;
          case RDF_DESCRIPTION: break;
          case RDF_Bag: break;
          case FLUXNS_LIMIT:
            bqbiolOntology = "fluxnsLimit";
            initMapList(annotation, bqbiolOntology);
            XmlObject fluxNsLimit = buildSimpleObject(startElement);
            annotation.get(bqbiolOntology).add(fluxNsLimit);
//            System.out.println(annotation);
//            if (startElement.getNamespaces().hasNext()) {
//              Object o = startElement.getNamespaceURI("flux");
//              System.out.println("FOUND LIMIT ! " + o);
//            }
//            
//            System.out.println("FOUND LIMIT ! " + startElement.getName().getNamespaceURI());
//            System.out.println(getAttributes(startElement));
            break;
          case BQBIOL_U_QUALIFIER:
          case BQBIOL_IS_ENCODED_BY:
          case BQBIOL_IS_VERSION_OF:
          case BQBIOL_IS:
          case DC_RELATION:
          case BQBIOL_IS_DESCRIBED_BY:
            bqbiolOntology = startElement.getName().getLocalPart();
            initMapList(annotation, bqbiolOntology);
            break;
          case RDF_LIST_ITEM:
            if (bqbiolOntology == null) {
              logger.trace("unknown bqbiolOntology");
            } else {
              annotation.get(bqbiolOntology).add(assembleObject(startElement));
            }
            
//            rdfListItem = new XmlObject();
//            rdfListItem.getAttributes().putAll(getAttributes(startElement));
            break;
          default:
            logger.trace("ignored +++ {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          case RDF_RDF: break;
          case RDF_DESCRIPTION: break;
          case RDF_Bag: break;
          case FLUXNS_LIMIT:
          case BQBIOL_IS_VERSION_OF:
          case BQBIOL_IS:
          case DC_RELATION:
          case BQBIOL_IS_DESCRIBED_BY:
            bqbiolOntology = null;
            break;
          case RDF_LIST_ITEM:
//            annotation.get(bqbiolOntology).add(rdfListItem);
            break;
          case SBML_ANNOTATION: read = false; break;
          default:
            logger.trace("ignored --- {}", endElement.getName().getLocalPart());
            break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }
    logger.trace("--- reading annotation");
    return annotation;
  }  
}
