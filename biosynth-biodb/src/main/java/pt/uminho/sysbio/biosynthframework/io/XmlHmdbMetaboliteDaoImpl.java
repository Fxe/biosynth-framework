package pt.uminho.sysbio.biosynthframework.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteOntology;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.XmlHmdbMetabolite;

public class XmlHmdbMetaboliteDaoImpl extends AbstractReadOnlyMetaboliteDao<HmdbMetaboliteEntity> {

  private static final Logger logger = LoggerFactory.getLogger(XmlHmdbMetaboliteDaoImpl.class);
  
  private final String path;
  private ObjectMapper mapper = new XmlMapper();
  
  public XmlHmdbMetaboliteDaoImpl(String path, String version) {
    super(version);
    mapper = new XmlMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
    this.path = path;
  }
  
  @Override
  public HmdbMetaboliteEntity getMetaboliteById(Serializable id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HmdbMetaboliteEntity getMetaboliteByEntry(String entry) {

    File record = new File(String.format("%s/%s.xml", path, entry));
    if (!record.exists()) {
      logger.debug("{} not found", entry);
      return null;
    }
    
    XmlHmdbMetabolite xmlMetabolite = null;
    
    try {
      xmlMetabolite = mapper.readValue(record, XmlHmdbMetabolite.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    HmdbMetaboliteEntity cpd = convertXmlEntity(xmlMetabolite);
    cpd.setVersion(version);
    return cpd;
  }
  
  public static String fixString(String s) {
    if (s != null && !s.trim().isEmpty()) {
      return s.trim();
    }
    
    return null;
  }

  public static HmdbMetaboliteEntity convertXmlEntity(XmlHmdbMetabolite xmlEntity) {
    HmdbMetaboliteEntity cpd = new HmdbMetaboliteEntity();
    cpd.setEntry(fixString(xmlEntity.accession));
    cpd.setName(fixString(xmlEntity.traditional_iupac));
    cpd.setIupacName(fixString(xmlEntity.iupac_name));
    cpd.setFormula(fixString(xmlEntity.chemical_formula));
    cpd.setSmiles(fixString(xmlEntity.smiles));
    cpd.setInchi(fixString(xmlEntity.inchi));
    cpd.setInchikey(fixString(xmlEntity.inchikey));
    cpd.setMonisotopicMoleculateWeight(xmlEntity.monisotopic_moleculate_weight);
    cpd.setAverageMolecularWeight(xmlEntity.average_molecular_weight);
    cpd.setSynonyms(toSet(xmlEntity.synonyms));
    cpd.setBiofluids(toSet(xmlEntity.biofluid_locations));
    cpd.setTissues(toSet(xmlEntity.tissue_locations));
    cpd.setAccessions(toSet(xmlEntity.secondary_accessions));
    cpd.addCrossReference(makeXref("bigg_id", xmlEntity.bigg_id));
    cpd.addCrossReference(makeXref("biocyc_id", xmlEntity.biocyc_id));
    cpd.addCrossReference(makeXref("cas_registry_number", xmlEntity.cas_registry_number));
    cpd.addCrossReference(makeXref("chebi_id", xmlEntity.chebi_id));
    cpd.addCrossReference(makeXref("kegg_id", xmlEntity.kegg_id));
    cpd.addCrossReference(makeXref("chemspider_id", xmlEntity.chemspider_id));
    cpd.addCrossReference(makeXref("pubchem_compound_id", xmlEntity.pubchem_compound_id));
    cpd.addCrossReference(makeXref("wikipidia", xmlEntity.wikipidia));
    
    //build ontology
    if (xmlEntity.ontology != null) {
      HmdbMetaboliteOntology ontology = new HmdbMetaboliteOntology();
      ontology.setStatus(xmlEntity.ontology.status);
      ontology.setApplications(toSet(xmlEntity.ontology.applications));
      ontology.setBiofunctions(toSet(xmlEntity.ontology.biofunctions));
      ontology.setCellularLocations(toSet(xmlEntity.ontology.cellular_locations));
      ontology.setOrigins(toSet(xmlEntity.ontology.origins));
      cpd.setOntology(ontology);
    }

    return cpd;
  }
  
  private static Set<String> toSet(Collection<String> collection) {
    Set<String> result = new HashSet<> ();
    if (collection != null) {
      for (String e : collection) {
        if (e != null && !e.trim().isEmpty()) {
          result.add(e.trim());
        }
      }
    }
    return result;
  }
  
  private static HmdbMetaboliteCrossreferenceEntity makeXref(String database, String entry) {
    if (entry != null && !entry.trim().isEmpty()) {
      return new HmdbMetaboliteCrossreferenceEntity(
          ReferenceType.DATABASE, database.trim(), entry.trim());
    }

    return null;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    List<String> entries = new ArrayList<> ();
    File database = new File(path);
    for (File record : database.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.getName().endsWith(".xml") && 
               pathname.getName().length() == 13;
      }
    })) {
//      System.out.println(record.getName());
      entries.add(record.getName().replace(".xml", ""));
    }
    
    return entries;
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new UnsupportedOperationException();
  }
}
