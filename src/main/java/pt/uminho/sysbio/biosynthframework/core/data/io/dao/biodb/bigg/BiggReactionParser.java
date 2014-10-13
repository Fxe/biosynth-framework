package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;

public interface BiggReactionParser {
	public void setEquationParser(BiggEquationParser biggEquationParser);
	public List<BiggReactionEntity> parseReactions(InputStream inputStream) throws IOException;
}
