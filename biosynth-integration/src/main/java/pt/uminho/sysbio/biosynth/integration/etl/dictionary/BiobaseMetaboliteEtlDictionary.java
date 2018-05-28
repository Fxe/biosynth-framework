package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionEntity;

public class BiobaseMetaboliteEtlDictionary<M extends Metabolite> implements EtlDictionary<String, String, String> {

  private static final Logger logger = LoggerFactory.getLogger(BiobaseMetaboliteEtlDictionary.class);

  private final Class<M> clazz;
  private final Map<String, String> mapping = new HashMap<>();

  public BiobaseMetaboliteEtlDictionary(Class<M> clazz) {
    this.clazz = clazz;
  }
  
  public BiobaseMetaboliteEtlDictionary(Class<M> clazz, Map<String, String> mapping) {
    this.clazz = clazz;
    this.mapping.putAll(mapping);
  }

  @Override
  public String translate(String lookup, String entry) {
    if (mapping.containsKey(lookup)) {
      return mapping.get(lookup);
    }
    
    BioDbDictionary.setupDefaults();
    
    String result = BioDbDictionary.translate(clazz, lookup);
    
    if (result != null) {
      return result;
    }

//    if (lookup.toLowerCase().trim().equals("chebi")) {
//      if (reference.toLowerCase().startsWith("chebi:")) {
//        return StringUtils.removeStart(reference.toLowerCase(), "chebi:");
//      }
//    }

    if (lookup.toLowerCase().trim().equals("kegg")) {
      return translateKegg(lookup, entry);
    }

    if ((clazz.equals(KeggCompoundMetaboliteEntity.class) || 
         clazz.equals(KeggDrugMetaboliteEntity.class) || 
         clazz.equals(ChebiMetaboliteEntity.class)) && 
        lookup.equals("PubChem")) {
      result = MetaboliteMajorLabel.PubChemSubstance.toString();
    } else {
      result = BioDbDictionary.translateDatabase(lookup);
    }

    logger.debug(String.format("Translated %s -> %s using modifier %s", lookup, result, clazz));


    
    if (result.equals(GlobalLabel.KEGG.toString())) {
      result = resolveKeggDatabase(entry); 
      logger.debug(String.format("KEGG resolve %s -> %s", lookup, result, clazz));
    }
    
    return result;
  }

  /**
   * Determines KEGG database type from entry
   * @param entry a valid KEGG entry (C|D|G|R)XXXXX
   * @return LigandCompound|LigandDrug|LigandGlycan|LigandReaction|NOTFOUND
   */
  public String resolveKeggDatabase(String entry) {
    if (entry.length() == 6) {
      char initial = entry.charAt(0);
      switch (initial) {
        case 'C': return MetaboliteMajorLabel.LigandCompound.toString();
        case 'c': return MetaboliteMajorLabel.LigandCompound.toString();
        case 'D': return MetaboliteMajorLabel.LigandDrug.toString();
        case 'd': return MetaboliteMajorLabel.LigandDrug.toString();
        case 'G': return MetaboliteMajorLabel.LigandGlycan.toString();
        case 'g': return MetaboliteMajorLabel.LigandGlycan.toString();
        case 'E': return GlobalLabel.KeggEnviron.toString();
        case 'R': return ReactionMajorLabel.LigandReaction.toString();
        case 'r': return ReactionMajorLabel.LigandReaction.toString();
        default:
          logger.warn("Unknown KEGG initial - {}", entry);
          break;
        }
    } else {
      logger.warn("Invalid KEGG size - [{}] (length != 6)", entry);
    }

    return MetaboliteMajorLabel.NOTFOUND.toString();
  }

  public String translateKegg(String lookup, String entry) {
    String result = null;

    if (entry.length() == 6 && lookup.toLowerCase().equals("kegg")) {
      result = resolveKeggDatabase(entry);
    } else {
      if ((clazz.equals(KeggCompoundMetaboliteEntity.class) || 
          clazz.equals(KeggDrugMetaboliteEntity.class) || 
          clazz.equals(ChebiMetaboliteEntity.class)) && 
          lookup.equals("PubChem")) {
        result = MetaboliteMajorLabel.PubChemSubstance.toString();
      } else {
        result = BioDbDictionary.translateDatabase(lookup);
      }
    }


    
    if (result == null) {
      System.out.println("Unable to translate " + lookup + ":" + entry);
      result = MetaboliteMajorLabel.NOTFOUND.toString();
    }

    logger.debug(String.format("Translated %s -> %s using modifier %s", lookup, result, clazz));
    return result;
  }
}
