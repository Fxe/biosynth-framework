package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedMetaboliteEntity;

public class ModelSeedMetaboliteTransform extends AbstractMetaboliteTransform<ModelSeedMetaboliteEntity>{

  private static final String MODELSEED_METABOLITE_LABEL = 
      MetaboliteMajorLabel.ModelSeed.toString();
  
  public static Map<String, String> getDbMapping() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("BiGG1", MetaboliteMajorLabel.BiGG.toString());
    mapping.put("BiGG", MetaboliteMajorLabel.BiGGMetabolite.toString());
    return mapping;
  }
  
  public ModelSeedMetaboliteTransform() {
    super(MODELSEED_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(ModelSeedMetaboliteEntity.class, getDbMapping()));
  }
  
  @Override
  protected void configureNameLink(GraphMetaboliteEntity gcpd, ModelSeedMetaboliteEntity cpd) {
    Set<String> names = new HashSet<> ();
    if (cpd.getName() != null) {
      names.add(cpd.getName());
    }
    names.addAll(cpd.getNames());
    
    for (String name : names) {
      configureNameLink(gcpd, name);
    }
  }

  @Override
  protected void configureAdditionalPropertyLinks(GraphMetaboliteEntity gcpd,
      ModelSeedMetaboliteEntity cpd) {
    this.configureGenericPropertyLink(gcpd, cpd.getSearchInchi(), MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
    this.configureGenericPropertyLink(gcpd, cpd.getStructure(), MetabolitePropertyLabel.InChI, MetaboliteRelationshipType.has_inchi);
  }

}
