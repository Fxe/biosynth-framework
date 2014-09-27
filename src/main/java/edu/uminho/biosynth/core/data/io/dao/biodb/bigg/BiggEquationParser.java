package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import java.util.List;

import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggReactionRightEntity;

public interface BiggEquationParser {
	
	public void setEquation(String equation);
	public void parse();
	public List<BiggReactionLeftEntity> getLeft();
	public List<BiggReactionRightEntity> getRight();
}
