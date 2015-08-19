package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggKOEntity extends KeggEntity{
	
	protected Set<String> genes;
	protected Set<String> modules;
	protected Set<String> pathways;
	
	
	public void addGene(String gene){
		if(genes==null)
			genes = new HashSet<>();
		genes.add(gene);
	}
	
	public void addGenes(Collection<String> gs){
		if(genes==null)
			genes = new HashSet<>();
		genes.addAll(gs);
	}
	
	public void addModule(String module){
		if(modules==null)
			modules = new HashSet<>();
		modules.add(module);
	}

	public void addPathway(String pathway){
		if(pathways==null)
			pathways = new HashSet<>();
		pathways.add(pathway);
	}
	
	public Set<String> getGenes() {
		return genes;
	}
	public void setGenes(Set<String> genes) {
		this.genes = genes;
	}
	public Set<String> getModules() {
		return modules;
	}
	public void setModules(Set<String> modules) {
		this.modules = modules;
	}
	public Set<String> getPathways() {
		return pathways;
	}
	public void setPathways(Set<String> pathways) {
		this.pathways = pathways;
	}
	

	@Override
	public void addProperty(String key, String value) {
		Object addedValue = null;
		if(key.equals(KeggTokens.GENES))
		{
			addedValue = getGenesFromValue(value);
			addGenes((Set<String>) addedValue);
		}
		else if(key.equals(KeggTokens.MODULE))
		{
			addedValue = getModuleFromValue(value);
			addModule((String) addedValue);
		}
		else if(key.equals(KeggTokens.PATHWAY))
		{
			addedValue = getPathwayFromValue(value);
			addPathway((String) addedValue);
		}
		if(addedValue==null)
			super.addProperty(key, value);
	}
}
