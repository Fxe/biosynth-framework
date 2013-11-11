package edu.uminho.biosynth.core.data.io.parser;

import java.util.Set;

public interface IGenericMetaboliteParser {

	public String getEntry();
	public String getName();
	public String getFormula();
	public Set<String> getReactions();
	public String getComment();
	public String getRemark();
	public Set<String> getSimilarity();
	public Set<String> getEnzymes();
}
