package pt.uminho.sysbio.biosynth.test;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;

public class GraphMetaboliteEntityMother {
  public static final GraphMetaboliteEntity buildKeggC06142() {
    GraphMetaboliteEntity entity = new SomeNodeFactory()
        .withLabel(GlobalLabel.Metabolite)
        .withEntry("C06142")
        .withProperty("formula", "C4H10O")
        .withProperty("remark", "Same as: D03200")
        .withLinkTo(
            new SomeNodeFactory()
            .buildGraphMetabolitePropertyEntity(
                MetabolitePropertyLabel.MolecularFormula, "C4H10O"), 
            MetaboliteRelationshipType.has_molecular_formula, null)
        .buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandCompound);
    return entity;
  }
}
