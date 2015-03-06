package pt.uminho.sysbio.biosynthframework.io;

import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.DefaultModelMetaboliteEntity;

public interface MetabolicModelDao<
MMD extends AbstractBiosynthEntity,
SPI extends AbstractBiosynthEntity,
CPD extends AbstractBiosynthEntity,
RXN extends AbstractBiosynthEntity,
CMP extends AbstractBiosynthEntity> {
/*
M extends AbstractBiosynthEntity,
R extends AbstractBiosynthEntity,
S extends AbstractBiosynthEntity,
C extends AbstractBiosynthEntity> {
	*/
	public MMD getMetabolicModelById(long id);
	public MMD getMetabolicModelByEntry(String entry);
	public MMD saveMetabolicModel(MMD mmd);
	public Set<Long>   getAllMetabolicModelIds();
	public Set<String> getAllMetabolicModelEntries();
	public List<MMD> findMetabolicModelBySearchTerm(String search);
	public List<MMD> findAll(int page, int size);
	
	public CMP getCompartmentById(Long id);
	public CMP getCompartmentByModelAndEntry(MMD model, String cmpEntry);
	public CMP saveCompartment(MMD mmd, CMP cmp);
	public Set<Long>   getAllModelCompartmentIds(MMD model);
	public Set<String> getAllModelCompartmentEntries(MMD model);
	
	public SPI getModelMetaboliteSpecieById(Long id);
	public SPI getModelMetaboliteSpecieByByModelAndEntry(MMD model, String spiEntry);
	public SPI saveModelMetaboliteSpecie(MMD mmd, SPI spi);
	public Set<Long>   getAllModelSpecieIds(MMD model);
	public Set<String> getAllModelSpecieEntries(MMD model);
	
	public RXN getModelReactionById(Long id);
	public RXN getModelReactionByByModelAndEntry(MMD model, String rxnEntry);
	public RXN saveModelReaction(MMD mmd, RXN rxn);
	public Set<Long>   getAllModelReactionIds(MMD model);
	public Set<String> getAllModelReactionEntries(MMD model);
	
	public CPD getModelMetaboliteById(Long id);
	public CPD getModelMetaboliteByModelAndEntry(MMD model, String cpdEntry);
	public CPD saveModelMetabolite(MMD mmd, CPD cpd);
	public void deleteModelMetabolite(CPD mcpd);
	public Set<Long>   getAllModelMetaboliteIds(MMD model);
	public Set<String> getAllModelMetaboliteEntries(MMD model);
	
}
