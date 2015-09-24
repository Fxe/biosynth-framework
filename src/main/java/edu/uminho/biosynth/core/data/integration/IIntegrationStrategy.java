package edu.uminho.biosynth.core.data.integration;

import java.util.Collection;

public interface IIntegrationStrategy<T, I> {
	public Collection<I> integrate(Collection<T> entities);
}
