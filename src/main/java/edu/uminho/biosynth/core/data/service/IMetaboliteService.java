package edu.uminho.biosynth.core.data.service;

import java.util.List;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public interface IMetaboliteService<T extends GenericMetabolite> {
	
	public String getServiceId();
	public void setServiceId(String serviceId);
	
	public T getMetaboliteByEntry(String entry);
	public T getMetaboliteById(int id);
	
//	public T getMetaboliteByCrossreference
	
	public List<String> getAllMetabolitesEntries();
	public List<T> getAllMetabolites();
	public int countNumberOfMetabolites();
	
	
}
