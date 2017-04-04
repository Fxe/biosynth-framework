package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggECNumberEntity extends KeggEntity{
	
	/**
   * 
   */
  private static final long serialVersionUID = 1L;
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
	
	public void addOrthologs(Set<String> ortholog){
		if(orthologs==null)
			orthologs = new HashSet<>();
		orthologs.addAll(ortholog);
	}
	
	public void addReaction(String reaction){
		if(reactions==null)
			reactions = new HashSet<>();
		reactions.add(reaction);
	}
	
	public Set<String> getReactionsFromValue(String value){
//		String reactionsValue = retrieveValueRegExp(value, KeggTokens.EC_NUMBER_REACTIONS);
//		return reactionsValue==null ? null : retrieveValuesRegExp(reactionsValue, KeggTokens.REACTION_ID_EXP);
		return retrieveValuesRegExp(value, KeggTokens.REACTION_ID_EXP);
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

	
	@SuppressWarnings("unchecked")
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
				addOrthologs((Set<String>) addedValue);
		}
		else if(key.equals(KeggTokens.PATHWAY))
		{
			addedValue = getPathwayFromValue(value);
			if(addedValue!=null)
				addPathway((String) addedValue);
		}
		else if(key.equals(KeggTokens.REACTION) || key.equals(KeggTokens.ALL_REACTIONS))
		{
			addedValue = getReactionsFromValue(value);
			if(addedValue!=null)
				for(String r : (Set<String>) addedValue)
					addReaction(r);
		}
		
		if(addedValue==null)
			super.addProperty(key, value);
	}

}
