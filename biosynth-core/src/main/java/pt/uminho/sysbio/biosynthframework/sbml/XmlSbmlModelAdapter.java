package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.BFunction;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.EntityType;
import pt.uminho.sysbio.biosynthframework.ModelAdapter;
import pt.uminho.sysbio.biosynthframework.MultiNodeTree;
import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.SimpleCompartment;
import pt.uminho.sysbio.biosynthframework.SimpleModelReaction;
import pt.uminho.sysbio.biosynthframework.SimpleModelSpecie;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.SbmlUtils;

public class XmlSbmlModelAdapter implements ModelAdapter {

  private static final String SPI_ATTR_BC = "boundaryCondition";
  
  private static final Logger logger = LoggerFactory.getLogger(XmlSbmlModelAdapter.class);  
  
  public final XmlSbmlModel xmodel;
  protected Map<String, XmlSbmlReaction> xrxnMap = new HashMap<> ();
  protected Map<String, XmlSbmlSpecie> xspiMap = new HashMap<> ();
  protected Map<String, XmlSbmlCompartment> xcmpMap = new HashMap<> ();
  protected Set<String> boundarySpecies = new HashSet<> ();
  
  protected BMap<String, Integer> xspiDegreeMap = new BHashMap<> ();
  
  protected Map<String, XmlObject> parameters = new HashMap<> ();
  public BMap<String, EntityType> xrxnType = new BHashMap<> ();
  public BMap<String, EntityType> xspiType = new BHashMap<> ();
  
  protected SbmlNotesParser notesParser = new SbmlNotesParser();
  
  Map<Long, Double> lbMap = new HashMap<> (); //??
  Map<Long, Double> ubMap = new HashMap<> (); //??
  
  public boolean drainDeadEnds = false;
  public boolean drainBoundaryCondition = true;
  public String drainPattern = null;
  
  public Set<Object> fluxValues = new HashSet<> ();
  public Map<Object, Double> fluxValuesMap = new HashMap<> ();
  public double infValue = 1e30;
  public static double defaultLB = -1000;
  public static double defaultUB =  1000;
  
  public Map<String, Pair<Double, Double>> getDefaultMedium() {
    return null;
  }
  
  public String[] getFluxBoundFromParameters2(XmlSbmlReaction xrxn) {
    String[] bounds = new String[2];
    for (XmlObject p : xrxn.getListOfParameters()) {
      String parameterId = p.getAttributes().get("id");
      String name = p.getAttributes().get("name");
      if ((parameterId != null && 
           parameterId.toUpperCase().trim().equals("LOWER_BOUND")) ||
          (name != null && 
           name.toUpperCase().trim().equals("LOWER_BOUND"))) {
        bounds[0] = p.getAttributes().get("value");
      }
      if ((parameterId != null && 
           parameterId.toUpperCase().trim().equals("UPPER_BOUND")) || 
          (name != null && 
           name.toUpperCase().trim().equals("UPPER_BOUND"))) {
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
//    if (b != null) {
//      System.out.println(b + " " + b.length + " " + b[0] + " " + b[1]);
//    }

    
    if (b == null || (b.length == 2 && b[0] == null && b[1] == null)) {
//      System.out.println("!");
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
    logger.trace("get bounds {}", xrxn.getAttributes());
    String[] fbcBounds = getFluxBoundFromFbc2(xrxn, parameters);
    String[] paramsBounds = getFluxBoundFromParameters2(xrxn);
    String[] others = getFluxBoundFromFluxns(xrxn);
    fbcBounds = fixNullBounds(fbcBounds);
    paramsBounds = fixNullBounds(paramsBounds);
    others = fixNullBounds(others);
    
    logger.trace("FBC: {}, PARAMS: {}, NS: {}", fbcBounds, paramsBounds, others);
    
    Double[] bounds = null;
    if (fbcBounds != null) {
      bounds = convert2(fbcBounds);
    } else if (others != null) {
      bounds = convert2(others);
    } else if (paramsBounds != null) {
      bounds = convert2(paramsBounds);
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
  
  public String getSpecieAttribute(String spiEntry, String attribute) {
    return this.xspiMap.get(spiEntry).getAttributes().get(attribute);
  }
   

  
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
    if (lbStr != null && NumberUtils.isNumber(lbStr.trim())) {
      lb = Double.parseDouble(lbStr.trim());
    } else {
      lb = infParse(lbStr, -1 * infValue);
    }
    if (ubStr != null && NumberUtils.isNumber(ubStr.trim())) {
      ub = Double.parseDouble(ubStr.trim());
    } else {
      ub = infParse(ubStr, infValue);
    }
    
    return new Double[] {lb, ub};
  }
  
  @Override
  public Range getBounds(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    
    Double[] b = getSbmlReactionBounds2(xrxn, parameters);
    if (b == null) {
      return new Range(-1000.0, 1000.0);
    }
    return new Range(b[0], b[1]);
  }
  

  
  public Map<String, Pair<Double, Double>> getDefaultDrains() {
    Map<String, Pair<Double, Double>> result = new HashMap<> ();
    Set<String> drains = xrxnType.bget(EntityType.DRAIN);
    
    if (drains != null) {
      Map<String, Set<String>> specieToDrain = new HashMap<> ();
      for (String drxnEntry : drains) {
        Map<String, String> stoich = getStoichiometryString(drxnEntry);
//        System.out.println(drxnEntry + " " + stoich);
        //normalize value / bounds
        for (String spiEntry : stoich.keySet()) {
          CollectionUtils.insertHS(spiEntry, drxnEntry, specieToDrain);
        }
      }
      
      for (String spiEntry : specieToDrain.keySet()) {
        int degree = getSpecieDegree(spiEntry);
        logger.trace("[{}] degree: {}", spiEntry, degree);
        if (degree < 2) {
          logger.trace("WARN: discard specie [{}]", spiEntry);
        } else {
          List<Range> b = new ArrayList<> ();
          for (String drxnEntry : specieToDrain.get(spiEntry)) {
            Range bounds = getBounds(drxnEntry);
            Map<String, String> dstoich = getStoichiometryString(drxnEntry);
            
            Double dstoichValue = Double.parseDouble(dstoich.get(spiEntry));
            logger.trace("[{}] - {} {}: {} {}", spiEntry, getSpecieAttribute(spiEntry, "name"), drxnEntry, bounds, dstoich);
            
            if (dstoichValue != null) {
              //specie is a product flip bonds
              if (dstoichValue > 0) {
                double lb_ = bounds.ub * -1;
                double ub_ = bounds.lb * -1;
                bounds = new Range(lb_, ub_);
                logger.trace("[{}] FIX: flip bounds => [{}, {}]", spiEntry, lbMap, ubMap);
              }
            } 
//            else {
//              logger.error("[{}] specie not found in drain stoichiometry", spiEntry);
//            }
            if (bounds.lb < -1000000000) {
              logger.debug("[{}] lb value to high reducing to default", spiEntry);
              bounds = new Range(defaultLB, bounds.ub);
            }
            if (bounds.ub > 1000000000) {
              logger.debug("[{}] ub value to high reducing to default", spiEntry);
              bounds = new Range(bounds.lb, defaultUB);
            }
            
            b.add(bounds);
          }
          
          double lb = 0;
          double ub = 0;
          
          for (Range p : b) {
            lb += p.lb;
            ub += p.ub;
          }
          
          logger.trace("[{}]: [{}, {}]", spiEntry, lb, ub);
          
          //is specie in E ?
          //EX
          //else DM / SK
          
//          String prefix = "R_EX";
//          String outEntry  = "";
//          if (spiEntry.startsWith("M_")) {
//            outEntry = String.format("%s_%s", prefix, spiEntry.substring(2, spiEntry.length()));
//          } else {
//            outEntry = String.format("%s_%s", prefix, spiEntry);
//          }
//          System.out.println(outEntry + " " + lb + ", " + ub);
          result.put(spiEntry, new ImmutablePair<Double, Double>(lb, ub));
        }
      }
      
    }

    
    return result;
  }
  
  @Override
  public boolean isBoundarySpecie(String spiEntry) {
    XmlSbmlSpecie xspi = xspiMap.get(spiEntry);
    if (xspi.getAttributes().containsKey(SPI_ATTR_BC)) {
      return Boolean.parseBoolean(xspi.getAttributes().get(SPI_ATTR_BC));
    }
    return false;
  }
  
  @Override
  public boolean isDrain(String mrxnEntry) {
    
    if (xrxnType.containsKey(mrxnEntry) && 
        EntityType.DRAIN.equals(xrxnType.get(mrxnEntry))) {
      return true;
    }
    
    int rsize = getReactionSize(mrxnEntry);
    if (rsize == 1) {
      return true;
    }
    
    Map<String, String> stoich = getStoichiometryString(mrxnEntry);
    
    if (drainBoundaryCondition) {
      stoich.keySet().removeAll(boundarySpecies);
    }
    
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    if (stoich.size() == 1) {
      return true;
    }
    
    if (xrxn.getListOfProducts().size() == 1 && xrxn.getListOfReactants().size() == 1) {
      boolean maybeDrain = false;
      for (String s : stoich.keySet()) {
        Integer deg = getSpecieDegree(s);
        if (deg != null && deg == 1) {
          maybeDrain = true;
        }
      }
      if (maybeDrain && drainDeadEnds) {
        return true;
//        System.out.println(mrxnEntry + " " + stoich);
//        for (String s : stoich.keySet()) {
//          System.out.println(s + " " + getSpecieDegree(s));
//        }
//        System.out.println("-----");
      }

    }
    
    return false;
  }
  
  public Map<String, String> getStoichiometryString(String mrxnEntry) {
    Map<String, String> result = new HashMap<> ();
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    for (XmlObject o : xrxn.getListOfReactants()) {
      String s = o.getAttributes().get("species");
      String valueStr = o.getAttributes().get("stoichiometry");
      if (valueStr == null) {
        valueStr = "1";
      }
      result.put(s, valueStr);
    }
    for (XmlObject o : xrxn.getListOfProducts()) {
      String s = o.getAttributes().get("species");
      String valueStr = o.getAttributes().get("stoichiometry");
      if (valueStr == null) {
        valueStr = "1";
      }
      result.put(s, valueStr);
    }
    
    if (drainBoundaryCondition) {
      result.keySet().removeAll(boundarySpecies);
    }
    
    return result;
  }
  
  @Override
  public Integer getSpecieDegree(String spiEntry) {
    if (xspiMap.containsKey(spiEntry)) {
      if (!xspiDegreeMap.containsKey(spiEntry)) {
        int deg = 0;
        for (XmlSbmlReaction xrxn : xrxnMap.values()) {
          for (XmlObject o : xrxn.getListOfReactants()) {
            String s = o.getAttributes().get("species");
            if (spiEntry.equals(s)) {
              deg++;
            }
          }
          for (XmlObject o : xrxn.getListOfProducts()) {
            String s = o.getAttributes().get("species");
            if (spiEntry.equals(s)) {
              deg++;
            }
          }
        }
        xspiDegreeMap.put(spiEntry, deg);
      }
      return xspiDegreeMap.get(spiEntry);
    }
    
    return null;
  }
  
  @Override
  public String getSpecieCompartment(String spiEntry) {
    if (xspiMap.containsKey(spiEntry)) {
      return xspiMap.get(spiEntry).getAttributes().get("compartment");
    }
    return null;
  }
  
  @Override
  public int getReactionSize(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    return xrxn.getListOfProducts().size() + xrxn.getListOfReactants().size();
  }
  
  @Override
  public boolean isTranslocation(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    Set<String> cmp = new HashSet<> ();
    for (XmlObject o : xrxn.getListOfReactants()) {
      String spiEntry = o.getAttributes().get("species");
      String spiCmpEntry = getSpecieCompartment(spiEntry);
      if (spiCmpEntry != null) {
        cmp.add(spiCmpEntry);
      }
    }
    for (XmlObject o : xrxn.getListOfProducts()) {
      String spiEntry = o.getAttributes().get("species");
      String spiCmpEntry = getSpecieCompartment(spiEntry);
      if (spiCmpEntry != null) {
        cmp.add(spiCmpEntry);
      }
    }
    return cmp.size() > 1;
  }
  
  public EntityType detectSpecieType(String spiEntry) {
    if (isBoundarySpecie(spiEntry)) {
      return EntityType.SPECIE_BOUNDARY;
    }
    
    return EntityType.SPECIE;
  }
  
  
  public EntityType detectReactionType(String mrxnEntry) {
    if (isDrain(mrxnEntry)) {
      return EntityType.DRAIN;
    }
    
    if (isTranslocation(mrxnEntry)) {
      return EntityType.TRANSLOCATION;
    }
    
    return EntityType.REACTION;
  }
  
  public XmlSbmlModelAdapter(XmlSbmlModel xmodel) {
    this.xmodel = xmodel;

    for (XmlSbmlCompartment xo : xmodel.getCompartments()) {
      xcmpMap.put(xo.getAttributes().get("id"), xo);
    }
    
    for (XmlObject xparams : xmodel.getListOfParameters()) {
      parameters.put(xparams.getAttributes().get("id"), xparams);
    }
    
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String spiEntry = xspi.getAttributes().get("id");
      xspiMap.put(spiEntry, xspi);
    }
    
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String entry = xrxn.getAttributes().get("id");
      xrxnMap.put(entry, xrxn);
    }
    
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String spiEntry = xspi.getAttributes().get("id");
      xspiMap.put(spiEntry, xspi);
      EntityType t = detectSpecieType(spiEntry);
      xspiType.put(spiEntry, t);
      if (isBoundarySpecie(spiEntry)) {
        boundarySpecies.add(spiEntry);
      }
    }
    
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      String entry = xrxn.getAttributes().get("id");
      EntityType t = detectReactionType(entry);
      xrxnType.put(entry, t);
    }

    //      String gpr = null;

    //      
    //      List<String> geneOrs = new ArrayList<> ();
    //      for (XmlObject o : xrxn.getListOfModifiers()) {
    //        if (o.getAttributes().containsKey("species")) {
    //          String modSpecie = o.getAttributes().get("species");
    //          
    //          XmlSbmlSpecie xspiMod = spiMap.get(modSpecie);
    //          
    //          if (xspiMod != null) {
    //            List<String> genes = new ArrayList<> ();
    //            String str = xspiMod.getAttributes().get("name");
    //            genes.addAll(Arrays.asList(str.split(":")));
    //            if (!genes.isEmpty()) {
    //              geneOrs.add(String.format("( %s )", StringUtils.join(genes, " AND ")));
    //            }
    //          } else {
    //            logger.warn("modifier not found: {}", modSpecie);
    //          }
    //        }
    //      }
    //      
    //      if (!geneOrs.isEmpty()) {
    //        gpr = StringUtils.join(geneOrs, " OR ");
    //      }
    //      
    //      if (gpr != null) {
    //        rxnGpr.put(entry, gpr);
    //      }

  }
  
  public String getGprFromModifiers(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    String gpr = null;
    List<String> geneOrs = new ArrayList<> ();
    for (XmlObject o : xrxn.getListOfModifiers()) {
//      System.out.println(o);
      String sboterm = o.getAttributes().get("sboTerm");
      String modSpecie = o.getAttributes().get("species");
      if ("SBO:0000460".equals(sboterm)) {
        logger.debug("found enzymatic catalyst - [{}]", modSpecie);
      } else if (o.getAttributes().containsKey("species")) {
        XmlSbmlSpecie xspiMod = xspiMap.get(modSpecie);
        
        if (xspiMod != null) {
          logger.debug("found gene modifier: SBO: [{}] species: [{}]", sboterm, modSpecie);
          xspiType.put(modSpecie, EntityType.GENE);
          List<String> genes = new ArrayList<> ();
          String str = xspiMod.getAttributes().get("name");
          genes.addAll(Arrays.asList(str.split(":")));
          if (!genes.isEmpty()) {
            geneOrs.add(String.format("( %s )", StringUtils.join(genes, " AND ")));
          }
        } else {
          logger.warn("modifier not found: {}", modSpecie);
        }
      }
    }
    
    if (!geneOrs.isEmpty()) {
      gpr = StringUtils.join(geneOrs, " OR ");
    }
    
    return gpr;
  }
  
  public String getGprFromFbc(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    MultiNodeTree<Object> a = xrxn.getGpr();
    BFunction<Object, String> f = new BFunction<Object, String>() {
      
      @Override
      public String apply(Object t) {
        if (t instanceof Map) {
          @SuppressWarnings("rawtypes")
          Map<?, ?> m = (Map)t;
          if (m.containsKey("geneProduct")) {
            String geneProduct = (String) m.get("geneProduct");
            for (XmlObject o : xmodel.getListOfGeneProducts()) {
              if (o.getAttributes().get("id").equals(geneProduct)) {
                if (o.getAttributes().containsKey("label")) {
                  return o.getAttributes().get("label");
                }
                return o.getAttributes().get("name");
              }
            }
            return geneProduct;
          }
          return t.toString();
        }
        return t.toString();
      }
    };
    List<String> s = SbmlUtils.gprTreeToString(a, f);
    if (s != null && !s.isEmpty()) {
      return s.iterator().next();
    }
    
    return null;
  }
  
  public String getGprFromNotes(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    String notes = xrxn.getNotes();
    Map<String, Set<String>> ndata = notesParser.parseNotes2(null);
    String ngpr = null;
    if (ndata.containsKey("gene_association") && 
        ndata.get("gene_association").size() > 0) {
      ngpr = ndata.get("gene_association").iterator().next();
    }
    
    return ngpr;
  }
  
  public String[] getFluxBoundFromFluxns(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
    
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
  
  public Boolean isReactionReverible(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
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
  
  public String[] getFluxBoundFromFbc(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
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
  
  public String[] getFluxBoundFromParameters(String mrxnEntry) {
    XmlSbmlReaction xrxn = xrxnMap.get(mrxnEntry);
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
  
  @Override
  public String getGpr(String mrxnEntry) {
    String fgpr = getGprFromFbc(mrxnEntry);
    String mgpr = getGprFromModifiers(mrxnEntry);
    String ngpr = getGprFromNotes(mrxnEntry);
    
    int fgpr_ = DataUtils.empty(fgpr) ? 0 : 1;
    int mgpr_ = DataUtils.empty(mgpr) ? 0 : 1;
    int ngpr_ = DataUtils.empty(ngpr) ? 0 : 1;
    
    if (fgpr_ + mgpr_ + ngpr_ > 1) {
      logger.debug("multiple gpr: F:{}, N:{}, M:{}", fgpr, ngpr, mgpr);
    }
    
    if (fgpr_ > 0) {
      return fgpr;
    }
    
    if (ngpr_ > 0) {
      return ngpr;
    }
    
    if (mgpr_ > 0) {
      return mgpr;
    }
    
    return ngpr;
  }

  @Override
  public SimpleModelReaction<String> getReaction(String rxnId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleModelSpecie<String> getSpecies(String spiId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleCompartment<String> getCompartment(String cmpId) {
    XmlSbmlCompartment xcmp = this.xcmpMap.get(cmpId);
    if (xcmp == null) {
      return null;
    }
    
    SimpleCompartment<String> cmp = new SimpleCompartment<String>(cmpId);
    cmp.name = xcmp.getAttributes().get("name");
    return cmp;
  }

  @Override
  public Set<String> getReactionIds() {
    return new HashSet<>(this.xrxnMap.keySet());
  }

  @Override
  public Set<String> getSpeciesIds() {
    return new HashSet<>(this.xspiMap.keySet());
  }

  @Override
  public Set<String> getCompartmentIds() {
    return new HashSet<>(this.xcmpMap.keySet());
  }
  
  public Double getValue(String str) {
    if (NumberUtils.isParsable(str)) {
      return Double.parseDouble(str);
    }
    return null;
  }
  

  @Override
  public CompartmentalizedStoichiometry<String, String> getCompartmentalizedStoichiometry(String mrxnEntry) {
    XmlSbmlReaction xrxn = this.xrxnMap.get(mrxnEntry);
    
    if (xrxn == null) {
      return null;
    }
    
    CompartmentalizedStoichiometry<String, String> cstoich = new CompartmentalizedStoichiometry<>();
    for (XmlObject xo : xrxn.getListOfReactants()) {
      String specie = xo.getAttributes().get("species");
      String cmp = this.getSpecieCompartment(specie);
      Double value = getValue(xo.getAttributes().get("stoichiometry"));
      cstoich.addLeft(specie, cmp, value == null ? 1.0 : value);
    }
    for (XmlObject xo : xrxn.getListOfProducts()) {
      String specie = xo.getAttributes().get("species");
      String cmp = this.getSpecieCompartment(specie);
      Double value = getValue(xo.getAttributes().get("stoichiometry"));
      cstoich.addRight(specie, cmp, value == null ? 1.0 : value);
    }
    
    return cstoich;
  }
}
