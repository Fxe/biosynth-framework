package edu.uminho.biosynth.core.data.io.parser;

import java.util.List;

public interface IGenericMetaboliteParser {

	public String getEntry();
	public String getName();
	public String getFormula();
	
	public List<String> getReactions();
//	public String getComment();
//	public String getRemark();
//	public Set<String> getSimilarity();
//	public List<String> getEnzymes();
}
