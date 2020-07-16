package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegrationUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.integration.model.SbmlObjectMetadata.SbmlObjectMetadataType;
import pt.uminho.sysbio.biosynthframework.sbml.SbmlNotesParser;
import pt.uminho.sysbio.biosynthframework.sbml.XmlObject;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class XmlReferencesBaseIntegrationEngine implements BaseIntegrationEngine {

  private static final Logger logger = LoggerFactory.getLogger(XmlReferencesBaseIntegrationEngine.class);
  
  public List<XmlSbmlSpecie> xmlSpecies = new ArrayList<> ();
  public final SbmlNotesParser notesParser;
  public Map<String, String> tokenMapper = new HashMap<> ();
  
  public XmlReferencesBaseIntegrationEngine(SbmlNotesParser notesParser) {
    this.notesParser = notesParser;
    setupDefaults();
  }
  
  public void setupDefaults() {
    tokenMapper.put("metanetx.chemical/", MetaboliteMajorLabel.MetaNetX.toString());
    tokenMapper.put("hmdb/", MetaboliteMajorLabel.HMDB.toString());
    tokenMapper.put("unipathway.compound/", MetaboliteMajorLabel.UniPathway.toString());
    tokenMapper.put("bigg.metabolite/", MetaboliteMajorLabel.BiGG2.toString());
    tokenMapper.put("lipidmaps/", MetaboliteMajorLabel.LipidMAPS.toString());
    tokenMapper.put("seed.compound/", MetaboliteMajorLabel.ModelSeed.toString());
    tokenMapper.put("reactome/", MetaboliteMajorLabel.Reactome.toString());
  }
  
  public static String startsWithAny(String str, Collection<String> strs) {
    for (String k : strs) {
      if (str.startsWith(k)) {
        return k;
      }
    }
    return null;
  }
  
  public Pair<String, String> extractRdfAnnotation(String resource) {
    String database = null;
    String entry = null;
    //    System.out.println(resource);
    if (resource.startsWith("urn:miriam:")) {
      resource = resource.replaceFirst("urn:miriam:", "");
      //      System.out.println(resource);
      if (resource.startsWith("NCBI:")) {
        database = "NCBIGene";
        String[] str_ = resource.split(":");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("UniProt:")) {
        database = GlobalLabel.UniProt.toString();
        String[] str_ = resource.split(":");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("kegg.pathway:")) {
        database = GlobalLabel.KeggPathway.toString();
        String[] str_ = resource.split(":");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("kegg.compound:")) {
        database = MetaboliteMajorLabel.LigandCompound.toString();
        String[] str_ = resource.split(":");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("kegg.reaction:")) {
        database = ReactionMajorLabel.LigandReaction.toString();
        String[] str_ = resource.split(":");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("ec-code:")) {
        database = GlobalLabel.EnzymeCommission.toString();
        String[] str_ = resource.split(":");
        if (str_.length == 2) {
          Set<String> ecs = new HashSet<> ();
          for (String ec : str_[1].split(",")) {
            ecs.add(ec.trim());
          }
          entry = StringUtils.join(ecs, ';');
        }
      }
    } else if (resource.startsWith("http://identifiers.org/")) {
      resource = resource.replaceFirst("http://identifiers.org/", "");
      
      String token = startsWithAny(resource, tokenMapper.keySet());
      if (token != null) {
        database = tokenMapper.get(token);
        entry = resource.replaceFirst(token, "");
      } else if (resource.startsWith("kegg.compound/")) {
        database = MetaboliteMajorLabel.LigandCompound.toString();
        String[] str_ = resource.split("/");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("kegg.drug/")) {
        database = MetaboliteMajorLabel.LigandDrug.toString();
        String[] str_ = resource.split("/");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("3dmet/")) {
        database = MetaboliteMajorLabel.MET3D.toString();
        String[] str_ = resource.split("/");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("pubchem.substance/")) {
        database = MetaboliteMajorLabel.PubChemSubstance.toString();
        String[] str_ = resource.split("/");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("cas/")) {
        database = MetaboliteMajorLabel.CAS.toString();
        String[] str_ = resource.split("/");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("obo.chebi/CHEBI%3A")) {
        database = MetaboliteMajorLabel.ChEBI.toString();
        entry = resource.replaceFirst("obo.chebi/CHEBI%3A", "");
      } else if (resource.startsWith("chebi/CHEBI:")) {
        database = MetaboliteMajorLabel.ChEBI.toString();
        entry = resource.replaceFirst("chebi/CHEBI:", "");
      } else if (resource.startsWith("kegg.reaction/")) {
        database = ReactionMajorLabel.LigandReaction.toString();
        String[] str_ = resource.split("/");
        if (str_.length == 2) {
          entry = str_[1];
        }
      } else if (resource.startsWith("biocyc/META:")) {
        database = MetaboliteMajorLabel.MetaCyc.toString();
        entry = resource.replaceFirst("biocyc/", "");
      }
    } else if (resource.contains("#")) {
      
      String[] data = resource.split("#");
//      System.out.println("yes!" + data[0]);
      if (data.length == 2) {
        switch (data[0]) {
          case "http://bigg.ucsd.edu/":
            database = MetaboliteMajorLabel.BiGG.toString();
            entry = data[1];
            break;
          case "http://www.genome.jp/kegg/compound/":
            database = MetaboliteMajorLabel.LigandCompound.toString();
            entry = data[1];
            break;
          case "http://www.genoscope.cns.fr/acinetocyc/":
            database = "AcinetoCyc";
            entry = data[1];
            break;
          case "http://www.genoscope.cns.fr/acinetofuncat/":
            database = "AcinetoFuncat";
            entry = data[1];
            break;
          case "http://www.genoscope.cns.fr/acinetopathway/":
            database = "AcinetoPathway";
            entry = data[1];
            break;
          default:
            break;
        }
      }
    }
    
    return new ImmutablePair<>(database, entry);
  }
  
  public SbmlObjectMetadata detectMetadata(XmlSbmlSpecie xspi) {
    notesParser.messages.clear();
    SbmlObjectMetadata result = new SbmlObjectMetadata();
    result.type = SbmlObjectMetadataType.COMPOUND;
    
    Set<String> rejectedResources = new HashSet<> ();
    for (String k : xspi.getListOfAnnotations().keySet()) {
      switch (k) {
        case "relation":
        case "is":
          
          for (XmlObject o : xspi.getListOfAnnotations().get(k)) {
            String resource = o.getAttributes().get("resource");
            Pair<String, String> dbPair = extractRdfAnnotation(resource);
            if (dbPair.getLeft() == null || dbPair.getRight() == null) {
              rejectedResources.add(resource);
            } else {
              result.references.add(dbPair);
            }
          }
          break;
        case "isEncodedBy":
          result.type = SbmlObjectMetadataType.PROTEIN;
//          System.out.println( xspi.getListOfAnnotations().get(k));
          break;
        default: logger.warn("not sure what to do with [{}]", k); break;
      }
    }
    
    String notes = xspi.getNotes();
    if (!DataUtils.empty(notes)) {
      SbmlNotesParser notesParser = new SbmlNotesParser(notes);
      notesParser.parse();
      for (Tuple2<String> t : notesParser.getData()) {
        switch (t.e1) {
          case "CHARGE": break;
          case "FORMULA":
            result.setFormula(t.e2);
            break;
          case "INCHI":
            result.setFormula(t.e2);
            break;
          case "SMILES": 
            result.setFormula(t.e2);
            break;
          case "CHEBI": break;
          case "PUBCHEM": break;
          case "SEED":
            for (String s : t.e2.split(";")) {
              if (s.startsWith("cpd")) {
                result.references.add(new ImmutablePair<String, String>(MetaboliteMajorLabel.ModelSeed.toString(), s));
              }
            }
            break;
          case "KEGG":
            for (String s : t.e2.split(";")) {
              s = s.trim().toUpperCase();
              if (s.length() == 6) {
                switch (s.charAt(0)) {
                case 'C':
                  result.references.add(new ImmutablePair<String, String>(MetaboliteMajorLabel.LigandCompound.toString(), s));
                  break;
                case 'G':
                  result.references.add(new ImmutablePair<String, String>(MetaboliteMajorLabel.LigandGlycan.toString(), s));
                  break;
                case 'D':
                  result.references.add(new ImmutablePair<String, String>(MetaboliteMajorLabel.LigandDrug.toString(), s));
                  break;
                default: logger.warn("not sure what to do with [{}]", s); break;
                }
              }
            }
            break;
          case "METACYC":
            for (String s : t.e2.split(";")) {
              if (!s.startsWith("META:")) {
                s = "META:".concat(s);
              }
              result.references.add(new ImmutablePair<String, String>(MetaboliteMajorLabel.MetaCyc.toString(), s));
            }
            break;
          case "trash": break;
          default: logger.warn("not sure what to do with [{}]", t); break;
        }
      }
    }
    
//    List<String> notesData = SbmlNotesParser.parseNotes();
//    Map<String, Set<String>> noteData = notesParser.parseNotes2(notesData);
//    if (notesParser.messages.size() > 0) {
//      logger.warn("{}", notesParser.messages);
//    }
    

//    System.out.println(noteData);
    
//    System.out.println(rejectedResources);
    
    return result;
  }
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {
    IntegrationMap<String, MetaboliteMajorLabel> imap = new IntegrationMap<>();
    for (XmlSbmlSpecie xspi : xmlSpecies) {
      String spiEntry = xspi.getAttributes().get("id");
      SbmlObjectMetadata mdata = detectMetadata(xspi);
      for (Pair<String, String> p : mdata.references) {
        String dbStr = p.getLeft();
        String entry = p.getRight();
        try {
          MetaboliteMajorLabel db = MetaboliteMajorLabel.valueOf(dbStr);
          imap.addIntegration(spiEntry, db, entry);
        } catch (IllegalArgumentException e) {
          
        }
      }
    }
    
    return imap;
  }

}
