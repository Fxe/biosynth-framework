package edu.uminho.biosynth.core.data.service;

import java.util.List;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public interface IMetaboliteService<T extends GenericMetabolite> {
	public T getMetaboliteByEntry(String entry);
	public T getMetaboliteById(int id);
	
	public List<T> getAllMetabolites();
	public int countNumberOfMetabolites();
	
	
}
