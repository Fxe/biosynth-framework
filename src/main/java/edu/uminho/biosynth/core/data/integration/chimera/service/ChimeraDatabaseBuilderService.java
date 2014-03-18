package edu.uminho.biosynth.core.data.integration.chimera.service;

public interface ChimeraDatabaseBuilderService {
	public void changeIntegrationSet(Long id);
	public void resetTarget();
	public void generateIntegratedDatabase();
}
