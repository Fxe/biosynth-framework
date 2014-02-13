package edu.uminho.biosynth.core.data.integration.generator;

import java.io.Serializable;

public interface IKeyGenerator<T extends Serializable> {
	public T generateKey();
}
