package pt.uminho.sysbio.biosynthframework.core.data.io.parser;

import java.util.Set;

public interface IGenericReactionParser {

	public String getEntry();
	public String getName();
	public String getEquation();
	public String getRemark();
	public Set<String> getSimilarReactions();
	public Set<String> getRPair();
	public Set<String> getEnzymes();
}
