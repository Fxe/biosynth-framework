package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;

public class Bigg2MetaboliteTransform
extends AbstractMetaboliteTransform<Bigg2MetaboliteEntity> {
  
  private static final String BIGG_METABOLITE_LABEL = MetaboliteMajorLabel.BiGGMetabolite.toString();
  
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
    super.configureCrossreferences(centralMetaboliteEntity, metabolite);
  }
  
  @Override
  protected void configureFormulaLink(GraphMetaboliteEntity centralMetaboliteEntity, Bigg2MetaboliteEntity entity) {
    for (String f : entity.getFormula().split(";")) {
      this.configureGenericPropertyLink(centralMetaboliteEntity, f, 
          MetabolitePropertyLabel.MolecularFormula, MetaboliteRelationshipType.has_molecular_formula);
    }
  }
}
