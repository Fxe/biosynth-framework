package pt.uminho.sysbio.biosynthframework.integration.assembler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbPropertyNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.cheminformatics.ChemicalStructure;
import pt.uminho.sysbio.biosynthframework.cheminformatics.InChIKey;
import pt.uminho.sysbio.biosynthframework.cheminformatics.render.CdkSVGRenderer;
import pt.uminho.sysbio.biosynthframework.integration.TheBestIntegrationMethod;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.BiggReactionAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.BiocycReactionAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.HmdbAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.KeggReactionAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.assembler.database.LipidmapsAssemblePlugin;
import pt.uminho.sysbio.biosynthframework.integration.function.CurationFunction;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.ConnectedComponentsUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;

public class DefaultAssembler {
  
  private static final Logger logger = LoggerFactory.getLogger(DefaultAssembler.class);
  
  public static class DatabaseTable {
    public List<Object> records = new ArrayList<> ();
    
    public void add(MetaboliteData cpdData) {
      IndexCRecord record = new IndexCRecord(cpdData);
      records.add(record);
    }
    
    public void add(ReactionData rxnData) {
      IndexRRecord record = new IndexRRecord(rxnData);
      records.add(record);
    }
    
    public static class IndexCRecord {
      public String id;
      public boolean structure = false;
      public String formula;
      public String name;
      public Set<String> synonyms = new TreeSet<>();
      public int rxns = 0;
      public String alias;
      public Set<ExternalReference> references = new HashSet<> ();
      
      public IndexCRecord(MetaboliteData cpdData) {
        this.id = cpdData.id;
        this.formula = cpdData.formula;
        this.name = cpdData.name;
        this.synonyms = cpdData.synonyms;
        this.alias = cpdData.alias;
        this.references = cpdData.references;
        this.rxns = cpdData.reactions.size();
        if (!DataUtils.empty(cpdData.mol)) {
          this.structure = true;
        }
      }
    }
    
    public static class IndexRRecord {
      public String id;
      public String equation;
      public String ecn;
      public String name;
      public Set<String> synonyms = new TreeSet<>();
      public String alias;
      public SimpleStoichiometry<String, String> stoichiometry;
      public Set<ExternalReference> references = new HashSet<> ();
      
      public IndexRRecord(ReactionData rxnData) {
        this.id = rxnData.id;
        this.equation = rxnData.equation;
        this.ecn = rxnData.ecn;
        this.name = rxnData.name;
        this.alias = rxnData.alias;
        this.references = rxnData.references;
        this.stoichiometry = rxnData.stoichiometry;
      }
    }
  }
  
  private final BiodbGraphDatabaseService databaseService;
  public String outputPath = "/opt/nginx-1.9.6/html/biosynth-web-biobase/exports/idatabase";
  
  private final ConnectedComponents<Long> cpdIntegration;
  private final ConnectedComponents<Long> rxnIntegration;
  
  private Map<ExternalReference, String> rgeneratedIdentifier = new HashMap<>();
  private Map<Set<Long>, String> sgeneratedIdentifier = new HashMap<>();
  private Map<Long, String> igeneratedIdentifier = new HashMap<>();
  private Map<String, MetaboliteData> mdata = new HashMap<>();
  private Map<String, Set<Long>> rxnToStoichCpds = new HashMap<>();

  public AliasGenerator cpdAliasGenerator;
  public AliasGenerator rxnAliasGenerator;
  
  public DefaultAssembler(GraphDatabaseService databaseService, 
                             ConnectedComponents<Long> cpdIntegration,
                             ConnectedComponents<Long> rxnIntegration) {
    this.databaseService = new BiodbGraphDatabaseService(databaseService);
    this.cpdIntegration = cpdIntegration;
    this.rxnIntegration = rxnIntegration;
  }
  
  public static<C, V> Map<Set<C>, V> integratedStoich(Map<C, V> stoich, ConnectedComponents<C> ccs) {
    Map<Set<C>, V> result = new HashMap<>();
    
    for (C compound : stoich.keySet()) {
      Set<C> cc = ccs.getConnectedComponentOf(compound);
      if (cc == null) {
        cc = new HashSet<>();
      }
      cc.add(compound);
      result.put(cc, stoich.get(compound));
    }
    
    return result;
  }
  
  public void generateIntegratedIds() {
    idCounter = 0;
    for (Set<Long> cc : cpdIntegration) {
      String gid = generateId(cc, "icpd");
      sgeneratedIdentifier.put(cc, gid);
      for (long id : cc) {
        igeneratedIdentifier.put(id, gid);
      }
    }
    idCounter = 0;
    for (Set<Long> cc : rxnIntegration) {
      String gid = generateId(cc, "irxn");
      sgeneratedIdentifier.put(cc, gid);
      rxnToStoichCpds.put(gid, new HashSet<Long>());
      for (long id : cc) {
        BiodbReactionNode rxnNode = databaseService.getReaction(id);
        Set<Long> l = new HashSet<>(rxnNode.getLeftStoichiometry().keySet());
        Set<Long> r = new HashSet<>(rxnNode.getRightStoichiometry().keySet());
        rxnToStoichCpds.get(gid).addAll(l);
        rxnToStoichCpds.get(gid).addAll(r);
        igeneratedIdentifier.put(id, gid);
      }
    }
  }
  
  public Long assembleInchi(Set<BiodbMetaboliteNode> cpdNodes) {
    Map<Long, Integer> freq = new HashMap<>();
    int high = 0;
    for (BiodbMetaboliteNode cpdNode : cpdNodes) {
      MetabolitePropertyLabel property = MetabolitePropertyLabel.InChI;
      Set<BiodbPropertyNode> p = cpdNode.getMetaboliteProperties(property);
      System.out.println(cpdNode.getEntry() + " " + cpdNode.getDatabase());
      
      for (BiodbPropertyNode pp : p) {
        System.out.println(pp.getId());
        CollectionUtils.increaseCount(freq, pp.getId(), 1);
        int v = freq.get(pp.getId());
        if (v > high) {
          high = v;
        }
      }
    }
    
    if (!freq.isEmpty()) {
      BMap<Long, Integer> bfreq = new BHashMap<>(freq);
      Set<Long> highMatches = bfreq.bget(high);
      return highMatches.iterator().next();
    }
    
    return null;
  }
  
  public ChemicalStructure expandInchi(long inchiId) {
    ChemicalStructure result = new ChemicalStructure();
//    Map<MetabolitePropertyLabel, String> result = new HashMap<>();
    
    BiodbPropertyNode inchiNode = new BiodbPropertyNode(databaseService.getNodeById(inchiId), null);
    Map<Integer, Set<String>> names = new TreeMap<>(Collections.reverseOrder());
    Set<String> usmiles = new HashSet<> ();
    Set<String> smiles = new HashSet<> ();
    
    result.inchi = inchiNode.getValue();
    
    for (Relationship r : inchiNode.getRelationships()) {
      Node other = r.getOtherNode(inchiNode);
      if (other.hasLabel(GlobalLabel.MetaboliteProperty)) {
        String value = (String) other.getProperty(
            Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
        MetabolitePropertyLabel metaboliteProperty = MetabolitePropertyLabel.valueOf(
            (String) other.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
        switch (metaboliteProperty) {
          case MolecularFormula:
            if (result.formula == null) {
              result.formula = value;
            } else {
              logger.warn("MolecularFormula overlap: {} -> {}", result.formula, value);
            }
            break;
          case InChIKey:
            if (result.inchiKey == null) {
              result.inchiKey = new InChIKey(value);
            } else {
              logger.warn("InChIKey overlap: {} -> {}", result.inchiKey, value);
            }
            break;
          case SMILES:
            if (other.hasLabel(MetabolitePropertyLabel.UniversalSMILES)) {
              usmiles.add(value);
            } else {
              smiles.add(value);
            }
            break;
          case Name:
            int degree = other.getDegree();
            if (!names.containsKey(degree)) {
              names.put(degree, new HashSet<String>());
            }
            names.get(degree).add(value);
            break;
          default:
//            System.out.println(r.getType().name() + " " + other.getProperty("key") + " " + other.getDegree());
            break;
        }
        
      }
    }
    
    for (String s : usmiles) {
      if (result.usmiles == null) {
        result.usmiles = s;
      } else {
        logger.warn("UniversalSMILES overlap: {} -> {}", result.usmiles, s);
      }
    }
    if (DataUtils.empty(result.usmiles) && !smiles.isEmpty()) {
      result.usmiles = smiles.iterator().next();
    }
    
    if (!names.isEmpty()) {
      int high = names.keySet().iterator().next();
      Set<String> n = names.remove(high);
      Iterator<String> it = n.iterator();
      result.name = it.next();
      
      //add remaining as synonyms
      while (it.hasNext()) {
        result.names.add(it.next());
      }
      
      for (int k : names.keySet()) {
        result.names.addAll(names.get(k));
      }
      
    }
    
    return result;
  }
  
  
  public static class MetaboliteData {
    public String id;
    public String formula;
    public String name;
    public Set<String> synonyms = new TreeSet<>();
    public String alias;
    public Set<ExternalReference> references = new HashSet<> ();
    public String inchi;
    public String inchikey;
    public String smiles;
    public Map<String, Map<String, Object>> extra = new HashMap<> ();
    public String mol;
    public Set<String> reactions;
  }
  
  public static class SimpleStoichiometry<T, V> {
    public Map<T, V> l = new HashMap<>();
    public Map<T, V> r = new HashMap<>();
    
    @Override
    public String toString() {
      return l.toString() + " " + r.toString();
    }
  }
  
  public static class ReactionData {
    public String id;
    public String equation;
    public SimpleStoichiometry<String, String> stoichiometry;
    public String name;
    public String ecn;
    public Set<String> otherNames = new TreeSet<>();
    public String alias;
    public Set<ExternalReference> references = new HashSet<> ();
    public Double gibbs;
    public String metacycDirection;
    public Map<String, Map<String, Object>> extra = new HashMap<> ();
  }
  
  public int idCounter = 0;
  
  public String generateId(Set<Long> cpdIds, String prefix) {
    return String.format("%s%d", prefix, idCounter++);
  }
  

  public Map<String, AssemblePlugin> cpdAssemblePlugins = new HashMap<>();
  public Map<String, AssemblePlugin> rxnAssemblePlugins = new HashMap<>();
  
  public String decideMol(Map<MetaboliteMajorLabel, Set<String>> molStructures) {
    int total = 0;
    for (Object o : molStructures.keySet()) {
      total += molStructures.get(o).size();
    }
    if (total == 0) {
      return null;
    } else if (total == 1) {
      return molStructures.values().iterator().next().iterator().next();
    } else {
      if (molStructures.containsKey(MetaboliteMajorLabel.LipidMAPS)) {
        Set<String> mols = molStructures.get(MetaboliteMajorLabel.LipidMAPS);
        return mols.iterator().next();
      } else if (molStructures.containsKey(MetaboliteMajorLabel.LigandCompound)) {
        Set<String> mols = molStructures.get(MetaboliteMajorLabel.LigandCompound);
        return mols.iterator().next();
      } else {
        //return any
        return molStructures.values().iterator().next().iterator().next();
      }
    }
  }
  
  public String getMolStructure(Set<BiodbMetaboliteNode> ids) {
    logger.info("picutres ! {}", ids);
    Map<MetaboliteMajorLabel, Set<String>> molStructures = new HashMap<>();
    for (BiodbMetaboliteNode cpdNode : ids) {
      BiodbPropertyNode p = cpdNode.getMetaboliteProperty(MetabolitePropertyLabel.MDLMolFile);
      if (p != null) {
        MetaboliteMajorLabel database = cpdNode.getDatabase();
        if (!molStructures.containsKey(database)) {
          molStructures.put(database, new HashSet<String>());
        }
        molStructures.get(database).add(p.getValue());
      }
    }
    String mol = decideMol(molStructures);
    return mol;
  }
  
  public Set<String> getMetaboliteReactions(Set<Long> ids) {
    Set<String> result = new HashSet<>();
    for (String rxn : rxnToStoichCpds.keySet()) {
      if (!Sets.intersection(rxnToStoichCpds.get(rxn), ids).isEmpty()) {
        result.add(rxn);
      }
    }
    return result;
  }
  
  public MetaboliteData make(Set<Long> ids) {
    logger.info("Assemble: {}", ids);
    
    Set<ExternalReference> erefs = new HashSet<>();
    for (Long id : ids) {
      BiodbMetaboliteNode cpdNode = databaseService.getMetabolite(id);
      if (cpdNode != null && !cpdNode.isProxy()) {
        erefs.add(new ExternalReference(cpdNode.getEntry(), cpdNode.getDatabase().toString()));
      } else {
        logger.warn("ignored: {}", id);
      }
    }
    
    MetaboliteData cpdData = this.assemble2(ids);
//    Tuple2<Double> mm = new Tuple2<Double>(0.0, 0.0);
//    Hmm record = toSomething(icpd, mm);
    String id = sgeneratedIdentifier.get(ids);
    String alias = cpdAliasGenerator.apply(ids); //generateAlias(ids);
    cpdData.id = id;
    cpdData.alias = alias;
    cpdData.reactions = getMetaboliteReactions(ids);
    
    for (String plugin : cpdAssemblePlugins.keySet()) {
      Map<String, Object> extData = cpdAssemblePlugins.get(plugin).assemble(erefs);
      cpdData.extra.put(plugin, extData);
    }
    
    //get bigg
    
    
    return cpdData;
  }
  
  public Map<String, Double> integrateStoich(Map<Set<Long>, Double> istoich) {
    Map<String, Double> result = new HashMap<>();
    for (Set<Long> k : istoich.keySet()) {
      Set<String> gid = new HashSet<>();
      for (long ks : k) {
        if (igeneratedIdentifier.containsKey(ks)) {
          gid.add(igeneratedIdentifier.get(ks));
        }
      }
      if (!gid.isEmpty()) {
        result.put(gid.iterator().next(), istoich.get(k));
      } else {
        result.put(k.iterator().next().toString(), istoich.get(k));
      }
    }
    return result;
  }
  
  public static<T, V> String buildEqBlock(Map<T, V> map) {
    List<String> lstr = new ArrayList<> ();
    for (T k : map.keySet()) {
      V v = map.get(k);
      String str = "";
      if (DataUtils.empty(v)) {
        str = k.toString();
      } else {
        str += v + " " + k;
      }
      lstr.add(str);
    }
    
    return Joiner.on(" + ").join(lstr);
  }
  
  public String buildEquation(Map<String, Double> stoich) {
    Map<String, String> l = new HashMap<>();
    Map<String, String> r = new HashMap<>();
    for (String compound : stoich.keySet()) {
      Double v = stoich.get(compound);
      if (v < 0.0) {
        String value = "";
        if (v != -1.0) {
          value = Double.toString(Math.abs(v));
        }
        if (mdata.containsKey(compound) && 
            !DataUtils.empty(mdata.get(compound).alias)) {
          compound = mdata.get(compound).alias;
        }
        l.put(compound, value);
      } else if (v > 0.0) {
        String value = "";
        if (v != 1.0) {
          value = Double.toString(Math.abs(v));
        }
        if (mdata.containsKey(compound) && 
            !DataUtils.empty(mdata.get(compound).alias)) {
          compound = mdata.get(compound).alias;
        }
        r.put(compound, value);
      } else {
        logger.warn("invalid value: {}", v);
      }
    }
    List<String> lstr = new ArrayList<> ();
    for (String k : l.keySet()) {
      String v = l.get(k);
      if (DataUtils.empty(v)) {
        v = k;
      } else {
        v += " " + k;
      }
      lstr.add(v);
    }
    for (String k : l.keySet()) {
      String v = l.get(k);
      if (DataUtils.empty(v)) {
        v = k;
      } else {
        v += " " + k;
      }
      lstr.add(v);
    }
    
//    System.out.println(l);
//    System.out.println(r);
    return buildEqBlock(l) + " <=> " + buildEqBlock(r);
  }
  
  public SimpleStoichiometry<String, String> buildEquation2(Map<String, Double> stoich) {
    Map<String, String> l = new HashMap<>();
    Map<String, String> r = new HashMap<>();
    SimpleStoichiometry<String, String> result = new SimpleStoichiometry<>();
    for (String compound : stoich.keySet()) {
      Double v = stoich.get(compound);
      if (v < 0.0) {
        String value = "";
        if (v != -1.0) {
          value = Double.toString(Math.abs(v));
        }
//        if (mdata.containsKey(compound) && 
//            !DataUtils.empty(mdata.get(compound).alias)) {
//          compound = mdata.get(compound).alias;
//        }
        l.put(compound, value);
      } else if (v > 0.0) {
        String value = "";
        if (v != 1.0) {
          value = v.toString();
        }
//        if (mdata.containsKey(compound) && 
//            !DataUtils.empty(mdata.get(compound).alias)) {
//          compound = mdata.get(compound).alias;
//        }
        r.put(compound, value);
      } else {
        logger.warn("invalid value: {}", v);
      }
    }
    result.l.putAll(l);
    result.r.putAll(r);
    return result;
  }
  
  public ReactionData wutuwBasic(Set<BiodbReactionNode> rxnNodes, 
                                 ConnectedComponents<Long> cpdIntegration) {
    ReactionData result = new ReactionData();
    
    Set<ExternalReference> erefs = new HashSet<>();
    Set<String> names = new HashSet<>();
    Set<Long> ids = new HashSet<>();
    

    
    Map<String, Double> fstoich = null;
    Set<String> ecns = new HashSet<>();
    for (BiodbReactionNode rxnNode : rxnNodes) {
      if (rxnNode != null && !rxnNode.isProxy()) {
        ids.add(rxnNode.getId());
        erefs.add(new ExternalReference(rxnNode.getEntry(), rxnNode.getDatabase().toString()));
        Map<Long, Double> stoich = rxnNode.getStoichiometry();
        String name = (String) rxnNode.getProperty("name", null);
        if(!DataUtils.empty(name)) {
          names.add(name);
        }
        System.out.println(rxnNode.getAllProperties());
        System.out.println(stoich);
        Map<Set<Long>, Double> istoich = integratedStoich(stoich, cpdIntegration);
        fstoich = integrateStoich(istoich);
        logger.info("istoich: {}", istoich);
        logger.info("fstoich: {}", fstoich);
        for (Relationship r : rxnNode.getRelationships()) {
          Node other = r.getOtherNode(rxnNode);
          if (other.hasLabel(GlobalLabel.EnzymeCommission)) {
            ecns.add((String) other.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
          }
//          else if (other.hasLabel(GlobalLabel.ReactionProperty)){
//            logger.info("rprop: {}", Neo4jUtils.getLabelsAsString(other));
//          }
        }
      } else {
        logger.warn("ignored: {} {}", rxnNode, rxnNode != null ? rxnNode.getEntry() : "");
      }
    }

    logger.info("references: {}", erefs);
    if (erefs.isEmpty()) {
      return null;
    }
    String id = sgeneratedIdentifier.get(ids);
    result.id = id;
    result.alias = rxnAliasGenerator.apply(ids); //generateReactionAlias(ids);
    result.equation = buildEquation(fstoich);
    result.stoichiometry = buildEquation2(fstoich);
    result.references.addAll(erefs);
    result.ecn = Joiner.on("; ").join(ecns);
    logger.info("equation: {}", result.equation);
    logger.info("stoichiometry: {}", result.stoichiometry);
    logger.info("references: {}", result.references);
    
    for (String plugin : rxnAssemblePlugins.keySet()) {
      Map<String, Object> extData = rxnAssemblePlugins.get(plugin).assemble(erefs);
      result.extra.put(plugin, extData);
    }
    
    return result;
  }
  
  public void assembleRxn(ConnectedComponents<Long> ccRxnIds, ConnectedComponents<Long> ccCpdIds) {
    idCounter = 0;
    DatabaseTable table = new DatabaseTable();
    for (Set<Long> ids : ccRxnIds) {
      logger.info("Assemble: {}", ids);
      Set<BiodbReactionNode> rxnNodes = new HashSet<>();
      Set<ExternalReference> erefs = new HashSet<>();
      boolean basic = true;
      for (Long id : ids) {
        BiodbReactionNode rxnNode = databaseService.getReaction(id);
        
        if (rxnNode != null && !rxnNode.isProxy()) {
          rxnNodes.add(rxnNode);
          basic = basic && rxnNode.isBasic();
          erefs.add(new ExternalReference(rxnNode.getEntry(), rxnNode.getDatabase().toString()));
        } else {
          logger.warn("ignored: {}", id);
        }
      }
      
      if (basic) {
        ReactionData rxnData = wutuwBasic(rxnNodes, ccCpdIds);
        if (rxnData != null) {
          table.add(rxnData);
          try {
            IOUtils.writeToFile(
                DataUtils.toJson(rxnData, false), 
                outputPath + "/data/rxn/" + rxnData.id + ".json", true);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      
      
      System.out.println(erefs);
//      Hmm record = make(ids);
//      table.add(record);
//      try {
//        IOUtils.writeToFile(
//            TheBestMetaboliteReporterEver.toJson(record, false), 
//            outputPath + "/data/rxn/" + record.id + ".json", true);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
    }
    
    String json = DataUtils.toJson(table, false);
    try {
      IOUtils.writeToFile(json, outputPath + "/reactionTable.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void assembleCpd(ConnectedComponents<Long> ccs) {
    DatabaseTable table = new DatabaseTable();
    
    for (Set<Long> ids : ccs) {
      MetaboliteData record = make(ids);
      table.add(record);
      mdata.put(record.id, record);
      try {
        IOUtils.writeToFile(
            DataUtils.toJson(record, false), 
            outputPath + "/data/cpd/" + record.id + ".json", 
            true);
        
        if (record.mol != null) {
          File svgFile = new File(outputPath + "/data/structure/" + record.id + ".svg");
          OutputStream os = null;
          try {
            os = new FileOutputStream(svgFile);
            CdkSVGRenderer.generateSvg(new ByteArrayInputStream(record.mol.getBytes()), os);
            os.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    String json = DataUtils.toJson(table, false);
    try {
      IOUtils.writeToFile(json, outputPath + "/cpds.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public MetaboliteData assemble2(Set<Long> ids) {
    MetaboliteData cpdData = new MetaboliteData();
//    IntegratedMetaboliteEntity icpd = new IntegratedMetaboliteEntity();
    
    Set<BiodbMetaboliteNode> cpdNodes = new HashSet<>();
    Set<ExternalReference> erefs = new HashSet<>();
    for (Long id : ids) {
      BiodbMetaboliteNode cpdNode = databaseService.getMetabolite(id);
      
      if (cpdNode != null && !cpdNode.isProxy()) {
//        icpd.setEntry(cpdNode.getEntry());
        cpdNodes.add(cpdNode);
        erefs.add(new ExternalReference(cpdNode.getEntry(), cpdNode.getDatabase().toString()));
      } else {
        logger.warn("ignored: {}", id);
      }
    }

    cpdData.mol = getMolStructure(cpdNodes);
    
    Long inchiId = assembleInchi(cpdNodes);
    if (inchiId != null) {
      ChemicalStructure cs = expandInchi(inchiId);
      cpdData.formula = cs.formula;
      cpdData.name = cs.name;

      if (cs.names != null && !cs.names.isEmpty()) {
        cpdData.synonyms.addAll(cs.names);
      }
      if (cs.inchi != null && !cs.inchi.isEmpty()) {
        cpdData.inchi = cs.inchi;
      }
      if (cs.inchiKey != null) {
        cpdData.inchikey = cs.inchiKey.toString();
      }
      if (cs.usmiles != null && !cs.usmiles.isEmpty()) {
        cpdData.smiles = cs.usmiles;
      }
      
    } else {
      
    }
    
    for (ExternalReference eref : erefs) {
      cpdData.references.add(eref);
    }
    
    
    return cpdData;
  }
  
  public void assembleDatabase(ConnectedComponents<Long> ccsCpd) {
    //pre processing
    
    //processing
    for (Set<Long> ids : ccsCpd) {
      MetaboliteData record = make(ids);
      mdata.put(record.id, record);
    }
//    this.assembleCpd(null);
    this.assembleRxn(null, null);
    
    //post processing
    
//    try {
//      IOUtils.writeToFile(
//          DataUtils.toJson(record, false), 
//          outputPath + "/data/cpd/" + record.id + ".json", true);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }
  
  public IntegratedMetaboliteEntity assemble(Set<ExternalReference> references) {
    IntegratedMetaboliteEntity icpd = new IntegratedMetaboliteEntity();
    
    Set<BiodbMetaboliteNode> cpdNodes = new HashSet<>();
    Set<ExternalReference> erefs = new HashSet<>();
    for (ExternalReference reference :references) {
      BiodbMetaboliteNode cpdNode = databaseService.getMetabolite(reference);
      
      if (cpdNode != null && !cpdNode.isProxy()) {
        cpdNodes.add(cpdNode);
        erefs.add(reference);
      } else {
        logger.warn("ignored: {}", reference);
      }
    }
    
    Long inchiId = assembleInchi(cpdNodes);
    if (inchiId != null) {
      ChemicalStructure cs = expandInchi(inchiId);
      if (!DataUtils.empty(cs.formula)) {
        icpd.setFormula(cs.formula);
      }
      if (!DataUtils.empty(cs.name)) {
        icpd.setName(cs.name);
      }
      if (!cs.names.isEmpty()) {
        Map<Long, List<String>> synonyms = new HashMap<>();
        synonyms.put(-1L, new ArrayList<>(cs.names));
        icpd.setNames(synonyms);
      }
      if (cs.inchi != null && !cs.inchi.isEmpty()) {
        Map<Long, String> inchis = new HashMap<>();
        inchis.put(-1L, cs.inchi);
        icpd.setInchis(inchis);
      }
      if (cs.usmiles != null && !cs.usmiles.isEmpty()) {
        Map<Long, String> inchis = new HashMap<>();
        inchis.put(-1L, cs.usmiles);
        icpd.setSmiles(inchis);
      }
      
    } else {
      
    }
    
    for (ExternalReference eref : erefs) {
      IntegratedMetaboliteCrossreferenceEntity ref = new IntegratedMetaboliteCrossreferenceEntity();
      ref.setRef(eref.source);
      ref.setValue(eref.entry);
      icpd.getCrossreferences().add(ref);
    }
    
    
    return icpd;
  }
  
  public static MetaboliteData toSomething(IntegratedMetaboliteEntity cpd, Tuple2<Double> mm) {
    MetaboliteData result = new MetaboliteData();
    String entry = cpd.getEntry();
    String formula = cpd.getFormula();
    String name = cpd.getName();
    Set<String> synonyms = new HashSet<>();
    Set<ExternalReference> externalReferences = new HashSet<>();
    if (!cpd.getNames().isEmpty()) {
      synonyms.addAll(cpd.getNames().get(-1L));
    }
    for (IntegratedMetaboliteCrossreferenceEntity e : cpd.getCrossreferences()) {
      externalReferences.add(new ExternalReference(e.getValue(), e.getRef()));
    }
    
    result.id = entry;
    result.formula = formula;
    result.name = name;
    result.synonyms.addAll(synonyms);
    result.references.addAll(externalReferences);
//    result.inchi = cpd.getInchis()
//    String result = entry + "\t" + formula + "\t" + name + "\t" + synonyms + 
//        "\t" + externalReferences + "\t" + mm.e1 + "\t" + mm.e2;
    return result;
  }
  
  public static String toString(IntegratedMetaboliteEntity cpd, Tuple2<Double> mm) {
    String entry = cpd.getEntry();
    String formula = cpd.getFormula();
    String name = cpd.getName();
    Set<String> synonyms = new HashSet<>();
    Set<ExternalReference> externalReferences = new HashSet<>();
    if (!cpd.getNames().isEmpty()) {
      synonyms.addAll(cpd.getNames().get(-1L));
    }
    for (IntegratedMetaboliteCrossreferenceEntity e : cpd.getCrossreferences()) {
      externalReferences.add(new ExternalReference(e.getValue(), e.getRef()));
    }
    
    String result = entry + "\t" + formula + "\t" + name + "\t" + synonyms + 
        "\t" + externalReferences + "\t" + mm.e1 + "\t" + mm.e2;
    return result;
  }
  
  public static void main(String[] args) {
    BiodbGraphDatabaseService databaseService = null;
    
    Transaction dataTx = databaseService.beginTx();
    
    DatabaseAssembler facade = new DatabaseAssembler.Builder().withMetaboliteSets("/var/biobase/integration/cc/cpd/test_curated.tsv")
                                                                        .withReactionSets("/var/biobase/integration/cc/rxn/test_curated.tsv")
                                                                        .withMetaboliteCurationSets("")
                                                                        .withNeo4jGraphDatabaseService(databaseService)
                                                                        .build();
                                                                        
    facade.assemble("D:\\tmp\\biodb\\idatabase");
    
    dataTx.failure();
    dataTx.close();
    databaseService.shutdown();
  }
  
  public static void main2(String[] args) {
//    AssembleDatabaseFacade facade = new AssembleDatabaseFacade.Builder().withMetaboliteSets("/var/biobase/integration/cc/cpd/test_curated.tsv")
//                                                                        .withReactionSets("/var/biobase/integration/cc/rxn/test_curated.tsv")
//                                                                        .withMetaboliteCurationSets(Data.CURATION_PATH)
//                                                                        .build();
//    ConnectedComponents<String> curation = ConnectedComponentsUtils.loadConnectedComponents(Data.CURATION_PATH);
////    ConnectedComponents<String> sets = FileImport.loadConnectedComponents(
//////        "/var/biobase/integration/cc/integration_kmb_cura_t9.tsv"
////        "/var/biobase/integration/cc/cpd/test_with_hmdb.tsv"
////        );
//    ConnectedComponents<String> cpdSets = ConnectedComponentsUtils.loadConnectedComponents(
//        "/var/biobase/integration/cc/cpd/test_curated.tsv");
//    ConnectedComponents<String> rxnSets = ConnectedComponentsUtils.loadConnectedComponents(
//        "/var/biobase/integration/cc/rxn/test_curated.tsv");
////    ConnectedComponents<String> cpdSets = FileImport.loadConnectedComponents(
////        "/var/biobase/integration/cc/integration_kmb_t9_singles.tsv");
////    ConnectedComponents<String> rxnSets = FileImport.loadConnectedComponents(
////        "/var/biobase/integration/cc/rxn/integration_kmb_cura_t9_singles.tsv");
//    
//    GraphVizGenerator.GRAPH_VIZ_BIN_PATH = "/opt/graphviz/2.38/bin/";
//    BiodbGraphDatabaseService databaseService = new BiodbGraphDatabaseService(
//        FliuThesisInstanses.getDataDatabase());
//    
//    Transaction dataTx = databaseService.beginTx();
//    
//    ConnectedComponents<Long> ccCpdIds = Helper.toCpdIds(cpdSets, databaseService);
//    ConnectedComponents<Long> cuCpdIds = Helper.toCpdIds(curation, databaseService);
//    ConnectedComponents<Long> ccRxnIds = Helper.toRxnIds(rxnSets, databaseService);
//    
//
////    Set<ExternalReference> refs = new HashSet<>();
////    refs.add(new ExternalReference("pyr", "BiGG"));
////    refs.add(new ExternalReference("pyr", "BiGGMetabolite"));
////    refs.add(new ExternalReference("C00022", "LigandCompound"));
////    refs.add(new ExternalReference("META:PYRUVATE", "MetaCyc"));
//    
//
//    ConnectedComponents<Long> curationCc = Helper.toCpdIds(curation, databaseService);
//    
//    TheBestIntegrationMethod method = FliuThesisInstanses.getMetaboliteIntegrationMethod(databaseService);
//    CurationFunction C = new CurationFunction(curationCc);
//    C.alpha = 100;
//    C.beta = -10;
//    method.C = C;
//    
//    ConnectedComponentsMinMax minMax = new ConnectedComponentsMinMax(method);
//    TheBestMetaboliteReporterEver reporter = new TheBestMetaboliteReporterEver(databaseService, minMax);
//    
////    
////    Map<Tuple2<Long>, Double> scores = minMax.allScoresMap.get(ids);
////    System.out.println(mm);
////    System.out.println(scores);
////    reporter.makeHtml(scores, "/tmp/trash/wut.html");
//    StringBuilder sbBuilder = new StringBuilder();
//    MetaboliteMajorLabel[] databases = new MetaboliteMajorLabel[] {
//        MetaboliteMajorLabel.BiGGMetabolite,
//        MetaboliteMajorLabel.BiGG,
//        MetaboliteMajorLabel.LigandCompound,
//        MetaboliteMajorLabel.LigandGlycan,
//        MetaboliteMajorLabel.LigandDrug,
//        MetaboliteMajorLabel.ModelSeed,
//        MetaboliteMajorLabel.MetaCyc,
//        MetaboliteMajorLabel.LipidMAPS,
//        MetaboliteMajorLabel.HMDB
//    };
////    NodeAttributeReporter attributeReporter = new NodeAttributeReporter(databases, databaseService);
////    attributeReporter.generateReport();
//    
////    ConnectedComponents<Long> ccIds = Helper.toCpdIds(sets, databaseService);
//    
//    DefaultAssembler assembler = new DefaultAssembler(databaseService, ccCpdIds, ccRxnIds);
//    assembler.cpdAliasGenerator = new AliasGenerator(databaseService, MetaboliteMajorLabel.BiGGMetabolite);
//    assembler.rxnAliasGenerator = new AliasGenerator(databaseService, ReactionMajorLabel.BiGGReaction);
//    assembler.cpdAssemblePlugins.put("hmdb", new HmdbAssemblePlugin(databaseService));
//    assembler.cpdAssemblePlugins.put("lm", new LipidmapsAssemblePlugin(databaseService));
//    assembler.cpdAssemblePlugins.put("bigg", new BiggAssemblePlugin(databaseService));
//    assembler.cpdAssemblePlugins.put("ms", new ModelseedAssemblePlugin(databaseService));
//    assembler.cpdAssemblePlugins.put("metacyc", new BiocycAssemblePlugin(databaseService, MetaboliteMajorLabel.MetaCyc.toString()));
//    assembler.rxnAssemblePlugins.put("metacyc", new BiocycReactionAssemblePlugin(databaseService, ReactionMajorLabel.MetaCyc.toString()));
//    assembler.rxnAssemblePlugins.put("bigg", new BiggReactionAssemblePlugin(databaseService));
//    assembler.rxnAssemblePlugins.put("kegg", new KeggReactionAssemblePlugin(databaseService));
//    assembler.generateIntegratedIds();
//    assembler.assembleCpd(ccCpdIds);
//    assembler.assembleRxn(ccRxnIds, ccCpdIds);
//    assembler.assembleDatabase(ccCpdIds);
    
    Set<String> k = new HashSet<>();
    Set<String> rel = new HashSet<>();
    Set<ExternalReference> dbrefs = new HashSet<>();
//    for (BiodbMetaboliteNode n : databaseService.listMetabolites(MetaboliteMajorLabel.ModelSeed)) {
//      ExternalReference er = new ExternalReference(n.getEntry(), n.getDatabase().toString());
//      dbrefs.add(er);
//      k.addAll(n.getAllProperties().keySet());
//      for (Relationship r : n.getRelationships()) {
//        rel.add(r.getType().name());
//      }
//    }
//    AssemblePlugin plugin = new ModelseedAssemblePlugin(databaseService);
//    Map<String, Object> data = plugin.assemble(dbrefs);
//    System.out.println(data);
//    System.out.println(k);
//    System.out.println(rel);
    
//    for (BiodbReactionNode n : databaseService.listReactions(ReactionMajorLabel.BiGGReaction)) {
//      
//      Set<String> ss = new HashSet<>();
//      for (Relationship r : n.getRelationships()) {
//        ss.add(r.getType().name());
//      }
//      
//      System.out.println(n.getAllProperties());
//      System.out.println(ss);
//    }
    

    
//    for (Set<String> set : sets) {
//      Set<ExternalReference> references = 
//          set.stream()
//             .map(i -> new ExternalReference(i))
//             .collect(Collectors.<ExternalReference> toSet());
//      Set<Long> ids = Helper.toIds(references, databaseService);
//      Tuple2<Double> mm = minMax.getMinMax(ids);
////      System.out.println(references);
//      
//      IntegratedMetaboliteEntity icpd = assembler.assemble(references);
//      
//      String string = toString(icpd, mm);
//      sbBuilder.append(string).append("\n");
//    }
//    
//    System.out.println(sbBuilder);

    
//    dataTx.failure(); dataTx.close();
//    
//    databaseService.shutdown();
  }


}
