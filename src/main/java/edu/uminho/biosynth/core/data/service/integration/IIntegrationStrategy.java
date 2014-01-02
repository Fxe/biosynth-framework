package edu.uminho.biosynth.core.data.service.integration;

import java.util.List;

import edu.uminho.biosynth.core.components.integration.IntegratedMetabolite;

public interface IIntegrationStrategy {
	public List<IntegratedMetabolite> integrate(List<?> metabolite);
}
