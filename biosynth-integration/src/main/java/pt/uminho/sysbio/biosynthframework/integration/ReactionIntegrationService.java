package pt.uminho.sysbio.biosynthframework.integration;

import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;

public interface ReactionIntegrationService<T> {
  public ConnectedComponents<T> entityResolution(ConnectedComponents<T> cpdIntegration);
}
