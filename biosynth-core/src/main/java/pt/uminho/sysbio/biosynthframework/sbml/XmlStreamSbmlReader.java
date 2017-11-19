package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
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

import pt.uminho.sysbio.biosynthframework.MultiNodeTree;
import pt.uminho.sysbio.biosynthframework.Operator;
import pt.uminho.sysbio.biosynthframework.sbml.reader.AbstractXmlSbmlReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.XmlSbmlAnnotationReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.XmlSbmlKvdReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.XmlSbmlNotesReader;
import pt.uminho.sysbio.biosynthframework.sbml.reader.XmlSbmlSpeciesReader;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;

/**
 * Java XML reader for SBML models
 * @author Filipe Liu
 *
 */
public class XmlStreamSbmlReader extends AbstractXmlSbmlReader {

  private static final Logger logger = LoggerFactory.getLogger(XmlStreamSbmlReader.class);
 
  private XmlSbmlKvdReader kvdReader = new XmlSbmlKvdReader();
  private XmlSbmlNotesReader notesReader = new XmlSbmlNotesReader();
  private XmlSbmlAnnotationReader annotationReader = new XmlSbmlAnnotationReader(kvdReader);
  private XmlSbmlSpeciesReader speciesReader = new XmlSbmlSpeciesReader(notesReader, annotationReader);

  private String data;
  public boolean decodeUtf8 = true;
  
  public Map<String, Integer> rejectedElements = new HashMap<> ();

  public XmlStreamSbmlReader(String path) throws IOException {
    String xmlString = IOUtils.readFromFile(new File(path));
    data = xmlString;
    if (decodeUtf8) {
      data = decode(xmlString);
    }
    logger.trace("Loaded {} bytes", data.getBytes().length);
  }

  public XmlStreamSbmlReader(InputStream inputStream) throws IOException {
    String xmlString = IOUtils.readFromInputStream(inputStream);
    data = xmlString;
    if (decodeUtf8) {
      data = decode(xmlString);
    }
    logger.trace("Loaded {} bytes", data.getBytes().length);
  }
  
  public String decode(String str) throws CharacterCodingException {
    CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
    decoder.replaceWith(" ");
    decoder.onMalformedInput(CodingErrorAction.REPLACE);
    decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    ByteBuffer bb = ByteBuffer.wrap(str.getBytes());
    CharBuffer buffer = decoder.decode(bb);
    return buffer.toString();
  }

  public XmlSbmlModel parse() throws IOException {
//    DefaultMetabolicModelEntity mmd = new DefaultMetabolicModelEntity();
//    OptfluxContainerReactionEntity reactionEntity = null;

    //		Map<String, DefaultMetaboliteSpecie> specieMap = new HashMap<> ();
    //		Map<String, DefaultMetaboliteSpecie> reactionMap = new HashMap<> ();
    //		Map<String, DefaultMetaboliteSpecie> metaboliteMap = new HashMap<> ();

    List<XmlSbmlCompartment> compartments = new ArrayList<> ();
    List<XmlSbmlGroup>       groups       = new ArrayList<> ();
    List<XmlSbmlSpecie>      species      = new ArrayList<> ();
    List<XmlSbmlReaction>    reactions    = new ArrayList<> ();
    List<XmlObject>          fluxBounds = new ArrayList<> ();
    List<XmlObject> listOfGeneProducts = new ArrayList<> ();
    List<XmlObject> listOfParameters = new ArrayList<> ();
    Map<String, String> sbmlAttributes = new HashMap<> ();
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
          String startElementLocalPart = startElement.getName().getLocalPart();
          String namespace = startElement.getName().getNamespaceURI();
          logger.trace("+ {} {}", namespace, startElementLocalPart);
          switch (startElementLocalPart) {
          case SBML:
            sbmlAttributes = getAttributes(startElement);
            break;
          case SBML_MODEL:
            model = new XmlSbmlModel();
            model.columnNumber = startElement.getLocation().getColumnNumber();
            model.lineNumber = startElement.getLocation().getLineNumber();
            model.setAttributes(getAttributes(startElement));
            break;
          case SBML_NOTES:
            if (model != null) {
              model.setNotes(notesReader.parseNotes(xmlEventReader, startElement, rejectedElements));
            }
            break;
          case SBML_LIST_OF_UNIT_DEFINITIONS:
            model.units.addAll(parseSbmlUnits(xmlEventReader, startElement, SBML_LIST_OF_UNIT_DEFINITIONS));
            break;
          case SBML_LIST_OF_COMPARTMENTS: break;
          case SBML_LIST_OF_SPECIES: break;
          case SBML_LIST_OF_REACTIONS: break;

          case SBML_COMPARTMENT:
            XmlSbmlCompartment xmlSbmlCompartment = parseCompartment(xmlEventReader, startElement);
            compartments.add(xmlSbmlCompartment);
            break;
          case SBML_SPECIE: 
            XmlSbmlSpecie xmlSbmlSpecie = speciesReader.parseSpecie(xmlEventReader, startElement, rejectedElements);
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
          case SBML_LIST_OF_PARAMETERS:
            List<XmlObject> xmlObjects = parseListOfParameters(xmlEventReader);
            listOfParameters.addAll(xmlObjects);
            break;
            //fbc
          case "listOfGeneProducts":
            listOfGeneProducts.addAll(parseListOfGeneProducts(xmlEventReader));
//            List<XmlObject> xmlObjects = parseListOfParameters(xmlEventReader);
            break;
          default: 
            logger.trace("+?+ {}", startElement.getName().getLocalPart());
            logger.trace("+?+ {} {}", startElement.getName().getNamespaceURI(), startElement.getName().getPrefix());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
          }
        } else if (xmlEvent.isEndElement()) {
          EndElement endElement = xmlEvent.asEndElement();
          switch (endElement.getName().getLocalPart()) {
          //						case GRAPHICS: kgmlEntry.setKgmlGraphics(kgmlGraphics); break;
          //						case RELATION: kgmlRelationList.add(kgmlRelation); break;
          //						case RELATION_SUBTYPE: kgmlRelation.setSubtype(kgmlRelationSubtype); break;
          case SBML_MODEL:
            model.setCompartments(compartments);
            model.setSpecies(species);
            model.setGroups(groups);
            model.setReactions(reactions);
            model.setFluxBounds(fluxBounds);
            model.setSbmlAttributes(sbmlAttributes);
            model.setListOfParameters(listOfParameters);
            model.setListOfGeneProducts(listOfGeneProducts);
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
  
  public XmlSbmlCompartment parseCompartment(XMLEventReader xmlEventReader, 
      StartElement compartmentStartElement) throws XMLStreamException {
    logger.trace("+++ <compartment> reading compartment");
    boolean read = true;
    XmlSbmlCompartment xmlSbmlCompartment = new XmlSbmlCompartment();
    xmlSbmlCompartment.lineNumber = compartmentStartElement.getLocation().getLineNumber();
    xmlSbmlCompartment.columnNumber = compartmentStartElement.getLocation().getColumnNumber();
    xmlSbmlCompartment.setAttributes(getAttributes(compartmentStartElement));
    
//    XmlObject rdfListItem = null;
//    String bqbiolOntology = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        String startElementLocalPart = startElement.getName().getLocalPart();
        logger.trace(" ++ <{}> reading metabolite specie", startElementLocalPart);
        //              String namespace = startElement.getName().getNamespaceURI();
        switch (startElementLocalPart) {
        case SBML_COMPARTMENT: {
          //                        specieObject = new XMLObject();  
          //                        specieObject.attributes.putAll(getAttributes(startElement));
        } break;
        default: 
          //                        LOGGER.trace("+?+ " + startElement.getName().getLocalPart());
          CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
          break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
        case SBML_COMPARTMENT:
          read = false;
          break;
        default:
          logger.trace("-?- " + endElement.getName().getLocalPart());
          break;
        }
      } else if (xmlEvent.isEndDocument()) {

      }
    }
    logger.trace("--- reading metabolite specie");
    return xmlSbmlCompartment;
  }
  

  
 
  
  public List<XmlObject> parseListOfGeneProducts(XMLEventReader xmlEventReader) throws XMLStreamException {
    List<XmlObject> result = new ArrayList<> ();
    boolean read = true;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        switch (startElement.getName().getLocalPart()) {
          case "geneProduct":
            result.add(buildSimpleObject(startElement));
            break;
//            XmlObject parameter = new XmlObject();
//            parameter.lineNumber = startElement.getLocation().getLineNumber();
//            parameter.columnNumber = startElement.getLocation().getColumnNumber();
//            parameter.setAttributes(getAttributes(startElement));
//            listOfParameters.add(parameter);
//            break;
          default:
            logger.trace("ignored +++ {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          case "geneProduct": break;
          case "listOfGeneProducts": read = false; break;
          default:
//            logger.warn("ignored --- {}", endElement.getName().getLocalPart());
            break;
        }
      }
    }
    
    return result;
  }
  
  //TODO:fix this
  public List<XmlObject> parseListOfObjectives(XMLEventReader xmlEventReader) throws XMLStreamException {
    List<XmlObject> result = new ArrayList<> ();
    boolean read = true;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        switch (startElement.getName().getLocalPart()) {
//          case "geneProduct":
//            result.add(buildSimpleObject(startElement));
//            break;
//            XmlObject parameter = new XmlObject();
//            parameter.lineNumber = startElement.getLocation().getLineNumber();
//            parameter.columnNumber = startElement.getLocation().getColumnNumber();
//            parameter.setAttributes(getAttributes(startElement));
//            listOfParameters.add(parameter);
//            break;
          default:
            logger.trace("ignored +++ {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
//          case "geneProduct": break;
          case "listOfObjectives": read = false; break;
          default:
//            logger.warn("ignored --- {}", endElement.getName().getLocalPart());
            break;
        }
      }
    }
    
    return result;
  }
  
  public List<XmlObject> parseListOfParameters(XMLEventReader xmlEventReader) throws XMLStreamException {
    List<XmlObject> listOfParameters = new ArrayList<> ();
    logger.trace("+++ reading listOfParameters");
    boolean read = true;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        switch (startElement.getName().getLocalPart()) {
          case SBML_PARAMETER:
//            XmlObject parameter = new XmlObject();
//            parameter.lineNumber = startElement.getLocation().getLineNumber();
//            parameter.columnNumber = startElement.getLocation().getColumnNumber();
//            parameter.setAttributes(getAttributes(startElement));
            listOfParameters.add(buildSimpleObject(startElement));
            break;
          default:
            logger.trace("ignored +++ {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          case SBML_PARAMETER: break;
          case SBML_LIST_OF_PARAMETERS: read = false; break;
          default:
            logger.warn("ignored --- {}", endElement.getName().getLocalPart());
            break;
        }
      }
    }
    
    return listOfParameters;
  }
  


  
  public List<XmlObject> parseKineticLaw(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    boolean read = true;
    List<XmlObject> parameters = new ArrayList<> ();
    
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();

        switch (startElement.getName().getLocalPart()) {
          case SBML_PARAMETER:
            parameters.add(buildSimpleObject(startElement));
            break;
          case SBML_KINETIC_LAW_MATH:
            //for now do nothing !
            break;
          default:
            break;
        }
//        if (readBody) {
//          note = String.format("<%s>", startElement.getName().getLocalPart());
//        }
      }
//      if (xmlEvent.isCharacters()) {
//        String data = xmlEvent.asCharacters().getData();
//        if (readBody) {
//          note += data.trim();
//        }
//      }
      
      if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
//        if (readBody) {
//          note += String.format("</%s>", endElement.getName().getLocalPart());
//          notes.add(note);
//          note = null;
//        }
        switch (endElement.getName().getLocalPart()) {
          case SBML_REACTION_KINETIC_LAW: read = false; break;
          default: break;
        }

      }
    }
    return parameters;
  }

  public List<XmlObject> parseSpeciesReference(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    List<XmlObject> list = new ArrayList<> ();
    boolean read = true;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();

        switch (startElement.getName().getLocalPart()) {
          case SBML_REACTION_SPECIES_REFERENCE:
            list.add(buildSimpleObject(startElement));
            break;
          default: break;
        }
      }
      
      if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          case SBML_REACTION_LIST_OF_PRODUCTS: read = false; break;
          case SBML_REACTION_LIST_OF_REACTANTS: read = false; break;
          default: break;
        }
      }
    }
    return list;
  }
  
  public List<XmlObject> parseModifiers(XMLEventReader xmlEventReader, StartElement specieStartElement) throws XMLStreamException {
    List<XmlObject> result = new ArrayList<> ();
    while (xmlEventReader.hasNext()) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        logger.trace("+++ {}", startElement.getName().getLocalPart());
        switch (startElement.getName().getLocalPart()) {
          case "modifierSpeciesReference":
            result.add(buildSimpleObject(startElement));
            break;
          default:
            logger.trace("??? {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      }
      
      if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
//          case SBML_REACTION_LIST_OF_PRODUCTS: read = false; break;
//          case SBML_REACTION_LIST_OF_REACTANTS: read = false; break;
          default: break;
        }
        boolean end = xmlEvent.isEndElement() && 
            xmlEvent.asEndElement().getName().getLocalPart().equals("listOfModifiers");

        if (end) {
          logger.trace("end");
          break;
        }
      }
    }
    return result;
  }
  
  public MultiNodeTree<Object> parseFbcGeneProductAssociation(XMLEventReader xmlEventReader, StartElement reactionStartElement) throws XMLStreamException {
    MultiNodeTree<Object> tree = null;
    while (xmlEventReader.hasNext()) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        logger.trace("+++ {}", startElement.getName().getLocalPart());
        switch (startElement.getName().getLocalPart()) {
          case "and":
            MultiNodeTree<Object> andTree = new MultiNodeTree<Object>(Operator.AND);
            if (tree != null) {
              tree.addChild(andTree);
            }
            tree = andTree;
            break;
          case "or":
            MultiNodeTree<Object> orTree = new MultiNodeTree<Object>(Operator.OR);
            if (tree != null) {
              tree.addChild(orTree);
            }
            tree = orTree;
            break;
          case "geneProductRef":
            MultiNodeTree<Object> leaf = new MultiNodeTree<Object>(getAttributes(startElement));
//            System.out.println(getAttributes(startElement));
            if (tree == null) {
              tree = leaf;
            } else {
              tree.addChild(leaf);
            }
            break;
          default:
            logger.trace("??? {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        switch (endElement.getName().getLocalPart()) {
          // and|or go back !
          case "and":
          case "or":
            if (tree.parent != null) {
              tree = tree.parent;
            }
            break;
        }
        boolean end = xmlEvent.isEndElement() && 
            xmlEvent.asEndElement().getName().getLocalPart().equals("geneProductAssociation");

        if (end) {
          logger.trace("end");
          break;
        }
      }
    }
    
    return tree;
  }
  
  public XmlSbmlReaction parseReaction(XMLEventReader xmlEventReader, StartElement reactionStartElement) throws XMLStreamException {
    logger.trace("+++ reading reaction");
    boolean read = true;
//    List<XmlObject> listOfReactants = new ArrayList<> ();
//    List<XmlObject> listOfProducts = new ArrayList<> ();
    XmlSbmlReaction sbmlReaction = new XmlSbmlReaction();
    sbmlReaction.lineNumber = reactionStartElement.getLocation().getLineNumber();
    sbmlReaction.columnNumber = reactionStartElement.getLocation().getColumnNumber();
    sbmlReaction.setAttributes(getAttributes(reactionStartElement));
    
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        
        String namespace = startElement.getName().getNamespaceURI();
        logger.trace("{}", namespace);
        switch (startElement.getName().getLocalPart()) {
          case SBML_NOTES:
            List<String> notes = notesReader.parseNotes(xmlEventReader, startElement, rejectedElements);
            sbmlReaction.setNotes(notes);
            break;
//          case SBML_REACTION: {
//          //						specieObject = new XMLObject();  
//          //						specieObject.attributes.putAll(getAttributes(startElement));
//          } break;
          case "annotation":
            sbmlReaction.setListOfAnnotations(annotationReader.parseAnnotation(xmlEventReader, rejectedElements));
            break;
          case SBML_REACTION_LIST_OF_REACTANTS:
            sbmlReaction.getListOfReactants().addAll(
                parseSpeciesReference(xmlEventReader, startElement));
            break;
          case SBML_REACTION_LIST_OF_PRODUCTS:
            sbmlReaction.getListOfProducts().addAll(
                parseSpeciesReference(xmlEventReader, startElement));
            break;
          case SBML_REACTION_KINETIC_LAW:
            sbmlReaction.getListOfParameters().addAll(parseKineticLaw(xmlEventReader, startElement));
            break;
          case "geneProductAssociation":
            MultiNodeTree<Object> gpr = parseFbcGeneProductAssociation(xmlEventReader, startElement);
            sbmlReaction.setGpr(gpr);
            break;
          case "listOfModifiers":
            sbmlReaction.getListOfModifiers().addAll(parseModifiers(xmlEventReader, startElement));
            break;
          default:
            logger.trace("??? {}", startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
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

    logger.trace("--- reading reaction");
    return sbmlReaction;
  }

  

  

  
  public List<XmlUnitDefinition> parseSbmlUnits(XMLEventReader xmlEventReader, StartElement groupStartElement, String end) throws XMLStreamException {
    List<XmlUnitDefinition> units = new ArrayList<> ();
    boolean read = true;
    XmlUnitDefinition definition = new XmlUnitDefinition();
    XmlObject unit = null;
    while (xmlEventReader.hasNext() && read) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        switch (startElement.getName().getLocalPart()) {
          case SBML_UNIT_DEFINITION:
            definition = new XmlUnitDefinition();
            setupObject(definition, startElement);
            break;
          case SBML_LIST_OF_UNITS:
            break;
          case SBML_LIST_OF_UNITS_UNIT:
            unit = assembleObject(startElement);
            definition.listOfUnits.add(unit);
            break;
          case SBML_ANNOTATION:
            Map<String, List<XmlObject>> annotation = annotationReader.parseAnnotation(xmlEventReader, rejectedElements);
            definition.setListOfAnnotations(annotation);
            break;
          default:
            logger.trace("+?+ " + startElement.getName().getLocalPart());
            CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        String localPart = endElement.getName().getLocalPart();
        if (localPart.equals(end)) {
          read = false;
        }
        switch (localPart) {
          case SBML_LIST_OF_UNITS:
            units.add(definition);
            break;
          default:
            logger.trace("-?- " + endElement.getName().getLocalPart());
            break;
        }
      }
    }
    
    return units;
  }
  
  public XmlSbmlGroup parseGroup(XMLEventReader xmlEventReader, StartElement groupStartElement) throws XMLStreamException {
    logger.trace("+++ reading group");
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
          CollectionUtils.increaseCount(rejectedElements, startElement.getName().getLocalPart(), 1);
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
    logger.trace("--- reading group");

    return sbmlGroup;
  }


}