package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggTokens;

public class KeggModuleEntity extends KeggEntity{

	
	protected KeggModuleType type;
	
	protected Set<String> pathways;
	protected Set<String> orthologs;
	protected Set<String> compounds;
	protected Set<String> reactions;
	
	
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
	
	public void addCompound(String compound){
		if(compounds==null)
			compounds = new HashSet<>();
		compounds.add(compound);
	}
	
	public void addReaction(String reaction){
		if(reactions==null)
			reactions = new HashSet<>();
		reactions.add(reaction);
	}
	
	public String[] getEntryAndModuleTypeFromValue(String value){
		Pattern p = Pattern.compile(KeggTokens.MODULE_ENTRY_AND_TYPE);
		Matcher m = p.matcher(value);
		return m.find() ? new String[]{m.group(1), m.group(2)} : null;
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
	public Set<String> getCompounds() {
		return compounds;
	}
	public void setCompounds(Set<String> compounds) {
		this.compounds = compounds;
	}
	public Set<String> getReactions() {
		return reactions;
	}
	public void setReactions(Set<String> reactions) {
		this.reactions = reactions;
	}
	public KeggModuleType getType() {
		return type;
	}
	public void setType(KeggModuleType type) {
		this.type = type;
	}

	
	@Override
	public void addProperty(String key, String value) {
		Object addedValue = null;
		
		if(key.equals(KeggTokens.ENTRY))
		{
			addedValue = getEntryAndModuleTypeFromValue(value);
			if(addedValue!=null)
			{
				entry = ((String[]) addedValue)[0];
				type = KeggModuleType.getType(((String[]) addedValue)[1]);
			}
		}
		else if(key.equals(KeggTokens.COMPOUND))
		{
			addedValue = getCompoundFromValue(value);
			if(addedValue!=null)
				addCompound((String) addedValue);
		}
		else if(key.equals(KeggTokens.REACTION))
		{
			addedValue = getReactionFromValue(value);
			if(addedValue!=null)
				addReaction((String) addedValue);
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
		
		if(addedValue==null)
			super.addProperty(key, value);
	}
	
	// TODO orthologs by reaction or EC
	@Override
	public String getOrthologyFromValue(String value) {
		return null;
	}

}
