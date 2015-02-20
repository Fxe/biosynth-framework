package pt.uminho.sysbio.biosynthframework.io;

import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public interface MetabolicModelDao<
D extends AbstractBiosynthEntity> {
/*
M extends AbstractBiosynthEntity,
R extends AbstractBiosynthEntity,
S extends AbstractBiosynthEntity,
C extends AbstractBiosynthEntity> {
	*/
	public D getMetabolicModelById(long id);
	public D getMetabolicModelByEntry(String entry);
	public List<D> findMetabolicModelBySearchTerm(String search);
	public List<D> findAll(int page, int size);
	
	//public C getMetabolicModelCompartment();
	
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
