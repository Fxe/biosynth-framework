package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerReactionEntity;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtilsIO;

/**
 * Java XML Stream parser for SBML models
 * @author Filipe Liu
 *
 */
public class XmlStreamSbmlReader {

  private static final Logger logger = LoggerFactory.getLogger(XmlStreamSbmlReader.class);

  private static final String BQBIOL_IS = "is";
  private static final String BQBIOL_IS_ENCODED_BY   = "isEncodedBy";
  private static final String BQBIOL_IS_DESCRIBED_BY = "isDescribedBy";
  private static final String BQBIOL_IS_VERSION_OF   = "isVersionOf";
  private static final String BQBIOL_HAS_PART        = "hasPart";


  private final static String RDF_LIST_ITEM = "li";

  private final static String SBML_MODEL = "model";

  private final static String SBML_COMPARTMENT = "compartment";

  private final static String SBML_SPECIE = "species";

  private final static String SBML_GROUP = "group";
  private final static String SBML_GROUP_MEMBER = "member";
  private final static String SBML_GROUP_LIST_OF_MEMBER = "listOfMembers";

  private final static String SBML_LIST_OF_FLUX_BOUNDS = "listOfFluxBounds";
  private final static String SBML_FLUX_BOUND = "fluxBound";

  private final static String SBML_NOTES = "notes";
  
  private final static String SBML_REACTION = "reaction";
  private final static String SBML_REACTION_LIST_OF_REACTANTS = "listOfReactants";
  private final static String SBML_REACTION_LIST_OF_PRODUCTS = "listOfProducts";
  private final static String SBML_REACTION_SPECIES_REFERENCE = "speciesReference";
  
  private final static String SBML_NOTES_BODY = "body";

  private String data = null;

  public XmlStreamSbmlReader(String path) throws IOException {
    data = BioSynthUtilsIO.readFromFile(new File(path));
    logger.debug("Loaded {} bytes", data.getBytes().length);
  }

  public XmlStreamSbmlReader(InputStream inputStream) throws IOException {
    data = BioSynthUtilsIO.readFromInputStream(inputStream);
    logger.debug("Loaded {} bytes", data.getBytes().length);
  }

  public XmlSbmlModel parse() throws IOException {
    DefaultMetabolicModelEntity mmd = new DefaultMetabolicModelEntity();
    OptfluxContainerReactionEntity reactionEntity = null;

    //		Map<String, DefaultMetaboliteSpecie> specieMap = new HashMap<> ();
    //		Map<String, DefaultMetaboliteSpecie> reactionMap = new HashMap<> ();
    //		Map<String, DefaultMetaboliteSpecie> metaboliteMap = new HashMap<> ();

    List<XmlSbmlGroup>    groups    = new ArrayList<> ();
    List<XmlSbmlSpecie>   species   = new ArrayList<> ();
    List<XmlSbmlReaction> reactions = new ArrayList<> ();
    List<XmlObject> fluxBounds = new ArrayList<> ();
    XmlObject fluxBound = null;
    XmlSbmlModel model = null;
    try {
      XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
      XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
          new ByteArrayInputStream(data.getBytes()));
      while (xmlEventReader.hasNext()) {
        XMLEvent xmlEvent = xmlEventReader.nextEvent();
        if (xmlEvent.isStartDocument()) {
          //System.out.println("start");
        } else if (xmlEvent.isStartElement()) {
          StartElement startElement = xmlEvent.asStartElement();
          String namespace = startElement.getName().getNamespaceURI();
          switch (startElement.getName().getLocalPart()) {
          case SBML_MODEL:
            model = new XmlSbmlModel();
            model.setAttributes(getAttributes(startElement));
            break;
          case SBML_SPECIE: 
            XmlSbmlSpecie xmlSbmlSpecie = parseSpecie(xmlEventReader, startElement);
            species.add(xmlSbmlSpecie);
            break;
          case SBML_REACTION:
            XmlSbmlReaction xmlSbmlReaction = parseReaction(xmlEventReader, startElement);
            reactions.add(xmlSbmlReaction);
            break;
          case SBML_REACTION_SPECIES_REFERENCE:

          case SBML_GROUP:
            XmlSbmlGroup xmlSbmlGroup = parseGroup(xmlEventReader, startElement);
            groups.add(xmlSbmlGroup);
            break;
          case SBML_LIST_OF_FLUX_BOUNDS:
            break;
          case SBML_FLUX_BOUND:
            fluxBound = new XmlObject();
            fluxBound.setAttributes(getAttributes(startElement));
            break;
          default: 
            logger.trace("+?+ " + startElement.getName().getLocalPart());
            break;
          }
        } else if (xmlEvent.isEndElement()) {
          EndElement endElement = xmlEvent.asEndElement();
          switch (endElement.getName().getLocalPart()) {
          //						case GRAPHICS: kgmlEntry.setKgmlGraphics(kgmlGraphics); break;
          //						case RELATION: kgmlRelationList.add(kgmlRelation); break;
          //						case RELATION_SUBTYPE: kgmlRelation.setSubtype(kgmlRelationSubtype); break;
          case SBML_MODEL:
            model.setSpecies(species);
            model.setGroups(groups);
            model.setReactions(reactions);
            model.setFluxBounds(fluxBounds);
            break;
          case SBML_LIST_OF_FLUX_BOUNDS:
            break;
          case SBML_FLUX_BOUND:
            fluxBounds.add(fluxBound);
            break;
          default:
            logger.trace("-?- " + endElement.getName().getLocalPart());
            break;
          }
        } else if (xmlEvent.isEndDocument()) {

        }
      }
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }

    //		System.out.println(species.size());
    //		System.out.println(groups.size());
    //		System.out.println(fluxBounds.size());
    //		model.getAttributes().remove("id");
    //		model.getAttributes().put("entry", "ymn6");
    //		System.out.println(model.getAttributes());
    //		for (XmlSbmlGroup group : groups) {
    //			
    //			String cpdEntry = group.getAttributes().get("id");
    //			String name = group.getAttributes().get("name");
    //			String kind = group.getAttributes().get("kind");
    //			for (XmlObject o : group.getListOfMembers()) {
    //				String spiEntry = o.getAttributes().get("idRef");
    //				String record = String.format("%s,%s,%s,%s", spiEntry, cpdEntry, name, kind);
    //				System.out.println(record);
    //			}
    //		}

    return model;
  }

  public XmlSbmlSpecie parseSpecie(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    logger.debug("+++ reading metabolite specie");
    boolean read = true;
    XmlSbmlSpecie xmlSbmlSpecie = new XmlSbmlSpecie();
    xmlSbmlSpecie.setAttributes(getAttributes(specieStartElement));
    XmlObject xmlObject = new XmlObject();
    XmlObject rdfListItem = null;
    String bqbiolOntology = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        //				String namespace = startElement.getName().getNamespaceURI();

        switch (startElement.getName().getLocalPart()) {
        case BQBIOL_IS:
          bqbiolOntology = BQBIOL_IS;
          if (!xmlSbmlSpecie.getListOfAnnotations().containsKey(bqbiolOntology))
            xmlSbmlSpecie.getListOfAnnotations().put(
                bqbiolOntology, new ArrayList<XmlObject> ());
          break;
        case BQBIOL_IS_ENCODED_BY:
          bqbiolOntology = BQBIOL_IS_ENCODED_BY;
          if (!xmlSbmlSpecie.getListOfAnnotations().containsKey(bqbiolOntology))
            xmlSbmlSpecie.getListOfAnnotations().put(
                bqbiolOntology, new ArrayList<XmlObject> ());
          break;
        case BQBIOL_IS_DESCRIBED_BY:
          bqbiolOntology = BQBIOL_IS_DESCRIBED_BY;
          if (!xmlSbmlSpecie.getListOfAnnotations().containsKey(bqbiolOntology))
            xmlSbmlSpecie.getListOfAnnotations().put(
                bqbiolOntology, new ArrayList<XmlObject> ());
          break;
        case BQBIOL_HAS_PART:
          bqbiolOntology = BQBIOL_HAS_PART;
          if (!xmlSbmlSpecie.getListOfAnnotations().containsKey(bqbiolOntology))
            xmlSbmlSpecie.getListOfAnnotations().put(
                bqbiolOntology, new ArrayList<XmlObject> ());
        case BQBIOL_IS_VERSION_OF:
          bqbiolOntology = BQBIOL_IS_VERSION_OF;
          if (!xmlSbmlSpecie.getListOfAnnotations().containsKey(bqbiolOntology))
            xmlSbmlSpecie.getListOfAnnotations().put(
                bqbiolOntology, new ArrayList<XmlObject> ());
          break;
        case RDF_LIST_ITEM:
          rdfListItem = new XmlObject();
          rdfListItem.getAttributes().putAll(getAttributes(startElement));
          break;
        case SBML_SPECIE: {
          //						specieObject = new XMLObject();  
          //						specieObject.attributes.putAll(getAttributes(startElement));
        } break;
        default: 
          //						LOGGER.trace("+?+ " + startElement.getName().getLocalPart());
          break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
        case BQBIOL_IS:
          bqbiolOntology = null;
          break;
        case BQBIOL_IS_ENCODED_BY:
          bqbiolOntology = null;
          break;
        case BQBIOL_IS_DESCRIBED_BY:
          bqbiolOntology = null;
          break;
        case BQBIOL_HAS_PART:
          bqbiolOntology = null;
          break;
        case RDF_LIST_ITEM:

          //maybe this test should not be here !
          if (!xmlSbmlSpecie.getListOfAnnotations()
              .containsKey(bqbiolOntology)) {
            xmlSbmlSpecie.getListOfAnnotations().put(
                "error", new ArrayList<XmlObject> ());
          }
          //          System.out.println(xmlSbmlSpecie.getAttributes());
          //          System.out.println(bqbiolOntology);
          xmlSbmlSpecie.getListOfAnnotations()
          .get(bqbiolOntology)
          .add(rdfListItem);
          break;
        case SBML_SPECIE:
          read = false;
          break;
        default:
          //						LOGGER.trace("-?- " + endElement.getName().getLocalPart());
          break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }
    logger.debug("--- reading metabolite specie");
    return xmlSbmlSpecie;
  }
  
  public static void initMapList(Map<String, List<XmlObject>> map, String key) {
    if (!map.containsKey(key)) {
      map.put(key, new ArrayList<XmlObject> ());
    }
  }
  
  public Map<String, List<XmlObject>> parseAnnotation(XMLEventReader xmlEventReader) throws XMLStreamException {
    logger.debug("+++ reading annotation");
    Map<String, List<XmlObject>> annotation = new HashMap<> ();
    boolean read = true;
    String bqbiolOntology = null;
    XmlObject rdfListItem = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
//        String namespace = startElement.getName().getNamespaceURI();
        switch (startElement.getName().getLocalPart()) {
          case "RDF": break;
          case "Description": break;
          case "Bag": break;
          case BQBIOL_IS_VERSION_OF:
          case BQBIOL_IS:
          case BQBIOL_IS_DESCRIBED_BY:
            bqbiolOntology = startElement.getName().getLocalPart();
            initMapList(annotation, bqbiolOntology);
            break;
          case RDF_LIST_ITEM:
            rdfListItem = new XmlObject();
            rdfListItem.getAttributes().putAll(getAttributes(startElement));
            break;
          default:
            logger.warn("ignored +++ {}", startElement.getName().getLocalPart());
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          case "RDF": break;
          case "Description": break;
          case "Bag": break;
          case "annotation": read = false; break;
          case BQBIOL_IS_VERSION_OF:
          case BQBIOL_IS:
          case BQBIOL_IS_DESCRIBED_BY:
            bqbiolOntology = null;
            break;
          case RDF_LIST_ITEM:
            annotation.get(bqbiolOntology).add(rdfListItem);
            break;
          default:
            logger.warn("ignored --- {}", endElement.getName().getLocalPart());
            break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }
    logger.debug("--- reading annotation");
    return annotation;
  }
  
  public List<String> parseNotes(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
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
          default: break;
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

  public XmlSbmlReaction parseReaction(XMLEventReader xmlEventReader, StartElement reactionStartElement) throws XMLStreamException {
    logger.debug("+++ reading reaction");
    boolean read = true;
    XmlSbmlReaction sbmlReaction = new XmlSbmlReaction();
    sbmlReaction.setAttributes(getAttributes(reactionStartElement));
    
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        String namespace = startElement.getName().getNamespaceURI();
        switch (startElement.getName().getLocalPart()) {
          case SBML_NOTES: {
            List<String> notes = parseNotes(xmlEventReader, startElement);
            sbmlReaction.setNotes(notes);
            break;
          }
          case SBML_REACTION: {
          //						specieObject = new XMLObject();  
          //						specieObject.attributes.putAll(getAttributes(startElement));
          } break;
          case "annotation":
            sbmlReaction.setListOfAnnotations(parseAnnotation(xmlEventReader));
            break;
          default: 
//            System.out.println("###reaction###what do with " + startElement.getName().getLocalPart()); 
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
        case SBML_REACTION:	read = false;	break;
        default: break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }

    logger.debug("--- reading reaction");
    return sbmlReaction;
  }

  public XmlSbmlGroup parseGroup(XMLEventReader xmlEventReader, StartElement groupStartElement) throws XMLStreamException {
    logger.debug("+++ reading group");
    boolean read = true;
    XmlSbmlGroup sbmlGroup = new XmlSbmlGroup();
    sbmlGroup.setAttributes(getAttributes(groupStartElement));
    List<XmlObject> listOfMembers = null;
    XmlObject member = null;

    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        //				String namespace = startElement.getName().getNamespaceURI();
        switch (startElement.getName().getLocalPart()) {
        case SBML_GROUP_LIST_OF_MEMBER: 
          listOfMembers = new ArrayList<> ();
          break;
        case SBML_GROUP_MEMBER:
          member= new XmlObject();
          member.getAttributes().putAll(getAttributes(startElement));
          break;
        default: 
          logger.trace("+?+ " + startElement.getName().getLocalPart());
          break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
        case SBML_GROUP_LIST_OF_MEMBER:
          sbmlGroup.setListOfMembers(listOfMembers);
        case SBML_GROUP_MEMBER:
          listOfMembers.add(member);
          break;						
        case SBML_GROUP:
          read = false;
          break;
        default:
          logger.trace("-?- " + endElement.getName().getLocalPart());
          break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }
    logger.debug("--- reading group");

    return sbmlGroup;
  }

  public Map<String, String> getAttributes(StartElement startElement) {
    Map<String, String> attributes = new HashMap<> ();
    Iterator<?> i = startElement.getAttributes();
    while (i.hasNext()) {
      Attribute attribute = (Attribute) i.next();
      attributes.put(attribute.getName().getLocalPart(), attribute.getValue());
    }
    return attributes;
  }
}