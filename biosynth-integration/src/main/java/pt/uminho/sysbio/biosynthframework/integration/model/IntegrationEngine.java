package pt.uminho.sysbio.biosynthframework.integration.model;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public interface IntegrationEngine {
  public IntegrationMap<String, MetaboliteMajorLabel> integrate(IntegrationMap<String, MetaboliteMajorLabel> imap);
}
