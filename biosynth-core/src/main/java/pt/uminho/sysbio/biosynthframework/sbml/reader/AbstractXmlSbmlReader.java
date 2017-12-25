package pt.uminho.sysbio.biosynthframework.sbml.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSBaseObject;

public class AbstractXmlSbmlReader {
  
  protected static final String RDF_RDF = "RDF";
  protected static final String RDF_DESCRIPTION = "Description";
  protected static final String RDF_Bag = "Bag";
  
  protected static final String BQBIOL_U_QUALIFIER = "unknownQualifier";
  protected static final String BQBIOL_IS = "is";
  protected static final String BQBIOL_IS_ENCODED_BY   = "isEncodedBy";
  protected static final String BQBIOL_IS_DESCRIBED_BY = "isDescribedBy";
  protected static final String BQBIOL_IS_VERSION_OF   = "isVersionOf";
  protected static final String BQBIOL_HAS_PART        = "hasPart";
  

  
  protected static final String FLUXNS_LIMIT = "limit";
  
  protected static final String DC_RELATION        = "relation";

  protected final static String RDF_LIST_ITEM = "li";

  protected final static String SBML = "sbml";
  protected final static String SBML_MODEL = "model";

  protected final static String SBML_COMPARTMENT = "compartment";

  protected final static String SBML_SPECIE = "species";
  protected final static String SBML_LIST_OF_SPECIES = "listOfSpecies";
  protected final static String SBML_LIST_OF_COMPARTMENTS = "listOfCompartments";
  protected final static String SBML_LIST_OF_REACTIONS = "listOfReactions";

  protected final static String SBML_GROUP = "group";
  protected final static String SBML_GROUP_MEMBER = "member";
  protected final static String SBML_GROUP_LIST_OF_MEMBER = "listOfMembers";

  protected final static String SBML_LIST_OF_FLUX_BOUNDS = "listOfFluxBounds";
  protected final static String SBML_FLUX_BOUND = "fluxBound";

  protected final static String SBML_ANNOTATION = "annotation";
  
  protected final static String SBML_NOTES = "notes";
  protected final static String SBML_LIST_OF_PARAMETERS = "listOfParameters";
  protected final static String SBML_LIST_OF_UNIT_DEFINITIONS = "listOfUnitDefinitions";
  
  protected final static String SBML_UNIT_DEFINITION = "unitDefinition";
  protected final static String SBML_LIST_OF_UNITS = "listOfUnits";
  protected final static String SBML_LIST_OF_UNITS_UNIT = "unit";
  protected final static String SBML_REACTION = "reaction";
  protected final static String SBML_REACTION_LIST_OF_REACTANTS = "listOfReactants";
  protected final static String SBML_REACTION_LIST_OF_PRODUCTS = "listOfProducts";
  protected final static String SBML_REACTION_SPECIES_REFERENCE = "speciesReference";
  protected final static String SBML_REACTION_KINETIC_LAW = "kineticLaw";
  
  protected final static String SBML_PARAMETER = "parameter";
  protected final static String SBML_KINETIC_LAW_MATH = "math";
  
  protected final static String SBML_NOTES_BODY = "body";
  
  public static Map<String, String> getAttributes(StartElement startElement) {
    Map<String, String> attributes = new HashMap<> ();
    Iterator<?> i = startElement.getAttributes();
    while (i.hasNext()) {
      Attribute attribute = (Attribute) i.next();
      attributes.put(attribute.getName().getLocalPart(), attribute.getValue());
    }
    return attributes;
  }
  
  public static void setupObject(XmlObject xo, StartElement startElement) {
    xo.columnNumber = startElement.getLocation().getColumnNumber();
    xo.lineNumber = startElement.getLocation().getLineNumber();
    xo.setAttributes(getAttributes(startElement));
  }
  
  public XmlObject assembleObject(StartElement startElement) {
    XmlObject object = new XmlObject();
    setupObject(object, startElement);
    
    return object;
  }
  
  public static void initMapList(Map<String, List<XmlObject>> map, String key) {
    if (!map.containsKey(key)) {
      map.put(key, new ArrayList<XmlObject> ());
    }
  }
  
  public XmlSBaseObject buildSimpleObject(StartElement startElement) {
    XmlSBaseObject xmlObject = new XmlSBaseObject();
    xmlObject.lineNumber = startElement.getLocation().getLineNumber();
    xmlObject.columnNumber = startElement.getLocation().getColumnNumber();
    xmlObject.setAttributes(getAttributes(startElement));
    
    return xmlObject;
  }
}
