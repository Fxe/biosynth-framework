package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlSbmlModelAutofix {
  
  public boolean autoAssignMissingCompartment = true;
  public boolean autoCreateMissingCompartment = true;
  public boolean autoCreateMissingSpecies = true;
  
  private static final Logger logger = LoggerFactory.getLogger(XmlSbmlModelAutofix.class);
  
  public Set<String> ids = new HashSet<> ();
  
  public String fixId(String id) {
//    StringUtils.replaceChars(id, ' ', '_')
    return id.replace(' ', '_');
  }
  
  public String generateNextId(String id) {
    int counter = 1;
    String next = fixId(id);
    if (ids.contains(next)) {
      next = id + "_" + ++counter;
      while (ids.contains(next)) {
        next = id + "_" + ++counter;
      }
    }
    
    ids.add(next);
    
    return next;
  }
  
  public List<XmlMessage> messages = new ArrayList<> ();
  
  public XmlSbmlModelAutofix() {
    // TODO Auto-generated constructor stub
  }
  
  public Map<MessageCategory, XmlMessageGroup> group = new HashMap<> ();
  
  public void issueFixMessage(XmlObject xo, MessageCategory cat, String str, Object...args) {
    issueMessage(xo, cat, MessageType.FIX, str, args);
  }
  
  public void issueMessage(XmlObject xo, MessageCategory cat, MessageType type, String str, Object...args) {
    if (group.containsKey(cat)) {
      group.get(cat).add(xo);
    } else {
      messages.add(new XmlMessage(xo, cat, type, str, args));
    }
  }
  
  public void indexByLineAndColumn(XmlObject o, Map<Integer, Map<Integer, XmlObject>> index) {
    int cn = o.columnNumber;
    int ln = o.lineNumber;
    if (!index.containsKey(ln)) {
      index.put(ln, new HashMap<Integer, XmlObject> ());
    }
    if (!index.get(ln).containsKey(cn)) {
      if (o.getAttributes().containsKey("id")) {
        ids.add(o.getAttributes().get("id"));
      }
      index.get(ln).put(cn, o);
    } else {
      logger.warn("duplicate objects at L:{}, C:{}", ln, cn);
    }
  }
  
  public Map<Integer, Map<Integer, XmlObject>> indexByLineAndColumn(XmlSbmlModel m) {
    Map<Integer, Map<Integer, XmlObject>> index = new HashMap<> ();
    indexByLineAndColumn(m, index);
    for (XmlSbmlCompartment c : m.getCompartments()) {
      indexByLineAndColumn(c, index);
    }
    for (XmlSbmlSpecie s : m.getSpecies()) {
      indexByLineAndColumn(s, index);
    }
    for (XmlSbmlReaction r : m.getReactions()) {
      indexByLineAndColumn(r, index);
      for (XmlObject o : r.getListOfProducts()) {
        indexByLineAndColumn(o, index);
      }
      for (XmlObject o : r.getListOfReactants()) {
        indexByLineAndColumn(o, index);
      }
    }
    
    return index;
  }
  
  public XmlObject find(Map<Integer, Map<Integer, XmlObject>> index, int ln, int cn) {
    if (index.containsKey(ln)) {
      return index.get(ln).get(cn);
    }
    
    logger.warn("element L:{}, C:{} not found", ln, cn);
    
    return null;
  }
  
  public int counter = 0;
  
  public void fixMissingIdByUsingName(XmlObject o) {
    logger.trace("FIX [ID]: {}", o);
    if (o != null) {
      String n = o.getAttributes().get("name");
      String id = null;
      if (n != null && !n.trim().isEmpty()) {
        id = n.trim();
        id = generateNextId(id);
        issueFixMessage(o, MessageCategory.OBJECT_MISSING_ID, "using name attribute [%s] -> [%s]", n, id);
      } else {
        id = "id_" + ++counter;
        id = generateNextId(id);
        issueFixMessage(o, MessageCategory.OBJECT_MISSING_ID, "using generated id [%s] ", id);
      }
      logger.trace("FIX: set ID {}", id);
      o.getAttributes().put("id", id);;
    }
  }
  
  public boolean fixStoichValue(XmlObject o, XmlSbmlModel xmodel) {
    String vstr = o.getAttributes().get("stoichiometry");
    if (vstr == null) {
      o.getAttributes().put("stoichiometry", "1.0");
      issueMessage(o, MessageCategory.STOICH_NO_VALUE, MessageType.WARN, "assume value 1");
    } else if (NumberUtils.isNumber(vstr.trim())) {
      double v = Double.parseDouble(vstr.trim());
      if (v == 0) {
        o.getAttributes().put("stoichiometry", "1.0");
        issueFixMessage(o, MessageCategory.STOICH_INVALID_VALUE, "set value [%s] -> [1.0]", vstr);
      } else if (v < 0) {
        o.getAttributes().put("stoichiometry", Double.toString(Math.abs(v)));
        issueFixMessage(o, MessageCategory.STOICH_INVALID_VALUE, "set value [abs(%s)]", vstr, Math.abs(v));
      } else {
        o.getAttributes().put("stoichiometry", Double.toString(v));
        issueFixMessage(o, MessageCategory.STOICH_INVALID_VALUE, "set value [%s] -> [%f]", vstr, v);
      }
    } else {
      o.getAttributes().put("stoichiometry", "1.0");
      issueFixMessage(o, MessageCategory.STOICH_INVALID_VALUE, "set value [%s] -> 1", vstr);
    }
    
    return true;
  }
  
  public void fixSpecieMissingCompartment(XmlObject o, XmlSbmlModel xmodel) {
    String cmpEntry = null;
    
    if (xmodel.getCompartments().isEmpty()) {
      XmlSbmlCompartment xcmp = new XmlSbmlCompartment();
      xcmp.getAttributes().put("id", "c");
      xcmp.getAttributes().put("name", "generated_default_cytoplasm");
      xmodel.getCompartments().add(xcmp);
      issueFixMessage(o, MessageCategory.SPECIE_MISSING_COMPARMENT_ID, "generated compartment: %s", xcmp);
    }
    
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      cmpEntry = xcmp.getAttributes().get("id");
      if (cmpEntry != null) {
        break;
      }
    }
    
    if (cmpEntry != null) {
      o.getAttributes().put("compartment", cmpEntry);
      issueFixMessage(o, MessageCategory.SPECIE_MISSING_COMPARMENT_ID, "compartment: %s", cmpEntry);
    }
  }
  
//  public void fixSpeciesReference(XmlObject o) {
//    o.getAttributes().put("stoichiometry", "1");
//    issueFixMessage(o, MessageCategory.NULL, "add stoichiometry field as 1");
////    logger.info("FIX {} {}: add stoichiometry field as 1", o.lineNumber, o.columnNumber);
//  }
  
//  public void fixUndeclaredSpecieStoich(XmlObject o) {
//    if (o != null) {
//      
//    }
//  }
  
  public boolean fixUndeclaredSpecieCompartment(XmlObject o, XmlSbmlModel xmodel) {
    String cmpEntry = o.getAttributes().get("compartment");
    Map<String, XmlSbmlCompartment> xcmpMap = new HashMap<> ();
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      String id = xcmp.getAttributes().get("id");
      if (!xcmpMap.containsKey(id.trim().toLowerCase())) {
        xcmpMap.put(id.trim().toLowerCase(), xcmp);
      } else {
        xcmpMap.put(id.toLowerCase(), xcmp);
      }
    }
    if (xcmpMap.containsKey(cmpEntry)) {
      String i = xcmpMap.get(cmpEntry).getAttributes().get("id");
      o.getAttributes().put("compartment", i);
      issueFixMessage(o, MessageCategory.SPECIE_MISSING_COMPARMENT_ID, "compartment: %s -> %s", cmpEntry, i);
      return true;
    } else if (xcmpMap.containsKey(cmpEntry.trim().toLowerCase())) {
      String i = xcmpMap.get(cmpEntry.trim().toLowerCase()).getAttributes().get("id");
      o.getAttributes().put("compartment", i);
      issueFixMessage(o, MessageCategory.SPECIE_MISSING_COMPARMENT_ID, "compartment: %s -> %s", cmpEntry, i);
      return true;
    }
    
    
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      String id = xcmp.getAttributes().get("id");
      if (id != null && id.equals(cmpEntry)) {
        return true;
      }
    }
    
    //try create new compartment
    if (autoCreateMissingCompartment) {
      XmlSbmlCompartment xcmp = new XmlSbmlCompartment();
      xcmp.getAttributes().put("id", cmpEntry);
      xcmp.getAttributes().put("name", "generated_" + cmpEntry);
      xmodel.getCompartments().add(xcmp);
      issueFixMessage(o, MessageCategory.SPECIE_MISSING_COMPARMENT_ID, "generated compartment: %s", xcmp);
      return true;
    }
    
    return false;
  }
  
  public boolean fixUndeclaredSpecieStoich(XmlObject o, XmlSbmlModel xmodel) {
    String species = o.getAttributes().get("species");
    if (o != null) {
      
      for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
        String id = xspi.getAttributes().get("id");
        if (id != null && id.equals(species)) {
          return true;
        }
      }
    }
    
    if (autoCreateMissingSpecies) {
      XmlSbmlSpecie xspi = new XmlSbmlSpecie();
      xspi.columnNumber = -1;
      xspi.lineNumber = -1;
      xspi.getAttributes().put("id", species);
      xspi.getAttributes().put("name", "auto_generated_undeclated_specie_" + species);
      if (!xmodel.getCompartments().isEmpty()) {
        String cmpEntry = xmodel.getCompartments().iterator().next().getAttributes().get("id");
        xspi.getAttributes().put("compartment", cmpEntry);
      }
      
      issueFixMessage(o, MessageCategory.UNDECLARED_SPECIE, "generated specie %s", xspi.getAttributes());
      
      xmodel.getSpecies().add(xspi);
      
      return true;
    }
    
    return false;
  }
  
  public void fixDuplicateSpecies(XmlObject o) {
    if (o instanceof XmlSbmlReaction) {
      XmlSbmlReaction xrxn = (XmlSbmlReaction) o;
      
//      System.out.println(xrxn.getListOfProducts() + " " + xrxn.getListOfReactants());
      {
        Map<String, List<String>> a = new HashMap<> ();
        Map<String, XmlObject> xo = new HashMap<> ();
        for (XmlObject r : xrxn.getListOfReactants()) {
          String species = r.getAttributes().get("species");
          if (!a.containsKey(species)) {
            a.put(species, new ArrayList<String> ());
            xo.put(species, r);
          }
          String s = r.getAttributes().get("stoichiometry");
          a.get(species).add(s);
        }
//        System.out.println(a);
        List<XmlObject> xos = new ArrayList<> ();
        for (String s : a.keySet()) {
          if (a.get(s).size() > 1) {
            XmlObject merge = new XmlObject();
            merge.getAttributes().put("species", s);
            double v = 0.0;
            for (String sv : a.get(s)) {
              if (NumberUtils.isNumber(sv)) {
                v += Double.parseDouble(sv);
              }
            }
            merge.getAttributes().put("stoichiometry", Double.toString(v));
            xos.add(merge);
            issueFixMessage(o, MessageCategory.STOICH_DUP_SPECIES, 
                "merge specie stoichiometry [%s, %s] -> [%f]", s, a.get(s), v);
          } else {
            xos.add(xo.get(s));
          }
        }
        xrxn.setListOfReactants(xos);
      }
      {
        Map<String, List<String>> a = new HashMap<> ();
        Map<String, XmlObject> xo = new HashMap<> ();
        for (XmlObject r : xrxn.getListOfProducts()) {
          String species = r.getAttributes().get("species");
          if (!a.containsKey(species)) {
            a.put(species, new ArrayList<String> ());
            xo.put(species, r);
          }
          String s = r.getAttributes().get("stoichiometry");
          a.get(species).add(s);
        }
//        System.out.println(a);
        List<XmlObject> xos = new ArrayList<> ();
        for (String s : a.keySet()) {
          if (a.get(s).size() > 1) {
            XmlObject merge = new XmlObject();
            merge.getAttributes().put("species", s);
            double v = 0.0;
            for (String sv : a.get(s)) {
              if (NumberUtils.isNumber(sv)) {
                v += Double.parseDouble(sv);
              }
            }
            merge.getAttributes().put("stoichiometry", Double.toString(v));
            xos.add(merge);
            issueFixMessage(o, MessageCategory.STOICH_DUP_SPECIES, 
                "merge specie stoichiometry [%s, %s] -> [%f]", s, a.get(s), v);
          } else {
            xos.add(xo.get(s));
          }
        }
        xrxn.setListOfProducts(xos);
      }
    }
  }
  
  public void fixDuplicateId(XmlObject o) {
    String id = o.getAttributes().get("id");
    if (id != null) {
      
      String id2 = generateNextId(id);
      o.getAttributes().put("id", id2);
      issueFixMessage(o, MessageCategory.REACTION_ID_DUPLICATE, "changed id [%s] -> [%s]", id, id2);
    }
  }
  
  public void fix(XmlSbmlModel xmodel, List<XmlMessage> messages) {
    
    Map<Integer, Map<Integer, XmlObject>> index = indexByLineAndColumn(xmodel);
    
    for (XmlMessage m : messages) {
//      System.out.println("try fix: " + m);
      String message = m.message;
      MessageCategory cat = m.category;
      int cn = m.columnNumber;
      int ln = m.lineNumber;
      
      if (!MessageCategory.NULL.equals(cat)) {
        XmlObject o_ = null;
        switch(cat) {
//          case UNDECLARED_SPECIE:
////            logger.warn("no fix for {}", m);
//            break;
          case STOICH_DUP_SPECIES:
            o_ = find(index, ln, cn);
            if (o_ != null) {
              fixDuplicateSpecies(o_);
              break;
            }
          case SPECIE_MISSING_COMPARMENT_ID:
            o_ = find(index, ln, cn);
            if (autoAssignMissingCompartment && o_ != null) {
              fixSpecieMissingCompartment(o_, xmodel);
              break;
            }
          case STOICH_NO_VALUE:
          case STOICH_INVALID_VALUE:
            o_ = find(index, ln, cn);
            if (o_ != null) {
              fixStoichValue(o_, xmodel);
              break;
            }
          case MODEL_MISSING_ID:
          case OBJECT_MISSING_ID:
          case REACTION_MISSING_ID:
          case COMPARMENT_MISSING_ID:
          case SPECIE_MISSING_ID:
            o_ = find(index, ln, cn);
            if (o_ != null) {
              fixMissingIdByUsingName(o_);
              break;
            } else {
              System.out.println("!");
              logger.error("could not find object L:{}, C:{}", ln, cn);
            }
          case UNDECLARED_COMPARTMENT:
            o_ = find(index, ln, cn);
            if (o_ != null && fixUndeclaredSpecieCompartment(o_, xmodel)) {
              break;
            } else {
              System.out.println("!!");
              logger.error("could not find object L:{}, C:{}", ln, cn);
            }
          case UNDECLARED_SPECIE:
            o_ = find(index, ln, cn);
            if (o_ != null && fixUndeclaredSpecieStoich(o_, xmodel)) {
              break;
            } else {
              System.out.println("!!!");
              logger.error("could not find object L:{}, C:{}", ln, cn);
            }
            
          case REACTION_ID_DUPLICATE:
            o_ = find(index, ln, cn);
            if (o_ != null) {
              fixDuplicateId(o_);
              break;
            }
            
          default:
            logger.error("[{}]: no fix for {}", cat, m);
            break;
        }
      } else {
        switch (message) {
        case "Missing ID attribute":
          XmlObject o = find(index, ln, cn);
          fixMissingIdByUsingName(o);
          break;
//        case "compartment attribute not found":
//          break;
//        case "stoichiometry not found, assume 1":
//          try {
//            XmlObject o_ = find(index, ln, cn);
//            if (o_ != null) {
//              fixSpeciesReference(o_);
//            }
//          } catch (Exception e) {
//            logger.error("system error - {} {}", e.getMessage(), m);
//          }
//          break;
        default:
          logger.warn("no fix for {}", m);
          break;
      }
      }
      

    }
    //fix missing ids using name
    //predict missing compartment
    //add missing stoichiometry
    //sum duplicate reactions -> only if no removal
  }
}
