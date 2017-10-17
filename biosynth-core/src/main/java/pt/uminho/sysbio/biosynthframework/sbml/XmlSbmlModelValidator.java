package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

public class XmlSbmlModelValidator {
  
  public static Map<String, MessageType> xspiAttributes() {
    String[] attr = new String[] {
        "speciesType", "NONE",
        "charge", "NONE",
        "constant", "NONE",
        "metaid", "NONE",
        "hasOnlySubstanceUnits", "NONE",
        "sboTerm", "NONE",
        "boundaryCondition", "NONE",
        "chemicalFormula", "NONE",
        "initialAmount", "NONE",
        "name", "NONE",
        "compartment", "WARN",
        "id", "CRITICAL",
        "initialConcentration", "NONE",
    };
    Map<String, MessageType> fields = new HashMap<>();
    for (int i = 0; i < attr.length; i+=2) {
      fields.put(attr[i], MessageType.valueOf(attr[i + 1]));
    }
    return fields;
  }
  
  public static void xrxnAttributes(XmlSbmlModelValidator validator) {
    String[] xrxnAttr = new String[] {
        "upperFluxBound", "fast", "metaid", "reversible", "sboTerm", "name", "lowerFluxBound", "id"
    };
    String[] xrxnSpecieAttr = new String[] {
        "stoichiometry", "constant", "species", "metaid", "sboTerm"
    };
    validator.xrxnAttr.addAll(Arrays.asList(xrxnAttr));
    validator.xrxnStoichAttr.addAll(Arrays.asList(xrxnSpecieAttr));
  }
  
  public static void initializeDefaults(XmlSbmlModelValidator validator) {
    validator.xspiAttr.putAll(xspiAttributes());
    xrxnAttributes(validator);
  }
  
  public Map<String, XmlSbmlCompartment> xcmpMap = new HashMap<> ();
  public Map<String, XmlSbmlSpecie>      xspiMap = new HashMap<> ();
  public Map<String, XmlSbmlReaction>    xrxnMap = new HashMap<> ();
  public Map<String, MessageType> xspiAttr = new HashMap<> ();
  public Set<String> xrxnAttr = new HashSet<> ();
  public Set<String> xrxnStoichAttr = new HashSet<> ();
  public List<XmlMessage> messages = new ArrayList<> ();
  
  private final XmlSbmlModel xmodel;
  
  public XmlSbmlModelValidator(XmlSbmlModel xmodel) {
    this.xmodel = xmodel;
  }
  
  public XmlSbmlModelValidator(XmlSbmlModel xmodel, Map<String, MessageType> xspiAttr) {
    this.xmodel = xmodel;
    this.xspiAttr = xspiAttr;
  }
  
  
  public String getObjectId(XmlObject xo) {
    String entry = xo.getAttributes().get("id");
    if (entry == null) {
      issueMessage(xo, MessageCategory.OBJECT_MISSING_ID, MessageType.CRITICAL, "Missing ID attribute");
    }
    
    return entry;
  }
  
  public String getObjectId(XmlObject xo, String element) {
    String entry = xo.getAttributes().get("id");
    if (entry == null) {
      switch (element) {
        case "compartment":
          issueMessage(xo, MessageCategory.COMPARMENT_MISSING_ID, MessageType.CRITICAL, "Element [%s] missing ID attribute", element);
          break;
        case "species":
          issueMessage(xo, MessageCategory.SPECIE_MISSING_ID, MessageType.CRITICAL, "Element [%s] missing ID attribute", element);
          break;
        case "reaction":
          issueMessage(xo, MessageCategory.REACTION_MISSING_ID, MessageType.CRITICAL, "Element [%s] missing ID attribute", element);
          break;
        default:
          issueMessage(xo, MessageCategory.MODEL_MISSING_ID, MessageType.CRITICAL, "Element [%s] missing ID attribute", element);
          break;
      }
      
    }
    
    return entry;
  }
  
  @Deprecated
  public void issueMessage(XmlObject xo, MessageType type, String str, Object...args) {
    messages.add(new XmlMessage(xo, type, str, args));
  }
  
  public void issueMessage(XmlObject xo, MessageCategory cat, MessageType type, String str, Object...args) {
    messages.add(new XmlMessage(xo, cat, type, str, args));
  }
  
  /**
   * Lookup for ID
   */
  public void validateCompartments() {
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      String entry = getObjectId(xcmp, "compartment");
      if (entry != null) {
        if (xcmpMap.put(entry, xcmp) != null) {
          issueMessage(xcmp, MessageCategory.COMPARTMENT_DUPLICATE_ID, MessageType.CRITICAL, "Duplicate Compartment ID %s", entry);
        }
      }
    }
  }
  
  /**
   * Lookup for ID and valid Compartment ID
   * Parse Annotation and Notes
   * 
   */
  public void validateSpecies() {
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String entry = getObjectId(xspi, "species"); 
      if (entry != null) {
        if (xspiMap.put(entry, xspi) != null) {
          issueMessage(xspi, MessageCategory.SPECIE_DUPLICATE_ID, MessageType.CRITICAL, "Duplicate Specie ID %s", entry);
        }
//        xspiRefsMap.put(entry, new HashSet<> ());
      }

      String cmpEntry = xspi.getAttributes().get("compartment");
      if (cmpEntry == null) {
        issueMessage(xspi, MessageCategory.SPECIE_MISSING_COMPARMENT_ID, MessageType.CRITICAL, "compartment attribute not found");
      } else if (!xcmpMap.containsKey(cmpEntry)) {
        issueMessage(xspi, MessageCategory.UNDECLARED_COMPARTMENT, MessageType.CRITICAL, "non defined compartment [%s]", cmpEntry);
      }
      
      for (String attr : xspi.getAttributes().keySet()) {
        if (!xspiAttr.containsKey(attr)) {
          issueMessage(xspi, MessageType.WARN, "unknown attribute [%s]", attr);
        }
      }
      
//      xspiToXcmpMap.put(entry, cmpEntry);


//      if (!xspi.getNotes().isEmpty()) {
//        xspi.getAttributes().put("sbml_notes", StringUtils.join(xspi.getNotes(), "\n"));
//        Map<String, String> notes = parser.parseNotes(xspi.getNotes());
//        xspi.getNotes().clear();
//        for (String nattribute : notes.keySet()) {
//          String value = notes.get(nattribute);
//          if (xspi.getAttributes().containsKey(nattribute)) {
//            nattribute = "notes_" + nattribute;
//          }
//          xspi.getAttributes().put(nattribute, value);
//        }
//      }
//
//      if (!xspi.getListOfAnnotations().isEmpty()) {
//        //        System.out.println(xspi.getAttributes());
//        for (String k : xspi.getListOfAnnotations().keySet()) {
//          switch (k) {
//          case "is":
//            for (XmlObject xo : xspi.getListOfAnnotations().get(k)) {
//              for (String attribute : xo.getAttributes().keySet()) {
//                switch (attribute) {
//                case "resource":
//                  String resource = xo.getAttributes().get("resource");
//                  Pair<String, String> dbEntry = detectResource(resource);
//                  if (dbEntry.getLeft() == null || dbEntry.getRight() == null) {
//                    logger.warn("unable to parse resource {}", resource);
//                  } else {
//                    xspiRefsMap.get(entry).add(dbEntry);
//                  }
//                  break;
//                default:
//                  logger.warn("ignored annotation attribute {}", attribute);
//                  break;
//                }
//              }
//              //                      System.out.println("\t\tA\t" + k + "\t" + xo.getAttributes());
//            }
//            break;
//          case "relation":
//            validateRelationAnnotation(
//                entry, xspi.getListOfAnnotations().get(k), xspi);
//            break;
//          default:
//            logger.warn("ignored annotation {}", k);
//            
//            break;
//          }
//
//        }
//      }
//      //      System.out.println(xspiRefsMap.get(entry));
//      if (xspiRefsMap.get(entry) != null && !xspiRefsMap.get(entry).isEmpty()) {
//        EntityType type = detectDatabaseEntryType(xspiRefsMap.get(entry));
//        if (type == null) {
//          logger.warn("unable to detect type {}", xspiRefsMap.get(entry));
//        } else {
////          logger.info("specie {} type of {}", entry, type);
//          xspiTypeMap.put(entry, type);
//        }
//      }
      
    }
  }
  
  /**
   * Returns compartments
   * @param stoich
   * @return
   */
  public Map<String, String> validateStoichiometry(XmlObject xrxn, String rxnEntry, Collection<XmlObject> stoich) {
    Map<String, String> cmpMap = new HashMap<> ();
    Set<String> species = new HashSet<>();
    for (XmlObject xo : stoich) {
      String xspiEntry = xo.getAttributes().get("species");
      if (xspiEntry == null) {
        issueMessage(xo, MessageCategory.STOICH_NO_SPECIES, MessageType.CRITICAL, "missing species attribute");
      } else if (!species.add(xspiEntry)) {
        issueMessage(xrxn, MessageCategory.STOICH_DUP_SPECIES, MessageType.CRITICAL, "duplicate specie %s in stoichiometry ", xspiEntry);
      }
      
      String value_ = xo.getAttributes().get("stoichiometry");
      if (value_ == null) {
        issueMessage(xo, MessageCategory.STOICH_NO_VALUE, MessageType.WARN, "stoichiometry not found");
      } else if (NumberUtils.isNumber(value_)) {
        Double value = Double.parseDouble(value_);
        if (value <= 0.0) {
          issueMessage(xo, MessageCategory.STOICH_INVALID_VALUE, MessageType.CRITICAL, "invalid stoichiometry value %s", value_);
        }
      } else {
        issueMessage(xo, MessageCategory.STOICH_INVALID_VALUE, MessageType.CRITICAL, "invalid stoichiometry value %s", value_);
      }


//      cmpMap.put(xspiEntry, xspiToXcmpMap.get(xspiEntry));
      if (xspiEntry != null && !xspiMap.containsKey(xspiEntry)) {
        issueMessage(xo, MessageCategory.UNDECLARED_SPECIE, MessageType.CRITICAL, "reaction [%s] undefined specie [%s]", rxnEntry, xspiEntry);
//        logger.debug("non defined specie [{}] for component [{}]", xspiEntry, xo.getAttributes());
      }
      
      for (String attr : xo.getAttributes().keySet()) {
        if (!xrxnStoichAttr.contains(attr)) {
          issueMessage(xo, MessageType.WARN, "unknown attribute [%s]", attr);
        }
      }
    }

    return cmpMap;
  }
  
  /**
   * Lookup for ID and valid Specie ID (stoichiometry)
   * Parse Annotation and Notes
   * Lookup for bounds
   * Detect type (DRAIN / TRANSLOCATION)
   */
  public void validateReactions() {
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String entry = getObjectId(xrxn, "reaction");
      if (entry != null) {
//        if (xrxnMap.containsKey(entry)) {
//          String prev = entry;
//          entry = entry + "_" + idIncrement++;
//          messages.add(new XmlMessage(xrxn, MessageType.FIX, "Duplicate Reaction ID %s fixed to %s", prev, entry));
//          xrxn.getAttributes().put("id", entry);
//          xrxn.getAttributes().put("id_original", prev);
//        }
        if (xrxnMap.put(entry, xrxn) != null) {
          issueMessage(xrxn, MessageCategory.REACTION_ID_DUPLICATE, MessageType.CRITICAL, "Duplicate Reaction ID %s", entry);
        }
//        xrxnRefsMap.put(entry, new HashSet<> ());
      }

      //    System.out.println("\tR\t" + xrxn.getAttributes());
      
//      if (!xrxn.getNotes().isEmpty()) {
//        xrxn.getAttributes().put("sbml_notes", StringUtils.join(xrxn.getNotes(), "\n"));
//        Map<String, String> notes = parser.parseNotes(xrxn.getNotes());
//        xrxn.getNotes().clear();
//        for (String nattribute : notes.keySet()) {
//          String value = notes.get(nattribute);
//          if (xrxn.getAttributes().containsKey(nattribute)) {
//            nattribute = "notes_" + nattribute;
//          }
//          xrxn.getAttributes().put(nattribute, value);
//        }
//      }
//      logger.info("{}", xrxn.getAttributes());
      
//      if (!xrxn.getNotes().isEmpty()) {
//              System.out.println("\t\tN\t" + xrxn.getNotes());
//              System.out.println("\t\tN\t" + SbmlUtils.parseNotes(xrxn.getNotes()));
//      }

//      if (!xrxn.getListOfAnnotations().isEmpty()) {
//        for (String k : xrxn.getListOfAnnotations().keySet()) {
//          for (XmlObject xo : xrxn.getListOfAnnotations().get(k)) {
//            System.out.println("\t\tA\t" + k + "\t" + xo.getAttributes());
//          }
//        }
//      }

      Map<String, String> lcmp = validateStoichiometry(xrxn, entry, xrxn.getListOfReactants());
      Map<String, String> rcmp = validateStoichiometry(xrxn, entry, xrxn.getListOfProducts());
      
//      if (!lcmp.values().contains(null) && !rcmp.values().contains(null)) {
//        Set<String> uniqueCmp = new HashSet<> ();
//        uniqueCmp.addAll(lcmp.values());
//        uniqueCmp.addAll(rcmp.values());
//        //        System.out.println(entry + " " + uniqueCmp);
//        EntityType type = EntityType.REACTION;
//        if (maybeDrain(xrxn)) {
//          type = EntityType.DRAIN;
//        } else if (uniqueCmp.size() > 1) {
//          type = EntityType.TRANSLOCATION;
//        }
//        logger.debug("{} -> {}", entry, type);
//        xrxnTypeMap.put(entry, type);
//      } else {
//        xrxnTypeMap.put(entry, EntityType.ERROR);
//        logger.debug("{}: {} <=> {} - invalid compartment", lcmp, rcmp, entry);
//      }


//      if (!xrxn.getListOfAnnotations().isEmpty()) {
//        //      System.out.println(xrxn.getAttributes());
//        for (String k : xrxn.getListOfAnnotations().keySet()) {
//          switch (k) {
//            case "is":
//              validateReferenceAnnotation(
//                  entry, xrxn.getListOfAnnotations().get(k), xrxn);
//              break;
//            case "fluxnsLimit":
//              validateFluxNsAnnotation(
//                  entry, xrxn.getListOfAnnotations().get(k), xrxn);
//              break;
//            case "listOfKeyValueData":
//              validateKVDAnnotation(
//                  entry, xrxn.getListOfAnnotations().get(k), xrxn);
//              break;
//            case "relation":
//              validateRelationAnnotation(
//                  entry, xrxn.getListOfAnnotations().get(k), xrxn);
//              break;
//            default:
//            logger.warn("ignored annotation {}", k);
//            break;
//          }
//
//        }
//      }
      for (String attr : xrxn.getAttributes().keySet()) {
        if (!xrxnAttr.contains(attr)) {
          issueMessage(xrxn, MessageType.WARN, "unknown attribute [%s]", attr);
        }
      }
    }

    //    if (xrxnTypeMap.values().contains(EntityType.DRAIN)) {
    //      logger.info("D " + xrxnTypeMap.bget(EntityType.DRAIN).size());
    //    }

//    for (String entry : xrxnMap.keySet()) {
//      XmlSbmlReaction xrxn = xrxnMap.get(entry);
//      validateReactionContraint(entry, xrxn, xrxn.getListOfParameters());
//      //      System.out.println(xrxn.getAttributes().get("ec-code"));
//    }


  }
  
  public List<XmlMessage> validate() {
//    String modelId = 
    getObjectId(xmodel, "model");
    validateCompartments();
    validateSpecies();
    validateReactions();
    
    return messages;
  }
}
