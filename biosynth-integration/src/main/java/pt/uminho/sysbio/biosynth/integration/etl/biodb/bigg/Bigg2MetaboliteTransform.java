package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;

public class Bigg2MetaboliteTransform
extends AbstractMetaboliteTransform<Bigg2MetaboliteEntity> {
  
  private static final String BIGG_METABOLITE_LABEL = MetaboliteMajorLabel.BiGG2.toString();
  
  public Bigg2MetaboliteTransform() {
    super(BIGG_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(Bigg2MetaboliteEntity.class));
  }

  @Override
  protected void configureAdditionalPropertyLinks(GraphMetaboliteEntity centralMetaboliteEntity,
      Bigg2MetaboliteEntity metabolite) {
  }

  @Override
  protected void configureCrossreferences(GraphMetaboliteEntity centralMetaboliteEntity,
      Bigg2MetaboliteEntity metabolite) {
    // TODO Auto-generated method stub
    super.configureCrossreferences(centralMetaboliteEntity, metabolite);
  }
}
