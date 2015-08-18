package pt.uminho.sysbio.biosynthframework.core.data.service.integration;

import java.util.List;

import pt.uminho.sysbio.biosynthframework.deprecated.IntegratedMetabolite;

@Deprecated
public interface IIntegrationStrategy {
	public List<IntegratedMetabolite> integrate(List<?> metabolite);
}
