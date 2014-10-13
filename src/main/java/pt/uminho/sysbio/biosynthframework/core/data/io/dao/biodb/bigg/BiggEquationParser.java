package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg;

import java.util.List;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionLeftEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionRightEntity;

public interface BiggEquationParser {
	
	public void setEquation(String equation);
	public void parse();
	public List<BiggReactionLeftEntity> getLeft();
	public List<BiggReactionRightEntity> getRight();
}
