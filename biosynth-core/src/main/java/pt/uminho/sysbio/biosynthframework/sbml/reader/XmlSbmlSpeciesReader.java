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
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class XmlSbmlSpeciesReader extends AbstractXmlSbmlReader implements XmlSbmlComponentReader {
  
  private static final Logger logger = LoggerFactory.getLogger(XmlSbmlSpeciesReader.class);
  
  private final XmlSbmlNotesReader notesReader;
  private final XmlSbmlAnnotationReader annotationReader;
  
  public XmlSbmlSpeciesReader(XmlSbmlNotesReader notesReader, 
                              XmlSbmlAnnotationReader annotationReader) {
    this.notesReader = notesReader;
    this.annotationReader = annotationReader;
  }
  
  public XmlSbmlSpecie parseSpecie(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    return parseSpecie(xmlEventReader, specieStartElement, null);
  }
  
  public XmlSbmlSpecie parseSpecie(XMLEventReader xmlEventReader, 
                                   StartElement specieStartElement, 
                                   Map<String, Integer> rejectedElements) throws XMLStreamException {
    logger.trace("+++ <species> reading metabolite specie");
    boolean read = true;
    XmlSbmlSpecie xmlSbmlSpecie = new XmlSbmlSpecie();
    setupObject(xmlSbmlSpecie, specieStartElement);
//    xmlSbmlSpecie.lineNumber = specieStartElement.getLocation().getLineNumber();
//    xmlSbmlSpecie.columnNumber = specieStartElement.getLocation().getColumnNumber();
//    xmlSbmlSpecie.setAttributes(getAttributes(specieStartElement));
//    XmlObject xmlObject = new XmlObject();
    XmlObject rdfListItem = null;
    String bqbiolOntology = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        String startElementLocalPart = startElement.getName().getLocalPart();
        logger.trace(" ++ <{}> reading metabolite specie", startElementLocalPart);
        //              String namespace = startElement.getName().getNamespaceURI();
        switch (startElementLocalPart) {
        case SBML_NOTES: {
          List<String> notes = notesReader.parseNotes(xmlEventReader, startElement, rejectedElements);
          xmlSbmlSpecie.setNotes(notes);
          break;
        }
        case SBML_ANNOTATION:
          Map<String, List<XmlObject>> annotation = annotationReader.parseAnnotation(xmlEventReader, rejectedElements);
          xmlSbmlSpecie.setListOfAnnotations(annotation);
          break;
        case DC_RELATION:
        case BQBIOL_IS_VERSION_OF:
        case BQBIOL_HAS_PART:
        case BQBIOL_IS_DESCRIBED_BY:
        case BQBIOL_IS_ENCODED_BY:
        case BQBIOL_IS:
        case BQBIOL_U_QUALIFIER:
          bqbiolOntology = startElementLocalPart;
          if (!xmlSbmlSpecie.getListOfAnnotations().containsKey(bqbiolOntology))
            xmlSbmlSpecie.getListOfAnnotations().put(
                bqbiolOntology, new ArrayList<XmlObject> ());
          break;
        case RDF_LIST_ITEM:
          rdfListItem = assembleObject(startElement);
          rdfListItem.getAttributes().putAll(getAttributes(startElement));
          break;
        case SBML_SPECIE: {
          //                        specieObject = new XMLObject();  
          //                        specieObject.attributes.putAll(getAttributes(startElement));
        } break;
        default: 
          logger.trace("+?+ " + startElement.getName().getLocalPart());
          if (rejectedElements != null) {
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
          }
          
          break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
        case BQBIOL_IS:
        case BQBIOL_IS_ENCODED_BY:
        case BQBIOL_IS_DESCRIBED_BY:
        case BQBIOL_HAS_PART:
        case DC_RELATION:
          bqbiolOntology = null;
          break;
        case RDF_LIST_ITEM:
//          System.out.println(bqbiolOntology);
//          System.out.println(rdfListItem.getAttributes());
          //maybe this test should not be here !
          if (!xmlSbmlSpecie.getListOfAnnotations()
              .containsKey(bqbiolOntology)) {
            xmlSbmlSpecie.getListOfAnnotations().put(
                "error", new ArrayList<XmlObject> ());
          }
          //          System.out.println(xmlSbmlSpecie.getAttributes());
          //          System.out.println(bqbiolOntology);
//          System.out.println(bqbiolOntology);
//          System.out.println(rdfListItem);
          xmlSbmlSpecie.getListOfAnnotations()
                       .get(bqbiolOntology)
                       .add(rdfListItem);
          break;
        case SBML_SPECIE:
          read = false;
          break;
        default:
          //                        LOGGER.trace("-?- " + endElement.getName().getLocalPart());
          break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }
    logger.trace("--- reading metabolite specie");
    return xmlSbmlSpecie;
  }
}
