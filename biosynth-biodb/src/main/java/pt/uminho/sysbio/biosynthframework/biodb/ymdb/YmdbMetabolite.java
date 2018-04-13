package pt.uminho.sysbio.biosynthframework.biodb.ymdb;

import java.util.List;

public class YmdbMetabolite {
  public static class YmdbMetabolitePathway {
    public String name;
    public String kegg_map_id;
  }
  
  public static class YmdbMetaboliteLiterature {
    public String citation;
    public String pubmed_id;
  }
  
  public static class YmdbMetaboliteMedia {
    public String growth_media;
    public Double concentration;
    public String concentration_units;
    public Double error;
  }
  
  public String ymdb_id;
  public String name;
  public String iupac;
  public String traditional_iupac;
  public String location;
  public String description;
  public List<String> synonyms;

  public List<YmdbMetabolitePathway> pathways;

  public String kegg_id;
  public String chebi_id;
  public String pubchem_id;
  public String foodb_id;
  public String hmdb_id;
  public String biocyc_id;

  public String cas;
  public String cs_id;
  public String wikipedia_link;

  public List<Object> proteins;

  public Integer bioavailability;
  public Integer veber_rule;
  public String melting_point;
  public Double logp;
  public Double alogps_logs;
  public Double alogps_logp;
  public String experimental_logp_hydrophobicity;
  public String experimental_water_solubility;
  public Double pka;
  public Double pka_strongest_basic;
  public Double pka_strongest_acidic;

  
  public List<YmdbMetaboliteLiterature> references;
  public List<YmdbMetaboliteMedia> growth_conditions;

  public Integer number_of_rings;
  public Integer acceptor_count;
  public Integer donor_count;
  
  public Double formal_charge;
  public Double physiological_charge;
  
  public Object updated_at;
  public Object created_at;
  
  
  public Object rotatable_bond_count;
  public Object refractivity;
  public Object ghose_filter;
  public Object mddr_like_rule;
  public Object state;
  public Object alogps_solubility;
  
  public Object polarizability;
  
  public Object synthesis_reference;

  public Object rule_of_five;
  
  public Object polar_surface_area;
}
