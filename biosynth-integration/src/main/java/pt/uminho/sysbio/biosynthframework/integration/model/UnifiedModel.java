package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.BFunction;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.EntityType;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.Range;
import pt.uminho.sysbio.biosynthframework.SimpleModelReaction;
import pt.uminho.sysbio.biosynthframework.SimpleModelSpecie;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.integration.model.UnifiedModel.ModelMetadata.MetaboliteMetadata;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModelAdapter;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlReaction;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.util.BiosStringUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.GprUtils;
import pt.uminho.sysbio.biosynthframework.util.GraphUtils;
import pt.uminho.sysbio.biosynthframework.util.MapUtils;

public class UnifiedModel {
  
  private static final Logger logger = LoggerFactory.getLogger(UnifiedModel.class);
  
  public static class ModelMetadata {
    
    public static class MetaboliteMetadata {
      public Set<String> species = new HashSet<>();
      public Map<MetaboliteMajorLabel, String> databaseReferences = new HashMap<>();
      
      public ExternalReference getReference(MetaboliteMajorLabel database) {
        if (databaseReferences.containsKey(database)) {
          return new ExternalReference(
              databaseReferences.get(database), database.toString());
        }
        return null;
      }
    }
    
    public String model;
    public Map<String, SubcellularCompartment> compartments = new HashMap<>();
    public Map<String, MetaboliteMetadata> metabolites = new HashMap<>();
  }
  
  public static class IntegratedModelChangeReporter {
    
    private final UnifiedModel modelUnifier;
    
    public IntegratedModelChangeReporter(UnifiedModel modelUnifier) {
      this.modelUnifier = modelUnifier;
    }
    
    public Map<Long, Map<Long, Double>> originalStoich = new HashMap<>();
    
    public boolean stoichChange(long rxnId, Map<Long, Double> stoich) {
      if (!originalStoich.containsKey(rxnId)) {
        originalStoich.put(rxnId, new HashMap<>(stoich));
        return true;
      }
      return false;
    }
    
    public void report() {
      for (long rxnId : originalStoich.keySet()) {
        Map<Long, Double> before = originalStoich.get(rxnId);
        Map<Long, Double> after = modelUnifier.idToStoich.get(rxnId);
        String rxnEntry = modelUnifier.entryMap.get(rxnId);
        System.out.println(rxnEntry);
        System.out.println(BiosStringUtils.mapToString(before, modelUnifier.entryMap));
        System.out.println(BiosStringUtils.mapToString(after, modelUnifier.entryMap));
        System.out.println();
        
      }
    }
  }
  
  
  public Map<String, Set<Long>> geneToReaction = new HashMap<>();
  
  public Map<String, IntegratedModelChangeReporter> reporterMap = new HashMap<>();
  public Map<Long, Set<EntityType>> idToTypes = new HashMap<>();
  public BMap<Long, Long> idToCmpId = new BHashMap<>();
  public BMap<Long, Boolean> idToBoundary = new BHashMap<>();
  public BMap<Long, SubcellularCompartment> cmpIdToScmp = new BHashMap<>();
  public Map<Long, String> idToGpr = new HashMap<>();
  public Map<Long, Set<Long>> gnIdToRxnId = new HashMap<>();
  public BMap<Long, String> idToName = new BHashMap<>();
  public Map<Long, Range> idToBound = new HashMap<>();
  public Map<Long, Map<Long, Double>> idToStoich = new HashMap<>();
  public BMap<Long, String> namespaceMap = new BHashMap<>();
  public BMap<Long, String> entryMap = new BHashMap<>();
  public Map<Long, EntityType> typeMap = new HashMap<>();
  public Map<ExternalReference, String> cpdToSpiId = new HashMap<>();
  public Map<Long, XmlSbmlReaction> mrxnMap = new HashMap<>();
  public Map<Long, Set<Long>> idToRxnIds = new HashMap<>();
  public Set<Long> deletedObjects = new HashSet<>();
  
  //?
  public UndirectedGraph<Long, Object> g = new SimpleGraph<>(Object.class);
  //? model reaction annotation
  public UndirectedGraph<Long, Object> g2 = new SimpleGraph<>(Object.class);
  
  private long idCounter = 0;
  
  public boolean hasClass(long id, EntityType clazz) {
    if (idToTypes.containsKey(id) &&
        idToTypes.get(id).contains(clazz)) {
      return true;
    }
    return false;
  }
  
  public void addClassToObject(String database, String entry, EntityType clazz) {
    Long id = this.getId(entry, database);
    if (id != null) {
      addClassToObject(id, clazz);
    }
  }
  
  public void addClassToObject(long id, EntityType clazz) {
    if (!idToTypes.containsKey(id)) {
      idToTypes.put(id, new HashSet<EntityType>());
    }
    idToTypes.get(id).add(clazz);
    
    logger.info("[{}]{} -> {}", id, entryMap.get(id), idToTypes.get(id));
  }
  
  public void rebuildDegreeMap() {
    logger.info("build degree map ...");
    idToRxnIds.clear();
    for (long rxnId : idToStoich.keySet()) {
      Map<Long, Double> stoich = idToStoich.get(rxnId);
      for (long id : stoich.keySet()) {
        if (!idToRxnIds.containsKey(id)) {
          idToRxnIds.put(id, new HashSet<Long>());
        }
        idToRxnIds.get(id).add(rxnId);
      }
    }
    logger.info("build degree map ... done!");
  }
  
  public SimpleModelSpecie<String> getModelSpecie(long id) {
    String entry = this.entryMap.get(id);
    String name = this.idToName.get(id);
    Long cmpId = this.idToCmpId.get(id);
    String cmpEntry = null;
    if (cmpId != null) {
      cmpEntry = this.entryMap.get(cmpId);
    } else {
      logger.info("[{}]{}", id, entry);
    }
//    entry = entry.replaceFirst("M-", "");
//    System.out.println(entry + " " + cmpEntry + " " + name);
    SimpleModelSpecie<String> result = new SimpleModelSpecie<String>(entry, name, cmpEntry);
    
    return result;
  }
  
  public SimpleModelReaction<String> getModelReaction(long id) {
    SimpleModelReaction<String> result = null;
    Range bound = this.idToBound.get(id);
//    bound = new Range(0.0, 1000.0);
    String entry = this.entryMap.get(id);
    String gpr = this.idToGpr.get(id);
    Map<Long, Double> base = this.idToStoich.get(id);
    double lb = bound.lb;
    double ub = bound.ub;
    
    Map<String, Double> stoich = new HashMap<>();
    for (long i : base.keySet()) {
      String spiEntry = entryMap.get(i);
//      spiEntry = spiEntry.replaceFirst("M-", "");
      stoich.put(spiEntry, base.get(i));
    }
    
    result = new SimpleModelReaction<String>(entry, lb, ub);
    result.name = idToName.get(id);
    result.gpr = gpr;
    result.stoichiometry.putAll(stoich);
    
    return result;
  }
  
  public SimpleModelReaction<String> mergeReactions(Set<Long> ids, boolean ignoreGpr) {
    if (ids.size() > 1) {
      logger.info("merge: {}", ids);
    }
    SimpleModelReaction<String> result = null;
    
    List<String> entryArray = new ArrayList<>();
    List<String> gprArray = new ArrayList<>();
    List<Range> boundArray = new ArrayList<>();
    List<Map<Long, Double>> stoichArray = new ArrayList<>();
    List<String> nameArray = new ArrayList<>();
    
    for (long i : ids) {
      Range bound = this.idToBound.get(i);
//      bound = new Range(0.0, 1000.0);
      entryArray.add(this.entryMap.get(i));
      gprArray.add(this.idToGpr.get(i));
      boundArray.add(bound);
      stoichArray.add(this.idToStoich.get(i));
      nameArray.add(idToName.get(i));
    }
    
    if (ids.size() > 1) {
      logger.info("merge: {}", entryArray);
    }
    
    
//    logger.info("merge: {}", entryArray, stoichArray);
    
    if (new HashSet<>(gprArray).size() == 1 || ignoreGpr) {
      Set<String> nonEmpty = new HashSet<>();
      for (String g : gprArray) {
        if (!DataUtils.empty(g)) {
          nonEmpty.add(String.format("(%s)", g.trim()));
        }
      }
      String gprString = StringUtils.join(nonEmpty, " or ");
//      String name = nameArray.get(index)
      Map<Long, Double> base = stoichArray.get(0);
      Range bound = boundArray.get(0);
      double lb = bound.lb;
      double ub = bound.ub;
      
      for (int i = 1; i < entryArray.size(); i++) {
        Map<Long, Double> stoichMerge = stoichArray.get(i);
        Range boundMerge = boundArray.get(i);
//        System.out.println(base + " " + stoichMerge);
        if (base.equals(stoichMerge)) {
          
        } else if (base.equals(MapUtils.scale(stoichMerge, -1))) {
          logger.debug("FLIP");
          lb += boundMerge.ub * -1;
          ub += boundMerge.lb * -1;
        } else {
          logger.warn("wrong stoich");
        }
      }
      
      Map<String, Double> stoich = new HashMap<>();
      for (long i : base.keySet()) {
        String spiEntry = entryMap.get(i);
//        spiEntry = spiEntry.replaceFirst("M-", "");
        stoich.put(spiEntry, base.get(i));
      }
      result = new SimpleModelReaction<String>(entryArray.get(0), lb, ub);
      result.name = nameArray.get(0);
      result.gpr = gprString;
      result.stoichiometry.putAll(stoich);
      result.extra.put("id", ids.iterator().next());
      
    } else {
      logger.warn("!!!!!! {}", gprArray);
    }

    return result;
  }
  
  public Set<Long> getIds(String namespace, EntityType type) {
    Set<Long> ids = new HashSet<>();
    
    for (long id : entryMap.keySet()) {
      String ns = namespaceMap.get(id);
      EntityType t = typeMap.get(id);
      if (type.equals(t) && namespace.equals(ns)) {
        ids.add(id);
      }
    }
    
    return ids;
  }
  
  public Long getId(String entry, String namespace, EntityType type) {
    Set<Long> candidates = entryMap.bget(entry);
    Set<Long> match = new HashSet<>();
    if (candidates != null) {
      for (long id : candidates) {
        String ns = namespaceMap.get(id);
        EntityType etype = typeMap.get(id);
        if (ns.equals(namespace) &&
            type.equals(etype)) {
          match.add(id);
        }
      }
    }
    
    if (match.size() == 1) {
      return match.iterator().next();
    } else if (match.size() > 1){
      logger.warn("corrupted index multiple match {}", match);      
    }
    
    return null;
  }
  
  public Long getId(String entry, String namespace) {
    Set<Long> candidates = entryMap.bget(entry);
    Set<Long> match = new HashSet<>();
    if (candidates != null) {
      for (long id : candidates) {
        String ns = namespaceMap.get(id);
        if (ns.equals(namespace)) {
          match.add(id);
        }
      }
    }
    
    if (match.size() == 1) {
      return match.iterator().next();
    } else if (match.size() > 1){
      logger.warn("corrupted index multiple match {}", match);
    }
    
    return null;
  }
  
  public long generateId() {
    return idCounter++;
  }
  
  public Set<String> translateToModel(Collection<Long> ids) {
    Set<String> result = new HashSet<>();
    
    for (long i : ids) {
      result.add(namespaceMap.get(i));
    }
    
    return result;
  }
  
  public Set<String> translateToEntries(Collection<Long> ids) {
    Set<String> result = new HashSet<>();
    
    for (long i : ids) {
      result.add(entryMap.get(i));
    }
    
    return result;
  }
  
  public Set<ExternalReference> getReferences(long id) {
    Set<Long> cc = GraphUtils.getConnectedVertex(id, g);
    Set<ExternalReference> result = new HashSet<>();
    
    for (long i : cc) {
      EntityType t = typeMap.get(i);
      if (EntityType.METABOLITE.equals(t) || EntityType.REACTION.equals(t)) {
        String dbEntry = entryMap.get(i);
        String database = namespaceMap.get(i);
        ExternalReference reference = new ExternalReference(dbEntry, database);
        result.add(reference);
      }
    }
    return result;
  }
  
  public<E> Set<ExternalReference> getReferences(long id, UndirectedGraph<Long, E> g) {
    Set<Long> cc = GraphUtils.getConnectedVertex(id, g);
    Set<ExternalReference> result = new HashSet<>();
    if (cc != null) {
      for (long i : cc) {
        EntityType t = typeMap.get(i);
        if (EntityType.METABOLITE.equals(t) || EntityType.REACTION.equals(t)) {
          String dbEntry = entryMap.get(i);
          String database = namespaceMap.get(i);
          ExternalReference reference = new ExternalReference(dbEntry, database);
          result.add(reference);
        }
      }
    }

    return result;
  }
  
  public ExternalReference getReference(long id, String database) {
    logger.debug("{} {}", id, database);
    Set<Long> cc = GraphUtils.getConnectedVertex(id, g);
//    System.out.println(cc);
    ExternalReference result = null;
    
    for (long i : cc) {
      EntityType t = typeMap.get(i);
      if (EntityType.METABOLITE.equals(t) || EntityType.REACTION.equals(t)) {
        String dbEntry = entryMap.get(i);
        String databaseStr = namespaceMap.get(i);
        if (databaseStr.equals(database)) {
          if (result != null) {
            logger.warn("Reference conflicts: [{}]{} {} -> {}@{}", id, entryMap.get(id), result, dbEntry, databaseStr);
          }
          result = new ExternalReference(dbEntry, databaseStr);
        }
      }
    }
    return result;
  }
  
  public void getIntegratedMetabolite(long id) {
    
  }
  
  public Set<Long> getIntegratedMetabolite(ExternalReference reference) {
    Set<Long> result = new HashSet<>();
    Long id = getId(reference.entry, reference.source);
    if (id == null) {
      logger.warn("not found {}", reference);
      
      return result;
    }
    result = GraphUtils.getConnectedVertex(id, g);
//    UndirectedGraph<Long, Object> gg = ModelsAgain.subgraph(id, Object.class, g);
//    for (Object ee : gg.edgeSet()){
//      long source = gg.getEdgeSource(ee);
//      long target = gg.getEdgeTarget(ee);
//      String sentry = this.entryMap.get(source);
//      String tentry = this.entryMap.get(target);
//      String sname = this.idToName.get(source);
//      String tname = this.idToName.get(target);
//      System.out.println(sentry + "\t" + tentry + "\t" + sname + "\t" + tname);
//    }
//    ModelsAgain.ggg(gg);
    
    return result;
  }
  
  public void getReaction(Set<ExternalReference> a) {
    cpdToSpiId.get(a);
  }
  
  public long registerObject(String entry, String ns, EntityType type) {
    Long id = getId(entry, ns, type);
    if (id == null) {
      id = generateId();
      entryMap.put(id, entry);
      namespaceMap.put(id, ns);
      typeMap.put(id, type);
      GraphUtils.addVertexIfNotExists(g, id);
      logger.debug("[ADD] {} {} @ {}", type, entry, ns);
    }
    return id;
  }
  
  public long registerMetabolite(String cpdEntry, MetaboliteMajorLabel database) {
    return registerObject(cpdEntry, database.toString(), EntityType.METABOLITE);
  }
  
  public long registerReaction(String cpdEntry, ReactionMajorLabel database) {
    return registerObject(cpdEntry, database.toString(), EntityType.REACTION);
  }
  
  public long registerGene(String gnEntry, String modelEntry) {
    return registerObject(gnEntry, modelEntry, EntityType.GENE);
  }
  
  public long registerModelCompartment(String modelEntry, String cmpEntry, String name, SubcellularCompartment scmp) {
    Long cmpId = registerObject(cmpEntry, modelEntry, EntityType.AUXILIAR);
    if (scmp != null) {
      cmpIdToScmp.put(cmpId, scmp);
    }
    if (!DataUtils.empty(name)) {
      idToName.put(cmpId, name);
    }
    return cmpId;
  }
  
  public long registerSpecie(String modelEntry, String spiEntry, String cmpEntry, String name) {
    Long spiId = registerObject(spiEntry, modelEntry, EntityType.SPECIE);
    Long cmpId = getId(cmpEntry, modelEntry, EntityType.AUXILIAR);
    
    idToCmpId.put(spiId, cmpId);
    if (!DataUtils.empty(name)) {
      idToName.put(spiId, name);
    }
    
    return spiId;
  }
  
  public long registerModelReaction(String modelEntry, String mrxnEntry) {
    return registerObject(mrxnEntry, modelEntry, EntityType.MODEL_REACTION);
  }
  
  public void duplicates(Set<Long> ids) {
    GraphUtils.addConnectedSet(g, ids);
  }
  
  public void addModel(String modelEntry, XmlSbmlModel xmodel, XmlSbmlModelAdapter adapter) {
    this.addModel(modelEntry, xmodel, adapter, null);
  }
  
  
  
  public void registerMetaboliteSet(String modelEntry, Set<String> spiEntrySet) {
    Set<Long> ids = new HashSet<>();
    
    for (String spiEntry : spiEntrySet) {
      Long id = getId(spiEntry, modelEntry, EntityType.SPECIE);
      ids.add(id);
    }
    GraphUtils.addConnectedSet(g, ids);
  }
  
  public void addModel(String modelEntry, XmlSbmlModel xmodel, XmlSbmlModelAdapter adapter, ModelMetadata metadata) {
    for (XmlSbmlSpecie xspi : xmodel.getSpecies()) {
      String spiEntry = xspi.getAttributes().get("id");
      if (!DataUtils.empty(spiEntry)) {
        registerSpecie(modelEntry, spiEntry, null, null);
      }
    }
    
    if (metadata != null) {
      for (String k : metadata.metabolites.keySet()) {
        MetaboliteMetadata cpdData = metadata.metabolites.get(k);
        this.registerMetaboliteSet(modelEntry, cpdData.species);
//        Set<Long> ids = new HashSet<>();
//        for (String spiEntry : cpdData.species) {
//          Long id = getId(spiEntry, modelEntry, EntityType.SPECIE);
//          ids.add(id);
//          System.out.println(id + " " + k + " " + cpdData.species);
//        }
//        GraphUtils.addConnectedSet(g, ids);
      }
    }
    
    
    for (XmlSbmlReaction xrxn : xmodel.getReactions()) {
      long id = generateId();
      String mrxnEntry = xrxn.getAttributes().get("id");
      entryMap.put(id, mrxnEntry);
      mrxnMap.put(id, xrxn);
      namespaceMap.put(id, modelEntry);
      
      for (XmlObject o : xrxn.getListOfReactants()) {
//        o.getAttributes().get("species");
//        System.out.println(o);
      }
      
      if (mrxnEntry != null) {
        String gpr = adapter.getGpr(mrxnEntry);
        if (!DataUtils.empty(gpr)) {
          try {
            Set<String> gprGenes = getGenes(gpr, null);
            for (String gene : gprGenes) {
              if (!geneToReaction.containsKey(gene)) {
                geneToReaction.put(gene, new HashSet<Long>());
              }
              geneToReaction.get(gene).add(id);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
  
  public static Set<String> getGenes(String gprExpression) {
    Set<String> genes = GprUtils.getGenes(gprExpression);
    
//    if (gprExpression == null || gprExpression.trim().isEmpty()) {
//      return genes;
//    }
//    
//    if (gprExpression.contains("N/A")) {
//      gprExpression = gprExpression.replaceAll("N/A", "unknown");
//    }
//    
//    String[] tks = new String[]{"\""};
//    
//    String prev = gprExpression.trim();
//    do {
//      prev = gprExpression;
//      for (String s : tks) {
//        gprExpression = StringUtils.removeStart(gprExpression, s);
//        gprExpression = StringUtils.removeEnd(gprExpression, s);
//      }
//    } while (prev != gprExpression);
//    
////    @ |; | / 
//    for (String subgpr : gprExpression.split("[;|]")) {
//      if (subgpr != null && !subgpr.trim().isEmpty()) {
//        GeneReactionRuleCI grrci;
//        try {
//          grrci = new GeneReactionRuleCI(subgpr);
//          genes.addAll(getGenes(grrci));
//        } catch (Exception | TokenMgrError e) {
//          throw new IllegalArgumentException("invalid gpr:" + subgpr);
//        }
//      }
//
//    }
    return genes;
  }
  
  public static Set<String> getGenes(String gpr, BFunction<String, String> geneTransform) {
    Set<String> genes = new TreeSet<> ();
    Set<String> grp = getGenes(gpr);
    
    if (grp != null && !grp.isEmpty()) {
      Set<String> validLocus = new HashSet<> ();
      for (String g : grp) {
        if (!NumberUtils.isNumber(g.trim())) {
          if (geneTransform != null) {
            g = geneTransform.apply(g);
          }
          if (g != null && !g.trim().isEmpty()) {
            validLocus.add(g);
          }
        } else {
          logger.debug("[IGNORE NUMBER LOCUS] gene: {}", g);
        }
      }
      genes.addAll(validLocus);
    }
    
    logger.trace("GPR: {}, Genes: {}", gpr, genes);
    
    return genes;
  }

  public void registerModelReactionAnnoation(String modelEntry, long mrxnId, 
      Map<ReactionMajorLabel, String> references) {
    logger.info("[{}] {} {}", modelEntry, mrxnId, references);
    Set<Long> ids = new HashSet<>();
    ids.add(mrxnId);
    for (ReactionMajorLabel database : references.keySet()) {
      long id = this.registerReaction(references.get(database), database);
      ids.add(id);
    }

    GraphUtils.addConnectedSet(g, ids);
  }
  
  public void integratedCompartment(String modelEntry, String cmpEntry, SubcellularCompartment scmp) {
    Long cmpId = this.getId(cmpEntry, modelEntry);
    if (cmpId != null) {
      logger.info("[{}] {} -> {}", modelEntry, cmpEntry, scmp);
      cmpIdToScmp.put(cmpId, scmp);
    }
  }
  
  public void registerModelReactionAnnoation(String modelEntry, 
      Map<String, Map<ReactionMajorLabel, String>> annotation) {
    for (String mrxnEntry : annotation.keySet()) {
      Map<ReactionMajorLabel, String> references = annotation.get(mrxnEntry);
      Long mrxnId = this.getId(mrxnEntry, modelEntry, EntityType.MODEL_REACTION);
      if (mrxnId != null) {
        this.registerModelReactionAnnoation(modelEntry, mrxnId, references);
      }
    }
  }
  
  public void registerSpecieAnnoation(String modelEntry, String spiEntry, 
                                      Map<MetaboliteMajorLabel, String> references) {
    
    Set<Long> ids = new HashSet<>();
    Long spiId = getId(spiEntry, modelEntry, EntityType.SPECIE);
    if (spiId != null) {
      ids.add(spiId);

      for (MetaboliteMajorLabel database : references.keySet()) {
        String refEntry = references.get(database);
        long id = this.registerMetabolite(refEntry, database);
        logger.debug("[+REFERENCE] [{}]{} - {} -> [{}]{} - {}", spiId, spiEntry, idToName.get(spiId), id, refEntry, idToName.get(id));
        ids.add(id);
      }
    }
    
    GraphUtils.addConnectedSet(g, ids);
  }
  
  public void registerSpecieAnnoation(String modelEntry, 
      Map<String, Map<MetaboliteMajorLabel, String>> specieAnnotation) {
    for (String spiEntry : specieAnnotation.keySet()) {
      Map<MetaboliteMajorLabel, String> references = specieAnnotation.get(spiEntry);
      this.registerSpecieAnnoation(modelEntry, spiEntry, references);
    }
  }

  public void reduceMergeReaction(String modelEntry, 
                                  String rxnEntryA, 
                                  String rxnEntryB,
                                  String rxnEntryC,
                                  String component) {
    Long rxnIdA = this.getId(rxnEntryA, modelEntry);
    Long rxnIdB = this.getId(rxnEntryB, modelEntry);
    Long cpdId = this.getId(component, modelEntry);
    
    if (rxnIdA != null && rxnIdB != null && cpdId != null) {
      logger.info("Merge: [{}]{} + [{}]{} @ [{}]{} -> {}", rxnIdA, rxnEntryA, rxnIdB, rxnEntryB, cpdId, component, rxnEntryC);
      Map<Long, Double> stoichA = this.idToStoich.get(rxnIdA);
      Map<Long, Double> stoichB = this.idToStoich.get(rxnIdB);
      
      Map<Long, Double> stoichC = MapUtils.rxnMathSumNeu(stoichA, stoichB, cpdId);
      stoichC = MapUtils.filterZero(stoichC);
      
      Range rangeA = this.idToBound.get(rxnIdA);
      Range rangeB = this.idToBound.get(rxnIdB);
      String gprA = this.idToGpr.get(rxnIdA);
      String gprB = this.idToGpr.get(rxnIdB);
      logger.debug("{} {}", rangeA, rangeB);
      logger.debug("{} {}", gprA, gprB);
      
      
      
//      System.out.println(stoichA);
//      System.out.println(stoichB);
//      System.out.println(stoichC);
      
//      Map<Long, String> alias = new HashMap<>();
//      for (long a : Sets.union(stoichA.keySet(), stoichB.keySet())) {
//        alias.put(a, this.entryMap.get(a));
//      }
//      System.out.println(ReactionPrinter.mapToString(stoichA, alias));
//      System.out.println(ReactionPrinter.mapToString(stoichB, alias));
//      System.out.println(ReactionPrinter.mapToString(stoichC, alias));
      
      
      long rxnIdC = this.registerModelReaction(modelEntry, rxnEntryC);
      this.idToStoich.put(rxnIdC, stoichC);
      this.idToBound.put(rxnIdC, new Range(0.0, 1000.0));
    }
  }

  public void mergeCompounds(String modelEntry, String cpdEntryA, String cpdEntryB) {
    IntegratedModelChangeReporter changeReporter = this.reporterMap.get(modelEntry);
    Long cpdIdA = this.getId(cpdEntryA, modelEntry);
    Long cpdIdB = this.getId(cpdEntryB, modelEntry);
    
    if (cpdIdA != null && cpdIdB != null) {
      logger.info("Merge: [{}]{} -> [{}]{}", cpdIdA, cpdEntryA, cpdIdB, cpdEntryB);
      
      int modified = 0;
      
      for (long rxnId : idToStoich.keySet()) {
        Map<Long, Double> s = idToStoich.get(rxnId);
        if (s.containsKey(cpdIdA)) {
          if (changeReporter != null) {
            changeReporter.stoichChange(rxnId, s);
          }
          Double valueB = s.get(cpdIdB);
          if (valueB == null) {
            valueB = 0.0;
          }
          double valueA = s.remove(cpdIdA) + valueB;
          s.put(cpdIdB, valueA);
          modified++;
          
          Map<Long, Double> clean = MapUtils.filterZero(s);
          s.clear();
          s.putAll(clean);
          logger.debug("[{}] {}", rxnId, s);
        }
      }
      
      logger.info("Changed reactions: {}", modified);
    }
  }

  public void reactionDeduplication() {
    Map<Map<Long, Double>, Set<Long>> stoichToRxnIds = new HashMap<>();
    for (long rxnId : this.idToStoich.keySet()) {
      Map<Long, Double> stoich = this.idToStoich.get(rxnId);
      if (!stoichToRxnIds.containsKey(stoich)) {
        stoichToRxnIds.put(stoich, new HashSet<Long>());
      }
      stoichToRxnIds.get(stoich).add(rxnId);
    }
    
    for (Object s : stoichToRxnIds.keySet()) {
      if (stoichToRxnIds.get(s).size() > 1) {
        Set<String> aaa = new HashSet<>();
        for (long i : stoichToRxnIds.get(s)) {
          aaa.add(this.entryMap.get(i));
        }
        System.out.println(s + " " + aaa + " " + stoichToRxnIds.get(s));
      }
    }
  }

  public void deleteEmptyReactions(boolean includeDeletedObjects, boolean updateStoich) {
    Map<Long, Map<Long, Double>> update = new HashMap<>();
    for (long rxnId : this.idToStoich.keySet()) {
      Map<Long, Double> stoich = new HashMap<>(this.idToStoich.get(rxnId));
      if (stoich.keySet().removeAll(deletedObjects)) {
        update.put(rxnId, stoich);
      }
//      System.out.println(rxnId + " " + this.entryMap.get(rxnId) + " " + stoich);
      if (stoich.isEmpty()) {
        logger.info("[DELETE] : [{}]{}", rxnId, this.entryMap.get(rxnId));
        this.deletedObjects.add(rxnId);
      }
    }
    
    if (updateStoich) {
      for (long rxnId : update.keySet()) {
        logger.info("[UPDATE] : [{}]{} {} -> {}", rxnId, this.entryMap.get(rxnId), this.idToStoich.get(rxnId), update.get(rxnId));
        this.idToStoich.put(rxnId, update.get(rxnId));
      }
    }
  }

  public void deleteZeroDegreeSpecies(String namespace) {
    for (long id : this.getIds(namespace, EntityType.SPECIE)) {
      Set<Long> ids = idToRxnIds.get(id);
      if (ids == null) {
        ids = new HashSet<>();
      }
      if (ids.size() == 0) {
        logger.info("[DELETE] [{}] : [{}]{} - {}", namespace, id, this.entryMap.get(id), idToName.get(id));
        this.deletedObjects.add(id);
      }
    }
    
  }
  

  public boolean is(long id, EntityType t) {
    return this.idToTypes.containsKey(id) && this.idToTypes.get(id).contains(t);
  }
}
