package pt.uminho.sysbio.biosynthframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class StandardizationTools {
  private static final Logger logger = LoggerFactory.getLogger(StandardizationTools.class);
  
  public static String toStdCmp(String d, SubcellularCompartment cmp) {
    switch (cmp) {
      case BOUNDARY: return "b";
      case EXTRACELLULAR: return "e";
      case CYTOSOL: return "c";
      case PERIPLASM: return "p";
      
      case MITOCHONDRIA: return "m";
      case NUCLEUS: return "n";
      case VACUOLE: return "v";
      case GOLGI: return "g";
      case RETICULUM: return "r";
      
      case LIPID: return "lp";
      case MITOCHONDRIA_MEMBRANE: return "mm";
      case VACUOLAR_MEMBRANE: return "vm";
      case RETICULUM_MEMBRANE: return "rm";
      case GOLGI_MEMBRANE: return "gm";
      
      case PEROXISOME: return "x";
      case CARBOXYSOME: return "ca";
      case CELL_ENVELOPE: return "ce";
      
      case THYLAKOID_LUMEN: return "tk";
      
      default:
        logger.warn("unable to convert {} keep {}", cmp, d);
        break;
    }
    
    return d;
  }
  
  public static SubcellularCompartment compartmentSymbolToSubcellularCompartment(String name) {
    switch (name) {
      case "c": return SubcellularCompartment.CYTOSOL;
      case "p": return SubcellularCompartment.PERIPLASM;
      case "b": return SubcellularCompartment.BOUNDARY;
      case "x": return SubcellularCompartment.PEROXISOME;
      case "ca": return SubcellularCompartment.CARBOXYSOME;
      case "e": return SubcellularCompartment.EXTRACELLULAR;
      case "tk": return SubcellularCompartment.THYLAKOID_LUMEN;
//      case "Cell Envelope": return SubcellularCompartment.CELL_ENVELOPE;
//      case "Lipid Particle": return SubcellularCompartment.LIPID;
  
      case "g": return SubcellularCompartment.GOLGI;
      case "m": return SubcellularCompartment.MITOCHONDRIA;
      case "n": return SubcellularCompartment.NUCLEUS;
      case "r": return SubcellularCompartment.RETICULUM;
  
      case "v": return SubcellularCompartment.VACUOLE;
      case "gm": return SubcellularCompartment.GOLGI_MEMBRANE;
      case "vm": return SubcellularCompartment.VACUOLAR_MEMBRANE;
      case "mm": return SubcellularCompartment.MITOCHONDRIA_MEMBRANE;
      case "rm": return SubcellularCompartment.RETICULUM_MEMBRANE;
  
      default: 
        logger.warn("unable to convert {}", name);
        return SubcellularCompartment.UNKNOWN;
    }
  }
  
  public static String toStdCmpName(String d, SubcellularCompartment cmp) {
    switch (cmp) {
      case CYTOSOL: return "Cytosol";
      case PERIPLASM: return "Periplasm";
      case BOUNDARY: return "Boundary";
      case PEROXISOME: return "Peroxisome";
      case CARBOXYSOME: return "Carboxysome";
      case EXTRACELLULAR: return "Extra Cellular";
      case THYLAKOID_LUMEN: return "Thylakoid Lumen";

      case CELL_ENVELOPE: return "Cell Envelope";
      
      case LIPID: return "Lipid Particle";
      case GOLGI: return "Golgi";
      case MITOCHONDRIA: return "Mitochondrion";
      
      case NUCLEUS: return "Nucleus";
      case RETICULUM: return "Endoplasmic Reticulum";
      case VACUOLE: return "Vacuole";      
      
      case GOLGI_MEMBRANE: return "Golgi Membrane";
      case VACUOLAR_MEMBRANE: return "Vacuolar Membrane";
      case MITOCHONDRIA_MEMBRANE: return "Mitochondrial Membrane";
      case RETICULUM_MEMBRANE: return "Endoplasmic Reticulum Membrane";
      
      default: break;
    }
    
    return d;
  }
  
  public static SubcellularCompartment compartmentNameToSubcellularCompartment(String name) {
    switch (name) {
      case "Cytosol": return SubcellularCompartment.CYTOSOL;
      case "Periplasm": return SubcellularCompartment.PERIPLASM;
      case "Boundary": return SubcellularCompartment.BOUNDARY;
      case "Peroxisome": return SubcellularCompartment.PEROXISOME;
      case "Carboxysome": return SubcellularCompartment.CARBOXYSOME;
      case "Extra Cellular": return SubcellularCompartment.EXTRACELLULAR;
      case "Thylakoid Lumen": return SubcellularCompartment.THYLAKOID_LUMEN;
      case "Cell Envelope": return SubcellularCompartment.CELL_ENVELOPE;
      case "Lipid Particle": return SubcellularCompartment.LIPID;
  
      case "Golgi": return SubcellularCompartment.GOLGI;
      case "Mitochondrion": return SubcellularCompartment.MITOCHONDRIA;
      case "Nucleus": return SubcellularCompartment.NUCLEUS;
      case "Endoplasmic Reticulum": return SubcellularCompartment.RETICULUM;
  
      case "Vacuole": return SubcellularCompartment.VACUOLE;
      case "Golgi Membrane": return SubcellularCompartment.GOLGI_MEMBRANE;
      case "Vacuolar Membrane": return SubcellularCompartment.VACUOLAR_MEMBRANE;
      case "Mitochondrial Membrane": return SubcellularCompartment.MITOCHONDRIA_MEMBRANE;
      case "Endoplasmic Reticulum Membrane": return SubcellularCompartment.RETICULUM_MEMBRANE;
  
      default: 
        logger.warn("unable to convert {}", name);
        return SubcellularCompartment.UNKNOWN;
    }
  }
  
  public static String toStdDbName(String d, MetaboliteMajorLabel cmp) {
    switch (cmp) {
      case LigandCompound: return "KEGG";
      case MetaCyc: return "MetaCyc";
      case ModelSeed: return "ModelSeed";
      case BiGG2: return "BiGG";
      case LipidMAPS: return "LipidMAPS";
      default: return null;
    }
  }
  
  public static String toStdDbName(ReactionMajorLabel cmp) {
    switch (cmp) {
      case LigandReaction: return "KEGG";
      case MetaCyc: return "MetaCyc";
      case ModelSeedReaction: return "ModelSeed";
      case BiGG: return "BiGG";
      default: return null;
    }
  }
}
