package pt.uminho.sysbio.biosynthframework.io;

import java.util.Set;

public interface MetabolicModelDao {
	
	public Set<Long> getAllCompartmentIds();
	public Set<Long> getAllMetaboliteSpecieIds();
	public Set<Long> getAllReactionSpecieIds();
	
	public Set<String> getAllCompartmentEntries();
	public Set<String> getAllMetaboliteSpecieEntries();
	public Set<String> getAllReactionSpecieEntries();
	
	public void getCompartmentById(Long id);
	public void getMetaboliteSpecieById(Long id);
	public void getReactionSpecieById(Long id);
	
	public void getCompartmentByEntry(String entry);
	public void getMetaboliteSpecieByEntry(String entry);
	public void getReactionSpecieByEntry(String entry);
}
