package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteOntology;

public class HmdbMetaboliteTransform extends 
AbstractMetaboliteTransform<HmdbMetaboliteEntity> {

  private static final Logger logger = LoggerFactory.getLogger(HmdbMetaboliteTransform.class);
  
  private static final String HMDB_LABEL = MetaboliteMajorLabel.HMDB.toString();
  
  private final Map<String, String> biggInternalIdToEntryMap;
  
  public HmdbMetaboliteTransform(Map<String, String> biggInternalIdToEntryMap) {
    super(HMDB_LABEL, new BiobaseMetaboliteEtlDictionary<>(
        HmdbMetaboliteEntity.class));
    this.biggInternalIdToEntryMap = biggInternalIdToEntryMap;
  }

  @Override
  protected void configureAdditionalPropertyLinks(GraphMetaboliteEntity gcpd,
                                                  HmdbMetaboliteEntity cpd) {
    for (String cpdEntry : cpd.getAccessions()) {
      gcpd.addConnectedEntity(
          this.buildPair(
              new SomeNodeFactory()
              .withEntry(cpdEntry)
              .withMajorLabel(GlobalLabel.Metabolite)
              .withMajorLabel(HMDB_LABEL)
              .withProperty(Neo4jDefinitions.PROXY_PROPERTY, false)
              .withProperty("secondary_accession", true)
              .buildGenericNodeEntity(), 
              new SomeNodeFactory().buildMetaboliteEdge(
                  MetaboliteRelationshipType.has_secondary_accession)));
    }
    
    this.configureGenericPropertyLink(gcpd, cpd.getInchi(), 
        MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
    this.configureGenericPropertyLink(gcpd, cpd.getSmiles(), 
        MetabolitePropertyLabel.SMILES, MetaboliteRelationshipType.has_smiles);
    if (cpd.getInchi() != null) {
      this.configureGenericPropertyLink(gcpd, cpd.getInchikey().replace("InChIKey=", ""), 
          MetabolitePropertyLabel.InChIKey, MetaboliteRelationshipType.has_inchikey);
    }
    
    HmdbMetaboliteOntology ontology = cpd.getOntology();
    if (ontology != null) {
      gcpd.getProperties().put("ontology_status", ontology.getStatus());
      gcpd.getProperties().put("ontology_biofuncions", 
          StringUtils.join(ontology.getBiofunctions(), ";"));
      gcpd.getProperties().put("ontology_cellular_locations", 
          StringUtils.join(ontology.getCellularLocations(), ";"));
      gcpd.getProperties().put("ontology_origins", 
          StringUtils.join(ontology.getOrigins(), ";"));
      gcpd.getProperties().put("ontology_applications", 
          StringUtils.join(ontology.getApplications(), ";"));
    }
    if (!cpd.getBiofluids().isEmpty()) {
      gcpd.getProperties().put("biofluids", 
          StringUtils.join(cpd.getBiofluids(), ";"));
    }
    if (!cpd.getTissues().isEmpty()) {
      gcpd.getProperties().put("tissues", 
          StringUtils.join(cpd.getTissues(), ";"));
    }
  }
  
  @Override
  protected void configureNameLink(GraphMetaboliteEntity gcpd, 
                                   HmdbMetaboliteEntity cpd) {
    super.configureNameLink(gcpd, cpd);
    this.configureNameLink(gcpd, cpd.getIupacName());
    for (String name : cpd.getSynonyms()) {
      if (name != null && !name.trim().isEmpty()) {
        this.configureNameLink(gcpd, name);
      }
    }
  }
  
  @Override
  protected void configureCrossreferences(
      GraphMetaboliteEntity centralMetaboliteEntity,
      HmdbMetaboliteEntity cpd) {
    for (HmdbMetaboliteCrossreferenceEntity xref : cpd.getCrossreferences()) {
      if (xref.getRef().toLowerCase().equals("bigg_id")) {
        if (biggInternalIdToEntryMap.containsKey(xref.getValue())) {
          xref.setValue(biggInternalIdToEntryMap.get(xref.getValue()));
          logger.debug("Internal Id replaced: " + xref);
        } else {
          logger.warn("Unable to replace internal ID {}", xref.getValue());
        }
      }
    }
    super.configureCrossreferences(centralMetaboliteEntity, cpd);
  }
}
