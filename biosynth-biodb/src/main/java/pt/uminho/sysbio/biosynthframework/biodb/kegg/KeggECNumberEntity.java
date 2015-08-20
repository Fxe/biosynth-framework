package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggECNumberEntity extends KeggEntity{
	
	protected Set<String> genes;
	protected Set<String> pathways;
	protected Set<String> orthologs;
	protected Set<String> reactions;
	
	
	public void addGenes(Collection<String> gs){
		if(genes==null)
			genes = new HashSet<>();
		genes.addAll(gs);
	}
	
	public void addPathway(String pathway){
		if(pathways==null)
			pathways = new HashSet<>();
		pathways.add(pathway);
	}
	
	public void addOrtholog(String ortholog){
		if(orthologs==null)
			orthologs = new HashSet<>();
		orthologs.add(ortholog);
	}
	
	public void addReaction(String reaction){
		if(reactions==null)
			reactions = new HashSet<>();
		reactions.add(reaction);
	}
	
	public String getReactionFromValue(String value){
		return retrieveValueRegExp(value, KeggTokens.EC_NUMBER_REACTION);
	}
	
	public Set<String> getGenes() {
		return genes;
	}
	public void setGenes(Set<String> genes) {
		this.genes = genes;
	}
	public Set<String> getPathways() {
		return pathways;
	}
	public void setPathways(Set<String> pathways) {
		this.pathways = pathways;
	}
	public Set<String> getOrthologs() {
		return orthologs;
	}
	public void setOrthologs(Set<String> orthologs) {
		this.orthologs = orthologs;
	}
	public Set<String> getReactions() {
		return reactions;
	}
	public void setReactions(Set<String> reactions) {
		this.reactions = reactions;
	}

	
	@Override
	public void addProperty(String key, String value) {
		Object addedValue = null;
		if(key.equals(KeggTokens.ENTRY))
		{
			Pattern p = Pattern.compile(KeggTokens.ECNUMBER_REGEXP);
			Matcher m = p.matcher(value);
			if(m.find())
			{
				addedValue = m.group();
				if(addedValue!=null)
					entry = (String) addedValue;				
			}
		}
		else if(key.equals(KeggTokens.GENES))
		{
			addedValue = getGenesFromValue(value);
			if(addedValue!=null)
				addGenes((Set<String>) addedValue);
		}
		else if(key.equals(KeggTokens.ORTHOLOGY))
		{
			addedValue = getOrthologyFromValue(value);
			if(addedValue!=null)
				addOrtholog((String) addedValue);
		}
		else if(key.equals(KeggTokens.PATHWAY))
		{
			addedValue = getPathwayFromValue(value);
			if(addedValue!=null)
				addPathway((String) addedValue);
		}
		else if(key.equals(KeggTokens.REACTION))
		{
			addedValue = getReactionFromValue(value);
			if(addedValue!=null)
				addReaction((String) addedValue);
		}
		
		if(addedValue==null)
			super.addProperty(key, value);
	}

}
