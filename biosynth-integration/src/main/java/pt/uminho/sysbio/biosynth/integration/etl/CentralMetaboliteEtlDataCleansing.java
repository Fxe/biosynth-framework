package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.util.FormulaReader;

public class CentralMetaboliteEtlDataCleansing
implements EtlDataCleansing<GraphMetaboliteEntity> {

  private static final Logger logger = LoggerFactory.getLogger(CentralMetaboliteEtlDataCleansing.class);

  private static final String DCS_KEY_STATUS = "DCS-status";
  private static final String DCS_KEY_ORIGINAL = "DCS-original";

  private final FormulaReader formulaConverter;

  public CentralMetaboliteEtlDataCleansing(FormulaReader formulaConverter) {
    this.formulaConverter = formulaConverter;
  }

  //	public FormulaConverter getFormulaConverter() { return formulaConverter;}
  //	public void setFormulaConverter(FormulaConverter formulaConverter) { this.formulaConverter = formulaConverter;}

  private Triple<String, String, EtlCleasingType> cleanseFormula(String formula) {

    String formula_ = formulaConverter.convertToIsotopeMolecularFormula(formula, false);


    EtlCleasingType action = EtlCleasingType.UNCHANGED;
    if (formula_ != null && !formula_.equals(formula)) {
      action = EtlCleasingType.CORRECTED;
    } else if (formula_ == null) {
      formula_ = formula;
      action = EtlCleasingType.CORRUPT;
    }

    logger.debug(String.format("%s %s -> %s", action, formula, formula_));

    return new ImmutableTriple<String, String, EtlCleasingType>(formula_, formula, action);
  }

  public Triple<String, String, EtlCleasingType> cleanseName(String nameOriginal) {
    String nameFix = nameOriginal.toLowerCase();
    
    Map<String, String> replaceMap = new HashMap<> ();
    replaceMap.put("&alpha;", "alpha");
    replaceMap.put("&beta;", "beta");
    
    for (String k : replaceMap.keySet()) {
      String v = replaceMap.get(k);
      while (nameFix.contains(k)) {
        nameFix = StringUtils.replaceChars(nameFix, k, v);
      }
    }

    
    EtlCleasingType action = EtlCleasingType.UNCHANGED;
    if (!nameFix.equals(nameOriginal)) {
      action = EtlCleasingType.CORRECTED;
    }

    logger.debug(String.format("%s %s -> %s", action, nameOriginal, nameFix));

    return new ImmutableTriple<String, String, EtlCleasingType>(nameFix, nameOriginal, action);
  }
  
  private Triple<String, String, EtlCleasingType> cleanseDatabase(String entry, String db) {
    String fix = entry;
    switch (db) {
      case "LigandCompound":
        if (fix.startsWith("c") || fix.startsWith("d") || fix.startsWith("g")) {
          fix = fix.toUpperCase();
        }
        break;
      case "MetaCyc":
        if (!fix.startsWith("META:")) {
          fix = "META:".concat(fix);
        }
        break;
      default: break;
    }
    
    EtlCleasingType action = EtlCleasingType.UNCHANGED;
    if (!fix.equals(entry)) {
      action = EtlCleasingType.CORRECTED;
    }
    
    logger.debug("{} {} -> {}", action, entry, fix);
    
    return new ImmutableTriple<String, String, EtlCleasingType>(fix, entry, action);
  }

  public Triple<String, String, EtlCleasingType> cleanseSmiles(String formula) {
    return null;
  }

  @Override
  public Map<String, Triple<String, String, EtlCleasingType>> etlCleanse(GraphMetaboliteEntity metabolite) {
    Map<String, Triple<String, String, EtlCleasingType>> result =
        new HashMap<> ();

    for (String relationshipType : metabolite.getConnectedEntities().keySet()) {
      List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> pairs = metabolite.getConnectedEntities().get(relationshipType);
      for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : pairs) {
        AbstractGraphNodeEntity propertyEntity = p.getRight();
        AbstractGraphEdgeEntity relationshipEntity = p.getLeft();
        Triple<String, String, EtlCleasingType> triple;
        //				
        if (propertyEntity.getMajorLabel() == null) {
          System.out.println(propertyEntity.getMajorLabel() + " " + propertyEntity.getLabels());
        }
        Object key = propertyEntity.getProperties().get(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
        switch (propertyEntity.getMajorLabel()) {
          case "MolecularFormula":
            triple = this.cleanseFormula((String) key);
            propertyEntity.getProperties().put(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, nullToString(triple.getLeft()));
            //						propertyEntity.setUniqueKey(nullToString(triple.getLeft()));
            relationshipEntity.getProperties().put(DCS_KEY_STATUS, triple.getRight().toString());
            if (EtlCleasingType.CORRECTED.equals(triple.getRight())) {
              relationshipEntity.getProperties().put(DCS_KEY_ORIGINAL, key.toString());
            }
            result.put("ChemicalFormula", triple);
            break;
          case "Name":
            triple = this.cleanseName((String)propertyEntity.getProperties().get("key"));
            propertyEntity.getProperties().put(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, nullToString(triple.getLeft()));
            relationshipEntity.getProperties().put(DCS_KEY_STATUS, triple.getRight().toString());
            if (EtlCleasingType.CORRECTED.equals(triple.getRight())) {
              relationshipEntity.getProperties().put(DCS_KEY_ORIGINAL, key.toString());
            }
            result.put("Name", triple);
            break;
          case "MetaCyc":
          case "LigandCompound":
            triple = this.cleanseDatabase((String)propertyEntity.getProperties().get("entry"), propertyEntity.getMajorLabel());
            propertyEntity.getProperties().put(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, nullToString(triple.getLeft()));
            relationshipEntity.getProperties().put(DCS_KEY_STATUS, triple.getRight().toString());
            if (EtlCleasingType.CORRECTED.equals(triple.getRight())) {
              relationshipEntity.getProperties().put(DCS_KEY_ORIGINAL, key.toString());
            }
            result.put("LigandCompound", triple);
            break;
        default:
          logger.debug("Ignored connection: " + propertyEntity.getMajorLabel());
          break;
        }
      }
      
//      for (String k : metabolite.getc)<
    }


    //		for (Pair<GraphPropertyEntity, GraphRelationshipEntity> pair : 
    //			metabolite.getPropertyEntities()) {
    //			
    //			GraphPropertyEntity propertyEntity = pair.getLeft();
    //			GraphRelationshipEntity relationshipEntity = pair.getRight();
    //			Triple<String, String, EtlCleasingType> triple;
    //			
    //			switch (propertyEntity.getMajorLabel()) {
    //				case "MolecularFormula":
    //					triple = this.cleanseFormula((String)propertyEntity.getProperties().get("key"));
    //					propertyEntity.setUniqueKey(nullToString(triple.getLeft()));
    //					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight().toString());
    //					result.put("ChemicalFormula", triple);
    //					break;
    //				case "Name":
    //					triple = this.cleanseName((String)propertyEntity.getProperties().get("key"));
    //					propertyEntity.setUniqueKey(nullToString(triple.getLeft()));
    //					relationshipEntity.getProperties().put(DCS_STATUS_KEY, triple.getRight().toString());
    //					result.put("Name", triple);
    //					break;
    //				default:
    //					break;
    //			}
    //		}

    return result;
  }



  private Object nullToString(Object object) {
    if (object == null) return "null";
    return object;
  }
}
