package pt.uminho.sysbio.biosynthframework.biodb.hmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(value = { "taxonomy", "predicted_properties", "spectra", 
                                "pathways",
                                "normal_concentrations",
                                "abnormal_concentrations",
                                "diseases"})
@JacksonXmlRootElement(localName ="metabolite")
public class XmlHmdbMetabolite {
  
  public static class Ontology {
    public String status;
    
//    @JacksonXmlElementWrapper(useWrapping=true)
    public List<String> biofunctions;
    
//    @JacksonXmlElementWrapper(useWrapping=true)
    public List<String> applications;
    
//    @JacksonXmlElementWrapper(useWrapping=true)
    public List<String> origins;
    
//    @JacksonXmlElementWrapper(useWrapping=true)
    public List<String> cellular_locations;
  }

  public String version;
  public String creation_date;
  public String update_date;
  public String accession;
  
  @JacksonXmlProperty(localName = "secondary_accessions")
  @JacksonXmlElementWrapper(useWrapping=true)
  public List<String> secondary_accessions;
  public List<String> synonyms;
  public String chemical_formula;
  public double average_molecular_weight;
  public double monisotopic_moleculate_weight;
  public String iupac_name;
  public String traditional_iupac;
  public String cas_registry_number;
  public String smiles;
  public String inchi;
  public String inchikey;
  public Ontology ontology;
  public String state;
//  public String experimental_properties;
  public String kegg_id;
  public String biocyc_id;
  public String bigg_id;
  public String wikipidia;
  public String nugowiki;
  public String pubchem_compound_id;
  public String chebi_id;
  public String chemspider_id;
  
  public List<String> biofluid_locations;
  public List<String> tissue_locations;
//  public String version;
}
