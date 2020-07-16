package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggPathwayEntity extends KeggEntity{

  private static final long serialVersionUID = 1L;

  protected Set<String> modules;
	
	/** Map&lt;Gene, [EC Number]&gt; */
	protected Map<String, Set<String>> geneStepEcs;
	/** Map&lt;Gene, [Ortholog]&gt; */
	protected Map<String, Set<String>> geneStepOrthologs;
	
	
	protected Set<String> getValuesFromExp(String value, String regExp){
		Set<String> vs = new TreeSet<>();
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(value);
		while(m.find())
			vs.add(m.group());
		return vs.size()==0 ? null : vs;
	}
	
	
	public void addGeneStepInfo(String gene, Set<String> orthologs, Set<String> ecs){
		if(ecs!=null)
		{
			if(geneStepEcs==null)
				geneStepEcs = new HashMap<>();
			geneStepEcs.put(gene, ecs);
		}
		if(orthologs!=null)
		{
			if(geneStepOrthologs==null)
				geneStepOrthologs = new HashMap<>();
			geneStepOrthologs.put(gene, orthologs);
		}
	}
	
	public String addGeneStepInfo(String geneStepInfo){
		Set<String> orthologs=null, ecs=null;
		String[] tokens = geneStepInfo.split("\\s+", 2);
		String gene = tokens[0];
		Pattern p = Pattern.compile(KeggTokens.PATHWAY_GENE_ORTHOLOG_EC);
		Matcher m = p.matcher(tokens[1]);
		if(m.find())
		{
			orthologs = getValuesFromExp(m.group(1), KeggTokens.ORTHOLOG_REGEXP);
			ecs = getValuesFromExp(m.group(2), KeggTokens.ECNUMBER_REGEXP);
		}
		else
		{
			p = Pattern.compile(KeggTokens.PATHWAY_GENE_ORTHOLOG);
			m = p.matcher(tokens[1]);
			if(m.find())
				orthologs = getValuesFromExp(m.group(1), KeggTokens.ORTHOLOG_REGEXP);
		}
		addGeneStepInfo(gene, orthologs, ecs);
		return gene;
	}
	
	public void addModule(String module){
		if(modules==null)
			modules = new HashSet<>();
		modules.add(module);
	}

	public String getEntryFromValue(String value){
		String[] tokens = value.split("\\s+");
		return tokens[0];
	}
	
	public Set<String> getAllEcNumbers(){
		Set<String> ecs = new TreeSet<>();
		if(geneStepEcs!=null)
		{
			for(Set<String> es : geneStepEcs.values())
				ecs.addAll(es);
		}
		return ecs.size()==0 ? null : ecs;
	}
	
	public Set<String> getGenesFromEc(String ec){
		Set<String> gs = new TreeSet<>();
		for(String g : geneStepEcs.keySet())
			if(geneStepEcs.get(g).contains(ec))
				gs.add(g);
		return gs.size()==0 ? null : gs;
	}
	
	public Set<String> getGeneStepOrthologs(String gene) {
		return geneStepOrthologs.containsKey(gene) ? geneStepOrthologs.get(gene) : null;
	}
	
	public Set<String> getModules() {
		return modules;
	}
	public void setModules(Set<String> modules) {
		this.modules = modules;
	}
	public Map<String, Set<String>> getGeneStepEcs() {
		return geneStepEcs;
	}
	public void setGeneStepEcs(Map<String, Set<String>> geneStepEcs) {
		this.geneStepEcs = geneStepEcs;
	}
	public Map<String, Set<String>> getGeneStepOrthologs() {
		return geneStepOrthologs;
	}
	public void setGeneStepOrthologs(Map<String, Set<String>> geneStepOrthologs) {
		this.geneStepOrthologs = geneStepOrthologs;
	}
	
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer(); 
		str.append("Entry: " + entry);
		
		str.append("\nModules:");
		if(modules!=null)
			for(String module : modules)
				str.append("\n\t" + module);
		
		str.append("\nSteps:");
		
		if(geneStepOrthologs!=null)
		{
			for(String g : geneStepOrthologs.keySet())
			{
				String orths="", ecs="";
				for(String o : geneStepOrthologs.get(g))
					orths += o;
				
				if(geneStepEcs.containsKey(g))
					for(String e : geneStepEcs.get(g))
						ecs += e;
				
				str.append("\n\tGene: " + g + "\tOrthologs: " + orths + "\tECs: " + ecs);
			}
		}
		if(geneStepEcs!=null)
		{
			for(String g : geneStepEcs.keySet())
			{
				if(!geneStepOrthologs.containsKey(g))
				{
					String orths="", ecs="";
					if(geneStepEcs.containsKey(g))
						for(String e : geneStepEcs.get(g))
							ecs += e;
					str.append("\n\tGene: " + g + "\tOrthologs: " + orths + "\tECs: " + ecs);
				}
			}
		}
		
		return str.toString();
	}


	@Override
	public void addProperty(String key, String value) {
		Object addedValue = null;
		
		if(key.equals(KeggTokens.ENTRY))
		{
			addedValue = getEntryFromValue(value);
			if(addedValue!=null)
				entry = (String) addedValue;
		}
		else if(key.equals(KeggTokens.PATHWAY_GENE))
		{
			addedValue = addGeneStepInfo(value);
		}
		else if(key.equals(KeggTokens.MODULE))
		{
			addedValue = getModuleFromValue(value);
			if(addedValue!=null)
				addModule((String) addedValue);
		}
		if(addedValue==null)
			super.addProperty(key, value);
	}
}
