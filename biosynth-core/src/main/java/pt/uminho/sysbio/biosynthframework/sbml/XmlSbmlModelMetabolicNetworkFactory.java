package pt.uminho.sysbio.biosynthframework.sbml;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.DefaultMetabolicNetworkFactory;
import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.SimpleCompartment;

public class XmlSbmlModelMetabolicNetworkFactory extends DefaultMetabolicNetworkFactory<String> {

  private static final Logger logger = LoggerFactory.getLogger(XmlSbmlModelMetabolicNetworkFactory.class);
  
  public enum BoundType {
    INF_REV, //[-inf, inf]
    INF_LEFT_RIGHT, //[0, inf]
    INF_RIGHT_LEFT, //[-inf, 0]
    LIM_LEFT_RIGHT, //[0, a] a < inf
    INM_RIGHT_LEFT, //[-a, 0] a < inf
    LIM_REV, // [-a, b]
    POS_FORCE, // [k, b] k > 0
    NEG_FORCE, // [a, k] k < 0
    POSFIXED, // [a, a] a > 0  
    NEGFIXED, // [b, b] b < 0
    ZERO,      // [0, 0]
    INF_REV_UNBOUNDED, // [?, ?] reversible = true
    INF_IREV_UNBOUNDED, // [?, ?] reversible = false
    INF_REV_NOINFO , // [?, ?] reversible = ?
    WUT, //[wut? o.o]
  }
  
  public Set<Object> fluxValues = new HashSet<> ();
  public Map<Object, Double> fluxValuesMap = new HashMap<> ();
  public Map<String, Range> rxnBounds = new HashMap<> ();
  public Map<String, Boolean> rxnRev = new HashMap<> ();
  public BMap<String, BoundType> rxnBoundType = new BHashMap<>();
  
  public double infValue = 1e30;
  
  public Double infParse(String s, double defaultValue) {
    if (s == null) {
      s = "PARAMETER_NOT_FOUND";
    }
    
    if (s != null && "-inf".equals(s.trim().toLowerCase())) {
      fluxValuesMap.put(s.trim(), infValue * -1);
      return infValue * -1;
    }
    
    if (s != null && "inf".equals(s.trim().toLowerCase())) {
      fluxValuesMap.put(s.trim(), infValue);
      return infValue;
    }
    
    fluxValuesMap.put(s.trim(), defaultValue);
    return defaultValue;
  }
  
  public Double[] convert2(String[] bounds) {
    String lbStr = bounds[0];
    String ubStr = bounds[1];
    
    fluxValues.add(lbStr);
    fluxValues.add(ubStr);
    
    Double lb = null;
    Double ub = null;
    if (lbStr != null && NumberUtils.isNumber(lbStr)) {
      lb = Double.parseDouble(lbStr);
    } else {
      lb = infParse(lbStr, -1 * infValue);
    }
    if (ubStr != null && NumberUtils.isNumber(ubStr)) {
      ub = Double.parseDouble(ubStr);
    } else {
      ub = infParse(ubStr, infValue);
    }
    
    return new Double[] {lb, ub};
  }
  
  public String[] getFluxBoundFromParameters2(XmlSbmlReaction xrxn) {
    String[] bounds = new String[2];
    for (XmlObject p : xrxn.getListOfParameters()) {
      String parameterId = p.getAttributes().get("id");
      if (parameterId != null && parameterId.toUpperCase().trim().equals("LOWER_BOUND")) {
        bounds[0] = p.getAttributes().get("value");
      }
      if (parameterId != null && parameterId.toUpperCase().trim().equals("UPPER_BOUND")) {
        bounds[1] = p.getAttributes().get("value");
      }
    }

    return bounds;
  }

  public String[] getFluxBoundFromFbc2(XmlSbmlReaction xrxn, Map<String, XmlObject> parameters) {
    String lfbcId = xrxn.getAttributes().get("lowerFluxBound");
    String ufbcId = xrxn.getAttributes().get("upperFluxBound");
    if (lfbcId != null) {
      XmlObject p = parameters.get(lfbcId);
      lfbcId = p.getAttributes().get("value");
    }
    if (ufbcId != null) {
      XmlObject p = parameters.get(ufbcId);
      ufbcId = p.getAttributes().get("value");
    }

    String[] bounds = new String[]{lfbcId, ufbcId};
    return bounds;
  }
  
  public String[] getFluxBoundFromFluxns(XmlSbmlReaction xrxn) {
    if (xrxn.getListOfAnnotations() != null && 
        xrxn.getListOfAnnotations().containsKey("fluxnsLimit")) {
      for (XmlObject o : xrxn.getListOfAnnotations().get("fluxnsLimit")) {
        String lb = o.getAttributes().get("lower");
        String ub = o.getAttributes().get("upper");
        return new String[]{lb, ub};
      }
    }
    
    return null;
  }
  
  public String[] fixNullBounds(String[] b) {
    if (b == null || (b.length == 2 && b[0] == null && b[1] == null)) {
      return null;
    }
    
    String lb = b[0];
    String ub = b[1];
    
    if (lb == null) {
      lb = "?";
    }
    if (ub == null) {
      ub = "?";
    }
    
    return new String[] {lb, ub};
  }

  public Double[] getSbmlReactionBounds2(XmlSbmlReaction xrxn, Map<String, XmlObject> parameters) {
    String[] fbcBounds = getFluxBoundFromFbc2(xrxn, parameters);
    String[] notesBounds = getFluxBoundFromParameters2(xrxn);
    String[] others = getFluxBoundFromFluxns(xrxn);
    fixNullBounds(fbcBounds);
    fixNullBounds(notesBounds);
    fixNullBounds(others);
//    if (fbcBounds != null && fbcBounds.length >= 2) {
//      if (fbcBounds[0] == null && fbcBounds[1] == null) {
//        fbcBounds = null;
//      }
//    }
    
    
    Double[] bounds = null;
    if (fbcBounds != null) {
      bounds = convert2(fbcBounds);
    } else if (others != null) {
      bounds = convert2(others);
    } else if (notesBounds != null) {
      bounds = convert2(notesBounds);
    }
    
    if (bounds != null && bounds.length == 2 &&
        bounds[0] == null && bounds[1] == null) {
      //both null bounds no bounds should be returned
      return null;
    } else if (bounds != null && (bounds[0] == null || bounds[1] == null)) {
      //at least one of bounds is null
      logger.error("{}: {}", xrxn.getAttributes().get("id"), Arrays.toString(bounds));
    }
    
    return bounds;
  }
  
  public static Boolean getReverible(XmlSbmlReaction xrxn) {
    String revStr = xrxn.getAttributes().get("reversible");
    if (revStr != null) {
      try {
        Boolean rev = Boolean.parseBoolean(revStr);
        return rev;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    return null;
  }
  
  public void setupBoundType(String entry, Boolean rev, Range bounds, double lbMin, double ubMax) {
    if (bounds == null) {
      if (rev != null) {
        if (rev) {
          
          rxnBounds.put(entry, new Range(-1 * infValue, infValue));
          rxnBoundType.put(entry, BoundType.INF_REV_UNBOUNDED);
        } else {
          rxnBounds.put(entry, new Range(0, infValue));
          rxnBoundType.put(entry, BoundType.INF_IREV_UNBOUNDED);
        }
      } else {
        rxnBounds.put(entry, new Range(-1 * infValue, infValue));
        rxnBoundType.put(entry, BoundType.INF_REV_NOINFO);
      }
    } else {
      if (bounds.isFixed()) {
        //zero fixed neg / pos
        if (bounds.lb == 0.0) {
          rxnBoundType.put(entry, BoundType.ZERO);
        } else if (bounds.lb > 0.0) {
          rxnBoundType.put(entry, BoundType.POSFIXED);
        } else {
          rxnBoundType.put(entry, BoundType.NEGFIXED);
        }
      } else if (bounds.lb == 0.0) {
        if (bounds.ub >= ubMax) {
          rxnBoundType.put(entry, BoundType.INF_LEFT_RIGHT);
        } else if (bounds.ub < ubMax){
          rxnBoundType.put(entry, BoundType.LIM_LEFT_RIGHT);
        }
      } else if (bounds.ub == 0.0) {
        if (bounds.lb <= lbMin) {
          rxnBoundType.put(entry, BoundType.INF_RIGHT_LEFT);
        } else if (bounds.lb > lbMin) {
          rxnBoundType.put(entry, BoundType.LIM_LEFT_RIGHT);
        }
      } else {
        if (bounds.lb <= lbMin && bounds.ub >= ubMax) {
          rxnBoundType.put(entry, BoundType.INF_REV);
        } else {
          rxnBoundType.put(entry, BoundType.WUT);
        }
      }
    }
  }
  
  public XmlSbmlModelMetabolicNetworkFactory withXmlSbmlReaction(XmlSbmlReaction xrxn, Map<String, XmlObject> parameters) throws IOException {
    try {
      String entry = xrxn.getAttributes().get("id");
      
      Boolean rev = getReverible(xrxn);
      Double[] bounds = getSbmlReactionBounds2(xrxn, parameters);
      if (bounds != null) {
        rxnBounds.put(entry, new Range(bounds[0], bounds[1]));
      } else {
        rxnBounds.put(entry, null);
      }
      
      rxnBoundType.put(entry, BoundType.WUT);
      rxnRev.put(entry, rev);
    } catch (Exception e) {
      throw new IOException(e);
    }
    return this;
  }
  
  public XmlSbmlModelMetabolicNetworkFactory withXmlSbmlModel(XmlSbmlModel xmodel) {
    for (XmlSbmlCompartment xcmp : xmodel.getCompartments()) {
      String entry = xcmp.getAttributes().get("id");
      String name = xcmp.getAttributes().get("name");
      this.compartments.put(entry, new SimpleCompartment<String>(entry));
    }
    
    
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
//      Map<String, String> xspiData = new HashMap<> ();
//      xspiData.put("id", xspi.getAttributes().get("id"));
//      xspiData.put("name", xspi.getAttributes().get("name"));
//      xspiData.put("cmp", xspi.getAttributes().get("compartment"));
//      this.report.species.put(i++, xspiData);
    }
    
    Map<String, XmlObject> parameters = new HashMap<> ();
    for (XmlObject p : xmodel.getListOfParameters()) {
      parameters.put(p.getAttributes().get("id"), p);
    }
    
    //inf value
    double lbMin = 0;
    double ubMax = 0;
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      try {
        withXmlSbmlReaction(xrxn, parameters);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    for (Object o : fluxValues) {
      if (o != null) {
        if (fluxValuesMap.containsKey(o.toString().trim())) {
          logger.info("FIX: {} -> {}", o.toString(), fluxValuesMap.get(o.toString().trim()));
        } else if (NumberUtils.isNumber(o.toString().trim())) {
          double v = Double.parseDouble(o.toString().trim());
          if (v > ubMax) {
            ubMax = v;
          }
          if (v < lbMin) {
            lbMin = v;
          }
        } else {
          logger.warn("FAIL: {}", o.toString().trim());
        }
      }
    }
    
    logger.info("Default LB/UB [{}, {}]", lbMin, ubMax);
    System.out.println(fluxValues);
    
    for (String entry : this.rxnBounds.keySet()) {
      Boolean rev = rxnRev.get(entry);
      Range bounds = rxnBounds.get(entry);
      setupBoundType(entry, rev, bounds, lbMin, ubMax);
    }
    
    for (BoundType bt : rxnBoundType.bkeySet()) {
      logger.info("{}: {}", bt, rxnBoundType.bget(bt).size());
    }
    
    return this;
  }
  
  
  @Deprecated
  public static String[] getFluxBoundFromParameters(XmlSbmlReaction xrxn) {
    String[] bounds = new String[2];
    for (XmlObject p : xrxn.getListOfParameters()) {
      String parameterId = p.getAttributes().get("id");
      if (parameterId != null && parameterId.toUpperCase().trim().equals("LOWER_BOUND")) {
        bounds[0] = p.getAttributes().get("value");
      }
      if (parameterId != null && parameterId.toUpperCase().trim().equals("UPPER_BOUND")) {
        bounds[1] = p.getAttributes().get("value");
      }
    }

    return bounds;
  }

  @Deprecated
  public static String[] getFluxBoundFromFbc(XmlSbmlReaction xrxn, Map<String, XmlObject> parameters) {
    String lfbcId = xrxn.getAttributes().get("lowerFluxBound");
    String ufbcId = xrxn.getAttributes().get("upperFluxBound");
    if (lfbcId != null) {
      XmlObject p = parameters.get(lfbcId);
      lfbcId = p.getAttributes().get("value");
    }
    if (ufbcId != null) {
      XmlObject p = parameters.get(ufbcId);
      ufbcId = p.getAttributes().get("value");
    }

    String[] bounds = new String[]{lfbcId, ufbcId};
    return bounds;
  }
  
  @Deprecated
  public static double[] getSbmlReactionBounds(XmlSbmlReaction xrxn, Map<String, XmlObject> parameters) {
    String[] fbcBounds = getFluxBoundFromFbc(xrxn, parameters);
    String[] notesBounds = getFluxBoundFromParameters(xrxn);
    if (fbcBounds != null) {
      System.out.println("F: " + Arrays.toString(fbcBounds));
    }
    if (notesBounds != null) {
      System.out.println("N: " + Arrays.toString(notesBounds));
    }
    if (fbcBounds != null && fbcBounds.length >= 2) {
      if (fbcBounds[0] == null && fbcBounds[1] == null) {
        fbcBounds = null;
      }
    }
    double[] bounds = null;
    if (fbcBounds != null) {
      bounds = convert(fbcBounds);
    } else if (notesBounds != null) {
      bounds = convert(notesBounds);
    }
    return bounds;
  }
  
  @Deprecated
  public static double[] convert(String[] bounds) {
    String lbStr = bounds[0];
    String ubStr = bounds[1];
    double lb = -1000;
    double ub =  1000;
    if (lbStr != null && NumberUtils.isNumber(lbStr)) {
      lb = Double.parseDouble(lbStr);
    }
    if (ubStr != null && NumberUtils.isNumber(ubStr)) {
      ub = Double.parseDouble(ubStr);
    }
    return new double[] {lb, ub};
  }
}
