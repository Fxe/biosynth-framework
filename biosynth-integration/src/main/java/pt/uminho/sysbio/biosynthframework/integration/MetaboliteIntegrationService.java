package pt.uminho.sysbio.biosynthframework.integration;

import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;

public interface MetaboliteIntegrationService<T> {
  public ConnectedComponents<T> entityResolution();
}
