package pt.uminho.sysbio.biosynthframework.core.data.service;

import java.util.List;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;

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
