package pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.etl.AbstractMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BiobaseMetaboliteEtlDictionary;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;

public class BiggMetaboliteTransform
extends AbstractMetaboliteTransform<BiggMetaboliteEntity> {

  private static final String BIGG_METABOLITE_LABEL = MetaboliteMajorLabel.BiGG.toString();
  
  public BiggMetaboliteTransform() {
    super(BIGG_METABOLITE_LABEL, new BiobaseMetaboliteEtlDictionary<>(BiggMetaboliteEntity.class));
  }

  @Override
  protected void configureCrossreferences(GraphMetaboliteEntity centralMetaboliteEntity,
                                          BiggMetaboliteEntity metabolite) {
    for (BiggMetaboliteCrossreferenceEntity xref : metabolite.getCrossreferences()) {
      if (xref.getValue().startsWith("KEGG -")) {
        xref.setValue(xref.getValue().substring("KEGG -".length()).trim());
      }
      if (xref.getType().equals(ReferenceType.MODEL) && "Recon 1".equals(xref.getValue())) {
        xref.setValue("RECON1");
        xref.setRef("RECON1");
      }
    }
    super.configureCrossreferences(centralMetaboliteEntity, metabolite);
  }
  
  @Override
  protected void configureAdditionalPropertyLinks(
      GraphMetaboliteEntity centralMetaboliteEntity,
      BiggMetaboliteEntity entity) {
    
    configureGenericPropertyLink(centralMetaboliteEntity, entity.getCharge(), MetabolitePropertyLabel.Charge, MetaboliteRelationshipType.has_charge);

    for (String cmp : entity.getCompartments()) {
      centralMetaboliteEntity.addConnectedEntity(
          this.buildPair(
              new SomeNodeFactory()
              .withEntry(cmp.toLowerCase())
              .withLabel(GlobalLabel.SubcellularCompartment)
              .withMajorLabel(GlobalLabel.BiGG)
              .buildGenericNodeEntity(), 
              new SomeNodeFactory().buildMetaboliteEdge(
                  MetaboliteRelationshipType.found_in)));
    }
    //		centralMetaboliteEntity.addPropertyEntity(
    //				this.buildPropertyLinkPair(
    //						PROPERTY_UNIQUE_KEY, 
    //						entity.getCharge(), 
    //						METABOLITE_CHARGE_LABEL, 
    //						METABOLITE_CHARGE_RELATIONSHIP_TYPE));

    //		for (String cmp : entity.getCompartments()) {
    //			centralMetaboliteEntity.addPropertyEntity(
    //					this.buildPropertyLinkPair(
    //							"entry", 
    //							cmp.toLowerCase(), 
    //							GlobalLabel.SubcellularCompartment.toString(), 
    //							MetaboliteRelationshipType.found_in.toString()));			
    //		}
  }

  //	@Override
  //	protected void configureProperties(GraphMetaboliteEntity centralMetaboliteEntity, BiggMetaboliteEntity metabolite) {
  //		super.configureProperties(centralMetaboliteEntity, metabolite);
  ////		centralMetaboliteEntity.getProperties().put("id", metabolite.getId());
  //	};

}
