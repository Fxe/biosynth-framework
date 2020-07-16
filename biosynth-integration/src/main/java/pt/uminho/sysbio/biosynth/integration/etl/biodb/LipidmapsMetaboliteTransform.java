package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class LipidmapsMetaboliteTransform 
extends AbstractMetaboliteTransform<LipidmapsMetaboliteEntity> {

  private static final String LIPIDMAPS_LABEL = MetaboliteMajorLabel.LipidMAPS.toString();
  
  public LipidmapsMetaboliteTransform() {
    super(LIPIDMAPS_LABEL, new BiobaseMetaboliteEtlDictionary<>(
        LipidmapsMetaboliteEntity.class));
  }
  
  @Override
  public GraphMetaboliteEntity etlTransform(LipidmapsMetaboliteEntity metabolite) {
    GraphMetaboliteEntity gcpd = super.etlTransform(metabolite);
    gcpd.getProperties().remove("mol");
    return gcpd;
  }

  @Override
  protected void configureAdditionalPropertyLinks(GraphMetaboliteEntity centralMetaboliteEntity,
      LipidmapsMetaboliteEntity metabolite) {
    
    if (!DataUtils.empty(metabolite.getMol())) {
      this.configureGenericPropertyLink(centralMetaboliteEntity, 
                                        metabolite.getMol(), 
                                        MetabolitePropertyLabel.MDLMolFile, 
                                        MetaboliteRelationshipType.has_mdl_mol_file);
    }
    
    if (metabolite.getInchiKey() != null && 
        !metabolite.getInchiKey().trim().isEmpty()) {
      this.configureGenericPropertyLink(centralMetaboliteEntity, 
                                        metabolite.getInchiKey(), 
                                        MetabolitePropertyLabel.InChIKey, 
                                        MetaboliteRelationshipType.has_inchikey);
    }
    if (metabolite.getInchi() != null && 
        !metabolite.getInchi().trim().isEmpty()) {
      this.configureGenericPropertyLink(centralMetaboliteEntity, 
                                        metabolite.getInchi(), 
                                        MetabolitePropertyLabel.InChI, 
                                        MetaboliteRelationshipType.has_inchi);
    }
  }
  
  @Override
  protected void configureNameLink(
      GraphMetaboliteEntity centralMetaboliteEntity, 
      LipidmapsMetaboliteEntity entity) {
    super.configureNameLink(centralMetaboliteEntity, entity);
    configureNameLink(centralMetaboliteEntity, entity.getSystematicName());
    if (entity.getSynonyms() != null && !entity.getSynonyms().trim().isEmpty()) {
      for (String name : entity.getSynonyms().trim().split(";")) {
        configureNameLink(centralMetaboliteEntity, name);
      }
    }
  }
}
