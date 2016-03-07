package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteEntity;

public class LipidmapsMetaboliteTransform 
extends AbstractMetaboliteTransform<LipidmapsMetaboliteEntity> {

  private static final String LIPIDMAPS_LABEL = MetaboliteMajorLabel.LipidMAPS.toString();
  
  public LipidmapsMetaboliteTransform() {
    super(LIPIDMAPS_LABEL, new BiobaseMetaboliteEtlDictionary<>(
        LipidmapsMetaboliteEntity.class));
  }

  @Override
  protected void configureAdditionalPropertyLinks(GraphMetaboliteEntity centralMetaboliteEntity,
      LipidmapsMetaboliteEntity metabolite) {
    
    if (metabolite.getInchiKey() != null && 
        !metabolite.getInchiKey().trim().isEmpty()) {
      this.configureGenericPropertyLink(centralMetaboliteEntity, 
                                        metabolite.getInchiKey(), 
                                        MetabolitePropertyLabel.InChIKey, 
                                        MetaboliteRelationshipType.has_inchikey);
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
