package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer;
import pt.uminho.sysbio.biosynthframework.util.ZipContainer.ZipRecord;

public class SdfLipidmapsMetaboliteDaoImpl extends AbstractReadOnlyMetaboliteDao<LipidmapsMetaboliteEntity> {

  private static final Logger logger = LoggerFactory.getLogger(SdfLipidmapsMetaboliteDaoImpl.class);
  
  private final File file;
  private Map<String, LipidmapsMetaboliteEntity> data = null;
  
  public SdfLipidmapsMetaboliteDaoImpl(File file) {
    this.file = file;
  }
  
  private void load() {
    InputStream is = null;
    try {
      if (file.getName().endsWith(".zip")) {
        logger.info("loading ZIP file: {}", file.getAbsolutePath());
        ZipContainer container = new ZipContainer(file.getAbsolutePath());
        for (ZipRecord zr : container.getInputStreams()) {
          is = zr.is;
          String p[] = zr.name.split("/");
          String name = p[p.length - 1];
          if (name.endsWith("All.sdf")) {
            logger.info("found All.sdf: {}", zr.name);
            data = readSdf(is);
          }
          is.close();
        }
        if (data == null) {
          logger.warn("unable to detect All.sdf data file. Did internal files changed ?");
          data = new HashMap<>();
        }
        container.close();
      } else {
        logger.info("loading SDF file: {}", file.getAbsolutePath());
        is = new FileInputStream(file);
        data = readSdf(is);
        is.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static enum LipidmapsAttributes {
    LM_ID, COMMON_NAME, SYSTEMATIC_NAME, SYNONYMS, EXACT_MASS, FORMULA,
    STATUS,
    LIPIDBANK_ID, PUBCHEM_SID, PUBCHEM_CID, KEGG_ID, HMDBID, CHEBI_ID,
    METABOLOMICS_ID,
    INCHI_KEY, INCHI,
    PUBCHEM_SUBSTANCE_URL, LIPID_MAPS_CMPD_URL,
    CATEGORY, MAIN_CLASS, SUB_CLASS, CLASS_LEVEL4
  }
  
  public static Map<String, LipidmapsMetaboliteEntity> readSdf(InputStream is) throws IOException {
    Map<String, LipidmapsMetaboliteEntity> result = new HashMap<> ();

    Set<String> ignore = new HashSet<> ();
    ignore.add("cdk:Title");
    ignore.add("cdk:Remark");
    ignore.add("cdk:CtabSgroups");

    IteratingSDFReader reader = new IteratingSDFReader(is, DefaultChemObjectBuilder.getInstance());
    while (reader.hasNext()) {
      IAtomContainer atomContainer = reader.next();
      LipidmapsMetaboliteEntity cpd = new LipidmapsMetaboliteEntity();
      StringWriter sw = new StringWriter();
      MDLV2000Writer writer = new MDLV2000Writer(sw);
      try {
        writer.writeMolecule(atomContainer);
        writer.close();
        cpd.setMol(sw.toString());
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      for (Object key : atomContainer.getProperties().keySet()) {
        Object v =  atomContainer.getProperties().get(key);
        if (key instanceof String) {
          String attribute = (String) key;
          if (!ignore.contains(attribute)) {
            if (v == null || v instanceof String) {
              String value = (String) v;
              try {
                LipidmapsAttributes lattribute = LipidmapsAttributes.valueOf(attribute);
                switch (lattribute) {
                  case LM_ID: cpd.setEntry(value); break;
                  case COMMON_NAME: cpd.setName(value); break;
                  case SYSTEMATIC_NAME: cpd.setSystematicName(value); break;
                  case SYNONYMS: cpd.setSynonyms(value); break;
                  case FORMULA: cpd.setFormula(value); break;
                  case EXACT_MASS: cpd.setExactMass(Double.parseDouble(value)); break;
                  case INCHI_KEY: cpd.setInchiKey(value); break;
                  case CATEGORY: cpd.setCategory(value); break;
                  case MAIN_CLASS: cpd.setMainClass(value); break;
                  case SUB_CLASS: cpd.setSubSlass(value); break;
                  case CLASS_LEVEL4: cpd.setClassLevel4(value); break;
                  case PUBCHEM_SUBSTANCE_URL: cpd.setPubchemSubstanceUrl(value); break;
                  case LIPID_MAPS_CMPD_URL: cpd.setLipidMapsCmpdUrl(value); break;
                  case STATUS:
                    cpd.setStatus(value);
                    if (value.toLowerCase().equals("active")) {
                      cpd.setActive(true);
                      cpd.setGenerated(false);
                    } else if (value.toLowerCase().equals("active (generated by computational methods)")) {
                      cpd.setActive(true);
                      cpd.setGenerated(true);
                    } else {
                      logger.warn("{} - STATUS - {}", cpd, value);
                    }
                    break;
                  case INCHI: cpd.setInchi(value); break;
                  case KEGG_ID:
                  case PUBCHEM_CID:
                  case PUBCHEM_SID:
                  case HMDBID:
                  case LIPIDBANK_ID:
                  case METABOLOMICS_ID:
                  case CHEBI_ID:
                    LipidmapsMetaboliteCrossreferenceEntity xref = 
                    new LipidmapsMetaboliteCrossreferenceEntity(
                        ReferenceType.DATABASE, attribute, value);
                    cpd.addCrossReference(xref);
                    break;
                  default: System.out.println(attribute + "\t" + v); break;
                }
              } catch (IllegalArgumentException e) {
                logger.error("Invalid attribute - {}", attribute);
              }
            }
          }
        } else {
          logger.error("Invalid attribute type - {}:{} -> {}", 
              key.getClass().getSimpleName(), key, v);
        }
      }

      result.put(cpd.getEntry(), cpd);
    }
    reader.close();
    
    return result;
  }


  @Override
  public List<String> getAllMetaboliteEntries() {
    if (data == null) {
      this.load();
    }
    return new ArrayList<>(data.keySet());
  }
  

  @Override
  public LipidmapsMetaboliteEntity getMetaboliteByEntry(String entry) {
    if (data == null) {
      this.load();
    }
    return data.get(entry);
  }
}
